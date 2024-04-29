module edu.tasklynx.tasklynxjavafx {
    requires javafx.controls;
    requires javafx.fxml;


    opens edu.tasklynx.tasklynxjavafx to javafx.fxml;
    exports edu.tasklynx.tasklynxjavafx;
}