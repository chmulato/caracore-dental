package com.caracore.cca.config;

import com.caracore.cca.util.UserActivityLogger;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuração de teste para simular os componentes de MVC
 */
@TestConfiguration
public class TestWebMvcConfig {

    @Bean
    @Primary
    public UserActivityLogger mockUserActivityLogger() {
        return Mockito.mock(UserActivityLogger.class);
    }

    @Bean
    @Primary
    public UserActivityInterceptor mockUserActivityInterceptor() {
        return Mockito.mock(UserActivityInterceptor.class);
    }

    @Bean
    @Primary
    public WebMvcConfig mockWebMvcConfig() {
        return Mockito.mock(WebMvcConfig.class);
    }
}
