package me.ronygomes.userManagement.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import me.ronygomes.userManagement.common.dto.UserRegistrationDto;
import me.ronygomes.userManagement.common.dto.UserResponseDto;
import me.ronygomes.userManagement.common.dto.UserUpdateDto;
import me.ronygomes.userManagement.service.UserService;
import spark.Request;
import spark.Response;

public class UserController {

    private static final String ERROR_JSON_FORMAT = """
            { "error": "%s" }
            """;

    private final UserService userService;
    private final ObjectMapper objectMapper;

    public UserController(UserService userService) {
        this(userService, new ObjectMapper()
                .registerModule(new JavaTimeModule()));
    }

    public UserController(UserService userService, ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    public String registerUser(Request req, Response res) {
        try {
            UserRegistrationDto registrationDto = objectMapper.readValue(req.body(), UserRegistrationDto.class);
            UserResponseDto response = userService.registerUser(registrationDto);

            setJsonResponseStatus(res, 201);

            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            return processJsonProcessingException(res, e.getMessage());
        }
    }

    public String getUser(Request req, Response res) {
        try {
            String id = req.params(":id");

            UserResponseDto response = userService.findUser(id);
            setJsonResponseStatus(res, 200);

            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            return processJsonProcessingException(res, e.getMessage());
        }
    }

    public String updateUser(Request req, Response res) {
        try {
            String id = req.params(":id");

            UserUpdateDto updateDto = objectMapper.readValue(req.body(), UserUpdateDto.class);
            UserResponseDto response = userService.updateUser(id, updateDto);
            setJsonResponseStatus(res, 201);

            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            return processJsonProcessingException(res, e.getOriginalMessage());
        }
    }

    public String processJsonProcessingException(Response res, String message) {
        setJsonResponseStatus(res, 400);

        return String.format(ERROR_JSON_FORMAT,
                message.replace("\"", "\\\""));
    }

    public void validationExceptionHandler(RuntimeException e, Request req, Response res) {
        setJsonResponseStatus(res, 400);
        res.body(String.format(ERROR_JSON_FORMAT,
                e.getMessage().replace("\"", "\\\"")));
    }

    private void setJsonResponseStatus(Response res, int statusCode) {
        res.status(statusCode);
        res.type("application/json");
    }
}
