package com.caracore.cca.controller;

import com.caracore.cca.model.Dentista;
import com.caracore.cca.model.ImagemRadiologica;
import com.caracore.cca.model.Paciente;
import com.caracore.cca.model.Prontuario;
import com.caracore.cca.model.RegistroTratamento;
import com.caracore.cca.service.DentistaService;
import com.caracore.cca.service.PacienteService;
import com.caracore.cca.service.ProntuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller responsável pelo gerenciamento de prontuários médicos.
 * 
 * Funcionalidades:
 * - Listagem de prontuários por dentista
 * - Visualização de prontuário de paciente
 * - Upload de imagens radiológicas
 * - Adição de registros de tratamento
 * - Controle de acesso baseado em dentista
 * 
 * @author Sistema CCA
 * @version 1.0
 */
@Controller
@RequestMapping("/prontuarios")
@PreAuthorize("hasAnyRole('ADMIN', 'DENTIST')")
public class ProntuarioController {

    private static final Logger logger = LoggerFactory.getLogger(ProntuarioController.class);
    
    // Constantes para mensagens
    private static final String ERRO_DENTISTA_NAO_ENCONTRADO = "Dentista não encontrado";
    private static final String ERRO_PACIENTE_NAO_ENCONTRADO = "Paciente não encontrado";
    private static final String ERRO_ACESSO_NEGADO = "Acesso negado à imagem";
    private static final String SUCESSO_IMAGEM_ADICIONADA = "Imagem adicionada com sucesso!";
    private static final String SUCESSO_TRATAMENTO_ADICIONADO = "Tratamento adicionado com sucesso!";
    
    // Views
    private static final String VIEW_ERROR = "error";
    private static final String VIEW_MEUS_PRONTUARIOS = "prontuarios/meus-prontuarios";
    private static final String VIEW_VISUALIZAR = "prontuarios/visualizar";
    private static final String VIEW_VISUALIZAR_IMAGEM = "prontuarios/visualizar-imagem";

    @Autowired
    private ProntuarioService prontuarioService;

    @Autowired
    private DentistaService dentistaService;
    
    @Autowired
    private PacienteService pacienteService;

    /**
     * Lista todos os prontuários do dentista autenticado.
     * 
     * @param model Model para dados da view
     * @param principal Usuario autenticado
     * @return Nome da view ou página de erro
     */
    @GetMapping
    public String listarProntuarios(Model model, Principal principal) {
        logger.info("Listando prontuários para dentista: {}", principal.getName());
        
        Optional<Dentista> dentistaOpt = dentistaService.buscarPorEmail(principal.getName());
        if (dentistaOpt.isEmpty()) {
            logger.warn("Dentista não encontrado para email: {}", principal.getName());
            return "redirect:/acesso-negado";
        }
        
        Dentista dentista = dentistaOpt.get();
        List<Prontuario> prontuarios = prontuarioService.buscarProntuariosPorDentista(dentista.getId());
        
        logger.info("Encontrados {} prontuários para dentista ID: {}", prontuarios.size(), dentista.getId());
        
        model.addAttribute("prontuarios", prontuarios);
        return VIEW_MEUS_PRONTUARIOS;
    }

    /**
     * Visualiza o prontuário de um paciente específico.
     * 
     * @param id ID do paciente
     * @param model Model para dados da view
     * @param principal Usuario autenticado
     * @return Nome da view ou página de erro
     */
    @GetMapping("/paciente/{id}")
    public String visualizarProntuario(@PathVariable Long id, Model model, Principal principal) {
        logger.info("Visualizando prontuário do paciente ID: {} para dentista: {}", id, principal.getName());
        
        Optional<Dentista> dentistaOpt = dentistaService.buscarPorEmail(principal.getName());
        if (dentistaOpt.isEmpty()) {
            logger.warn("Dentista não encontrado para email: {}", principal.getName());
            model.addAttribute("erro", ERRO_DENTISTA_NAO_ENCONTRADO);
            return VIEW_ERROR;
        }
        
        Optional<Paciente> pacienteOpt = pacienteService.buscarPorId(id);
        if (pacienteOpt.isEmpty()) {
            logger.warn("Paciente não encontrado para ID: {}", id);
            model.addAttribute("erro", ERRO_PACIENTE_NAO_ENCONTRADO);
            return VIEW_ERROR;
        }
        
        Dentista dentista = dentistaOpt.get();
        Paciente paciente = pacienteOpt.get();
        
        try {
            Prontuario prontuario = prontuarioService.buscarOuCriarProntuario(id, dentista.getId());
            List<ImagemRadiologica> imagens = prontuarioService.buscarImagensProntuario(prontuario.getId());
            List<RegistroTratamento> tratamentos = prontuarioService.buscarRegistrosTratamento(prontuario.getId());
            
            // Calcular estatísticas do prontuário
            Map<String, Object> estatisticas = new HashMap<>();
            estatisticas.put("totalImagens", imagens.size());
            estatisticas.put("totalTratamentos", tratamentos.size());
            
            logger.info("Prontuário carregado - Imagens: {}, Tratamentos: {}", imagens.size(), tratamentos.size());
            
            model.addAttribute("paciente", paciente);
            model.addAttribute("prontuario", prontuario);
            model.addAttribute("imagens", imagens);
            model.addAttribute("tratamentos", tratamentos);
            model.addAttribute("estatisticas", estatisticas);
            
            return VIEW_VISUALIZAR;
        } catch (Exception e) {
            logger.error("Erro ao carregar prontuário para paciente ID: {}", id, e);
            model.addAttribute("erro", "Erro ao carregar prontuário: " + e.getMessage());
            return VIEW_ERROR;
        }
    }

    /**
     * Faz upload de uma imagem radiológica para o prontuário.
     * 
     * @param id ID do prontuário
     * @param arquivo Arquivo da imagem
     * @param descricao Descrição da imagem
     * @param tipoImagem Tipo da imagem radiológica
     * @param principal Usuario autenticado
     * @param redirectAttributes Atributos para redirect
     * @return Redirect para página do paciente
     */
    @PostMapping("/{id}/imagem/upload")
    public String uploadImagem(@PathVariable Long id, 
                              @RequestParam("arquivo") MultipartFile arquivo,
                              @RequestParam(value = "descricao", defaultValue = "") String descricao,
                              @RequestParam(value = "tipoImagem", defaultValue = "Radiografia") String tipoImagem,
                              Principal principal, 
                              RedirectAttributes redirectAttributes) {
        
        logger.info("Upload de imagem para prontuário ID: {} por dentista: {}", id, principal.getName());
        
        // Validação básica do arquivo
        if (arquivo.isEmpty()) {
            logger.warn("Tentativa de upload de arquivo vazio para prontuário ID: {}", id);
            redirectAttributes.addFlashAttribute("erro", "Arquivo não pode estar vazio");
            return "redirect:/prontuarios/paciente/" + id;
        }
        
        Optional<Dentista> dentistaOpt = dentistaService.buscarPorEmail(principal.getName());
        if (dentistaOpt.isEmpty()) {
            logger.warn("Dentista não encontrado para email: {}", principal.getName());
            redirectAttributes.addFlashAttribute("erro", ERRO_DENTISTA_NAO_ENCONTRADO);
            return "redirect:/prontuarios/paciente/" + id + "?erro=" + ERRO_DENTISTA_NAO_ENCONTRADO;
        }
        
        try {
            Dentista dentista = dentistaOpt.get();
            ImagemRadiologica imagem = prontuarioService.adicionarImagemRadiologica(
                id, arquivo, tipoImagem, descricao, dentista.getId());
                
            logger.info("Imagem adicionada com sucesso - ID: {} para prontuário: {}", 
                       imagem.getId(), id);
                       
            redirectAttributes.addFlashAttribute("sucesso", SUCESSO_IMAGEM_ADICIONADA);
            return "redirect:/prontuarios/paciente/" + imagem.getProntuario().getPaciente().getId();
        } catch (Exception e) {
            logger.error("Erro ao fazer upload de imagem para prontuário ID: {}", id, e);
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/prontuarios/paciente/" + id + "?erro=" + e.getMessage();
        }
    }

    /**
     * Faz upload AJAX de imagem em formato base64.
     * 
     * @param id ID do prontuário
     * @param requestData Dados da requisição contendo imagem em base64
     * @param principal Usuario autenticado
     * @return ResponseEntity com resultado do upload
     */
    @PostMapping("/{id}/imagem/upload-ajax")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> uploadImagemAjax(@PathVariable Long id, 
                                             @RequestBody Map<String, String> requestData,
                                             Principal principal) {
        
        logger.info("Upload AJAX de imagem para prontuário ID: {} por dentista: {}", id, principal.getName());
        
        Map<String, Object> response = new HashMap<>();
        
        Optional<Dentista> dentistaOpt = dentistaService.buscarPorEmail(principal.getName());
        if (dentistaOpt.isEmpty()) {
            logger.warn("Dentista não encontrado para email: {}", principal.getName());
            response.put("success", false);
            response.put("message", ERRO_DENTISTA_NAO_ENCONTRADO);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        
        try {
            Dentista dentista = dentistaOpt.get();
            String imagemBase64 = requestData.get("imagemBase64");
            String nomeArquivo = requestData.get("nomeArquivo");
            String tipoImagem = requestData.get("tipoImagem");
            String descricao = requestData.get("descricao");
            
            // Validação dos dados recebidos
            if (imagemBase64 == null || imagemBase64.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Imagem não pode estar vazia");
                return ResponseEntity.badRequest().body(response);
            }
            
            prontuarioService.adicionarImagemBase64(
                id, imagemBase64, nomeArquivo, tipoImagem, descricao, dentista.getId());
                
            logger.info("Upload AJAX de imagem realizado com sucesso para prontuário ID: {}", id);
            
            response.put("success", true);
            response.put("message", SUCESSO_IMAGEM_ADICIONADA);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Erro no upload AJAX de imagem para prontuário ID: {}", id, e);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Adiciona um registro de tratamento ao prontuário.
     * 
     * @param id ID do prontuário
     * @param procedimento Tipo do procedimento
     * @param descricao Descrição do tratamento
     * @param dente Dente tratado
     * @param materialUtilizado Material utilizado no tratamento
     * @param observacoes Observações adicionais
     * @param valorProcedimento Valor do procedimento
     * @param principal Usuario autenticado
     * @param redirectAttributes Atributos para redirect
     * @return Redirect para página do paciente
     */
    @PostMapping("/{id}/tratamento")
    public String adicionarTratamento(@PathVariable Long id,
                                     @RequestParam("procedimento") String procedimento,
                                     @RequestParam("descricao") String descricao,
                                     @RequestParam(value = "dente", defaultValue = "") String dente,
                                     @RequestParam(value = "materialUtilizado", defaultValue = "") String materialUtilizado,
                                     @RequestParam(value = "observacoes", defaultValue = "") String observacoes,
                                     @RequestParam("valorProcedimento") Double valorProcedimento,
                                     Principal principal,
                                     RedirectAttributes redirectAttributes) {
        
        logger.info("Adicionando tratamento ao prontuário ID: {} por dentista: {}", id, principal.getName());
        
        // Validações básicas
        if (procedimento == null || procedimento.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("erro", "Procedimento é obrigatório");
            return "redirect:/prontuarios/paciente/" + id;
        }
        
        if (valorProcedimento == null || valorProcedimento < 0) {
            redirectAttributes.addFlashAttribute("erro", "Valor do procedimento deve ser informado e positivo");
            return "redirect:/prontuarios/paciente/" + id;
        }
        
        Optional<Dentista> dentistaOpt = dentistaService.buscarPorEmail(principal.getName());
        if (dentistaOpt.isEmpty()) {
            logger.warn("Dentista não encontrado para email: {}", principal.getName());
            redirectAttributes.addFlashAttribute("erro", ERRO_DENTISTA_NAO_ENCONTRADO);
            return "redirect:/prontuarios/paciente/" + id + "?erro=" + ERRO_DENTISTA_NAO_ENCONTRADO;
        }
        
        try {
            Dentista dentista = dentistaOpt.get();
            prontuarioService.adicionarRegistroTratamento(id, dentista.getId(), procedimento, 
                descricao, dente, materialUtilizado, observacoes, valorProcedimento);
                
            logger.info("Tratamento '{}' adicionado com sucesso ao prontuário ID: {}", 
                       procedimento, id);
                       
            redirectAttributes.addFlashAttribute("sucesso", SUCESSO_TRATAMENTO_ADICIONADO);
        } catch (Exception e) {
            logger.error("Erro ao adicionar tratamento ao prontuário ID: {}", id, e);
            redirectAttributes.addFlashAttribute("erro", "Erro ao adicionar tratamento: " + e.getMessage());
        }
        
        return "redirect:/prontuarios/paciente/" + id;
    }

    /**
     * Visualiza uma imagem radiológica em tela cheia.
     * 
     * @param id ID da imagem
     * @param model Model para dados da view
     * @param principal Usuario autenticado
     * @return Nome da view ou página de erro
     */
    @GetMapping("/imagem/{id}")
    public String visualizarImagem(@PathVariable Long id, Model model, Principal principal) {
        logger.info("Visualizando imagem ID: {} por dentista: {}", id, principal.getName());
        
        Optional<Dentista> dentistaOpt = dentistaService.buscarPorEmail(principal.getName());
        if (dentistaOpt.isEmpty()) {
            logger.warn("Dentista não encontrado para email: {}", principal.getName());
            model.addAttribute("erro", ERRO_DENTISTA_NAO_ENCONTRADO);
            return VIEW_ERROR;
        }
        
        try {
            Dentista dentista = dentistaOpt.get();
            ImagemRadiologica imagem = prontuarioService.buscarImagemPorId(id);
            
            if (imagem == null) {
                logger.warn("Imagem não encontrada para ID: {}", id);
                model.addAttribute("erro", "Imagem não encontrada");
                return VIEW_ERROR;
            }
            
            // Verificar se o dentista tem acesso à imagem
            if (imagem.getDentista() == null || !imagem.getDentista().getId().equals(dentista.getId())) {
                logger.warn("Acesso negado à imagem ID: {} para dentista: {}", id, principal.getName());
                model.addAttribute("erro", ERRO_ACESSO_NEGADO);
                return VIEW_ERROR;
            }
            
            // Carregar paciente para a visualização
            Prontuario prontuario = prontuarioService.buscarOuCriarProntuario(id, dentista.getId());
            if (prontuario != null && prontuario.getPaciente() != null) {
                model.addAttribute("paciente", prontuario.getPaciente());
            }
            
            logger.info("Acesso autorizado à imagem ID: {} para dentista: {}", id, principal.getName());
            model.addAttribute("imagem", imagem);
            return VIEW_VISUALIZAR_IMAGEM;
        } catch (Exception e) {
            logger.error("Erro ao visualizar imagem ID: {}", id, e);
            model.addAttribute("erro", "Erro ao carregar imagem: " + e.getMessage());
            return VIEW_ERROR;
        }
    }

    /**
     * Remove uma imagem radiológica do prontuário.
     * 
     * @param id ID da imagem a ser removida
     * @param principal Usuario autenticado
     * @return ResponseEntity com resultado da operação
     */
    @DeleteMapping("/imagem/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> removerImagem(@PathVariable Long id, Principal principal) {
        logger.info("Removendo imagem ID: {} por dentista: {}", id, principal.getName());
        
        Map<String, Object> response = new HashMap<>();
        
        Optional<Dentista> dentistaOpt = dentistaService.buscarPorEmail(principal.getName());
        if (dentistaOpt.isEmpty()) {
            logger.warn("Dentista não encontrado para email: {}", principal.getName());
            response.put("success", false);
            response.put("message", ERRO_DENTISTA_NAO_ENCONTRADO);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        
        try {
            Dentista dentista = dentistaOpt.get();
            ImagemRadiologica imagem = prontuarioService.buscarImagemPorId(id);
            
            if (imagem == null) {
                logger.warn("Imagem não encontrada para ID: {}", id);
                response.put("success", false);
                response.put("message", "Imagem não encontrada");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            // Verificar se o dentista tem acesso à imagem
            if (imagem.getDentista() == null || !imagem.getDentista().getId().equals(dentista.getId())) {
                logger.warn("Acesso negado para remoção da imagem ID: {} por dentista: {}", id, principal.getName());
                response.put("success", false);
                response.put("message", ERRO_ACESSO_NEGADO);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            prontuarioService.removerImagemRadiologica(id);
            
            logger.info("Imagem ID: {} removida com sucesso por dentista: {}", id, principal.getName());
            
            response.put("success", true);
            response.put("message", "Imagem removida com sucesso");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Erro ao remover imagem ID: {}", id, e);
            response.put("success", false);
            response.put("message", "Erro ao remover imagem: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
