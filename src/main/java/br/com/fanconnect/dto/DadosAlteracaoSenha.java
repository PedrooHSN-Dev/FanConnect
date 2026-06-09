package br.com.fanconnect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DadosAlteracaoSenha(
        String senhaAtual,

        @NotBlank
        @Size(min = 6)
        String senhaNova
) {}