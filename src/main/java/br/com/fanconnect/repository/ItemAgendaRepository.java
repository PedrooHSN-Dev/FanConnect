package br.com.fanconnect.repository;

import br.com.fanconnect.entity.ItemAgenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemAgendaRepository extends JpaRepository<ItemAgenda, Long> {
}