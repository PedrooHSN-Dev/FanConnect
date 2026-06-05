package br.com.fanconnect.dto;

import br.com.fanconnect.entity.Comentario;
import java.time.LocalDateTime;

public record DadosListagemComentario(
        Long id,
        String conteudo,
        String nomeAutor,
        LocalDateTime dataCriacao
) {
    public DadosListagemComentario(Comentario comentario) {
        this(
                comentario.getId(),
                comentario.getConteudo(),
                comentario.getAutor().getNome(),
                comentario.getDataCriacao()
        );
    }
}