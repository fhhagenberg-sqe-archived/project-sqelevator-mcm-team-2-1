package at.fhhagenberg.sqelevator.gui;

import at.fhhagenberg.sqelevator.communication.ElevatorChangeListener;
import at.fhhagenberg.sqelevator.communication.UIActionListener;
import at.fhhagenberg.sqelevator.model.Elevator;
import at.fhhagenberg.sqelevator.model.ElevatorSystem;
import at.fhhagenberg.sqelevator.model.states.CommittedDirection;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import lombok.Getter;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class ElevatorController implements Initializable, ElevatorChangeListener {

    public static final int GRID_SIZE = 30;
    public static final String PRESSED_BACKGROUND = "#d9ff00";

    @FXML public Text elevatorName;
    @FXML public Text elevatorDirection;
    @FXML public Text speed;
    @FXML public Text acceleration;
    @FXML public Text weight;
    @FXML public Text doorStatus;
    @FXML public TextField manualInput;

    @FXML public Pane gridContainer;
    @FXML public Pane elevatorBtnsContainer;
    @FXML public ToggleButton enableAuto;
    @FXML public Button manualSend;

    private ImageView elevatorImage = null;

    @Getter
    private UIActionListener uiActionListener;
    private int maxFloor;

    @Getter
    private Elevator elevator;

    public void setUiActionListener(UIActionListener uiActionListener) {
        this.uiActionListener = uiActionListener;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadImage(); //TODO: Move to some singleton
    }

    public void changeAutoMode(){

        Boolean autoOn = enableAuto.isSelected();
        manualInput.setDisable(autoOn);
        manualSend.setDisable(autoOn);

        elevator.setAutomaticModeActive(autoOn);
        uiActionListener.setAutoMode(elevator.getId(), autoOn);

        System.out.println("Automatic mode: " + autoOn.toString());
        /*
        if(enableAuto.isSelected()){
            manualInput.setDisable(true);
            manualSend.setDisable(true);
        }else{
            manualInput.setDisable(false);
            manualSend.setDisable(false);
        }
        elevator.setAutomaticModeActive(enableAuto.isSelected());

         */
    }

    @FXML
    public void sendRequest() {

        //if(!enableAuto.isSelected()) {
        if(!elevator.isAutomaticModeActive()){
            String text = manualInput.getText();
            // text must be a positive integer and not be empty
            if (!text.isEmpty() && text.matches("^[1-9]\\d*$") && uiActionListener != null) {
                Integer selectedFloor = Integer.parseInt(text) - 1;
                if (selectedFloor < maxFloor) {
                    uiActionListener.floorSelected(elevator.getId(), selectedFloor);
                    if (selectedFloor < elevator.getFloor())
                        uiActionListener.changeCommittedDirection(elevator.getId(), CommittedDirection.DOWN);
                    else if (selectedFloor > elevator.getFloor())
                        uiActionListener.changeCommittedDirection(elevator.getId(), CommittedDirection.UP);
                }
            }
        }
    }


    @Override
    public void update(ElevatorSystem system, int elevatorId) {
        //TODO: better threading!
        Platform.runLater(() -> {
            if(system!=null && system.getElevators().get(elevatorId)!=null) {
                this.elevator = system.getElevators().get(elevatorId);
                maxFloor = system.getFloorCount();
                elevatorName.setText("Elevator " + (elevator.getId()+1));
                elevatorDirection.setText(elevator.getCommittedDirection().getPrintValue());
                speed.setText(elevator.getSpeed() + " ft/s");
                acceleration.setText("Max: " + elevator.getAcceleration() + "ft/s²");
                weight.setText(elevator.getWeight() + " kg");
                doorStatus.setText(elevator.getDoorStatus().getPrintValue());
                updateElevatorGrid(system); // TODO: Only redraw things that change
                updateElevatorBtnsGrid();
            }
        });
    }

    private void updateElevatorGrid(ElevatorSystem elevatorSystem) {
        GridPane gridPane = new GridPane();
        gridPane.setGridLinesVisible(true); //TODO: Use css instead, as this is reserved for debugging purposes

        int currentFloor = elevator.getFloor();

        for(int i = elevatorSystem.getFloorCount()-1; i >=0; i--) {
            Text levelText = new Text();

            if(elevatorSystem.getFloorButtons().get(i)!=null) {
                levelText.setText(String.valueOf(i+1) + "   " + elevatorSystem.getFloorButtons().get(i).getPrintValue());
            }

            gridPane.add(levelText, 0, elevatorSystem.getFloorCount()-1-i);

            StackPane placeholder = new StackPane();
            placeholder.setPrefWidth(GRID_SIZE);
            placeholder.setPrefHeight(GRID_SIZE);

            if(i == currentFloor) {
                placeholder.getChildren().add(elevatorImage);
            }

            gridPane.add(placeholder, 1, elevatorSystem.getFloorCount()-1-i);
        }

        gridContainer.getChildren().clear();
        gridContainer.getChildren().add(gridPane);
    }

    private void updateElevatorBtnsGrid() {
        GridPane gridPane = new GridPane();
        gridPane.setGridLinesVisible(true); //TODO: Use css instead, as this is reserved for debugging purposes

        HashMap<Integer, Boolean> buttons = elevator.getButtons();
        for(int i = buttons.size()-1; i >= 0; i--) {
            boolean pressed = buttons.get(i);

            StackPane placeholder = new StackPane();
            placeholder.setPrefHeight(GRID_SIZE);
            placeholder.setPrefWidth(GRID_SIZE);
            if(pressed) placeholder.setStyle("-fx-background-color: " + PRESSED_BACKGROUND);

            Text levelText = new Text(Integer.toString(i+1));
            placeholder.getChildren().add(levelText);

            gridPane.add(placeholder, 0, buttons.size()-1-i);
        }

        elevatorBtnsContainer.getChildren().clear();
        elevatorBtnsContainer.getChildren().add(gridPane);
    }

    private void loadImage() {
        try {
            Image image = new Image((getClass().getClassLoader().getResource("icons8-elevator-64.png").openStream()));
            elevatorImage = new ImageView(image);
            elevatorImage.setFitHeight(28);
            elevatorImage.setFitWidth(28);
        } catch (FileNotFoundException e) {
            LoggerFactory.getLogger(ElevatorController.class).error("File not found", e);
        } catch (IOException e) {
            LoggerFactory.getLogger(ElevatorController.class).error("Image could not be loaded", e);
        }
    }
}
