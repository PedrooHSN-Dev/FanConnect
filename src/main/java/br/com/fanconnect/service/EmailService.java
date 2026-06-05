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

            helper.setFrom("fanconnectlembrete@gmail.com", "Fan Connect");
            helper.setTo(destinatario);
            helper.setSubject("FanConnect - Seu código de recuperação");

            // Opcional: Você poderia colocar HTML aqui!
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
}