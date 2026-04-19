package me.ronygomes.userManagement.api;

import me.ronygomes.userManagement.common.exception.ValidationException;

import static spark.Spark.*;

public class MainApplication {

    private static final int SERVER_PORT = 8080;

    static void main() {
        port(SERVER_PORT);

        var manager = new ConfigurationManager();
        setupRoutes(manager);

        System.out.println("User Management API started on port " + SERVER_PORT);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            manager.close();
            stop();
            System.out.println("Application stopped");
        }));
    }

    private static void setupRoutes(ConfigurationManager m) {
        var controller = new UserController(m.getUserService());

        post("/register", controller::registerUser);
        get("/users/:id", controller::getUser);
        put("/users/:id", controller::updateUser);

        exception(ValidationException.class, controller::validationExceptionHandler);
    }
}
