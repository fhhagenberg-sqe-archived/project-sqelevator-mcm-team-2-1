package at.fhhagenberg.sqelevator.gui;

import at.fhhagenberg.sqelevator.launcher.ElevatorApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import sqelevator.IElevator;

import java.util.concurrent.TimeoutException;

import static org.mockito.Mockito.*;

public class ElevatorApplicationTest {
    FxRobot robot;

    ElevatorApplication app;
    ElevatorApplication appSpy;

    @Mock
    IElevator rmiInstance;

    @BeforeEach
    public void setup() throws TimeoutException {
        MockitoAnnotations.initMocks(this);
        app = new ElevatorApplication();
        appSpy = spy(app);
        doReturn(rmiInstance).when(appSpy).getRMIInstance();

        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(() -> appSpy);
        robot = new FxRobot();
    }

    @Test
    public void testListenersSetCorrectly() {
        System.out.println("TODO");
    }


}
