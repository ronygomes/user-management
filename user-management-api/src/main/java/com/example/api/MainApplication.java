package com.example.api;

import com.example.repository.mongodb.MongoUserRepository;
import com.example.service.UserService;
import com.example.service.UserServiceImpl;
import com.mongodb.client.MongoClients;

import static spark.Spark.*;

public class MainApplication {
    public static void main(String[] args) {
        // Simple manual DI and configuration
        String mongoUri = System.getProperty("mongodb.uri", "mongodb://admin:admin@localhost:27017");
        String dbName = "user_management";

        var mongoClient = MongoClients.create(mongoUri);
        var userRepository = new MongoUserRepository(mongoClient, dbName);
        UserService userService = new UserServiceImpl(userRepository);

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
