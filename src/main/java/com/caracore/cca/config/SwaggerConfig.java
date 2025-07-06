package com.caracore.cca.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuração do Swagger/OpenAPI 3 para documentação da API
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sistema de Agendamento - Cara Core API")
                        .version("1.0.0")
                        .description("API para sistema de agendamento odontológico com funcionalidades públicas e administrativas")
                        .contact(new Contact()
                                .name("Cara Core")
                                .email("contato@caracore.com.br")
                                .url("https://www.caracore.com.br"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de Desenvolvimento"),
                        new Server()
                                .url("https://api.caracore.com.br")
                                .description("Servidor de Produção")))
                .tags(List.of(
                        new Tag()
                                .name("Agendamento Público")
                                .description("Endpoints públicos para agendamento de consultas"),
                        new Tag()
                                .name("Agendamentos")
                                .description("Gerenciamento de agendamentos (requer autenticação)"),
                        new Tag()
                                .name("Dentistas")
                                .description("Gerenciamento de dentistas (requer autenticação)"),
                        new Tag()
                                .name("Pacientes")
                                .description("Gerenciamento de pacientes (requer autenticação)"),
                        new Tag()
                                .name("Usuários")
                                .description("Gerenciamento de usuários (requer autenticação admin)"),
                        new Tag()
                                .name("Configurações")
                                .description("Configurações do sistema (requer autenticação admin)")
                ));
    }
}
