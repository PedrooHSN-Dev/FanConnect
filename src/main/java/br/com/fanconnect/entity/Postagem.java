package br.com.fanconnect.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "postagens")
public class Postagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O conteúdo da postagem não pode estar vazio")
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

    public void alternarCurtida(Usuario usuarioLogado) {
        // Verifica se o usuário já está na lista
        boolean jaCurtiu = usuariosQueCurtiram.stream()
                .anyMatch(u -> u.getId().equals(usuarioLogado.getId()));

        if (jaCurtiu) {
            // Se já curtiu, remove o usuário da lista e tira 1 do contador (UNLIKE)
            usuariosQueCurtiram.removeIf(u -> u.getId().equals(usuarioLogado.getId()));
            this.quantidadeCurtidas--;
        } else {
            // Se não curtiu, adiciona à lista e soma 1 (LIKE)
            this.usuariosQueCurtiram.add(usuarioLogado);
            this.quantidadeCurtidas++;
        }
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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "postagem_curtidas",
            joinColumns = @JoinColumn(name = "postagem_id"),
            inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Set<Usuario> usuariosQueCurtiram = new HashSet<>();
}