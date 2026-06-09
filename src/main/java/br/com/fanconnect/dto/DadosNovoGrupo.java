package br.com.fanconnect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record DadosNovoGrupo(
        @NotBlank(message = "O nome do grupo é obrigatório")
        String nome,

        @NotEmpty(message = "O grupo precisa ter pelo menos um participante além de você")
        List<Long> participantesIds
) {}