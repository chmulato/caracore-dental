package com.caracore.cca.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
public class TestViewConfiguration {
    
    @Bean
    @Primary
    public ViewResolver mockViewResolver() {
        // Create a mock view resolver that doesn't try to parse Thymeleaf templates
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("");
        viewResolver.setSuffix("");
        return viewResolver;
    }
}
