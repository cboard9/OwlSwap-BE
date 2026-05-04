package com.cboard.owlswap.owlswap_backend.service;

import com.cboard.owlswap.owlswap_backend.exception.EmailDeliveryException;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService
{

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String toEmail, String verifyUrl) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Verify your OwlSwap account");
            message.setText(
                    "Welcome to OwlSwap!\n\n" +
                            "Please verify your email by clicking the link below:\n\n" +
                            verifyUrl + "\n\n" +
                            "If you did not create an account, you can ignore this email."
            );

            mailSender.send(message);
        }catch (MailException e)
        {
            throw new EmailDeliveryException("Failed to send verification email.", e);
        }
    }
}
