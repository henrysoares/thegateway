package com.meteorinc.thegateway.application.email;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
