package br.com.fanconnect.repository;

import br.com.fanconnect.entity.CodigoRecuperacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CodigoRecuperacaoRepository extends JpaRepository<CodigoRecuperacao, Long> {

    Optional<CodigoRecuperacao> findByCodigo(String codigo);

    // Limpa códigos antigos
    void deleteByUsuarioId(Long usuarioId);
}