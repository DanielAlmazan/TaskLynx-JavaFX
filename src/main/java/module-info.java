module edu.tasklynx.tasklynxjavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.desktop;

    requires jdk.httpserver;
    requires com.google.api.client;
    requires com.google.api.client.auth;
    requires com.google.api.services.gmail;
    requires google.api.client;
    requires jakarta.mail;
    requires com.google.api.client.json.gson;
    requires com.google.api.client.extensions.jetty.auth;
    requires com.google.api.client.extensions.java6.auth;
    requires jdk.jshell;
    requires kernel;
    requires layout;

    opens edu.tasklynx.tasklynxjavafx to javafx.fxml;
    exports edu.tasklynx.tasklynxjavafx;
    exports edu.tasklynx.tasklynxjavafx.controllers;
    exports edu.tasklynx.tasklynxjavafx.utils to javafx.fxml;
    exports edu.tasklynx.tasklynxjavafx.model to javafx.fxml;
    opens edu.tasklynx.tasklynxjavafx.controllers to javafx.fxml;
    opens edu.tasklynx.tasklynxjavafx.controllers.modalsControllers to javafx.fxml;
    opens edu.tasklynx.tasklynxjavafx.model;
    opens edu.tasklynx.tasklynxjavafx.model.responses;
    opens edu.tasklynx.tasklynxjavafx.utils;
}