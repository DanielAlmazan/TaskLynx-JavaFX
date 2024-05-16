module edu.tasklynx.tasklynxjavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.desktop;
    requires jdk.jshell;

    opens edu.tasklynx.tasklynxjavafx to javafx.fxml;
    exports edu.tasklynx.tasklynxjavafx;
    exports edu.tasklynx.tasklynxjavafx.controllers;
    opens edu.tasklynx.tasklynxjavafx.controllers to javafx.fxml;
    opens edu.tasklynx.tasklynxjavafx.controllers.modalsControllers to javafx.fxml;
    opens edu.tasklynx.tasklynxjavafx.model;
    opens edu.tasklynx.tasklynxjavafx.model.responses;
    opens edu.tasklynx.tasklynxjavafx.utils;
}