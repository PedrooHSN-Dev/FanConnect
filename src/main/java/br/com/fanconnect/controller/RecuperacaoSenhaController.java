package br.com.fanconnect.controller;

import br.com.fanconnect.dto.DadosEsqueciSenha;
import br.com.fanconnect.dto.DadosRedefinirSenha;
import br.com.fanconnect.entity.CodigoRecuperacao;
import br.com.fanconnect.entity.Usuario;
import br.com.fanconnect.repository.CodigoRecuperacaoRepository;
import br.com.fanconnect.repository.UsuarioRepository;
import br.com.fanconnect.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class RecuperacaoSenhaController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CodigoRecuperacaoRepository codigoRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/esqueci-senha")
    @Transactional
    public ResponseEntity<String> solicitarRecuperacao(@RequestBody DadosEsqueciSenha dados) {
        var userDetails = usuarioRepository.findByEmail(dados.email());


        if (userDetails == null) {
            return ResponseEntity.ok("Se o e-mail existir em nossa base, um código será enviado.");
        }

        Usuario usuario = (Usuario) userDetails;

        // Limpa códigos antigos para não sujar o banco
        codigoRepository.deleteByUsuarioId(usuario.getId());

        // Gera um código numérico de 6 dígitos (ex: 049281)
        String codigo = String.format("%06d", new Random().nextInt(999999));

        // Salva no banco com 15 minutos de validade
        CodigoRecuperacao codigoRecuperacao = new CodigoRecuperacao(codigo, usuario, 15);
        codigoRepository.save(codigoRecuperacao);

        emailService.enviarCodigoRecuperacao(usuario.getEmail(), codigo);

        return ResponseEntity.ok("Se o e-mail existir em nossa base, um código será enviado.");
    }

    @PostMapping("/redefinir-senha")
    @Transactional
    public ResponseEntity<String> redefinirSenha(@RequestBody DadosRedefinirSenha dados) {
        var codigoOptional = codigoRepository.findByCodigo(dados.codigo());

        if (codigoOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Código inválido ou não encontrado.");
        }

        CodigoRecuperacao codigoRecuperacao = codigoOptional.get();

        if (codigoRecuperacao.isExpirado()) {
            codigoRepository.delete(codigoRecuperacao);
            return ResponseEntity.badRequest().body("Este código expirou. Solicite um novo.");
        }

        // Recupera o usuário e criptografa a nova senha com BCrypt
        Usuario usuario = codigoRecuperacao.getUsuario();
        usuario.setSenha(passwordEncoder.encode(dados.novaSenha()));
        usuarioRepository.save(usuario);

        // Invalida o código para que não seja usado duas vezes
        codigoRepository.delete(codigoRecuperacao);

        return ResponseEntity.ok("Senha redefinida com sucesso!");
    }
}