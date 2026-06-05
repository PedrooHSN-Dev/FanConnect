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
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<List<Postagem>> listarFeed() {
        List<Postagem> postagens = postagemRepository.findAllByOrderByScoreRelevanciaDescDataCriacaoDesc();
        return ResponseEntity.ok(postagens);
    }

    // Endpoint para publicar no feed (O React vai fazer um POST aqui)
    @PostMapping
    public ResponseEntity<Postagem> criarPostagem(@RequestBody Postagem novaPostagem) {
        // A anotação @PrePersist faz o algoritmo de Score ser calculado automaticamente nos bastidores antes de salvar
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

    @PostMapping("/{postagemId}/salvar-agenda/{usuarioId}")
    public ResponseEntity<ItemAgenda> salvarEventoNaAgenda(@PathVariable Long postagemId, @PathVariable Long usuarioId) {

        // Verifica se a postagem existe
        Postagem postagem = postagemRepository.findById(postagemId).orElse(null);
        if (postagem == null || postagem.getEventoProposto() == null) {
            return ResponseEntity.badRequest().build(); // Retorna 400 se a postagem não existir ou não tiver evento
        }

        // Verifica se o usuário existe
        Usuario aluno = usuarioRepository.findById(usuarioId).orElse(null);
        if (aluno == null) {
            return ResponseEntity.notFound().build();
        }

        // Cria a cópia privada para o aluno
        ItemAgenda eventoOriginal = postagem.getEventoProposto();

        ItemAgenda eventoPrivado = new ItemAgenda();
        eventoPrivado.setTitulo(eventoOriginal.getTitulo());
        // Adiciona um aviso na descrição para lembrar de onde veio
        eventoPrivado.setDescricao("Salvo do Feed: " + postagem.getConteudo());
        eventoPrivado.setDataHora(eventoOriginal.getDataHora());
        eventoPrivado.setLocalizacao(eventoOriginal.getLocalizacao());
        eventoPrivado.setCategoria(eventoOriginal.getCategoria());

        eventoPrivado.setVisibilidade(VisibilidadeEvento.PRIVADO);
        eventoPrivado.setDono(aluno);
        eventoPrivado.setLembreteAtivo(true);
        eventoPrivado.setMinutosAvisoLembrete(60); // Avisa 1 hora antes por padrão

        // Salva e retorna o novo compromisso
        ItemAgenda eventoSalvo = agendaRepository.save(eventoPrivado);

        return ResponseEntity.ok(eventoSalvo);
    }
}