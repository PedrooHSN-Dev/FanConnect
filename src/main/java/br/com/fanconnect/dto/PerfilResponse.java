package br.com.fanconnect.dto;

import br.com.fanconnect.entity.EstadoCivil;
import br.com.fanconnect.entity.Usuario;

import java.time.LocalDate;

public record PerfilResponse(
        Long id,
        String nome,
        String biografia,
        String localizacao,
        String telefone,
        LocalDate dataNascimento,
        EstadoCivil estadoCivil,
        String nomeParceiro,
        int quantidadeSeguidores,
        int quantidadeSeguindo
) {
    public PerfilResponse(Usuario usuario) {
        this(
                usuario.getId(),
                usuario.getNome(),
                usuario.getBiografia(),
                usuario.getLocalizacao(),
                usuario.getTelefone(),
                usuario.getDataNascimento(),
                usuario.getEstadoCivil(),
                usuario.getNomeParceiro(),
                usuario.getSeguidores() != null ? usuario.getSeguidores().size() : 0,
                usuario.getSeguindo() != null ? usuario.getSeguindo().size() : 0
        );
    }
}