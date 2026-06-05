package br.com.fanconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "codigos_recuperacao")
public class CodigoRecuperacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 6)
    private String codigo;

    @Column(nullable = false)
    private LocalDateTime dataExpiracao;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    public CodigoRecuperacao() {}

    public CodigoRecuperacao(String codigo, Usuario usuario, int minutosValidade) {
        this.codigo = codigo;
        this.usuario = usuario;
        this.dataExpiracao = LocalDateTime.now().plusMinutes(minutosValidade);
    }

    // Verifica se o código já passou do tempo limite
    public boolean isExpirado() {
        return LocalDateTime.now().isAfter(this.dataExpiracao);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public LocalDateTime getDataExpiracao() { return dataExpiracao; }
    public void setDataExpiracao(LocalDateTime dataExpiracao) { this.dataExpiracao = dataExpiracao; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}