package br.com.fanconnect.controller;

import br.com.fanconnect.entity.ItemAgenda;
import br.com.fanconnect.entity.Usuario;
import br.com.fanconnect.entity.VisibilidadeEvento;
import br.com.fanconnect.repository.ItemAgendaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agenda")
@CrossOrigin(origins = "*")
public class AgendaController {

    private final ItemAgendaRepository agendaRepository;

    public AgendaController(ItemAgendaRepository agendaRepository) {
        this.agendaRepository = agendaRepository;
    }

    @GetMapping("/global")
    public ResponseEntity<List<ItemAgenda>> listarEventosGlobais() {
        var eventos = agendaRepository.findByVisibilidade(VisibilidadeEvento.GLOBAL);
        return ResponseEntity.ok(eventos);
    }

    @PostMapping
    public ResponseEntity<?> salvarEvento(@RequestBody ItemAgenda novoEvento, @AuthenticationPrincipal Usuario usuarioLogado) {
        if (agendaRepository.existsByDonoIdAndTitulo(usuarioLogado.getId(), novoEvento.getTitulo())) {
            return ResponseEntity.badRequest().body("Você já possui um evento com este título na sua agenda.");
        }

        novoEvento.setDono(usuarioLogado);
        ItemAgenda eventoSalvo = agendaRepository.save(novoEvento);

        return ResponseEntity.status(HttpStatus.CREATED).body(eventoSalvo);
    }

    @GetMapping("/meus-eventos")
    public ResponseEntity<List<ItemAgenda>> listarAgendaCompleta(@AuthenticationPrincipal Usuario usuarioLogado) {
        Long usuarioId = usuarioLogado.getId();
        Long turmaId = (usuarioLogado.getTurma() != null) ? usuarioLogado.getTurma().getId() : null;

        List<ItemAgenda> todosOsEventos = agendaRepository.buscarAgendaCompletaDoAluno(turmaId, usuarioId);

        return ResponseEntity.ok(todosOsEventos);
    }
}