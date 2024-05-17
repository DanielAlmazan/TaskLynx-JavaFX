package edu.tasklynx.tasklynxjavafx.utils;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.text.Normalizer;
import java.util.regex.Pattern;

public class Utils {
    public static String removeInvalidCharacters(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("");
    }

    public static Stage showModal(FXMLLoader view, Stage stage) {
        Scene mainScene = null;
        Stage secondaryStage = new Stage();

        try {
            mainScene = new Scene(view.load());
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

        if (mainScene != null) {
            secondaryStage = new Stage();
            secondaryStage.setScene(mainScene);
            secondaryStage.initModality(Modality.WINDOW_MODAL);
            secondaryStage.initOwner(stage);
        }

        return secondaryStage;
    }

    public static Alert showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        return alert;
    }
}
