package at.fhhagenberg.sqelevator.gui;

import at.fhhagenberg.sqelevator.communication.UIActionListener;
import at.fhhagenberg.sqelevator.mocks.RMIInstanceMock;
import at.fhhagenberg.sqelevator.model.states.CommittedDirection;
import at.fhhagenberg.sqelevator.model.states.DoorStatus;
import at.fhhagenberg.sqelevator.statemanagement.ElevatorManagement;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.robot.Robot;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import sqelevator.IElevator;

import java.rmi.RemoteException;
import java.util.concurrent.Semaphore;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DashboardElevatorGUITest extends ApplicationTest {
    private DashboardController controller;

    private IElevator rmiMock;
    private ElevatorManagement management;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("dashboard.fxml"));
        Parent mainNode = fxmlLoader.load();
        controller = Mockito.spy(fxmlLoader.<DashboardController>getController());

        stage.setScene(new Scene(mainNode));
        stage.show();
        stage.toFront();
    }

    @AfterEach
    public void tearDown() throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    @BeforeEach
    public void setUp() throws Exception {
        rmiMock = spy(new RMIInstanceMock());
        management = spy(new ElevatorManagement(rmiMock, false));
        management.addListener(controller);
    }

    /**
     * This test asserts if the initialization of the elevator system after the first update works as expected
     *
     * @throws InterruptedException
     */
    @Test
    public void testElevatorSystemDisplayElevator() throws InterruptedException {
        management.pollElevatorSystem();
        waitForRunLater();

        // Check general elevator status
        ElevatorController displayedElevator = controller.getElevators().get(0);
        assertEquals("Elevator 1", displayedElevator.elevatorName.getText().trim());
        assertEquals("10 kg", displayedElevator.weight.getText().trim());
        assertEquals("0 ft/s", displayedElevator.speed.getText().trim());
        assertEquals("Max: 10ft/s²", displayedElevator.acceleration.getText().trim());
        assertEquals(DoorStatus.OPEN.getPrintValue(), displayedElevator.doorStatus.getText().trim());
        assertEquals(CommittedDirection.UNCOMMITTED.getPrintValue(), displayedElevator.elevatorDirection.getText().trim());

        // Check elevator/floors representation
        GridPane elevatorGrid = (GridPane) displayedElevator.gridContainer.getChildren().get(0);
        // 20 cells and -1 for the Group that is the gridpane per default
        assertEquals(20, elevatorGrid.getChildren().size() - 1);
        // Check if elevator image is at the right position
        StackPane elevatorImgContainer = (StackPane) getNodeFromGridPane(elevatorGrid, 1, 9);
        assertNotNull(elevatorImgContainer.getChildren());
        assertTrue(elevatorImgContainer.getChildren().get(0) instanceof ImageView);
        // Check if floor buttons are displayed accordingly
        Text floorText = (Text) getNodeFromGridPane(elevatorGrid, 0, 0);
        assertEquals("10   ▲", floorText.getText());

        // Check elevator buttons
        GridPane elevatorBtns = (GridPane) displayedElevator.elevatorBtnsContainer.getChildren().get(0);
        assertEquals(11, elevatorBtns.getChildren().size());
        StackPane floor10 = (StackPane) getNodeFromGridPane(elevatorBtns, 0, 0);
        StackPane floor9 = (StackPane) getNodeFromGridPane(elevatorBtns, 0, 1);
        assertTrue(floor10.getStyle().isEmpty());
        assertEquals("-fx-background-color: " + ElevatorController.PRESSED_BACKGROUND, floor9.getStyle());

    }

    @Test
    public void testGUIInvalidInteractionManualMode() throws InterruptedException, RemoteException {
        controller.setUiListener(management);
        management.pollElevatorSystem();
        waitForRunLater();

        ElevatorController displayedElevator = controller.getElevators().get(0);

        displayedElevator.manualInput.setText("0");
        displayedElevator.sendRequest();
        verify(management, never()).floorSelected(Mockito.anyInt(), Mockito.anyInt());
        verify(management, never()).changeCommittedDirection(Mockito.anyInt(), any());
        verify(rmiMock, never()).setTarget(Mockito.anyInt(), Mockito.anyInt());
        verify(rmiMock, never()).setCommittedDirection(Mockito.anyInt(), anyInt());

        displayedElevator.manualInput.setText("11");
        displayedElevator.sendRequest();
        verify(management, never()).floorSelected(Mockito.anyInt(), Mockito.anyInt());
        verify(management, never()).changeCommittedDirection(Mockito.anyInt(), any());
        verify(rmiMock, never()).setTarget(Mockito.anyInt(), Mockito.anyInt());
        verify(rmiMock, never()).setCommittedDirection(Mockito.anyInt(), anyInt());
    }

    @Test
    public void testGUIValidInteractionManualMode() throws InterruptedException, RemoteException {
        controller.setUiListener(management);
        management.pollElevatorSystem();
        waitForRunLater();

        ElevatorController displayedElevator = controller.getElevators().get(0);

        displayedElevator.manualInput.setText("1");
        displayedElevator.sendRequest();
        verify(management, times(1)).floorSelected(0, 0);
        verify(management, never()).changeCommittedDirection(Mockito.anyInt(), any());
        verify(rmiMock, times(1)).setTarget(0, 0);
        verify(rmiMock, never()).setCommittedDirection(Mockito.anyInt(), anyInt());

        displayedElevator.manualInput.setText("10");
        displayedElevator.sendRequest();
        verify(management, times(1)).floorSelected(0, 9);
        verify(management, times(1)).changeCommittedDirection(0, CommittedDirection.UP);
        verify(rmiMock, times(1)).setTarget(0, 9);
        verify(rmiMock, times(1)).setCommittedDirection(0, CommittedDirection.UP.getRawValue());
    }

    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (!(node instanceof Group) && GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }

    private void waitForRunLater() throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        Platform.runLater(semaphore::release);
        semaphore.acquire();
    }

    @Test
    public void testAutoModeActivation() throws InterruptedException {
        controller.setUiListener(management);
        management.pollElevatorSystem();

        waitForRunLater();

        ElevatorController displayedElevator = controller.getElevators().get(0);
        //test one time activate and one time disactivate auto mode
        //select
        displayedElevator.enableAuto.setSelected(true);
        displayedElevator.changeAutoMode();

        //test if manual input disabled
        assertTrue(displayedElevator.manualInput.isDisabled());
        assertTrue(displayedElevator.manualSend.isDisabled());


        //deselect
        displayedElevator.enableAuto.setSelected(false);
        displayedElevator.changeAutoMode();
        //test if manual input enabled
        assertFalse(displayedElevator.manualInput.isDisabled());
        assertFalse(displayedElevator.manualSend.isDisabled());


    }
}
