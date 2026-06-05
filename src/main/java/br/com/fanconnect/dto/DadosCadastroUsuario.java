package br.com.fanconnect.dto;

public record DadosCadastroUsuario(
        String nome,
        String email,
        String senha,
        String matricula
) {}