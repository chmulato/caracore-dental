package com.caracore.cca.service;

import com.caracore.cca.model.Agendamento;
import com.caracore.cca.repository.AgendamentoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Serviço para inicializar dados de teste em ambientes de desenvolvimento
 */
@Service
@Profile("local")
public class DataInitializerService implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializerService.class);
    
    @Autowired
    private AgendamentoRepository agendamentoRepository;
    
    @Override
    public void run(String... args) throws Exception {
        logger.info("Inicializando dados de teste para ambiente local...");
        
        // Criar alguns agendamentos de teste
        criarAgendamentosDemo();
        
        logger.info("Dados de teste inicializados com sucesso!");
        logger.info("------------------------------------------------------------------------------------");
        logger.info("ENDPOINTS DISPONÍVEIS:");
        logger.info("* http://localhost:8080/ - Página HTML com tabela de agendamentos");
        logger.info("* http://localhost:8080/agendamentos - API REST que retorna agendamentos em formato JSON");
        logger.info("------------------------------------------------------------------------------------");
    }
    
    private void criarAgendamentosDemo() {
        // Agendamento 1
        Agendamento agendamento1 = new Agendamento();
        agendamento1.setPaciente("Maria Silva");
        agendamento1.setDentista("Dr. Ana Silva - Clínico Geral");
        agendamento1.setDataHora(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0));
        agendamento1.setStatus("AGENDADO");
        agendamento1.setTelefoneWhatsapp("11987654321");
        agendamento1.setObservacao("Primeira consulta - avaliação inicial");
        agendamento1.setDuracaoMinutos(30);
        agendamentoRepository.save(agendamento1);
        logger.info("Agendamento criado: {}", agendamento1);
        
        // Agendamento 2
        Agendamento agendamento2 = new Agendamento();
        agendamento2.setPaciente("João Pereira");
        agendamento2.setDentista("Dr. Carlos Oliveira - Ortodontista");
        agendamento2.setDataHora(LocalDateTime.now().plusDays(1).withHour(14).withMinute(30));
        agendamento2.setStatus("CONFIRMADO");
        agendamento2.setTelefoneWhatsapp("11912345678");
        agendamento2.setObservacao("Manutenção do aparelho");
        agendamento2.setDuracaoMinutos(45);
        agendamentoRepository.save(agendamento2);
        logger.info("Agendamento criado: {}", agendamento2);
        
        // Agendamento 3
        Agendamento agendamento3 = new Agendamento();
        agendamento3.setPaciente("Ana Luiza Santos");
        agendamento3.setDentista("Dra. Mariana Santos - Endodontista");
        agendamento3.setDataHora(LocalDateTime.now().plusDays(2).withHour(9).withMinute(0));
        agendamento3.setStatus("AGENDADO");
        agendamento3.setTelefoneWhatsapp("11998765432");
        agendamento3.setObservacao("Tratamento de canal");
        agendamento3.setDuracaoMinutos(60);
        agendamentoRepository.save(agendamento3);
        logger.info("Agendamento criado: {}", agendamento3);
    }
}
