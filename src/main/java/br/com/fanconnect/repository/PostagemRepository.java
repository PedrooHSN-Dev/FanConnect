package br.com.fanconnect.repository;

import br.com.fanconnect.entity.Postagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostagemRepository extends JpaRepository<Postagem, Long> {

    // O próprio nome do método gera a consulta SQL!
    // Equivalente a: SELECT * FROM postagens ORDER BY score_relevancia DESC, data_criacao DESC
    List<Postagem> findAllByOrderByScoreRelevanciaDescDataCriacaoDesc();
}