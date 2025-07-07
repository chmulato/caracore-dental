package com.caracore.cca.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.VersionResourceResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(@org.springframework.lang.NonNull ResourceHandlerRegistry registry) {
        // Configuração explícita para WebJars com versão e cache
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/")
                .setCachePeriod(31556926) // 1 ano em segundos
                .resourceChain(false)
                .addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"));
        
        // Configuração específica para Chart.js
        registry.addResourceHandler("/webjars/chart.js/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/chart.js/")
                .setCachePeriod(31556926)
                .resourceChain(false);
        
        // Outros handlers de recursos
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
    
    @Override
    public void configureContentNegotiation(@org.springframework.lang.NonNull ContentNegotiationConfigurer configurer) {
        configurer
            .defaultContentType(MediaType.TEXT_HTML)
            .mediaType("js", new MediaType("application", "javascript"))
            .mediaType("css", new MediaType("text", "css"))
            .mediaType("html", MediaType.TEXT_HTML)
            .mediaType("json", MediaType.APPLICATION_JSON)
            .mediaType("png", MediaType.IMAGE_PNG)
            .mediaType("jpg", MediaType.IMAGE_JPEG)
            .mediaType("jpeg", MediaType.IMAGE_JPEG);
    }
}
