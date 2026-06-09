package br.com.fanconnect.repository;

import br.com.fanconnect.entity.Conversa;
import br.com.fanconnect.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversaRepository extends JpaRepository<Conversa, Long> {

    @Query("SELECT c FROM Conversa c JOIN c.participantes p WHERE p.id = :usuarioId ORDER BY c.dataCriacao DESC")
    List<Conversa> buscarConversasDoUsuario(@Param("usuarioId") Long usuarioId);

    @Query("""
        SELECT c FROM Conversa c 
        JOIN c.participantes p1 
        JOIN c.participantes p2 
        WHERE c.tipo = 'INDIVIDUAL' 
        AND p1.id = :usuarioId1 AND p2.id = :usuarioId2
    """)
    Optional<Conversa> buscarConversaIndividualExistente(
            @Param("usuarioId1") Long usuarioId1,
            @Param("usuarioId2") Long usuarioId2);
}