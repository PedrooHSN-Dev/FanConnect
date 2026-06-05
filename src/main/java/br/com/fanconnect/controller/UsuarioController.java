package br.com.fanconnect.controller;

import br.com.fanconnect.dto.DadosCadastroUsuario;
import br.com.fanconnect.entity.TipoUsuario;
import br.com.fanconnect.entity.Usuario;
import br.com.fanconnect.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity cadastrar(@RequestBody DadosCadastroUsuario dados) {
        if (repository.findByEmail(dados.email()) != null) {
            return ResponseEntity.badRequest().body("E-mail já cadastrado!");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dados.nome());
        usuario.setEmail(dados.email());
        usuario.setMatricula(dados.matricula());
        usuario.setTipoPerfil(TipoUsuario.ALUNO);

        // Aplica o hash da senha usando o encoder injetado
        usuario.setSenha(passwordEncoder.encode(dados.senha()));

        repository.save(usuario);

        return ResponseEntity.status(201).build();
    }
}