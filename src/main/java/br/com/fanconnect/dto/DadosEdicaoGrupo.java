package br.com.fanconnect.dto;

import jakarta.validation.constraints.NotBlank;

public record DadosEdicaoGrupo(
        @NotBlank(message = "O novo nome não pode estar vazio")
        String novoNome
) {}