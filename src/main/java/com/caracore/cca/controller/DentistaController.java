package com.caracore.cca.controller;

import com.caracore.cca.model.Dentista;
import com.caracore.cca.service.DentistaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/dentistas")
@PreAuthorize("hasRole('ADMIN') or hasRole('RECEPTIONIST') or hasRole('DENTIST')")
public class DentistaController {

    @Autowired
    private DentistaService dentistaService;

    @GetMapping
    public String listarDentistas(Model model) {
        List<Dentista> dentistas = dentistaService.listarTodos();
        model.addAttribute("dentistas", dentistas);
        return "dentistas/lista";
    }

    @GetMapping("/buscar")
    public String buscarDentistas(@RequestParam(required = false) String termo, Model model) {
        List<Dentista> dentistas = dentistaService.buscarPorTermo(termo);
        model.addAttribute("dentistas", dentistas);
        model.addAttribute("termoBusca", termo);
        return "dentistas/lista";
    }

    @GetMapping("/{id}")
    public String detalhesDentista(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Dentista> dentistaOpt = dentistaService.buscarPorId(id);
        
        if (dentistaOpt.isPresent()) {
            model.addAttribute("dentista", dentistaOpt.get());
            return "dentistas/detalhes";
        } else {
            redirectAttributes.addFlashAttribute("erro", "Dentista não encontrado");
            return "redirect:/dentistas";
        }
    }

    @GetMapping("/novo")
    @PreAuthorize("hasRole('ADMIN')")
    public String novoDentistaForm(Model model) {
        model.addAttribute("dentista", new Dentista());
        return "dentistas/form";
    }

    @GetMapping("/{id}/editar")
    @PreAuthorize("hasRole('ADMIN')")
    public String editarDentistaForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Dentista> dentistaOpt = dentistaService.buscarPorId(id);
        
        if (dentistaOpt.isPresent()) {
            model.addAttribute("dentista", dentistaOpt.get());
            return "dentistas/form";
        } else {
            redirectAttributes.addFlashAttribute("erro", "Dentista não encontrado");
            return "redirect:/dentistas";
        }
    }

    @PostMapping("/salvar")
    @PreAuthorize("hasRole('ADMIN')")
    public String salvarDentista(@Valid @ModelAttribute("dentista") Dentista dentista,
                              BindingResult result,
                              RedirectAttributes redirectAttributes,
                              Principal principal) {
        if (result.hasErrors()) {
            return "dentistas/form";
        }
        
        dentista = dentistaService.salvar(dentista, principal);
        redirectAttributes.addFlashAttribute("sucesso", "Dentista salvo com sucesso");
        return "redirect:/dentistas";
    }

    @PostMapping("/{id}/atualizar")
    @PreAuthorize("hasRole('ADMIN')")
    public String atualizarDentista(@PathVariable Long id,
                                 @Valid @ModelAttribute("dentista") Dentista dentista,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes,
                                 Principal principal) {
        if (result.hasErrors()) {
            return "dentistas/form";
        }
        
        if (!id.equals(dentista.getId())) {
            redirectAttributes.addFlashAttribute("erro", "ID inválido");
            return "redirect:/dentistas";
        }
        
        dentista = dentistaService.salvar(dentista, principal);
        redirectAttributes.addFlashAttribute("sucesso", "Dentista atualizado com sucesso");
        return "redirect:/dentistas";
    }

    @PostMapping("/{id}/excluir")
    @PreAuthorize("hasRole('ADMIN')")
    public String excluirDentista(@PathVariable Long id,
                               RedirectAttributes redirectAttributes,
                               Principal principal) {
        if (dentistaService.excluir(id, principal)) {
            redirectAttributes.addFlashAttribute("sucesso", "Dentista desativado com sucesso");
        } else {
            redirectAttributes.addFlashAttribute("erro", "Dentista não encontrado");
        }
        return "redirect:/dentistas";
    }
}
