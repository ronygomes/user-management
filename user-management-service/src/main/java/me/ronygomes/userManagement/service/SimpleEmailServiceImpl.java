package me.ronygomes.userManagement.service;

import me.ronygomes.userManagement.common.model.User;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

public class SimpleEmailServiceImpl implements EmailService {

    private static final String EMAIL_FROM_NAME = "User Management System";
    private static final String EMAIL_BODY_SUBJECT = "Welcome to User Management System!";

    private static final String EMAIL_BODY_TEMPLATE = """
            Hello %s,
            
            Welcome to our platform! Your account has been successfully registered.
            """;

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
                .from(EMAIL_FROM_NAME, fromEmail)
                .to(user.getFirstName() + " " + user.getLastName(), user.getEmail())
                .withSubject(EMAIL_BODY_SUBJECT)
                .withPlainText(EMAIL_BODY_TEMPLATE.formatted(user.getFirstName()))
                .buildEmail();

        mailer.sendMail(email);
    }
}
