package com.caracore.cca.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * Serviço para validação de Captcha (Google reCAPTCHA)
 */
@Service
public class CaptchaService {
    
    private static final Logger logger = LoggerFactory.getLogger(CaptchaService.class);
    
    @Value("${recaptcha.secret:}")
    private String recaptchaSecret;
    
    @Value("${recaptcha.site-key:}")
    private String recaptchaSiteKey;
    
    @Value("${recaptcha.verify-url:https://www.google.com/recaptcha/api/siteverify}")
    private String recaptchaVerifyUrl;
    
    @Value("${recaptcha.enabled:false}")
    private boolean recaptchaEnabled;
    
    private final RestTemplate restTemplate;
    
    public CaptchaService() {
        this.restTemplate = new RestTemplate();
    }
    
    /**
     * Verifica se o reCAPTCHA está habilitado
     */
    public boolean isEnabled() {
        return recaptchaEnabled && recaptchaSecret != null && !recaptchaSecret.isEmpty();
    }
    
    /**
     * Retorna a chave pública do site para uso no frontend
     */
    public String getSiteKey() {
        return recaptchaSiteKey;
    }
    
    /**
     * Valida o token do reCAPTCHA
     */
    public boolean validateCaptcha(String captchaToken, String clientIp) {
        // Se não estiver habilitado, retorna true (desenvolvimento)
        if (!isEnabled()) {
            logger.debug("reCAPTCHA desabilitado - validação pulada para IP: {}", clientIp);
            return true;
        }
        
        if (captchaToken == null || captchaToken.isEmpty()) {
            logger.warn("Token de captcha vazio para IP: {}", clientIp);
            return false;
        }
        
        try {
            // Preparar parâmetros para validação
            Map<String, String> params = new HashMap<>();
            params.put("secret", recaptchaSecret);
            params.put("response", captchaToken);
            params.put("remoteip", clientIp);
            
            // Fazer requisição para Google
            String url = recaptchaVerifyUrl + "?secret=" + recaptchaSecret + 
                        "&response=" + captchaToken + "&remoteip=" + clientIp;
            
            RecaptchaResponse response = restTemplate.getForObject(url, RecaptchaResponse.class);
            
            if (response != null && response.isSuccess()) {
                logger.info("Captcha validado com sucesso para IP: {}", clientIp);
                return true;
            } else {
                logger.warn("Captcha inválido para IP: {} - Erros: {}", clientIp, 
                           response != null ? response.getErrorCodes() : "null response");
                return false;
            }
            
        } catch (Exception e) {
            logger.error("Erro ao validar captcha para IP: {}", clientIp, e);
            return false;
        }
    }
    
    /**
     * Classe para mapear a resposta do Google reCAPTCHA
     */
    public static class RecaptchaResponse {
        private boolean success;
        private String[] errorCodes;
        private String challengeTs;
        private String hostname;
        
        public boolean isSuccess() {
            return success;
        }
        
        public void setSuccess(boolean success) {
            this.success = success;
        }
        
        public String[] getErrorCodes() {
            return errorCodes;
        }
        
        public void setErrorCodes(String[] errorCodes) {
            this.errorCodes = errorCodes;
        }
        
        public String getChallengeTs() {
            return challengeTs;
        }
        
        public void setChallengeTs(String challengeTs) {
            this.challengeTs = challengeTs;
        }
        
        public String getHostname() {
            return hostname;
        }
        
        public void setHostname(String hostname) {
            this.hostname = hostname;
        }
    }
}
