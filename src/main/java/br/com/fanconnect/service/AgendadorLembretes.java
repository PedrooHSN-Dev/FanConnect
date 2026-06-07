package br.com.fanconnect.service;

import br.com.fanconnect.entity.ItemAgenda;
import br.com.fanconnect.repository.ItemAgendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class AgendadorLembretes {

    @Autowired
    private ItemAgendaRepository agendaRepository;

    @Autowired
    private EmailService emailService;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void verificarEEnviarLembretes() {
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime limiteFuturo = agora.plusMinutes(60);

        List<ItemAgenda> eventosProximos = agendaRepository
                .findByLembreteAtivoTrueAndLembreteEnviadoFalseAndDataHoraBetween(agora, limiteFuturo);

        if (!eventosProximos.isEmpty()) {
            System.out.println("[SCHEDULER] Encontrados " + eventosProximos.size() + " eventos próximos para notificar.");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (ItemAgenda evento : eventosProximos) {
                if (evento.getDono() != null && evento.getDono().getEmail() != null) {

                    String dataFormatada = evento.getDataHora().format(formatter);

                    emailService.enviarEmailLembrete(
                            evento.getDono().getEmail(),
                            evento.getDono().getNome(),
                            evento.getTitulo(),
                            dataFormatada,
                            evento.getLocalizacao()
                    );

                    evento.setLembreteEnviado(true);
                    agendaRepository.save(evento);

                    System.out.println("[SCHEDULER] Lembrete enviado com sucesso para: " + evento.getDono().getEmail());
                }
            }
        }
    }
}