package br.com.fanconnect.repository;

import br.com.fanconnect.entity.Postagem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostagemRepository extends JpaRepository<Postagem, Long> {

    Page<Postagem> findAllByOrderByScoreRelevanciaDescDataCriacaoDesc(Pageable pageable);
}