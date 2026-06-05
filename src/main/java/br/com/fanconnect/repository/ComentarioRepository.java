package br.com.fanconnect.repository;

import br.com.fanconnect.entity.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    List<Comentario> findByPostagemIdOrderByDataCriacaoDesc(Long postagemId);
}