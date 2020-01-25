package at.fhhagenberg.sqelevator.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

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
