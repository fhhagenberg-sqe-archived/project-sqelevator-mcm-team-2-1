package at.fhhagenberg.sqelevator.statemanagement;

import at.fhhagenberg.sqelevator.communication.ElevatorSystemChangeListener;
import at.fhhagenberg.sqelevator.model.states.CommittedDirection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sqelevator.IElevator;

import java.rmi.RemoteException;

import static org.mockito.Mockito.*;


public class ElevatorManagementUpdateTest {
    @Mock
    IElevator rmiInstance;

    ElevatorManagement elevatorManagement;

    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
        elevatorManagement = new ElevatorManagement(rmiInstance, false);
    }

    @Test
    void testNotifications() {
        ElevatorSystemChangeListener mock = mock(ElevatorSystemChangeListener.class);
        ElevatorSystemChangeListener mock2 = mock(ElevatorSystemChangeListener.class);
        elevatorManagement.addListener(mock);
        elevatorManagement.addListener(mock2);
        elevatorManagement.pollElevatorSystem();
        verify(mock, times(1)).update(any());
        verify(mock2, times(1)).update(any());
    }

    @Test
    void testSelectFloor() throws RemoteException {
        elevatorManagement.floorSelected(0, 10);
        verify(rmiInstance, times(1)).setTarget(0,10);
        elevatorManagement.floorSelected(3, 1);
        verify(rmiInstance, times(1)).setTarget(3,1);
    }

    @Test
    void testChangeCommittedDirection() throws RemoteException {
        elevatorManagement.changeCommittedDirection(0, CommittedDirection.DOWN);
        verify(rmiInstance, times(1)).setCommittedDirection(0, CommittedDirection.DOWN.getRawValue());
        elevatorManagement.changeCommittedDirection(1, CommittedDirection.UP);
        verify(rmiInstance, times(1)).setCommittedDirection(1, CommittedDirection.UP.getRawValue());
        elevatorManagement.changeCommittedDirection(2, CommittedDirection.UNCOMMITTED);
        verify(rmiInstance, times(1)).setCommittedDirection(2, CommittedDirection.UNCOMMITTED.getRawValue());
    }
}
