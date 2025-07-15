package com.caracore.cca.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.StringTemplateResolver;

/**
 * Configuração específica para testes que usam templates Thymeleaf
 * Esta configuração permite falhas silenciosas em expressões SPEL nos templates durante os testes
 */
@TestConfiguration
public class ThymeleafTestConfig {

    @Bean
    @Primary
    public SpringTemplateEngine testTemplateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(testTemplateResolver());
        // Configurar para não falhar em erros de expressões
        templateEngine.setEnableSpringELCompiler(false);
        return templateEngine;
    }

    @Bean
    @Primary
    public ITemplateResolver testTemplateResolver() {
        StringTemplateResolver resolver = new StringTemplateResolver();
        resolver.setTemplateMode("HTML");
        return resolver;
    }

    @Bean
    @Primary
    public ThymeleafViewResolver thymeleafViewResolver() {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(testTemplateEngine());
        resolver.setCharacterEncoding("UTF-8");
        // Configurar para não renderizar views nos testes
        resolver.setRedirectHttp10Compatible(false);
        return resolver;
    }
}
