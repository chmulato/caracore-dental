package com.caracore.cca.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para o CaptchaService
 */
@ExtendWith(MockitoExtension.class)
class CaptchaServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CaptchaService captchaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Configurar valores das propriedades via ReflectionTestUtils
        ReflectionTestUtils.setField(captchaService, "recaptchaSecret", "test-secret-key");
        ReflectionTestUtils.setField(captchaService, "recaptchaSiteKey", "test-site-key");
        ReflectionTestUtils.setField(captchaService, "recaptchaVerifyUrl", "https://www.google.com/recaptcha/api/siteverify");
        ReflectionTestUtils.setField(captchaService, "recaptchaEnabled", true);
        ReflectionTestUtils.setField(captchaService, "restTemplate", restTemplate);
    }

    @Test
    void testIsEnabledWhenConfigured() {
        // Given: reCAPTCHA habilitado e configurado
        
        // When
        boolean enabled = captchaService.isEnabled();
        
        // Then
        assertTrue(enabled, "reCAPTCHA deve estar habilitado quando configurado");
    }

    @Test
    void testIsEnabledWhenDisabled() {
        // Given: reCAPTCHA desabilitado
        ReflectionTestUtils.setField(captchaService, "recaptchaEnabled", false);
        
        // When
        boolean enabled = captchaService.isEnabled();
        
        // Then
        assertFalse(enabled, "reCAPTCHA deve estar desabilitado quando configurado como false");
    }

    @Test
    void testIsEnabledWhenSecretEmpty() {
        // Given: secret vazio
        ReflectionTestUtils.setField(captchaService, "recaptchaSecret", "");
        
        // When
        boolean enabled = captchaService.isEnabled();
        
        // Then
        assertFalse(enabled, "reCAPTCHA deve estar desabilitado quando secret está vazio");
    }

    @Test
    void testGetSiteKey() {
        // When
        String siteKey = captchaService.getSiteKey();
        
        // Then
        assertEquals("test-site-key", siteKey, "Deve retornar a chave pública configurada");
    }

    @Test
    void testValidateCaptchaWhenDisabled() {
        // Given: reCAPTCHA desabilitado
        ReflectionTestUtils.setField(captchaService, "recaptchaEnabled", false);
        
        // When
        boolean result = captchaService.validateCaptcha("any-token", "127.0.0.1");
        
        // Then
        assertTrue(result, "Deve retornar true quando reCAPTCHA está desabilitado");
        verify(restTemplate, never()).getForObject(any(), any());
    }

    @Test
    void testValidateCaptchaWithEmptyToken() {
        // Given: token vazio
        
        // When
        boolean result = captchaService.validateCaptcha("", "127.0.0.1");
        
        // Then
        assertFalse(result, "Deve retornar false para token vazio");
        verify(restTemplate, never()).getForObject(any(), any());
    }

    @Test
    void testValidateCaptchaWithNullToken() {
        // Given: token nulo
        
        // When
        boolean result = captchaService.validateCaptcha(null, "127.0.0.1");
        
        // Then
        assertFalse(result, "Deve retornar false para token nulo");
        verify(restTemplate, never()).getForObject(any(), any());
    }

    @Test
    void testValidateCaptchaSuccess() {
        // Given: resposta de sucesso do Google
        CaptchaService.RecaptchaResponse mockResponse = new CaptchaService.RecaptchaResponse();
        mockResponse.setSuccess(true);
        
        when(restTemplate.getForObject(any(String.class), eq(CaptchaService.RecaptchaResponse.class)))
                .thenReturn(mockResponse);
        
        // When
        boolean result = captchaService.validateCaptcha("valid-token", "127.0.0.1");
        
        // Then
        assertTrue(result, "Deve retornar true para captcha válido");
        verify(restTemplate).getForObject(any(String.class), eq(CaptchaService.RecaptchaResponse.class));
    }

    @Test
    void testValidateCaptchaFailure() {
        // Given: resposta de falha do Google
        CaptchaService.RecaptchaResponse mockResponse = new CaptchaService.RecaptchaResponse();
        mockResponse.setSuccess(false);
        mockResponse.setErrorCodes(new String[]{"invalid-input-response"});
        
        when(restTemplate.getForObject(any(String.class), eq(CaptchaService.RecaptchaResponse.class)))
                .thenReturn(mockResponse);
        
        // When
        boolean result = captchaService.validateCaptcha("invalid-token", "127.0.0.1");
        
        // Then
        assertFalse(result, "Deve retornar false para captcha inválido");
        verify(restTemplate).getForObject(any(String.class), eq(CaptchaService.RecaptchaResponse.class));
    }

    @Test
    void testValidateCaptchaWithNullResponse() {
        // Given: resposta nula do Google
        when(restTemplate.getForObject(any(String.class), eq(CaptchaService.RecaptchaResponse.class)))
                .thenReturn(null);
        
        // When
        boolean result = captchaService.validateCaptcha("any-token", "127.0.0.1");
        
        // Then
        assertFalse(result, "Deve retornar false para resposta nula");
        verify(restTemplate).getForObject(any(String.class), eq(CaptchaService.RecaptchaResponse.class));
    }

    @Test
    void testValidateCaptchaWithException() {
        // Given: exceção durante a validação
        when(restTemplate.getForObject(any(String.class), eq(CaptchaService.RecaptchaResponse.class)))
                .thenThrow(new RuntimeException("Network error"));
        
        // When
        boolean result = captchaService.validateCaptcha("any-token", "127.0.0.1");
        
        // Then
        assertFalse(result, "Deve retornar false quando ocorre exceção");
        verify(restTemplate).getForObject(any(String.class), eq(CaptchaService.RecaptchaResponse.class));
    }

    @Test
    void testRecaptchaResponseClass() {
        // Given: instância da classe RecaptchaResponse
        CaptchaService.RecaptchaResponse response = new CaptchaService.RecaptchaResponse();
        
        // When & Then: testar getters e setters
        response.setSuccess(true);
        assertTrue(response.isSuccess());
        
        response.setErrorCodes(new String[]{"error1", "error2"});
        assertArrayEquals(new String[]{"error1", "error2"}, response.getErrorCodes());
        
        response.setChallengeTs("2025-07-06T10:00:00Z");
        assertEquals("2025-07-06T10:00:00Z", response.getChallengeTs());
        
        response.setHostname("example.com");
        assertEquals("example.com", response.getHostname());
    }

    @Test
    void testValidateCaptchaUrlConstruction() {
        // Given: mock para capturar URL
        CaptchaService.RecaptchaResponse mockResponse = new CaptchaService.RecaptchaResponse();
        mockResponse.setSuccess(true);
        
        when(restTemplate.getForObject(any(String.class), eq(CaptchaService.RecaptchaResponse.class)))
                .thenReturn(mockResponse);
        
        // When
        captchaService.validateCaptcha("test-token", "192.168.1.1");
        
        // Then: verificar se a URL foi construída corretamente
        verify(restTemplate).getForObject(
                eq("https://www.google.com/recaptcha/api/siteverify?secret=test-secret-key&response=test-token&remoteip=192.168.1.1"),
                eq(CaptchaService.RecaptchaResponse.class)
        );
    }
}
