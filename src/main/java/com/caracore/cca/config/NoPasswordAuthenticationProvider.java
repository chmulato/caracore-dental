package com.caracore.cca.config;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * Provedor de autenticação personalizado que aceita qualquer usuário sem validar senha.
 * ATENÇÃO: Use apenas para fins de depuração e desenvolvimento.
 */
@Component
public class NoPasswordAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        
        // Aceita qualquer usuário, não valida senha
        System.out.println("Login sem senha para usuário: " + username);
        
        // Cria uma autenticação com permissões padrão
        return new UsernamePasswordAuthenticationToken(
                username,
                "",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
