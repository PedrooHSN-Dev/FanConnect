package br.com.fanconnect.repository;

import br.com.fanconnect.entity.ItemAgenda;
import br.com.fanconnect.entity.VisibilidadeEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemAgendaRepository extends JpaRepository<ItemAgenda, Long> {

    // Busca os eventos globais (calendário acadêmico da instituição)
    List<ItemAgenda> findByVisibilidade(VisibilidadeEvento visibilidade);

    // Busca as anotações privadas de um aluno específico
    List<ItemAgenda> findByVisibilidadeAndDonoId(VisibilidadeEvento visibilidade, Long donoId);

    // O robô só quer eventos que o lembrete está ATIVO e que AINDA NÃO foram enviados
    List<ItemAgenda> findByLembreteAtivoTrueAndLembreteEnviadoFalse();

    // O Spring traduz isso para: SELECT * FROM itens_agenda WHERE visibilidade = ? OR turma_id = ? ORDER BY data_inicio ASC
    @Query("SELECT i FROM ItemAgenda i WHERE i.visibilidade = :visibilidade OR (i.turmaAlvo IS NOT NULL AND i.turmaAlvo.id = :turmaId) ORDER BY i.dataHora ASC")
    List<ItemAgenda> buscarEventosPermitidos(@Param("visibilidade") VisibilidadeEvento visibilidade, @Param("turmaId") Long turmaId);

    // Método de segurança caso o aluno ainda não tenha turma
    List<ItemAgenda> findByVisibilidadeOrderByDataHoraAsc(VisibilidadeEvento visibilidade);
}