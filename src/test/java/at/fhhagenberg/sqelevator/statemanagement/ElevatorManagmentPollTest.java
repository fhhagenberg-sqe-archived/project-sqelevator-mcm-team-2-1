package at.fhhagenberg.sqelevator.statemanagement;

import at.fhhagenberg.sqelevator.RMIInstanceMock;
import at.fhhagenberg.sqelevator.model.Elevator;
import at.fhhagenberg.sqelevator.model.ElevatorSystem;
import at.fhhagenberg.sqelevator.model.states.ButtonState;
import at.fhhagenberg.sqelevator.model.states.CommittedDirection;
import at.fhhagenberg.sqelevator.model.states.DoorStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sqelevator.IElevator;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ElevatorManagmentPollTest {
    private IElevator mock;
    private ElevatorManagement elevatorManagement;

    @BeforeEach
    public void init() {
        mock = new RMIInstanceMock();
        elevatorManagement = new ElevatorManagement(mock, false);
    }

    @Test
    public void testPollingResult() {
        elevatorManagement.pollElevatorSystem();
        ElevatorSystem result = elevatorManagement.getElevatorSystem();

        assertEquals(1, result.getElevatorCount());
        assertEquals(10, result.getFloorHeight());
        assertEquals(10, result.getFloorCount());

        HashMap<Integer, ButtonState> buttons = new HashMap<>();
        buttons.put(0,ButtonState.DOWN);
        buttons.put(1,ButtonState.UP);
        buttons.put(2,ButtonState.DOWN);
        buttons.put(3,ButtonState.UP);
        buttons.put(4,ButtonState.DOWN);
        buttons.put(5,ButtonState.UP);
        buttons.put(6,ButtonState.DOWN);
        buttons.put(7,ButtonState.UP);
        buttons.put(8,ButtonState.DOWN);
        buttons.put(9,ButtonState.UP);
        assertEquals(buttons, result.getFloorButtons());

        Elevator resultElevator = result.getElevators().get(0);
        assertNotNull(resultElevator);
        assertEquals(0, resultElevator.getId());
        assertEquals(10, resultElevator.getAcceleration());
        assertEquals(5, resultElevator.getSpeed());
        assertEquals(10, resultElevator.getCapacity());
        assertEquals(0, resultElevator.getFloor());
        assertEquals(0, resultElevator.getPosition());
        assertEquals(10, resultElevator.getWeight());
        assertEquals(DoorStatus.OPEN, resultElevator.getDoorStatus());
        assertEquals(0, resultElevator.getTarget());
        assertEquals(CommittedDirection.UP, resultElevator.getCommittedDirection());
    }
}
