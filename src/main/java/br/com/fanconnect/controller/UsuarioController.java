package br.com.fanconnect.controller;

import br.com.fanconnect.dto.DadosAtivacaoConta;
import br.com.fanconnect.dto.DadosCadastroUsuario;
import br.com.fanconnect.dto.PerfilRequest;
import br.com.fanconnect.dto.PerfilResponse;
import br.com.fanconnect.entity.CodigoRecuperacao;
import br.com.fanconnect.entity.TipoUsuario;
import br.com.fanconnect.entity.Usuario;
import br.com.fanconnect.repository.CodigoRecuperacaoRepository;
import br.com.fanconnect.repository.UsuarioRepository;
import br.com.fanconnect.service.EmailService;
import br.com.fanconnect.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CodigoRecuperacaoRepository codigoRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    @Transactional
    public ResponseEntity<String> cadastrar(@RequestBody @Valid DadosCadastroUsuario dados) {
        if (repository.findByEmail(dados.email()) != null) {
            return ResponseEntity.badRequest().body("E-mail já cadastrado!");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dados.nome());
        usuario.setEmail(dados.email());
        usuario.setMatricula(dados.matricula());
        usuario.setTipoPerfil(TipoUsuario.ALUNO);
        usuario.setSenha(passwordEncoder.encode(dados.senha()));

        usuario.setAtivo(false);

        repository.save(usuario);

        String codigo = String.format("%06d", new Random().nextInt(999999));

        CodigoRecuperacao codigoVerificacao = new CodigoRecuperacao(codigo, usuario, 1440);
        codigoRepository.save(codigoVerificacao);

        emailService.enviarCodigoAtivacao(usuario.getEmail(), codigo);

        return ResponseEntity.status(201).body("Conta criada! Por favor, verifique o seu e-mail para ativar a conta.");
    }

    @PostMapping("/ativar-conta")
    @Transactional
    public ResponseEntity<String> ativarConta(@RequestBody DadosAtivacaoConta dados) {
        var codigoOptional = codigoRepository.findByCodigo(dados.codigo());

        if (codigoOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Código de ativação inválido ou não encontrado.");
        }

        CodigoRecuperacao codigoVerificacao = codigoOptional.get();

        if (codigoVerificacao.isExpirado()) {
            codigoRepository.delete(codigoVerificacao);
            return ResponseEntity.badRequest().body("Este código expirou. Por favor, cadastre-se novamente ou solicite um novo código.");
        }

        Usuario usuario = codigoVerificacao.getUsuario();
        usuario.setAtivo(true);
        repository.save(usuario);

        codigoRepository.delete(codigoVerificacao);

        return ResponseEntity.ok("Conta ativada com sucesso! Já pode fazer login.");
    }

    // ========================================================================
    // ROTAS DE PERFIL SOCIAL (REQUEREM LOGIN)
    // ========================================================================

    // Visualizar o perfil
    @GetMapping("/{id}")
    public ResponseEntity<PerfilResponse> buscarPerfil(@PathVariable Long id) {
        PerfilResponse perfil = usuarioService.buscarPerfilPorId(id);
        return ResponseEntity.ok(perfil);
    }

    // Atualizar perfil
    @PutMapping("/perfil")
    public ResponseEntity<PerfilResponse> atualizarPerfil(
            @RequestBody @Valid PerfilRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        PerfilResponse perfilAtualizado = usuarioService.atualizarPerfil(usuarioLogado, request);
        return ResponseEntity.ok(perfilAtualizado);
    }

    @GetMapping("/me")
    public ResponseEntity<PerfilResponse> buscarMeuPerfil(@AuthenticationPrincipal Usuario usuarioLogado) {
        PerfilResponse perfil = usuarioService.buscarPerfilPorId(usuarioLogado.getId());
        return ResponseEntity.ok(perfil);
    }

    @PostMapping("/{idSeguido}/seguir")
    public ResponseEntity<String> seguirUsuario(
            @PathVariable Long idSeguido,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        String mensagem = usuarioService.seguirUsuario(usuarioLogado, idSeguido);
        return ResponseEntity.ok(mensagem);
    }

    @DeleteMapping("/{idSeguido}/seguir")
    public ResponseEntity<String> deixarDeSeguir(
            @PathVariable Long idSeguido,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        String mensagem = usuarioService.deixarDeSeguir(usuarioLogado, idSeguido);
        return ResponseEntity.ok(mensagem);
    }
}