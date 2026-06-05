package br.com.fanconnect.controller;

import br.com.fanconnect.entity.Turma;
import br.com.fanconnect.repository.TurmaRepository;
import br.com.fanconnect.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/turmas")
@CrossOrigin(origins = "*")
public class TurmaController {

    @Autowired
    private TurmaRepository turmaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Listar todas as turmas (Mntar um select/dropdown no front)
    @GetMapping
    public ResponseEntity<List<Turma>> listarTurmas() {
        return ResponseEntity.ok(turmaRepository.findAll());
    }

    // Criar uma nova turma
    @PostMapping
    public ResponseEntity<Turma> criarTurma(@RequestBody Turma novaTurma) {
        Turma turmaSalva = turmaRepository.save(novaTurma);
        return ResponseEntity.status(201).body(turmaSalva);
    }

    // Matricular um aluno em uma turma
    @PostMapping("/{turmaId}/matricular/{usuarioId}")
    public ResponseEntity<String> matricularAluno(@PathVariable Long turmaId, @PathVariable Long usuarioId) {

        var turmaOptional = turmaRepository.findById(turmaId);
        var usuarioOptional = usuarioRepository.findById(usuarioId);

        if (turmaOptional.isEmpty() || usuarioOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var turma = turmaOptional.get();
        var aluno = usuarioOptional.get();

        // Faz o vínculo do aluno com a turma
        aluno.setTurma(turma);
        usuarioRepository.save(aluno);

        return ResponseEntity.ok("Aluno " + aluno.getNome() + " matriculado na turma " + turma.getNomeCurso() + " com sucesso!");
    }
}