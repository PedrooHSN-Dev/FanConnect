package br.com.fanconnect.dto;

import br.com.fanconnect.entity.EstadoCivil;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PerfilRequest(
        @Size(max = 500, message = "A biografia pode ter no máximo 500 caracteres")
        String biografia,

        @Size(max = 20, message = "O telefone não pode exceder 20 caracteres")
        String telefone,

        @Size(max = 100, message = "A localização não pode exceder 100 caracteres")
        String localizacao,

        @Past(message = "A data de nascimento deve ser uma data no passado")
        LocalDate dataNascimento,

        EstadoCivil estadoCivil,

        @Size(max = 100, message = "O nome do parceiro não pode exceder 100 caracteres")
        String nomeParceiro
) {}