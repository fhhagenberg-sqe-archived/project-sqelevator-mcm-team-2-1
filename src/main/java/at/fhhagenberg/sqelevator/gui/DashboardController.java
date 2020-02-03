package at.fhhagenberg.sqelevator.gui;

import at.fhhagenberg.sqelevator.communication.ElevatorSystemChangeListener;
import at.fhhagenberg.sqelevator.communication.UIActionListener;
import at.fhhagenberg.sqelevator.model.ElevatorSystem;
import at.fhhagenberg.sqelevator.statemanagement.ElevatorManagement;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.FlowPane;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Acts as an JavaFX controller for the dashboard. The dashboard is the container embedding the representation of each
 * elevator. Thus, it expects to get updated with new elevator system data by its implementation of a ElevatorSystemChangeListener.
 *
 * @see ElevatorSystemChangeListener
 * @see ElevatorManagement#pollElevatorSystem()
 * @see ElevatorManagement#addListener(ElevatorSystemChangeListener)
 * @see ElevatorController
 *
 * @author Martin Schneglberger
 */
public class DashboardController implements ElevatorSystemChangeListener {
    @FXML
    public FlowPane dashBoard;
    @Getter
    private boolean initialized = false;
    @Getter
    private HashMap<Integer, ElevatorController> elevators = new HashMap<>();
    @Setter
    private UIActionListener uiListener;

    @Override
    public void update(ElevatorSystem system) {
        if (!initialized) {
            this.initPane(system);
            initialized = true;
        }
        elevators.keySet().forEach(id -> elevators.get(id).update(system, id));
    }

    /**
     * Sets up the pane by creating an ElevatorController for each elevator of the system
     *
     * @param system representation of the current elevator system
     */
    private void initPane(ElevatorSystem system) {
        ArrayList<Node> nodes = new ArrayList<>();
        try {
            int elevatorCount = system.getElevators().keySet().size();
            for (int i = 0; i < elevatorCount; i++) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("elevator.fxml"));
                Parent parent = fxmlLoader.load();
                ElevatorController controller = fxmlLoader.<ElevatorController>getController();
                controller.setUiActionListener(uiListener);
                elevators.put(i, controller);
                nodes.add(parent);
            }
            Platform.runLater(() -> dashBoard.getChildren().addAll(nodes));
        } catch (IOException exception) {
            LoggerFactory.getLogger(DashboardController.class).error("Error creating controllers", exception);
        }

    }
}
