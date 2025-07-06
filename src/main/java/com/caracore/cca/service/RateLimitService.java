package com.caracore.cca.service;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Serviço de Rate Limiting para prevenir abuso de endpoints públicos
 */
@Service
public class RateLimitService {
    
    private static final Logger logger = LoggerFactory.getLogger(RateLimitService.class);
    
    // Configurações de limite
    private static final int MAX_REQUESTS_PER_MINUTE = 5;
    private static final int MAX_REQUESTS_PER_HOUR = 20;
    private static final int MAX_REQUESTS_PER_DAY = 50;
    
    // Armazenamento em memória dos contadores (em produção, usar Redis)
    private final ConcurrentHashMap<String, RequestCounter> counters = new ConcurrentHashMap<>();
    
    /**
     * Verifica se o IP está dentro dos limites permitidos
     */
    public boolean isAllowed(String clientIp) {
        RequestCounter counter = counters.computeIfAbsent(clientIp, k -> new RequestCounter());
        
        LocalDateTime now = LocalDateTime.now();
        
        // Limpar contadores antigos
        counter.cleanOldRequests(now);
        
        // Verificar limites
        if (counter.getRequestsLastMinute(now) >= MAX_REQUESTS_PER_MINUTE) {
            logger.warn("Rate limit excedido por minuto para IP: {} ({})", 
                       clientIp, counter.getRequestsLastMinute(now));
            return false;
        }
        
        if (counter.getRequestsLastHour(now) >= MAX_REQUESTS_PER_HOUR) {
            logger.warn("Rate limit excedido por hora para IP: {} ({})", 
                       clientIp, counter.getRequestsLastHour(now));
            return false;
        }
        
        if (counter.getRequestsLastDay(now) >= MAX_REQUESTS_PER_DAY) {
            logger.warn("Rate limit excedido por dia para IP: {} ({})", 
                       clientIp, counter.getRequestsLastDay(now));
            return false;
        }
        
        // Registrar nova requisição
        counter.addRequest(now);
        logger.debug("Requisição permitida para IP: {} (Min: {}, Hora: {}, Dia: {})", 
                    clientIp, 
                    counter.getRequestsLastMinute(now),
                    counter.getRequestsLastHour(now),
                    counter.getRequestsLastDay(now));
        
        return true;
    }
    
    /**
     * Obtém informações sobre o status atual do IP
     */
    public RateLimitStatus getStatus(String clientIp) {
        RequestCounter counter = counters.get(clientIp);
        if (counter == null) {
            return new RateLimitStatus(0, 0, 0);
        }
        
        LocalDateTime now = LocalDateTime.now();
        counter.cleanOldRequests(now);
        
        return new RateLimitStatus(
            counter.getRequestsLastMinute(now),
            counter.getRequestsLastHour(now),
            counter.getRequestsLastDay(now)
        );
    }
    
    /**
     * Classe para contar requisições por IP
     */
    private static class RequestCounter {
        private final ConcurrentHashMap<LocalDateTime, AtomicInteger> requests = new ConcurrentHashMap<>();
        
        public void addRequest(LocalDateTime timestamp) {
            // Arredondar para o minuto
            LocalDateTime minuteKey = timestamp.withSecond(0).withNano(0);
            requests.computeIfAbsent(minuteKey, k -> new AtomicInteger(0)).incrementAndGet();
        }
        
        public int getRequestsLastMinute(LocalDateTime now) {
            LocalDateTime oneMinuteAgo = now.minusMinutes(1);
            return requests.entrySet().stream()
                .filter(entry -> entry.getKey().isAfter(oneMinuteAgo))
                .mapToInt(entry -> entry.getValue().get())
                .sum();
        }
        
        public int getRequestsLastHour(LocalDateTime now) {
            LocalDateTime oneHourAgo = now.minusHours(1);
            return requests.entrySet().stream()
                .filter(entry -> entry.getKey().isAfter(oneHourAgo))
                .mapToInt(entry -> entry.getValue().get())
                .sum();
        }
        
        public int getRequestsLastDay(LocalDateTime now) {
            LocalDateTime oneDayAgo = now.minusDays(1);
            return requests.entrySet().stream()
                .filter(entry -> entry.getKey().isAfter(oneDayAgo))
                .mapToInt(entry -> entry.getValue().get())
                .sum();
        }
        
        public void cleanOldRequests(LocalDateTime now) {
            LocalDateTime cutoff = now.minusDays(1);
            requests.entrySet().removeIf(entry -> entry.getKey().isBefore(cutoff));
        }
    }
    
    /**
     * Classe para retornar o status atual do rate limiting
     */
    public static class RateLimitStatus {
        private final int requestsLastMinute;
        private final int requestsLastHour;
        private final int requestsLastDay;
        
        public RateLimitStatus(int requestsLastMinute, int requestsLastHour, int requestsLastDay) {
            this.requestsLastMinute = requestsLastMinute;
            this.requestsLastHour = requestsLastHour;
            this.requestsLastDay = requestsLastDay;
        }
        
        public int getRequestsLastMinute() {
            return requestsLastMinute;
        }
        
        public int getRequestsLastHour() {
            return requestsLastHour;
        }
        
        public int getRequestsLastDay() {
            return requestsLastDay;
        }
        
        public boolean isNearLimit() {
            return requestsLastMinute >= MAX_REQUESTS_PER_MINUTE - 1 ||
                   requestsLastHour >= MAX_REQUESTS_PER_HOUR - 2 ||
                   requestsLastDay >= MAX_REQUESTS_PER_DAY - 5;
        }
    }
}
