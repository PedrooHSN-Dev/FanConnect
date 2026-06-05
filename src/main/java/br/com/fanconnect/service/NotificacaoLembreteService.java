package br.com.fanconnect.service;

import br.com.fanconnect.entity.ItemAgenda;
import br.com.fanconnect.repository.ItemAgendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import java.io.UnsupportedEncodingException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class NotificacaoLembreteService {

    @Autowired
    private ItemAgendaRepository agendaRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Transactional
    @Scheduled(fixedRate = 60000)
    public void verificarLembretesPendentes() {

        List<ItemAgenda> eventosPendentes = agendaRepository.findByLembreteAtivoTrueAndLembreteEnviadoFalse();
        LocalDateTime agora = LocalDateTime.now();

        for (ItemAgenda evento : eventosPendentes) {
            if (evento.getDataHora() == null || evento.getMinutosAvisoLembrete() == null) continue;

            long minutosFaltantes = ChronoUnit.MINUTES.between(agora, evento.getDataHora());

            if (minutosFaltantes >= 0 && minutosFaltantes <= evento.getMinutosAvisoLembrete()) {

                // Log
                System.out.println("Enviando e-mail de lembrete para: " + evento.getDono().getEmail());

                try {
                    MimeMessage mensagem = mailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(mensagem, "utf-8");

                    helper.setFrom("fanconnectlembrete@gmail.com", "Fan Connect");

                    helper.setTo(evento.getDono().getEmail());
                    helper.setSubject("Lembrete: " + evento.getTitulo());

                    String corpoTexto = "Olá " + evento.getDono().getNome() + ",\n\n"
                            + "Este é um lembrete automático da sua Agenda FanConnect!\n"
                            + "O seu compromisso '" + evento.getTitulo() + "' vai começar em " + minutosFaltantes + " minutos.\n"
                            + "Localização: " + evento.getLocalizacao() + "\n\n"
                            + "Bons estudos,\nEquipe FanConnect";

                    helper.setText(corpoTexto);

                    mailSender.send(mensagem);

                    evento.setLembreteEnviado(true);
                    agendaRepository.save(evento);

                } catch (MessagingException | UnsupportedEncodingException e) {
                    System.out.println("Erro ao enviar e-mail: " + e.getMessage());
                }
            }
        }
    }
}