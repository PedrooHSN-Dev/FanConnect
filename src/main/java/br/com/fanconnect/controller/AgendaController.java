package br.com.fanconnect.controller;

import br.com.fanconnect.entity.ItemAgenda;
import br.com.fanconnect.repository.ItemAgendaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agenda")
public class AgendaController {

    private final ItemAgendaRepository agendaRepository;

    public AgendaController(ItemAgendaRepository agendaRepository) {
        this.agendaRepository = agendaRepository;
    }

    // 1. Listar todos os eventos (GET)
    @GetMapping
    public ResponseEntity<List<ItemAgenda>> listarEventos() {
        List<ItemAgenda> eventos = agendaRepository.findAll();
        return ResponseEntity.ok(eventos);
    }

    // 2. Criar um novo evento (POST)
    @PostMapping
    public ResponseEntity<ItemAgenda> criarEvento(@RequestBody ItemAgenda novoEvento) {
        ItemAgenda eventoSalvo = agendaRepository.save(novoEvento);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventoSalvo);
    }
}