package br.com.fanconnect.dto;

import jakarta.validation.constraints.NotNull;

public record DadosNovaConversaIndividual(
        @NotNull
        Long usuarioIdDestino
) {}