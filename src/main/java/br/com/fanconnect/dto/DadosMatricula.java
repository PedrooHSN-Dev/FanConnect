package br.com.fanconnect.dto;

import jakarta.validation.constraints.NotBlank;

public record DadosMatricula(
        @NotBlank(message = "A matrícula não pode estar vazia.")
        String matricula
) {}