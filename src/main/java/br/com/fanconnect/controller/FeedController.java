package br.com.fanconnect.controller;

import br.com.fanconnect.dto.DadosListagemComentario;
import br.com.fanconnect.dto.DadosNovoComentario;
import br.com.fanconnect.entity.Comentario;
import br.com.fanconnect.entity.ItemAgenda;
import br.com.fanconnect.entity.Postagem;
import br.com.fanconnect.entity.Usuario;
import br.com.fanconnect.entity.VisibilidadeEvento;
import br.com.fanconnect.repository.ComentarioRepository;
import br.com.fanconnect.repository.ItemAgendaRepository;
import br.com.fanconnect.repository.PostagemRepository;
import br.com.fanconnect.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feed")
@CrossOrigin(origins = "*")
public class FeedController {

    @Autowired
    private PostagemRepository postagemRepository;

    @Autowired
    private ItemAgendaRepository agendaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ComentarioRepository comentarioRepository;


    @GetMapping
    public ResponseEntity<Page<Postagem>> listarFeed(@PageableDefault(size = 10, page = 0) Pageable pageable) {

        Page<Postagem> postagens = postagemRepository.findAllByOrderByScoreRelevanciaDescDataCriacaoDesc(pageable);

        return ResponseEntity.ok(postagens);
    }

    @PostMapping
    public ResponseEntity<Postagem> criarPostagem(
            @RequestBody @Valid Postagem novaPostagem,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        novaPostagem.setAutor(usuarioLogado);

        if (novaPostagem.getEventoProposto() != null) {
            novaPostagem.getEventoProposto().setDono(usuarioLogado);
        }

        Postagem postagemSalva = postagemRepository.save(novaPostagem);
        return ResponseEntity.status(201).body(postagemSalva);
    }

    @PostMapping("/{id}/curtir")
    public ResponseEntity<Postagem> curtirPostagem(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        return postagemRepository.findById(id)
                .map(postagem -> {
                    postagem.alternarCurtida(usuarioLogado);

                    Postagem postagemAtualizada = postagemRepository.save(postagem);
                    return ResponseEntity.ok(postagemAtualizada);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Endpoint para comentar na postagem
    @PostMapping("/{postagemId}/comentar")
    public ResponseEntity<String> comentar(
            @PathVariable Long postagemId,
            @RequestBody DadosNovoComentario dados,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        // Verifica se a postagem existe
        var postagemOptional = postagemRepository.findById(postagemId);
        if (postagemOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var postagem = postagemOptional.get();

        // Cria e guarda o comentário
        Comentario comentario = new Comentario();
        comentario.setConteudo(dados.conteudo());
        comentario.setAutor(usuarioLogado);
        comentario.setPostagem(postagem);

        comentarioRepository.save(comentario);

        // Atualiza o contador de comentários na Postagem
        postagem.incrementarComentarios();
        postagemRepository.save(postagem);

        return ResponseEntity.status(201).body("Comentário adicionado com sucesso!");
    }

    @GetMapping("/{postagemId}/comentarios")
    public ResponseEntity<List<DadosListagemComentario>> listarComentarios(@PathVariable Long postagemId) {

        if (!postagemRepository.existsById(postagemId)) {
            return ResponseEntity.notFound().build();
        }

        var comentarios = comentarioRepository.findByPostagemIdOrderByDataCriacaoDesc(postagemId)
                .stream()
                .map(DadosListagemComentario::new)
                .toList();

        return ResponseEntity.ok(comentarios);
    }

    @PostMapping("/{postagemId}/salvar-agenda")
    public ResponseEntity<ItemAgenda> salvarEventoNaAgenda(
            @PathVariable Long postagemId,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        Postagem postagem = postagemRepository.findById(postagemId).orElse(null);
        if (postagem == null || postagem.getEventoProposto() == null) {
            return ResponseEntity.badRequest().build();
        }

        Usuario aluno = usuarioLogado;

        ItemAgenda eventoOriginal = postagem.getEventoProposto();
        ItemAgenda eventoPrivado = new ItemAgenda();
        eventoPrivado.setTitulo(eventoOriginal.getTitulo());
        eventoPrivado.setDescricao("Salvo do Feed: " + postagem.getConteudo());
        eventoPrivado.setDataHora(eventoOriginal.getDataHora());
        eventoPrivado.setLocalizacao(eventoOriginal.getLocalizacao());
        eventoPrivado.setCategoria(eventoOriginal.getCategoria());
        eventoPrivado.setVisibilidade(VisibilidadeEvento.PRIVADO);

        eventoPrivado.setDono(aluno);

        eventoPrivado.setLembreteAtivo(true);
        eventoPrivado.setMinutosAvisoLembrete(60);

        ItemAgenda eventoSalvo = agendaRepository.save(eventoPrivado);
        return ResponseEntity.ok(eventoSalvo);
    }
}