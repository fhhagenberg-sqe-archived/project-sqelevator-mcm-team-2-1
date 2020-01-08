package at.fhhagenberg.sqelevator.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Launcher extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("elevator_control.fxml"));
        primaryStage.setTitle("Elevator Control 2000");
        primaryStage.setScene(new Scene(root, 800, 500));
        primaryStage.show();
    }
}
