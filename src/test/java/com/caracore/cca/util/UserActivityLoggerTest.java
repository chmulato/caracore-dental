package com.caracore.cca.util;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("test@example.com"); // Not "anonymousUser"

        // Mock Request Context
        ServletRequestAttributes attributes = mock(ServletRequestAttributes.class);
        when(attributes.getRequest()).thenReturn(request);
        RequestContextHolder.setRequestAttributes(attributes);
        
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getHeader("User-Agent")).thenReturn("Mozilla/5.0 (Test)");
    }
    
    @AfterEach
    public void tearDown() throws Exception {
        closeable.close();
        SecurityContextHolder.clearContext();
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("Deve registrar atividade com todos os parâmetros")
    public void deveRegistrarAtividadeCompleta(CapturedOutput output) {
        // Configuração - garantir que o nome de usuário é test@example.com
        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("test@example.com");
        
        // Act
        activityLogger.logActivity("TEST_ACTION", "Teste de ação", "Entidade:123", "Observação de teste");
        
        // Assert - verifica se o log contém as informações esperadas
        assertThat(output.toString()).contains("test@example.com");
        assertThat(output.toString()).contains("TEST_ACTION");
        assertThat(output.toString()).contains("IP: 127.0.0.1");
        assertThat(output.toString()).contains("Mozilla/5.0 (Test)");
        assertThat(output.toString()).contains("Teste de ação");
        assertThat(output.toString()).contains("Entidade:123");
        assertThat(output.toString()).contains("Observação de teste");
    }

    @Test
    @DisplayName("Deve registrar login bem-sucedido")
    public void deveRegistrarLoginSucesso(CapturedOutput output) {
        // Act
        activityLogger.logLogin("test@example.com", true, "Login normal");
        
        // Assert
        assertThat(output.toString()).contains("LOGIN");
        assertThat(output.toString()).contains("Login bem-sucedido");
        assertThat(output.toString()).contains("test@example.com");
        assertThat(output.toString()).contains("Login normal");
    }

    @Test
    @DisplayName("Deve registrar tentativa de login falha")
    public void deveRegistrarLoginFalha(CapturedOutput output) {
        // Act
        activityLogger.logLogin("test@example.com", false, "Senha incorreta");
        
        // Assert
        assertThat(output.toString()).contains("FAILED_LOGIN");
        assertThat(output.toString()).contains("Tentativa de login falhou");
        assertThat(output.toString()).contains("test@example.com");
        assertThat(output.toString()).contains("Senha incorreta");
    }

    @Test
    @DisplayName("Deve registrar logout")
    public void deveRegistrarLogout(CapturedOutput output) {
        // Act
        activityLogger.logLogout("Logout pelo botão");
        
        // Assert
        assertThat(output.toString()).contains("LOGOUT");
        assertThat(output.toString()).contains("Logout do sistema");
        assertThat(output.toString()).contains("test@example.com");
        assertThat(output.toString()).contains("Logout pelo botão");
    }

    @Test
    @DisplayName("Deve registrar criação de entidade")
    public void deveRegistrarCriacaoDeEntidade(CapturedOutput output) {
        // Act
        activityLogger.logCreate("Usuario", 42L, "João Silva", "Criado pelo admin");
        
        // Assert
        assertThat(output.toString()).contains("CREATE_USUARIO");
        assertThat(output.toString()).contains("Criou novo registro de usuario");
        assertThat(output.toString()).contains("Usuario ID 42 (João Silva)");
        assertThat(output.toString()).contains("Criado pelo admin");
        assertThat(output.toString()).contains("test@example.com");
    }
    
    @Test
    @DisplayName("Deve registrar modificação de entidade")
    public void deveRegistrarModificacaoDeEntidade(CapturedOutput output) {
        // Act
        activityLogger.logModify("Usuario", 42L, "João Silva", "Atualizado pelo admin");
        
        // Assert
        assertThat(output.toString()).contains("MODIFY_USUARIO");
        assertThat(output.toString()).contains("Modificou registro de usuario");
        assertThat(output.toString()).contains("Usuario ID 42 (João Silva)");
        assertThat(output.toString()).contains("Atualizado pelo admin");
        assertThat(output.toString()).contains("test@example.com");
    }
    
    @Test
    @DisplayName("Deve registrar exclusão de entidade")
    public void deveRegistrarExclusaoDeEntidade(CapturedOutput output) {
        // Act
        activityLogger.logDelete("Usuario", 42L, "João Silva", "Excluído pelo admin");
        
        // Assert
        assertThat(output.toString()).contains("DELETE_USUARIO");
        assertThat(output.toString()).contains("Excluiu registro de usuario");
        assertThat(output.toString()).contains("Usuario ID 42 (João Silva)");
        assertThat(output.toString()).contains("Excluído pelo admin");
        assertThat(output.toString()).contains("test@example.com");
    }
}
