package me.ronygomes.userManagement.api;

import me.ronygomes.userManagement.common.dto.UserRegistrationDto;
import me.ronygomes.userManagement.common.dto.UserResponseDto;
import me.ronygomes.userManagement.common.dto.UserUpdateDto;
import me.ronygomes.userManagement.service.UserService;
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
            UserRegistrationDto registrationDTO = objectMapper.readValue(req.body(), UserRegistrationDto.class);
            UserResponseDto response = userService.registerUser(registrationDTO);
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
            UserUpdateDto updateDTO = objectMapper.readValue(req.body(), UserUpdateDto.class);
            UserResponseDto response = userService.updateUser(id, updateDTO);
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
