package at.fhhagenberg.sqelevator.gui;

import at.fhhagenberg.sqelevator.communication.ElevatorChangeListener;
import at.fhhagenberg.sqelevator.model.Elevator;
import at.fhhagenberg.sqelevator.model.ElevatorSystem;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ElevatorController implements Initializable, ElevatorChangeListener {

    public static final int PREF_HEIGHT = 30;
    @FXML public Text elevatorName;
    @FXML public Text elevatorDirection;
    @FXML public Text speed;
    @FXML public Text weight;
    @FXML public Text doorStatus;
    @FXML public TextField manualInput;

    @FXML public Pane gridContainer;

    private boolean firstExecution = true;
    private ImageView elevatorImage = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadImage(); //TODO: Move to some singleton
    }

    @FXML
    public void sendRequest() {
        String text = manualInput.getText();
        // text must be a positive integer and not be empty
        if(!text.isEmpty() && text.matches("^[1-9]\\d*$")) {
            System.out.println(text);
        }
    }

    @Override
    public void update(ElevatorSystem system) {
        //TODO: better threading!
        Platform.runLater(() -> {
            Elevator elevator = system.getElevators().get(0);
            if(elevator!=null) {
                elevatorName.setText("Elevator " + elevator.getId());
                elevatorDirection.setText(elevator.getCommittedDirection().getPrintValue());
                speed.setText(elevator.getSpeed() + " ft/s " + elevator.getAcceleration() + "ft/s²");
                weight.setText(elevator.getWeight() + " kg");
                doorStatus.setText(elevator.getDoorStatus().getPrintValue());
                generateElevatorGrid(system); // TODO: Only redraw things that change
            }
        });

    }

    private void generateElevatorGrid(ElevatorSystem elevatorSystem) {
        GridPane gridPane = new GridPane();
        gridPane.setGridLinesVisible(true); //TODO: Use css instead, as this is reserved for debugging purposes

        int currentFloor = elevatorSystem.getElevators().get(0).getFloor();

        for(int i = 0; i < elevatorSystem.getFloorCount(); i++) {
            Text levelText = new Text();
            levelText.setText(String.valueOf(i+1) + "   " + elevatorSystem.getFloorButtons().get(i).getPrintValue());
            gridPane.add(levelText, 0, i);

            StackPane placeholder = new StackPane();
            placeholder.setPrefWidth(PREF_HEIGHT);
            placeholder.setPrefHeight(PREF_HEIGHT);

            if(i == currentFloor) {
                placeholder.getChildren().add(new Text("here"));
                //placeholder.getChildren().add(elevatorImage);
            }

            gridPane.add(placeholder, 1, i);
        }

        gridContainer.getChildren().clear();
        gridContainer.getChildren().add(gridPane);
    }

    private void loadImage() {
        try {
            Image image = new Image((getClass().getClassLoader().getResource("icons8-elevator-64.png").openStream()));
            elevatorImage = new ImageView(image);
        } catch (FileNotFoundException e) {
            System.err.println("Image not found");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
