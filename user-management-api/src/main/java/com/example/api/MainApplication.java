package com.example.api;

import com.example.repository.mongodb.MongoUserRepository;
import com.example.service.UserService;
import com.example.service.UserServiceImpl;
import com.example.service.validator.*;
import com.mongodb.client.MongoClients;

import java.util.List;

import static spark.Spark.*;

public class MainApplication {
    public static void main(String[] args) {
        // Simple manual DI and configuration
        String mongoUri = System.getProperty("mongodb.uri", "mongodb://admin:admin@localhost:27017");
        String dbName = "user_management";

        var mongoClient = MongoClients.create(mongoUri);
        var userRepository = new MongoUserRepository(mongoClient, dbName);

        // Initialize Validators
        var validators = List.of(
                new EmailUniquenessValidator(userRepository),
                new PhoneUniquenessValidator(userRepository),
                new PasswordPolicyValidator(),
                new AgePolicyValidator(13),
                new NameValidator());

        UserService userService = new UserServiceImpl(userRepository, validators);

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
