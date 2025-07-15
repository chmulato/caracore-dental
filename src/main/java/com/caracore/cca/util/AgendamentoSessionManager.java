package com.caracore.cca.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * Utilitário para gerenciamento avançado de sessão no fluxo de agendamento
 */
@Component
public class AgendamentoSessionManager {
    
    private static final Logger logger = LoggerFactory.getLogger(AgendamentoSessionManager.class);
    
    // Constantes para chaves da sessão
    public static final String SESSION_PREFIX = "agendamento.";
    public static final String PACIENTE_KEY = SESSION_PREFIX + "paciente";
    public static final String TELEFONE_KEY = SESSION_PREFIX + "telefone";
    public static final String EMAIL_KEY = SESSION_PREFIX + "email";
    public static final String DENTISTA_KEY = SESSION_PREFIX + "dentista";
    public static final String DATA_KEY = SESSION_PREFIX + "data";
    public static final String HORARIO_KEY = SESSION_PREFIX + "horario";
    public static final String TIMESTAMP_KEY = SESSION_PREFIX + "timestamp";
    public static final String ETAPA_ATUAL_KEY = SESSION_PREFIX + "etapa_atual";
    public static final String SESSION_ID_KEY = SESSION_PREFIX + "session_id";
    
    // Configurações
    private static final int SESSION_TIMEOUT_MINUTES = 30; // 30 minutos
    private static final String[] ALL_SESSION_KEYS = {
        PACIENTE_KEY, TELEFONE_KEY, EMAIL_KEY, DENTISTA_KEY, 
        DATA_KEY, HORARIO_KEY, TIMESTAMP_KEY, ETAPA_ATUAL_KEY, SESSION_ID_KEY
    };
    
    /**
     * Inicia uma nova sessão de agendamento
     */
    public void iniciarSessao(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        String sessionId = session.getId();
        
        // Limpar dados anteriores se existirem
        limparSessao(request);
        
        // Configurar nova sessão
        session.setAttribute(SESSION_ID_KEY, sessionId);
        session.setAttribute(TIMESTAMP_KEY, LocalDateTime.now().toString());
        session.setAttribute(ETAPA_ATUAL_KEY, 1);
        
        logger.info("Nova sessão de agendamento iniciada - ID: {}", sessionId);
    }
    
    /**
     * Salva dados da Etapa 1 na sessão
     */
    public void salvarEtapa1(HttpServletRequest request, String paciente, String telefone, String email, String dentista) {
        HttpSession session = request.getSession();
        
        // Validar sessão
        if (!isSessionValid(request)) {
            throw new SessionExpiredException("Sessão expirada. Reinicie o processo.");
        }
        
        // Salvar dados
        session.setAttribute(PACIENTE_KEY, sanitize(paciente));
        session.setAttribute(TELEFONE_KEY, sanitize(telefone));
        session.setAttribute(EMAIL_KEY, sanitize(email));
        session.setAttribute(DENTISTA_KEY, sanitize(dentista));
        session.setAttribute(ETAPA_ATUAL_KEY, 2);
        session.setAttribute(TIMESTAMP_KEY, LocalDateTime.now().toString());
        
        logger.info("Dados da Etapa 1 salvos na sessão - Paciente: {}, Dentista: {}, SessionID: {}", 
                   paciente, dentista, session.getId());
    }
    
    /**
     * Salva dados da Etapa 2 na sessão
     */
    public void salvarEtapa2(HttpServletRequest request, String data, String horario) {
        HttpSession session = request.getSession();
        
        // Validar sessão e dados da etapa anterior
        if (!isSessionValid(request) || !hasEtapa1Data(request)) {
            throw new SessionExpiredException("Dados da sessão incompletos. Reinicie o processo.");
        }
        
        // Salvar dados
        session.setAttribute(DATA_KEY, sanitize(data));
        session.setAttribute(HORARIO_KEY, sanitize(horario));
        session.setAttribute(ETAPA_ATUAL_KEY, 3);
        session.setAttribute(TIMESTAMP_KEY, LocalDateTime.now().toString());
        
        logger.info("Dados da Etapa 2 salvos na sessão - Data: {}, Horário: {}, SessionID: {}", 
                   data, horario, session.getId());
    }
    
    /**
     * Recupera todos os dados da sessão
     */
    public AgendamentoSessionData recuperarDados(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        
        if (session == null || !isSessionValid(request)) {
            return null;
        }
        
        AgendamentoSessionData data = new AgendamentoSessionData();
        data.setPaciente((String) session.getAttribute(PACIENTE_KEY));
        data.setTelefone((String) session.getAttribute(TELEFONE_KEY));
        data.setEmail((String) session.getAttribute(EMAIL_KEY));
        data.setDentista((String) session.getAttribute(DENTISTA_KEY));
        data.setData((String) session.getAttribute(DATA_KEY));
        data.setHorario((String) session.getAttribute(HORARIO_KEY));
        data.setEtapaAtual((Integer) session.getAttribute(ETAPA_ATUAL_KEY));
        data.setSessionId((String) session.getAttribute(SESSION_ID_KEY));
        
        return data;
    }
    
    /**
     * Verifica se a sessão é válida e não expirou
     */
    public boolean isSessionValid(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        
        if (session == null) {
            return false;
        }
        
        String timestampStr = (String) session.getAttribute(TIMESTAMP_KEY);
        if (timestampStr == null) {
            return false;
        }
        
        try {
            LocalDateTime timestamp = LocalDateTime.parse(timestampStr);
            LocalDateTime now = LocalDateTime.now();
            long minutesElapsed = ChronoUnit.MINUTES.between(timestamp, now);
            
            return minutesElapsed <= SESSION_TIMEOUT_MINUTES;
        } catch (Exception e) {
            logger.warn("Erro ao validar timestamp da sessão: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Verifica se há dados da Etapa 1 na sessão
     */
    public boolean hasEtapa1Data(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        
        if (session == null) {
            return false;
        }
        
        String paciente = (String) session.getAttribute(PACIENTE_KEY);
        String dentista = (String) session.getAttribute(DENTISTA_KEY);
        
        return paciente != null && !paciente.trim().isEmpty() && 
               dentista != null && !dentista.trim().isEmpty();
    }
    
    /**
     * Verifica se há dados da Etapa 2 na sessão
     */
    public boolean hasEtapa2Data(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        
        if (session == null) {
            return false;
        }
        
        String data = (String) session.getAttribute(DATA_KEY);
        String horario = (String) session.getAttribute(HORARIO_KEY);
        
        return data != null && !data.trim().isEmpty() && 
               horario != null && !horario.trim().isEmpty();
    }
    
    /**
     * Verifica se todos os dados estão completos para finalização
     */
    public boolean hasCompleteData(HttpServletRequest request) {
        return isSessionValid(request) && hasEtapa1Data(request) && hasEtapa2Data(request);
    }
    
    /**
     * Limpa todos os dados da sessão de agendamento
     */
    public void limparSessao(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            String sessionId = session.getId();
            
            for (String key : ALL_SESSION_KEYS) {
                session.removeAttribute(key);
            }
            
            logger.info("Sessão de agendamento limpa - SessionID: {}", sessionId);
        }
    }
    
    /**
     * Limpa apenas os dados da Etapa 2 (para navegação regressiva)
     */
    public void limparDadosEtapa2(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            session.removeAttribute(DATA_KEY);
            session.removeAttribute(HORARIO_KEY);
            
            // Resetar etapa atual para 1
            session.setAttribute(ETAPA_ATUAL_KEY, 1);
            session.setAttribute(TIMESTAMP_KEY, LocalDateTime.now().toString());
            
            logger.info("Dados da Etapa 2 limpos - SessionID: {}", session.getId());
        }
    }
    
    /**
     * Limpa dados da Etapa 3 (reservado para futuras implementações)
     */
    public void limparDadosEtapa3(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            // Por enquanto, apenas resetar etapa atual para 2
            session.setAttribute(ETAPA_ATUAL_KEY, 2);
            session.setAttribute(TIMESTAMP_KEY, LocalDateTime.now().toString());
            
            logger.info("Dados da Etapa 3 limpos - SessionID: {}", session.getId());
        }
    }
    
    /**
     * Renova o timestamp da sessão (para manter ativa)
     */
    public void renovarSessao(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            session.setAttribute(TIMESTAMP_KEY, LocalDateTime.now().toString());
            logger.debug("Timestamp da sessão renovado - SessionID: {}", session.getId());
        }
    }
    
    /**
     * Obtém informações de debug da sessão
     */
    public Map<String, Object> getSessionInfo(HttpServletRequest request) {
        Map<String, Object> info = new HashMap<>();
        HttpSession session = request.getSession(false);
        
        if (session == null) {
            info.put("session_exists", false);
            return info;
        }
        
        info.put("session_exists", true);
        info.put("session_id", session.getId());
        info.put("is_valid", isSessionValid(request));
        info.put("has_etapa1_data", hasEtapa1Data(request));
        info.put("has_etapa2_data", hasEtapa2Data(request));
        info.put("has_complete_data", hasCompleteData(request));
        info.put("etapa_atual", session.getAttribute(ETAPA_ATUAL_KEY));
        info.put("timestamp", session.getAttribute(TIMESTAMP_KEY));
        
        return info;
    }
    
    /**
     * Sanitiza uma string removendo caracteres potencialmente perigosos
     */
    private String sanitize(String input) {
        if (input == null) {
            return null;
        }
        return input.trim();
    }
    
    /**
     * Exceção personalizada para sessão expirada
     */
    public static class SessionExpiredException extends RuntimeException {
        public SessionExpiredException(String message) {
            super(message);
        }
        
        private static final long serialVersionUID = 1L;
    }
    
    /**
     * Classe para armazenar dados da sessão de agendamento
     */
    public static class AgendamentoSessionData {
        private String paciente;
        private String telefone;
        private String email;
        private String dentista;
        private String data;
        private String horario;
        private Integer etapaAtual;
        private String sessionId;
        
        // Getters e Setters
        public String getPaciente() { return paciente; }
        public void setPaciente(String paciente) { this.paciente = paciente; }
        
        public String getTelefone() { return telefone; }
        public void setTelefone(String telefone) { this.telefone = telefone; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getDentista() { return dentista; }
        public void setDentista(String dentista) { this.dentista = dentista; }
        
        public String getData() { return data; }
        public void setData(String data) { this.data = data; }
        
        public String getHorario() { return horario; }
        public void setHorario(String horario) { this.horario = horario; }
        
        public Integer getEtapaAtual() { return etapaAtual; }
        public void setEtapaAtual(Integer etapaAtual) { this.etapaAtual = etapaAtual; }
        
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        
        public boolean isComplete() {
            return paciente != null && !paciente.trim().isEmpty() &&
                   dentista != null && !dentista.trim().isEmpty() &&
                   data != null && !data.trim().isEmpty() &&
                   horario != null && !horario.trim().isEmpty();
        }
        
        @Override
        public String toString() {
            return String.format("AgendamentoSessionData{paciente='%s', dentista='%s', data='%s', horario='%s', etapa=%d}", 
                               paciente, dentista, data, horario, etapaAtual);
        }
    }
}
