package br.com.fanconnect.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conversas")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Conversa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoConversa tipo;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Usuario admin;

    @ManyToMany
    @JoinTable(
            name = "conversa_participantes",
            joinColumns = @JoinColumn(name = "conversa_id"),
            inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    private List<Usuario> participantes = new ArrayList<>();

    @OneToMany(mappedBy = "conversa", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Mensagem> mensagens = new ArrayList<>();

    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Transient
    public boolean isGrupo() {
        return this.tipo == TipoConversa.GRUPO;
    }
}