package edu.tasklynx.tasklynxjavafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

public class TaskLynxApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TaskLynxApplication.class.getResource("tasklynx-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("TaskLynx");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}