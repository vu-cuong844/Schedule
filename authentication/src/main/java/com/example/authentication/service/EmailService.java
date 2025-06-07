package com.example.authentication.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.authentication.dto.MessageMail;
import com.example.authentication.dto.SendEmailResponse;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String SENDER_EMAIL = "cuongdaynemano@gmail.com";

    public EmailService(JavaMailSender javaMailSender) {
        this.mailSender = javaMailSender;
    }

    public SendEmailResponse sendTextEmail(MessageMail messageMail) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(SENDER_EMAIL);
            message.setTo(messageMail.getTo());
            message.setSubject(messageMail.getSubject());
            message.setText(messageMail.getContent());

            mailSender.send(message);

            return SendEmailResponse.builder()
                    .isSended(true)
                    .message("Mail send")
                    .build();

        } catch (Exception e) {
            return SendEmailResponse.builder()
                    .isSended(false)
                    .message("Error: " + e.getMessage())
                    .build();
        }
    }
}
