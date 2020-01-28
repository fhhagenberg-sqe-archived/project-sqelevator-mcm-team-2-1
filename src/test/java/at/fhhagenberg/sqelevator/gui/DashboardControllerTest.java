package at.fhhagenberg.sqelevator.gui;

import at.fhhagenberg.sqelevator.mocks.RMIInstanceMock;
import at.fhhagenberg.sqelevator.statemanagement.ElevatorManagement;
import com.sun.tools.javac.Main;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import sqelevator.IElevator;

import java.util.concurrent.Semaphore;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

// This is also an integration test between ElevatorManagement and the DashboardController
public class DashboardControllerTest extends ApplicationTest {
    private DashboardController controller;

    private IElevator rmiMock;
    private ElevatorManagement management;

    @Override
    public void start (Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("dashboard.fxml"));
        Parent mainNode = fxmlLoader.load();
        controller = Mockito.spy(fxmlLoader.<DashboardController>getController());

        stage.setScene(new Scene(mainNode));
        stage.show();
        stage.toFront();
    }

    @AfterEach
    public void tearDown () throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    @BeforeEach
    public void setUp () throws Exception {
        rmiMock = new RMIInstanceMock();
        management = new ElevatorManagement(rmiMock, false);
        management.addListener(controller);
    }

    @Test
    public void elevatorSystemFirstUpdateTest() throws InterruptedException {
        assertFalse(controller.isInitialized());
        assertEquals(0, controller.getElevators().size());
        management.pollElevatorSystem();
        verify(controller, times(1)).update(any());
        assertTrue(controller.isInitialized());
        assertEquals(1, controller.getElevators().size());
        waitForRunLater();
        assertNotNull(controller.getElevators().get(0).getElevator());
    }


    public void waitForRunLater() throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        Platform.runLater(semaphore::release);
        semaphore.acquire();
    }
}
