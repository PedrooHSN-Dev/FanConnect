package br.com.fanconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "itens_agenda")
public class ItemAgenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    private String localizacao;

    @Column(nullable = false, length = 30)
    private String categoria;

    // NOVOS CAMPOS DOS REQUISITOS

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VisibilidadeEvento visibilidade = VisibilidadeEvento.GLOBAL;

    // Se for privado ou de turma, precisamos saber de quem é
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dono_id")
    private Usuario dono;

    @Column(nullable = false)
    private Boolean lembreteAtivo = false;

    private Integer minutosAvisoLembrete;

    public ItemAgenda() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public String getLocalizacao() { return localizacao; }
    public void setLocalizacao(String localizacao) { this.localizacao = localizacao; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public VisibilidadeEvento getVisibilidade() { return visibilidade; }
    public void setVisibilidade(VisibilidadeEvento visibilidade) { this.visibilidade = visibilidade; }

    public Usuario getDono() { return dono; }
    public void setDono(Usuario dono) { this.dono = dono; }

    public Boolean getLembreteAtivo() { return lembreteAtivo; }
    public void setLembreteAtivo(Boolean lembreteAtivo) { this.lembreteAtivo = lembreteAtivo; }

    public Integer getMinutosAvisoLembrete() { return minutosAvisoLembrete; }
    public void setMinutosAvisoLembrete(Integer minutosAvisoLembrete) { this.minutosAvisoLembrete = minutosAvisoLembrete; }
}