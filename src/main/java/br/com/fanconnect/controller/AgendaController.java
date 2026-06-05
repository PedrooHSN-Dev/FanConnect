package br.com.fanconnect.controller;

import br.com.fanconnect.entity.ItemAgenda;
import br.com.fanconnect.entity.VisibilidadeEvento;
import br.com.fanconnect.repository.ItemAgendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import br.com.fanconnect.entity.Usuario;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@RestController
@RequestMapping("/api/agenda")
@CrossOrigin(origins = "*")
public class AgendaController {

    private final ItemAgendaRepository agendaRepository;

    @Autowired
    private ItemAgendaRepository repository;

    public AgendaController(ItemAgendaRepository agendaRepository) {
        this.agendaRepository = agendaRepository;
    }

    // Listar eventos GLOBAIS (Calendário Acadêmico Oficial)
    @GetMapping("/global")
    public ResponseEntity<List<ItemAgenda>> listarEventosGlobais() {
        List<ItemAgenda> eventos = agendaRepository.findByVisibilidade(VisibilidadeEvento.GLOBAL);
        return ResponseEntity.ok(eventos);
    }

    // Listar eventos PRIVADOS de um aluno específico
    @GetMapping("/privado/{usuarioId}")
    public ResponseEntity<List<ItemAgenda>> listarEventosPrivados(@PathVariable Long usuarioId) {
        List<ItemAgenda> eventos = agendaRepository.findByVisibilidadeAndDonoId(VisibilidadeEvento.PRIVADO, usuarioId);
        return ResponseEntity.ok(eventos);
    }

    // Criar um novo evento (Global, Turma ou Privado)
    @PostMapping
    public ResponseEntity<ItemAgenda> criarEvento(@RequestBody ItemAgenda novoEvento) {
        // O Spring vai mapear automaticamente o JSON com as novas colunas
        ItemAgenda eventoSalvo = agendaRepository.save(novoEvento);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventoSalvo);
    }

    @GetMapping("/meus-eventos")
    public ResponseEntity<List<ItemAgenda>> listarMeusEventos(@AuthenticationPrincipal Usuario usuarioLogado) {

        // Verifica se o usuário tem uma turma vinculada
        if (usuarioLogado.getTurma() != null) {
            Long turmaId = usuarioLogado.getTurma().getId();

            // Busca eventos Globais OU eventos exclusivos da turma do aluno
            var eventos = repository.buscarEventosPermitidos(VisibilidadeEvento.GLOBAL, turmaId);
            return ResponseEntity.ok(eventos);
        }

        // Se o aluno ainda não tiver turma, mostra só os eventos globais
        var eventosGlobais = repository.findByVisibilidadeOrderByDataHoraAsc(VisibilidadeEvento.GLOBAL);
        return ResponseEntity.ok(eventosGlobais);
    }
}