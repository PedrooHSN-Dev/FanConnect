package br.com.fanconnect.security;

import br.com.fanconnect.entity.TipoUsuario;
import br.com.fanconnect.entity.Usuario;
import br.com.fanconnect.repository.UsuarioRepository;
import br.com.fanconnect.service.TokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    @org.springframework.context.annotation.Lazy
    private PasswordEncoder passwordEncoder;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String nome = oAuth2User.getAttribute("name");

        Usuario usuario = (Usuario) usuarioRepository.findByEmail(email);

        if (usuario == null) {
            usuario = new Usuario();
            usuario.setNome(nome);
            usuario.setEmail(email);
            usuario.setSenha(passwordEncoder.encode(UUID.randomUUID().toString()));
            usuario.setMatricula("GOO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            usuario.setTipoPerfil(TipoUsuario.ALUNO);
            usuario.setAtivo(true); // Se veio do Google, o e-mail já está verificado por eles!

            usuario = usuarioRepository.save(usuario);
        }

        String tokenJwt = tokenService.gerarToken(usuario);

        response.sendRedirect("http://localhost:4200/auth-callback?token=" + tokenJwt);
    }
}