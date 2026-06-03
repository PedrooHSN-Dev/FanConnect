package br.com.fanconnect.repository;

import br.com.fanconnect.entity.ItemAgenda;
import br.com.fanconnect.entity.VisibilidadeEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemAgendaRepository extends JpaRepository<ItemAgenda, Long> {

    // Busca os eventos globais (calendário acadêmico da instituição)
    List<ItemAgenda> findByVisibilidade(VisibilidadeEvento visibilidade);

    // Busca as anotações privadas de um aluno específico
    List<ItemAgenda> findByVisibilidadeAndDonoId(VisibilidadeEvento visibilidade, Long donoId);
}