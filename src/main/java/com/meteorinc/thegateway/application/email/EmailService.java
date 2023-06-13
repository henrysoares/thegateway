package com.meteorinc.thegateway.application.email;

import com.meteorinc.thegateway.domain.event.Event;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
@AllArgsConstructor(onConstructor_ = @Autowired)
public class EmailService {

    JavaMailSender javaMailSender;


    public void sendEmail(String destiny, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destiny);
        message.setSubject(subject);
        message.setText(body);
        javaMailSender.send(message);
    }

    @SneakyThrows
    @Async
    public void sendQRCode(String destiny, final String eventName, byte[] qrCode) {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        final String subject = String.format("QRCode do evento %s",eventName);

        helper.setTo(destiny);
        helper.setSubject(subject);
        helper.setText("Olá!\n O codigo do seu evento foi gerado.");

        ByteArrayResource attachment = new ByteArrayResource(qrCode);
        helper.addAttachment("event_qr_code.png", attachment, "application/png");

        javaMailSender.send(message);
    }

    @SneakyThrows
    public void sendStatistics(String destiny, final String eventName, String csv, int numberOfCheckIns, @NonNull final Event event) {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        final String subject = String.format("Estatisticas do evento %s",eventName);

    final String text =
        "Olá!\n Relatorio do evento.\n"
            + "Total de checkins: %s \n"
            + "Começo do evento: %s ";




        helper.setTo(destiny);
        helper.setSubject(subject);
        helper.setText(String.format(text, numberOfCheckIns,event.getStartsAt()));

        ByteArrayResource attachment = new ByteArrayResource(csv.getBytes());
        helper.addAttachment("statistics.csv", attachment, "application/csv");

        javaMailSender.send(message);
    }

    @SneakyThrows
    public void sendCertificate(String destiny, String subject, String body, byte[] pdf) {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(destiny);
        helper.setSubject(subject);
        helper.setText(body);

        ByteArrayResource attachment = new ByteArrayResource(pdf);
        helper.addAttachment("certificate.pdf", attachment, "application/pdf");

        javaMailSender.send(message);
    }

}
