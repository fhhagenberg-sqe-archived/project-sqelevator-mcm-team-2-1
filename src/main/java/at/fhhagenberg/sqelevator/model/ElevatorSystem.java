package at.fhhagenberg.sqelevator.model;

import at.fhhagenberg.sqelevator.model.states.ButtonState;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

/**
 * This class represents the current state of the overall elevator system
 *
 * @author Martin Schneglberger
 */
@Data
@NoArgsConstructor
public class ElevatorSystem {

    private long clockTickRate;
    private int elevatorCount;
    private int floorCount;
    private int floorHeight;

    private HashMap<Integer, Elevator> elevators = new HashMap<>();
    private HashMap<Integer, ButtonState> floorButtons = new HashMap<>();
}
