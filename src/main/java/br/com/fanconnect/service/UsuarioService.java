package br.com.fanconnect.service;

import br.com.fanconnect.dto.PerfilRequest;
import br.com.fanconnect.dto.PerfilResponse;
import br.com.fanconnect.entity.Usuario;
import br.com.fanconnect.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Transactional
    public PerfilResponse atualizarPerfil(Usuario usuarioLogado, PerfilRequest request) {
        Usuario usuario = repository.findById(usuarioLogado.getId()).orElseThrow();

        if (request.nome() != null && !request.nome().trim().isEmpty()) usuario.setNome(request.nome());
        if (request.fotoPerfil() != null) usuario.setFotoPerfil(request.fotoPerfil());
        if (request.biografia() != null) usuario.setBiografia(request.biografia());
        if (request.telefone() != null) usuario.setTelefone(request.telefone());
        if (request.localizacao() != null) usuario.setLocalizacao(request.localizacao());
        if (request.dataNascimento() != null) usuario.setDataNascimento(request.dataNascimento());
        if (request.estadoCivil() != null) usuario.setEstadoCivil(request.estadoCivil());
        if (request.nomeParceiro() != null) usuario.setNomeParceiro(request.nomeParceiro());

        repository.save(usuario);
        return new PerfilResponse(usuario);
    }

    @Transactional
    public String seguirUsuario(Usuario usuarioLogado, Long idSeguido) {
        if (usuarioLogado.getId().equals(idSeguido)) {
            throw new IllegalArgumentException("Você não pode seguir a si mesmo.");
        }

        Usuario seguidor = repository.findById(usuarioLogado.getId()).orElseThrow();
        Usuario seguido = repository.findById(idSeguido)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        seguidor.getSeguindo().add(seguido);
        repository.save(seguidor);

        return "Você agora está seguindo " + seguido.getNome();
    }

    @Transactional
    public String deixarDeSeguir(Usuario usuarioLogado, Long idSeguido) {
        Usuario seguidor = repository.findById(usuarioLogado.getId()).orElseThrow();
        Usuario seguido = repository.findById(idSeguido)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        seguidor.getSeguindo().remove(seguido);
        repository.save(seguidor);

        return "Você deixou de seguir " + seguido.getNome();
    }

    public PerfilResponse buscarPerfilPorId(Long id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
        return new PerfilResponse(usuario);
    }
}