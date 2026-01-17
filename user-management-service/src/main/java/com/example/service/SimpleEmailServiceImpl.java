package com.example.service;

import com.example.common.model.User;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

public class SimpleEmailServiceImpl implements EmailService {

    private final Mailer mailer;
    private final String fromEmail;

    public SimpleEmailServiceImpl(String host, int port, String username, String password, String fromEmail) {
        this.fromEmail = fromEmail;
        this.mailer = MailerBuilder
                .withSMTPServer(host, port, username, password)
                .withTransportStrategy(TransportStrategy.SMTP_TLS)
                .buildMailer();
    }

    @Override
    public void sendWelcomeEmail(User user) {
        Email email = EmailBuilder.startingBlank()
                .from("User Management", fromEmail)
                .to(user.getFirstName() + " " + user.getLastName(), user.getEmail())
                .withSubject("Welcome to User Management System!")
                .withPlainText("Hello " + user.getFirstName()
                        + ",\n\nWelcome to our platform! Your account has been successfully registered.")
                .buildEmail();

        mailer.sendMail(email);
    }
}
