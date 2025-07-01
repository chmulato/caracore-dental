package com.caracore.cca.config;

import com.caracore.cca.util.UserActivityLogger;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserActivityInterceptorTest {

    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    @Mock
    private Object handler;
    
    @Mock
    private UserActivityLogger activityLogger;
    
    @Mock
    private SecurityContext securityContext;
    
    @Mock
    private Authentication authentication;
    
    @InjectMocks
    private UserActivityInterceptor interceptor;
    
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        
        // Configure SecurityContext
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        
        // Injetar o logger de atividade usando ReflectionTestUtils (forma segura)
        ReflectionTestUtils.setField(interceptor, "activityLogger", activityLogger);
    }
    
    @Test
    @DisplayName("preHandle deve retornar true permitindo que a solicitação continue")
    public void preHandleDeveRetornarTrue() {
        // Act
        boolean result = interceptor.preHandle(request, response, handler);
        
        // Assert
        assertTrue(result, "preHandle deve retornar true para continuar a solicitação");
    }
    
    @Test
    @DisplayName("postHandle deve ignorar recursos estáticos")
    public void postHandleDeveIgnorarRecursosEstaticos() {
        // Arrange - configurar recurso estático
        when(request.getRequestURI()).thenReturn("/css/style.css");
        
        // Act
        interceptor.postHandle(request, response, handler, null);
        
        // Assert - verificar que nenhum log foi criado
        verifyNoInteractions(activityLogger);
    }
    
    @Test
    @DisplayName("postHandle deve ignorar página de login")
    public void postHandleDeveIgnorarPaginaLogin() {
        // Arrange - configurar página de login
        when(request.getRequestURI()).thenReturn("/login");
        
        // Act
        interceptor.postHandle(request, response, handler, null);
        
        // Assert - verificar que nenhum log foi criado
        verifyNoInteractions(activityLogger);
    }
    
    @Test
    @DisplayName("postHandle deve registrar acesso a página normal para usuário autenticado")
    public void postHandleDeveRegistrarAcessoAoPagina() {
        // Arrange
        when(request.getRequestURI()).thenReturn("/agendamentos");
        when(request.getMethod()).thenReturn("GET");
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("user@example.com");
        
        ModelAndView modelAndView = new ModelAndView("agendamentos");
        
        // Act
        interceptor.postHandle(request, response, handler, modelAndView);
        
        // Assert
        verify(activityLogger, times(1)).logActivity(
            eq("PAGE_VIEW"),
            contains("/agendamentos"),
            isNull(),
            isNull()
        );
    }
    
    @Test
    @DisplayName("postHandle deve registrar criação para métodos POST")
    public void postHandleDeveRegistrarCriacao() {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/agendamentos/create");
        when(request.getMethod()).thenReturn("POST");
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("user@example.com");
        
        // Act
        interceptor.postHandle(request, response, handler, null);
        
        // Assert
        verify(activityLogger, times(1)).logActivity(
            eq("CREATE"),
            contains("/api/agendamentos/create"),
            isNull(),
            isNull()
        );
    }
    
    @Test
    @DisplayName("postHandle deve registrar atualização para métodos PUT")
    public void postHandleDeveRegistrarAtualizacao() {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/agendamentos/1");
        when(request.getMethod()).thenReturn("PUT");
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("user@example.com");
        
        // Act
        interceptor.postHandle(request, response, handler, null);
        
        // Assert
        verify(activityLogger, times(1)).logActivity(
            eq("UPDATE"),
            contains("/api/agendamentos/1"),
            isNull(),
            isNull()
        );
    }
    
    @Test
    @DisplayName("postHandle deve registrar exclusão para métodos DELETE")
    public void postHandleDeveRegistrarExclusao() {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/agendamentos/1");
        when(request.getMethod()).thenReturn("DELETE");
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("user@example.com");
        
        // Act
        interceptor.postHandle(request, response, handler, null);
        
        // Assert
        verify(activityLogger, times(1)).logActivity(
            eq("DELETE"),
            contains("/api/agendamentos/1"),
            isNull(),
            isNull()
        );
    }
    
    @Test
    @DisplayName("postHandle não deve registrar para usuário anônimo")
    public void postHandleNaoDeveRegistrarParaUsuarioAnonimo() {
        // Arrange
        when(request.getRequestURI()).thenReturn("/agendamentos");
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("anonymousUser");
        
        // Act
        interceptor.postHandle(request, response, handler, null);
        
        // Assert
        verifyNoInteractions(activityLogger);
    }
}
