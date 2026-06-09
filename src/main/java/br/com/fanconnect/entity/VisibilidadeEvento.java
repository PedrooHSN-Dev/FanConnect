package br.com.fanconnect.entity;

public enum VisibilidadeEvento {
    GLOBAL,       // Visível para todos (Ex: Palestras, Feriados)
    TURMA,        // Visível apenas para alunos da mesma turma (Ex: Prazos de trabalhos)
    PRIVADO,       // Visível apenas para o dono (Ex: Anotações pessoais)
    PUBLICO
}