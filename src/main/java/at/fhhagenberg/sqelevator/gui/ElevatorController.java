package at.fhhagenberg.sqelevator.gui;

import at.fhhagenberg.sqelevator.communication.ElevatorChangeListener;
import at.fhhagenberg.sqelevator.model.Elevator;
import at.fhhagenberg.sqelevator.model.ElevatorSystem;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class ElevatorController implements Initializable, ElevatorChangeListener {

    public static final int PREF_HEIGHT = 30;
    @FXML public Text elevatorName;
    @FXML public Text speed;
    @FXML public Text weight;
    @FXML public Text doorStatus;

    @FXML public Pane gridContainer;

    private boolean firstExecution = true;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @Override
    public void update(ElevatorSystem system) {
        Elevator elevator = system.getElevators().get(0);
        if(elevator!=null) {
            elevatorName.setText("Elevator " + elevator.getId() + " " + elevator.getCommittedDirection().getPrintValue());
            speed.setText(elevator.getSpeed() + " ft/s " + elevator.getAcceleration() + "ft/s²");
            weight.setText(elevator.getWeight() + " kg");
            doorStatus.setText(elevator.getDoorStatus().getPrintValue());
        }
        if(firstExecution) {
            generateElevatorGrid(system);
            firstExecution = false;
        }
    }

    private void generateElevatorGrid(ElevatorSystem elevatorSystem) {
        GridPane gridPane = new GridPane();
        gridPane.setGridLinesVisible(true);

        for(int i = 0; i < elevatorSystem.getFloorCount(); i++) {
            Text levelText = new Text();
            levelText.setText(String.valueOf(i+1));
            gridPane.add(levelText, 0, i);

            Pane placeholder = new Pane();
            placeholder.setPrefWidth(PREF_HEIGHT);
            placeholder.setPrefHeight(PREF_HEIGHT);
            gridPane.add(placeholder, 1, i);
        }

        gridContainer.getChildren().add(gridPane);
    }
}
