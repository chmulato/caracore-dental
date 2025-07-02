package com.caracore.cca.service;

import com.caracore.cca.model.Dentista;
import com.caracore.cca.repository.DentistaRepository;
import com.caracore.cca.util.UserActivityLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
public class DentistaService {

    @Autowired
    private DentistaRepository dentistaRepository;
    
    @Autowired
    private UserActivityLogger activityLogger;

    public List<Dentista> listarTodos() {
        return dentistaRepository.findAll();
    }

    public List<Dentista> listarAtivos() {
        return dentistaRepository.findByAtivoTrue();
    }

    public Optional<Dentista> buscarPorId(Long id) {
        return dentistaRepository.findById(id);
    }

    public List<Dentista> buscarPorTermo(String termo) {
        if (termo == null || termo.trim().isEmpty()) {
            return listarTodos();
        }
        return dentistaRepository.buscarPorTermo(termo);
    }

    @Transactional
    public Dentista salvar(Dentista dentista, Principal principal) {
        boolean isNovo = (dentista.getId() == null);
        
        Dentista dentistaSalvo = dentistaRepository.save(dentista);
        
        if (isNovo) {
            activityLogger.logActivity(
                "CADASTRAR_DENTISTA", 
                principal != null ? principal.getName() : "sistema", 
                dentistaSalvo.getId().toString(),
                "Dentista cadastrado: " + dentistaSalvo.getNome()
            );
        } else {
            activityLogger.logActivity(
                "ATUALIZAR_DENTISTA", 
                principal != null ? principal.getName() : "sistema", 
                dentistaSalvo.getId().toString(),
                "Dentista atualizado: " + dentistaSalvo.getNome()
            );
        }
        
        return dentistaSalvo;
    }

    @Transactional
    public boolean excluir(Long id, Principal principal) {
        Optional<Dentista> dentistaOpt = dentistaRepository.findById(id);
        
        if (dentistaOpt.isPresent()) {
            Dentista dentista = dentistaOpt.get();
            dentista.setAtivo(false);
            dentistaRepository.save(dentista);
            
            activityLogger.logActivity(
                "EXCLUIR_DENTISTA", 
                principal != null ? principal.getName() : "sistema", 
                id.toString(),
                "Dentista desativado: " + dentista.getNome()
            );
            return true;
        }
        return false;
    }
}
