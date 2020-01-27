package at.fhhagenberg.sqelevator.gui;

import at.fhhagenberg.sqelevator.communication.ElevatorSystemChangeListener;
import at.fhhagenberg.sqelevator.communication.UIActionListener;
import at.fhhagenberg.sqelevator.model.ElevatorSystem;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.FlowPane;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class DashboardController implements ElevatorSystemChangeListener {
    @FXML
    public FlowPane dashBoard;
    private boolean initialized = false;

    private HashMap<Integer, ElevatorController> elevators = new HashMap<>();

    private UIActionListener uiListener;

    @Override
    public void update(ElevatorSystem system) {
        elevators.keySet().forEach(id -> elevators.get(id).update(system, id));
        if (!initialized) {
            this.initPane(system);
            initialized = true;
        }
    }

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

    public void setUiListener(UIActionListener uiListener) {
        this.uiListener = uiListener;
    }
}
