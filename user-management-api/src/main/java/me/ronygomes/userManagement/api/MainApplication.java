package me.ronygomes.userManagement.api;

import me.ronygomes.userManagement.repository.mongodb.MongoUserRepository;
import me.ronygomes.userManagement.service.SimpleEmailServiceImpl;
import me.ronygomes.userManagement.service.UserService;
import me.ronygomes.userManagement.service.UserServiceImpl;
import com.mongodb.client.MongoClients;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import me.ronygomes.userManagement.service.validator.*;

import java.util.List;

import static spark.Spark.port;
import static spark.Spark.stop;

public class MainApplication {
    public static void main(String[] args) {
        // Simple manual DI and configuration
        String mongoUri = System.getProperty("mongodb.uri", "mongodb://admin:admin@localhost:27017");
        String dbName = "aihackathon_user_management";

        var mongoClient = MongoClients.create(mongoUri);
        var userRepository = new MongoUserRepository(mongoClient, dbName);

        // Initialize Bean Validation
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        // Initialize Email Service
        String smtpHost = System.getProperty("smtp.host", "sandbox.smtp.mailtrap.io");
        int smtpPort = Integer.getInteger("smtp.port", 25);
        String smtpUser = System.getProperty("smtp.username", "8ba1d650eb9fb3");
        String smtpPass = System.getProperty("smtp.password", "ee9e0484aeef9a");
        String fromEmail = System.getProperty("smtp.from", "noreply@example.com");

        var emailService = new SimpleEmailServiceImpl(smtpHost, smtpPort, smtpUser, smtpPass, fromEmail);

        // Initialize Validators
        var validators = List.of(
                new EmailUniquenessValidator(userRepository),
                new PhoneUniquenessValidator(userRepository),
                new PasswordPolicyValidator(),
                new AgePolicyValidator(13),
                new NameValidator());

        UserService userService = new UserServiceImpl(userRepository, validators, validator, emailService);

        port(8080);

        new UserController(userService);

        System.out.println("User Management API started on port 8080");

        // Ensure graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            stop();
            mongoClient.close();
            System.out.println("Application stopped");
        }));
    }
}
