package br.com.fanconnect.controller;

import br.com.fanconnect.entity.Postagem;
import br.com.fanconnect.repository.PostagemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feed")
@CrossOrigin(origins = "*") // Permite que o frontend em React consuma esta API sem bloqueios de segurança do navegador
public class FeedController {

    @Autowired
    private PostagemRepository postagemRepository;

    // Endpoint para buscar o feed (O React vai fazer um GET aqui)
    @GetMapping
    public ResponseEntity<List<Postagem>> listarFeed() {
        List<Postagem> postagens = postagemRepository.findAllByOrderByScoreRelevanciaDescDataCriacaoDesc();
        return ResponseEntity.ok(postagens);
    }

    // Endpoint para publicar no feed (O React vai fazer um POST aqui)
    @PostMapping
    public ResponseEntity<Postagem> criarPostagem(@RequestBody Postagem novaPostagem) {
        // A anotação @PrePersist faz o algoritmo de Score será calculado automaticamente nos bastidores antes de salvar
        Postagem postagemSalva = postagemRepository.save(novaPostagem);
        return ResponseEntity.status(201).body(postagemSalva);
    }

    @PostMapping("/{id}/curtir")
    public ResponseEntity<Postagem> curtirPostagem(@PathVariable Long id) {
        return postagemRepository.findById(id)
                .map(postagem -> {
                    // Chama o seu método já existente!
                    postagem.incrementarCurtidas();

                    Postagem postagemAtualizada = postagemRepository.save(postagem);
                    return ResponseEntity.ok(postagemAtualizada);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}