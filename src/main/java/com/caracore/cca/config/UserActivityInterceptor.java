package com.caracore.cca.config;

import com.caracore.cca.util.UserActivityLogger;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * Interceptor que registra automaticamente atividades do usuário
 * quando acessam determinados endpoints.
 */
@Component
public class UserActivityInterceptor implements HandlerInterceptor {

    @Autowired
    private UserActivityLogger activityLogger;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Permite que a solicitação continue normalmente
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        // Não registra solicitações para recursos estáticos
        String path = request.getRequestURI();
        if (isStaticResource(path) || isLoginPage(path)) {
            return;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            String method = request.getMethod();
            String actionType = determineActionType(method, path);
            
            if (actionType != null) {
                String details = String.format("Acessou %s via método %s", path, method);
                activityLogger.logActivity(actionType, details, null, null);
            }
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // Nada a fazer após a conclusão
    }

    /**
     * Verifica se o recurso é estático (CSS, JS, imagens)
     */
    private boolean isStaticResource(String path) {
        return path.contains("/css/") || 
               path.contains("/js/") || 
               path.contains("/img/") || 
               path.contains("/webjars/") ||
               path.contains("/favicon.ico");
    }
    
    /**
     * Verifica se é a página de login para evitar logging redundante
     */
    private boolean isLoginPage(String path) {
        return path.equals("/login");
    }
    
    /**
     * Determina o tipo de ação com base no método HTTP e no caminho
     */
    private String determineActionType(String method, String path) {
        if (method.equals("GET")) {
            if (path.contains("/api/")) {
                return "API_VIEW";
            } else {
                return "PAGE_VIEW";
            }
        } else if (method.equals("POST")) {
            if (path.endsWith("/create") || path.contains("/api/") && !path.contains("/update")) {
                return "CREATE";
            } else if (path.endsWith("/update") || path.contains("/update/")) {
                return "UPDATE";
            } else {
                return "ACTION";
            }
        } else if (method.equals("PUT")) {
            return "UPDATE";
        } else if (method.equals("DELETE")) {
            return "DELETE";
        }
        
        return null;
    }
}
