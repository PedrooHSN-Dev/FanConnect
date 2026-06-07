package br.com.fanconnect.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarCodigoRecuperacao(String destinatario, String codigo) {
        try {
            MimeMessage mensagem = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensagem, "utf-8");

            helper.setTo(destinatario);
            helper.setFrom("${FANCONNECT_EMAIL_USER}", "Fan Connect");
            helper.setSubject("FanConnect - Seu código de recuperação");
            String corpo = "Olá,\n\n"
                    + "Recebemos uma solicitação de redefinição de senha para a sua conta.\n"
                    + "O seu código de recuperação é: " + codigo + "\n\n"
                    + "Este código é válido por apenas 15 minutos.\n"
                    + "Se você não solicitou esta alteração, por favor, ignore este e-mail.\n\n"
                    + "Atenciosamente,\nEquipe FanConnect";

            helper.setText(corpo);

            mailSender.send(mensagem);
        } catch (Exception e) {
            System.out.println("Erro ao enviar e-mail: " + e.getMessage());
        }
    }

    public void enviarCodigoAtivacao(String destinatario, String codigo) {
        try {
            MimeMessage mensagem = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensagem, "utf-8");

            helper.setFrom("${FANCONNECT_EMAIL_USER}", "Fan Connect");
            helper.setTo(destinatario);
            helper.setSubject("FanConnect - Bem-vindo! Ative a sua conta");

            String corpo = "Olá,\n\n"
                    + "Bem-vindo ao FanConnect! Para ativar a sua conta e libertar o seu acesso à plataforma, utilize o código abaixo:\n\n"
                    + "Código de Ativação: " + codigo + "\n\n"
                    + "Este código é válido por 24 horas.\n"
                    + "Se você não se registou na nossa plataforma, por favor, ignore este e-mail.\n\n"
                    + "Atenciosamente,\nEquipe FanConnect";

            helper.setText(corpo);

            mailSender.send(mensagem);
        } catch (Exception e) {
            System.out.println("Erro ao enviar e-mail de ativação: " + e.getMessage());
        }
    }

    public void enviarEmailLembrete(String destinatario, String nomeUsuario, String tituloEvento, String dataHoraFormatada, String local) {
        try {
            MimeMessage mensagem = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensagem, "utf-8");

            helper.setFrom("fanconnectlembrete@gmail.com", "Fan Connect");
            helper.setTo(destinatario);
            helper.setSubject(" Lembrete de Evento: " + tituloEvento);

            String corpo = "Olá, " + nomeUsuario + "!\n\n"
                    + "Este é um lembrete automático do FanConnect para o seu evento agendado:\n\n"
                    + " Evento: " + tituloEvento + "\n"
                    + " Data/Hora: " + dataHoraFormatada + "\n"
                    + " Local: " + (local != null ? local : "Não informado") + "\n\n"
                    + "Não se atrase!\n\n"
                    + "Atenciosamente,\nEquipe FanConnect";

            helper.setText(corpo);
            mailSender.send(mensagem);
        } catch (Exception e) {
            System.out.println("Erro ao enviar e-mail de lembrete: " + e.getMessage());
        }
    }
}