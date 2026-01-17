package com.example.api;

import com.example.common.dto.UserRegistrationDTO;
import com.example.common.dto.UserResponseDTO;
import com.example.common.dto.UserUpdateDTO;
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
        this.objectMapper.registerModule(new JavaTimeModule()); // For LocalDate
        setupRoutes();
    }

    private void setupRoutes() {
        get("/hello", (req, res) -> "Hello World");
        post("/register", this::registerUser);
        put("/users/:id", this::updateUser);
    }

    private String registerUser(Request req, Response res) {
        try {
            UserRegistrationDTO registrationDTO = objectMapper.readValue(req.body(), UserRegistrationDTO.class);
            UserResponseDTO response = userService.registerUser(registrationDTO);
            res.status(201);
            res.type("application/json");
            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            res.status(400);
            res.type("application/json");
            return "{\"error\": \"" + e.getMessage().replace("\"", "\\\"") + "\"}";
        }
    }

    private String updateUser(Request req, Response res) {
        try {
            String id = req.params(":id");
            UserUpdateDTO updateDTO = objectMapper.readValue(req.body(), UserUpdateDTO.class);
            UserResponseDTO response = userService.updateUser(id, updateDTO);
            res.status(200);
            res.type("application/json");
            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            res.status(400);
            res.type("application/json");
            return "{\"error\": \"" + e.getMessage().replace("\"", "\\\"") + "\"}";
        }
    }
}
