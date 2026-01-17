package com.example.api;

import com.example.common.model.User;
import com.example.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import spark.Request;
import spark.Response;

import static spark.Spark.*;

public class UserController {
    private final UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UserController(UserService userService) {
        this.userService = userService;
        this.objectMapper.registerModule(new JavaTimeModule());
        setupRoutes();
    }

    private void setupRoutes() {
        get("/hello", (req, res) -> "Hello World");
        post("/register", this::registerUser);
        put("/users/:id", this::updateUser);
    }

    private String registerUser(Request req, Response res) {
        try {
            User user = objectMapper.readValue(req.body(), User.class);
            userService.registerUser(user);
            res.status(201);
            return objectMapper.writeValueAsString(user);
        } catch (Exception e) {
            res.status(400);
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    private String updateUser(Request req, Response res) {
        try {
            String id = req.params(":id");
            User user = objectMapper.readValue(req.body(), User.class);
            userService.updateUser(id, user);
            res.status(200);
            return "{\"message\": \"User updated successfully\"}";
        } catch (Exception e) {
            res.status(400);
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }
}
