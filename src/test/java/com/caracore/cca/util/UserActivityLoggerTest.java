package com.caracore.cca.util;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(OutputCaptureExtension.class)
public class UserActivityLoggerTest {

    @InjectMocks
    private UserActivityLogger activityLogger;

    @Mock
    private HttpServletRequest request;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;
    
    private AutoCloseable closeable;

    @BeforeEach
    public void setup() {
        closeable = MockitoAnnotations.openMocks(this);
        
        // Mock Security Context
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("test@example.com");

        // Mock Request Context
        ServletRequestAttributes attributes = mock(ServletRequestAttributes.class);
        when(attributes.getRequest()).thenReturn(request);
        RequestContextHolder.setRequestAttributes(attributes);
        
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getHeader("User-Agent")).thenReturn("Mozilla/5.0 (Test)");
    }
    
    @AfterEach
    void closeService() throws Exception {
        closeable.close();
        SecurityContextHolder.clearContext();
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("Deve registrar atividade com todos os parâmetros")
    public void deveRegistrarAtividadeCompleta(CapturedOutput output) {
        // Act
        activityLogger.logActivity("TEST_ACTION", "Teste de ação", "Entidade:123", "Observação de teste");
        
        // Assert - verifica se o log contém as informações esperadas
        assertThat(output.getOut())
            .contains("test@example.com") // Username
            .contains("TEST_ACTION")      // Action
            .contains("IP: 127.0.0.1")    // IP address
            .contains("Mozilla/5.0")      // User agent
            .contains("Teste de ação")    // Details
            .contains("Entidade:123")     // Entities
            .contains("Observação de teste"); // Observations
    }

    @Test
    @DisplayName("Deve registrar login bem-sucedido")
    public void deveRegistrarLoginSucesso(CapturedOutput output) {
        // Act
        activityLogger.logLogin("test@example.com", true, "Login normal");
        
        // Assert
        assertThat(output.getOut())
            .contains("LOGIN")
            .contains("Login bem-sucedido");
    }

    @Test
    @DisplayName("Deve registrar tentativa de login falha")
    public void deveRegistrarLoginFalha(CapturedOutput output) {
        // Act
        activityLogger.logLogin("test@example.com", false, "Senha incorreta");
        
        // Assert
        assertThat(output.getOut())
            .contains("FAILED_LOGIN")
            .contains("Tentativa de login falhou");
    }

    @Test
    @DisplayName("Deve registrar logout")
    public void deveRegistrarLogout(CapturedOutput output) {
        // Act
        activityLogger.logLogout("Logout pelo botão");
        
        // Assert
        assertThat(output.getOut())
            .contains("LOGOUT")
            .contains("Logout do sistema");
    }

    @Test
    @DisplayName("Deve registrar criação de entidade")
    public void deveRegistrarCriacaoDeEntidade(CapturedOutput output) {
        // Act
        activityLogger.logCreate("Usuario", 42L, "João Silva", "Criado pelo admin");
        
        // Assert
        assertThat(output.getOut())
            .contains("CREATE_USUARIO")
            .contains("Criou novo registro de usuario")
            .contains("Usuario ID 42 (João Silva)");
    }
}
