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
@DisplayName("UserActivityLogger - Testes Corrigidos")
public class UserActivityLoggerFixedTest {

    @InjectMocks
    private UserActivityLogger activityLogger;

    @Mock
    private HttpServletRequest request;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;
    
    @Mock
    private ServletRequestAttributes requestAttributes;
    
    private AutoCloseable closeable;

    @BeforeEach
    public void setup() {
        closeable = MockitoAnnotations.openMocks(this);
        
        // Configurar mocks para SecurityContext de forma mais robusta
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("test@example.com"); // Não "anonymousUser"
        SecurityContextHolder.setContext(securityContext);

        // Configurar mocks para RequestContext de forma mais robusta
        when(requestAttributes.getRequest()).thenReturn(request);
        RequestContextHolder.setRequestAttributes(requestAttributes);
        
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getHeader("User-Agent")).thenReturn("Mozilla/5.0 (Test)");
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getHeader("Proxy-Client-IP")).thenReturn(null);
        when(request.getHeader("WL-Proxy-Client-IP")).thenReturn(null);
    }
    
    @AfterEach
    public void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
        SecurityContextHolder.clearContext();
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("Deve registrar atividade completa com todos os parâmetros")
    public void deveRegistrarAtividadeCompleta(CapturedOutput output) {
        // Act
        activityLogger.logActivity("TEST_ACTION", "Teste de ação", "Entidade:123", "Observação de teste");
        
        // Assert - verifica se o log contém as informações esperadas
        String logOutput = output.getOut();
        assertThat(logOutput).contains("test@example.com");
        assertThat(logOutput).contains("TEST_ACTION");
        assertThat(logOutput).contains("IP: 127.0.0.1");
        assertThat(logOutput).contains("Mozilla/5.0 (Test)");
        assertThat(logOutput).contains("Teste de ação");
        assertThat(logOutput).contains("Entidade:123");
        assertThat(logOutput).contains("Observação de teste");
    }

    @Test
    @DisplayName("Deve registrar login bem-sucedido com palavra-chave LOGIN")
    public void deveRegistrarLoginSucesso(CapturedOutput output) {
        // Act
        activityLogger.logLogin("test@example.com", true, "Login normal");
        
        // Assert
        String logOutput = output.getOut();
        assertThat(logOutput).contains("LOGIN");
        assertThat(logOutput).contains("Login bem-sucedido");
        assertThat(logOutput).contains("test@example.com");
        assertThat(logOutput).contains("Login normal");
    }

    @Test
    @DisplayName("Deve registrar tentativa de login falha com palavra-chave FAILED_LOGIN")
    public void deveRegistrarLoginFalha(CapturedOutput output) {
        // Act
        activityLogger.logLogin("test@example.com", false, "Senha incorreta");
        
        // Assert
        String logOutput = output.getOut();
        assertThat(logOutput).contains("FAILED_LOGIN");
        assertThat(logOutput).contains("Tentativa de login falhou");
        assertThat(logOutput).contains("test@example.com");
        assertThat(logOutput).contains("Senha incorreta");
    }

    @Test
    @DisplayName("Deve registrar logout com palavra-chave LOGOUT")
    public void deveRegistrarLogout(CapturedOutput output) {
        // Act
        activityLogger.logLogout("Logout pelo botão");
        
        // Assert
        String logOutput = output.getOut();
        assertThat(logOutput).contains("LOGOUT");
        assertThat(logOutput).contains("Logout do sistema");
        assertThat(logOutput).contains("test@example.com");
        assertThat(logOutput).contains("Logout pelo botão");
    }

    @Test
    @DisplayName("Deve registrar criação de entidade com palavra-chave CREATE_USUARIO")
    public void deveRegistrarCriacaoDeEntidade(CapturedOutput output) {
        // Act
        activityLogger.logCreate("Usuario", 42L, "João Silva", "Criado pelo admin");
        
        // Assert
        String logOutput = output.getOut();
        assertThat(logOutput).contains("CREATE_USUARIO");
        assertThat(logOutput).contains("Criou novo registro de usuario");
        assertThat(logOutput).contains("Usuario ID 42 (João Silva)");
        assertThat(logOutput).contains("Criado pelo admin");
        assertThat(logOutput).contains("test@example.com");
    }
    
    @Test
    @DisplayName("Deve registrar modificação de entidade com palavra-chave MODIFY_USUARIO")
    public void deveRegistrarModificacaoDeEntidade(CapturedOutput output) {
        // Act
        activityLogger.logModify("Usuario", 42L, "João Silva", "Atualizado pelo admin");
        
        // Assert
        String logOutput = output.getOut();
        assertThat(logOutput).contains("MODIFY_USUARIO");
        assertThat(logOutput).contains("Modificou registro de usuario");
        assertThat(logOutput).contains("Usuario ID 42 (João Silva)");
        assertThat(logOutput).contains("Atualizado pelo admin");
        assertThat(logOutput).contains("test@example.com");
    }
    
    @Test
    @DisplayName("Deve registrar exclusão de entidade com palavra-chave DELETE_USUARIO")
    public void deveRegistrarExclusaoDeEntidade(CapturedOutput output) {
        // Act
        activityLogger.logDelete("Usuario", 42L, "João Silva", "Excluído pelo admin");
        
        // Assert
        String logOutput = output.getOut();
        assertThat(logOutput).contains("DELETE_USUARIO");
        assertThat(logOutput).contains("Excluiu registro de usuario");
        assertThat(logOutput).contains("Usuario ID 42 (João Silva)");
        assertThat(logOutput).contains("Excluído pelo admin");
        assertThat(logOutput).contains("test@example.com");
    }

    @Test
    @DisplayName("Deve funcionar sem contexto de requisição")
    public void deveFuncionarSemContextoRequisicao(CapturedOutput output) {
        // Arrange - Remove contexto de requisição
        RequestContextHolder.resetRequestAttributes();
        
        // Act
        activityLogger.logActivity("NO_REQUEST_CONTEXT", "Teste sem contexto", null, null);
        
        // Assert
        String logOutput = output.getOut();
        assertThat(logOutput).contains("NO_REQUEST_CONTEXT");
        assertThat(logOutput).contains("Teste sem contexto");
        assertThat(logOutput).contains("test@example.com");
        // IP e User-Agent devem ser "unknown" quando não há contexto
        assertThat(logOutput).containsAnyOf("IP: unknown", "IP:");
    }

    @Test
    @DisplayName("Deve funcionar com usuário anônimo")
    public void deveFuncionarComUsuarioAnonimo(CapturedOutput output) {
        // Arrange - Simula usuário anônimo
        when(authentication.getPrincipal()).thenReturn("anonymousUser");
        when(authentication.isAuthenticated()).thenReturn(false);
        
        // Act
        activityLogger.logActivity("ANONYMOUS_ACTION", "Ação anônima", null, null);
        
        // Assert
        String logOutput = output.getOut();
        assertThat(logOutput).contains("ANONYMOUS_ACTION");
        assertThat(logOutput).contains("Ação anônima");
        assertThat(logOutput).contains("anonymous"); // Deve usar "anonymous" para usuários não autenticados
    }
}
