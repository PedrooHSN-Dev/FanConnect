package br.com.fanconnect.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "postagens")
public class Postagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String conteudo;

    private String anexoUrl;

    @Column(nullable = false)
    private Boolean oficial = false;

    @Column(precision = 10, scale = 4)
    private BigDecimal scoreRelevancia = BigDecimal.ZERO;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    // Relação: Muitos posts pertencem a UM Autor
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Usuario autor;

    // Relação: Um post pode divulgar UM Item na Agenda
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "item_agenda_id")
    private ItemAgenda eventoProposto;

    // Contadores com valor padrão
    private int quantidadeCurtidas = 0;
    private int quantidadeComentarios = 0;

    public Postagem() {}

    // RF03 - Cálculo de Relevância
    @PrePersist
    @PreUpdate
    public void atualizarScore() {
        if (this.dataCriacao == null) {
            this.dataCriacao = LocalDateTime.now();
        }

        double engajamento = this.quantidadeCurtidas + (this.quantidadeComentarios * 1.5);
        double pesoAutoridade = (this.oficial != null && this.oficial) ? 2.5 : 1.0;

        long horasDecorridas = ChronoUnit.HOURS.between(this.dataCriacao, LocalDateTime.now());
        double tempoDecaimento = Math.pow((horasDecorridas + 2), 1.5);

        double calculoFinal = (engajamento * pesoAutoridade) / tempoDecaimento;

        this.scoreRelevancia = new BigDecimal(calculoFinal).setScale(4, RoundingMode.HALF_UP);
    }

    // Getters e Setters Básicos
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }

    public String getAnexoUrl() { return anexoUrl; }
    public void setAnexoUrl(String anexoUrl) { this.anexoUrl = anexoUrl; }

    public Boolean getOficial() { return oficial; }
    public void setOficial(Boolean oficial) { this.oficial = oficial; }

    public BigDecimal getScoreRelevancia() { return scoreRelevancia; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }

    public Usuario getAutor() { return autor; }
    public void setAutor(Usuario autor) { this.autor = autor; }

    public ItemAgenda getEventoProposto() { return eventoProposto; }
    public void setEventoProposto(ItemAgenda eventoProposto) { this.eventoProposto = eventoProposto; }

    public int getQuantidadeCurtidas() { return quantidadeCurtidas; }
    public void incrementarCurtidas() {
        this.quantidadeCurtidas++;
        atualizarScore();
    }
    public void decrementarCurtidas() {
        if(this.quantidadeCurtidas > 0) {
            this.quantidadeCurtidas--;
            atualizarScore();
        }
    }

    public int getQuantidadeComentarios() { return quantidadeComentarios; }
    public void incrementarComentarios() {
        this.quantidadeComentarios++;
        atualizarScore();
    }
}