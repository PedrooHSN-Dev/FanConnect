package br.com.fanconnect.controller;

import br.com.fanconnect.entity.ItemAgenda;
import br.com.fanconnect.entity.VisibilidadeEvento;
import br.com.fanconnect.repository.ItemAgendaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    // 1. Listar eventos GLOBAIS (Calendário Acadêmico Oficial)
    @GetMapping("/global")
    public ResponseEntity<List<ItemAgenda>> listarEventosGlobais() {
        List<ItemAgenda> eventos = agendaRepository.findByVisibilidade(VisibilidadeEvento.GLOBAL);
        return ResponseEntity.ok(eventos);
    }

    // 2. Listar eventos PRIVADOS de um aluno específico
    @GetMapping("/privado/{usuarioId}")
    public ResponseEntity<List<ItemAgenda>> listarEventosPrivados(@PathVariable Long usuarioId) {
        List<ItemAgenda> eventos = agendaRepository.findByVisibilidadeAndDonoId(VisibilidadeEvento.PRIVADO, usuarioId);
        return ResponseEntity.ok(eventos);
    }

    // 3. Criar um novo evento (Global, Turma ou Privado)
    @PostMapping
    public ResponseEntity<ItemAgenda> criarEvento(@RequestBody ItemAgenda novoEvento) {
        // O Spring vai mapear automaticamente o JSON com as novas colunas
        ItemAgenda eventoSalvo = agendaRepository.save(novoEvento);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventoSalvo);
    }
}