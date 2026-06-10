package br.com.fanconnect.controller;

import br.com.fanconnect.dto.DadosEdicaoGrupo;
import br.com.fanconnect.dto.DadosNovaConversaIndividual;
import br.com.fanconnect.dto.DadosNovoGrupo;
import br.com.fanconnect.dto.DadosEnviarMensagem;
import br.com.fanconnect.entity.Conversa;
import br.com.fanconnect.entity.Mensagem;
import br.com.fanconnect.entity.TipoConversa;
import br.com.fanconnect.entity.Usuario;
import br.com.fanconnect.repository.ConversaRepository;
import br.com.fanconnect.repository.MensagemRepository;
import br.com.fanconnect.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private ConversaRepository conversaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MensagemRepository mensagemRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // ==========================================
    // LISTAR MINHAS CONVERSAS
    // ==========================================
    @GetMapping("/minhas-conversas")
    public ResponseEntity<List<Conversa>> listarMinhasConversas(@AuthenticationPrincipal Usuario usuarioLogado) {
        List<Conversa> minhasConversas = conversaRepository.buscarConversasDoUsuario(usuarioLogado.getId());
        return ResponseEntity.ok(minhasConversas);
    }

    // ==========================================
    // BUSCAR USUÁRIOS PARA CONVERSAR
    // ==========================================
    @GetMapping("/usuarios/buscar")
    public ResponseEntity<List<Usuario>> buscarUsuariosParaChat(@RequestParam String termo, @AuthenticationPrincipal Usuario usuarioLogado) {
        List<Usuario> usuarios = usuarioRepository.findAll().stream()
                .filter(u -> !u.getId().equals(usuarioLogado.getId()))
                .filter(u -> u.getNome().toLowerCase().contains(termo.toLowerCase()) ||
                        u.getEmail().toLowerCase().contains(termo.toLowerCase()))
                .toList();

        return ResponseEntity.ok(usuarios);
    }

    // ==========================================
    // CRIAR CONVERSA INDIVIDUAL
    // ==========================================
    @PostMapping("/individual")
    public ResponseEntity<?> criarOuAbrirConversaIndividual(
            @RequestBody @Valid DadosNovaConversaIndividual dados,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        Optional<Conversa> conversaExistente = conversaRepository.buscarConversaIndividualExistente(usuarioLogado.getId(), dados.usuarioIdDestino());
        if (conversaExistente.isPresent()) {
            return ResponseEntity.ok(conversaExistente.get());
        }

        Usuario destino = usuarioRepository.findById(dados.usuarioIdDestino())
                .orElseThrow(() -> new RuntimeException("Usuário destino não encontrado"));

        Conversa novaConversa = new Conversa();
        novaConversa.setTipo(TipoConversa.INDIVIDUAL);

        novaConversa.setAdmin(usuarioLogado);

        novaConversa.getParticipantes().add(usuarioLogado);
        novaConversa.getParticipantes().add(destino);

        Conversa conversaSalva = conversaRepository.save(novaConversa);

        messagingTemplate.convertAndSend("/topic/usuarios/" + usuarioLogado.getId() + "/notificacoes", "ATUALIZAR_LISTA");

        return ResponseEntity.ok(conversaSalva);
    }

    // ==========================================
    // CRIAR E EDITAR GRUPOS
    // ==========================================
    @PostMapping("/grupo")
    public ResponseEntity<?> criarGrupo(
            @RequestBody @Valid DadosNovoGrupo dados,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        Conversa novoGrupo = new Conversa();
        novoGrupo.setTipo(TipoConversa.GRUPO);
        novoGrupo.setNome(dados.nome());
        novoGrupo.setAdmin(usuarioLogado);

        novoGrupo.getParticipantes().add(usuarioLogado);

        List<Usuario> convidados = usuarioRepository.findAllById(dados.participantesIds());
        novoGrupo.getParticipantes().addAll(convidados);

        Conversa grupoSalvo = conversaRepository.save(novoGrupo);

        grupoSalvo.getParticipantes().forEach(participante -> {
            messagingTemplate.convertAndSend("/topic/usuarios/" + participante.getId() + "/notificacoes", "ATUALIZAR_LISTA");
        });

        return ResponseEntity.ok(grupoSalvo);
    }

    @PutMapping("/grupo/{id}/nome")
    public ResponseEntity<?> alterarNomeDoGrupo(
            @PathVariable Long id,
            @RequestBody @Valid DadosEdicaoGrupo dados,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        Conversa grupo = conversaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grupo não encontrado"));

        if (!grupo.isGrupo() || grupo.getAdmin() == null || !grupo.getAdmin().getId().equals(usuarioLogado.getId())) {
            return ResponseEntity.status(403).body("Apenas o administrador do grupo pode alterar o nome.");
        }

        grupo.setNome(dados.novoNome());
        conversaRepository.save(grupo);

        return ResponseEntity.ok().build();
    }

    // ==========================================
    // HISTÓRICO DE MENSAGENS (REST Clássico)
    // ==========================================
    @GetMapping("/{conversaId}/mensagens")
    public ResponseEntity<List<Mensagem>> buscarHistorico(@PathVariable Long conversaId) {
        List<Mensagem> historico = mensagemRepository.findByConversaIdOrderByDataEnvioAsc(conversaId);
        return ResponseEntity.ok(historico);
    }

    // ==========================================
    // ENVIAR MENSAGEM (WebSocket/STOMP)
    // ==========================================
    @Transactional
    @MessageMapping("/chat/enviar")
    public void enviarMensagem(DadosEnviarMensagem dados) {
        Conversa conversa = conversaRepository.findById(dados.conversaId())
                .orElseThrow(() -> new RuntimeException("Conversa não encontrada"));

        Usuario remetente = usuarioRepository.findById(dados.remetenteId())
                .orElseThrow(() -> new RuntimeException("Remetente não encontrado"));

        Mensagem novaMensagem = new Mensagem();
        novaMensagem.setConversa(conversa);
        novaMensagem.setRemetente(remetente);
        novaMensagem.setConteudo(dados.conteudo());
        Mensagem mensagemSalva = mensagemRepository.save(novaMensagem);

        Map<String, Object> mensagemLimpa = new HashMap<>();
        mensagemLimpa.put("id", mensagemSalva.getId());
        mensagemLimpa.put("conteudo", mensagemSalva.getConteudo());
        mensagemLimpa.put("dataEnvio", mensagemSalva.getDataEnvio().toString());

        Map<String, Object> remetenteMap = new HashMap<>();
        remetenteMap.put("id", remetente.getId());
        remetenteMap.put("nome", remetente.getNome());
        remetenteMap.put("fotoPerfil", remetente.getFotoPerfil());
        mensagemLimpa.put("remetente", remetenteMap);

        messagingTemplate.convertAndSend("/topic/conversas/" + conversa.getId(), mensagemLimpa);

        conversa.getParticipantes().forEach(participante -> {
            messagingTemplate.convertAndSend("/topic/usuarios/" + participante.getId() + "/notificacoes", "ATUALIZAR_LISTA");
        });
    }
}