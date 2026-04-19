package me.ronygomes.userManagement.api;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import me.ronygomes.userManagement.repository.mongodb.MongoUserRepository;
import me.ronygomes.userManagement.service.EmailService;
import me.ronygomes.userManagement.service.SimpleEmailServiceImpl;
import me.ronygomes.userManagement.service.UserService;
import me.ronygomes.userManagement.service.UserServiceImpl;
import me.ronygomes.userManagement.service.validator.*;

import java.util.List;
import java.util.Objects;

public class ConfigurationManager {

    private static final String DEFAULT_MONGO_URL = "mongodb://admin:admin@localhost:27017";
    private static final String DATABASE_NAME = "aihackathon_user_management";

    private static final String DEFAULT_SMTP_HOST = "sandbox.smtp.mailtrap.io";

    // Replace credential of free test sandbox from mailtrap.io
    private static final String DEFAULT_SMTP_USER = "";
    private static final String DEFAULT_SMTP_PASSWORD = "";

    private static final String MONGO_URL = System.getProperty("mongodb.uri", DEFAULT_MONGO_URL);

    private static final String SMTP_HOST = System.getProperty("smtp.host", DEFAULT_SMTP_HOST);
    private static final int SMTP_PORT = Integer.getInteger("smtp.port", 25);
    private static final String SMTP_USER = System.getProperty("smtp.username", DEFAULT_SMTP_USER);
    private static final String SMTP_PASS = System.getProperty("smtp.password", DEFAULT_SMTP_PASSWORD);
    private static final String SMTP_FROM_EMAIL = System.getProperty("smtp.from", "noreply@example.com");

    private final MongoClient mongoClient;
    private final UserService userService;

    public ConfigurationManager() {

        this.mongoClient = MongoClients.create(MONGO_URL);

        var userRepository = new MongoUserRepository(mongoClient, DATABASE_NAME);
        var validators = List.of(
                new EmailUniquenessValidator(userRepository),
                new PhoneUniquenessValidator(userRepository),
                new PasswordPolicyValidator(),
                new AgePolicyValidator(13),
                new NameValidator());

        var validator = createValidator();
        var emailService = createEmailService();

        this.userService = new UserServiceImpl(userRepository, validators, validator, emailService);
    }

    public void close() {
        if (Objects.nonNull(mongoClient)) {
            mongoClient.close();
        }
    }

    public UserService getUserService() {
        return userService;
    }

    private Validator createValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        return factory.getValidator();
    }

    private EmailService createEmailService() {
        return new SimpleEmailServiceImpl(SMTP_HOST, SMTP_PORT, SMTP_USER, SMTP_PASS, SMTP_FROM_EMAIL);
    }
}
