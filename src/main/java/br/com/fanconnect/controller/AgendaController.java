package br.com.fanconnect.controller;

import br.com.fanconnect.entity.ItemAgenda;
import br.com.fanconnect.entity.Usuario;
import br.com.fanconnect.entity.VisibilidadeEvento;
import br.com.fanconnect.repository.ItemAgendaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarEvento(@PathVariable Long id, @RequestBody ItemAgenda eventoAtualizado, @AuthenticationPrincipal Usuario usuarioLogado) {
        return agendaRepository.findById(id).map(eventoExistente -> {
            // Verifica se o usuário logado é o dono do evento
            if (!eventoExistente.getDono().getId().equals(usuarioLogado.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Você não tem permissão para editar este evento.");
            }

            eventoExistente.setTitulo(eventoAtualizado.getTitulo());
            eventoExistente.setDescricao(eventoAtualizado.getDescricao());
            eventoExistente.setDataHora(eventoAtualizado.getDataHora());
            eventoExistente.setLocalizacao(eventoAtualizado.getLocalizacao());
            eventoExistente.setCategoria(eventoAtualizado.getCategoria());
            eventoExistente.setVisibilidade(eventoAtualizado.getVisibilidade());
            eventoExistente.setLembreteAtivo(eventoAtualizado.getLembreteAtivo());
            eventoExistente.setMinutosAvisoLembrete(eventoAtualizado.getMinutosAvisoLembrete());

            agendaRepository.save(eventoExistente);
            return ResponseEntity.ok(eventoExistente);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarEvento(@PathVariable Long id, @AuthenticationPrincipal Usuario usuarioLogado) {
        return agendaRepository.findById(id).map(eventoExistente -> {
            // Verifica se o usuário logado é o dono do evento
            if (!eventoExistente.getDono().getId().equals(usuarioLogado.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Você não tem permissão para deletar este evento.");
            }

            agendaRepository.delete(eventoExistente);
            return ResponseEntity.noContent().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}