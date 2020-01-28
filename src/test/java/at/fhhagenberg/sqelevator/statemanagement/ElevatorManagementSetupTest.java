package at.fhhagenberg.sqelevator.statemanagement;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import sqelevator.IElevator;

import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class ElevatorManagementSetupTest {
    @Mock
    IElevator rmiInstance;

    ElevatorManagement elevatorManagement;
    ElevatorManagement managementSpy;

    @BeforeEach
    void init() throws RemoteException {
        MockitoAnnotations.initMocks(this);
        when(rmiInstance.getClockTick()).thenReturn(Long.valueOf(100));
    }

    @AfterEach
    void tearDown() {
        managementSpy.getScheduler().shutdown();
    }

    @Test
    void testRMIConstructor() {
        elevatorManagement = new ElevatorManagement(rmiInstance);
        managementSpy = Mockito.spy(elevatorManagement);

        doNothing().when(managementSpy).pollElevatorSystem();
        assertEquals(100, elevatorManagement.getElevatorSystem().getClockTickRate());
        assertNotNull(managementSpy.getFuture());
        assertFalse(managementSpy.getScheduler().isShutdown());
        assertFalse(managementSpy.getScheduler().isTerminated());
    }

    @Test
    void testPollingFalseConstructor() {
        elevatorManagement = new ElevatorManagement(rmiInstance, false);
        managementSpy = Mockito.spy(elevatorManagement);

        doNothing().when(managementSpy).pollElevatorSystem();
        assertEquals(0, elevatorManagement.getElevatorSystem().getClockTickRate());
        assertNull(managementSpy.getFuture());
    }

    @Test
    void testPollingTrueConstructor() {
        elevatorManagement = new ElevatorManagement(rmiInstance, true);
        managementSpy = Mockito.spy(elevatorManagement);

        doNothing().when(managementSpy).pollElevatorSystem();
        assertEquals(100, elevatorManagement.getElevatorSystem().getClockTickRate());
        assertNotNull(managementSpy.getFuture());
    }
}
