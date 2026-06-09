package br.com.fanconnect.dto;

public record DadosEnviarMensagem(
        Long conversaId,
        Long remetenteId,
        String conteudo
) {}