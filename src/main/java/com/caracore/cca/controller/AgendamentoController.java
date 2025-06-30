package com.caracore.cca.controller;

import com.caracore.cca.dto.AgendamentoForm;
import com.caracore.cca.model.Agendamento;
import com.caracore.cca.repository.AgendamentoRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AgendamentoController {

    private final AgendamentoRepository repository;

    public AgendamentoController(AgendamentoRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("agendamentos", repository.findAll());
        return "index";
    }

    @GetMapping("/agendamentos")
    @ResponseBody
    public java.util.List<Agendamento> listarAgendamentos() {
        return repository.findAll();
    }

    @GetMapping("/novo-agendamento")
    public String novoAgendamentoView(Model model) {
        model.addAttribute("agendamentoForm", new AgendamentoForm());
        return "novo-agendamento";
    }

    @PostMapping("/agendar")
    public String agendar(@ModelAttribute("agendamentoForm") @Valid AgendamentoForm form) {
        Agendamento agendamento = new Agendamento();
        agendamento.setPaciente(form.getPaciente());
        agendamento.setDentista(form.getDentista());
        agendamento.setDataHora(form.getDataHora());
        repository.save(agendamento);
        return "redirect:/";
    }
}