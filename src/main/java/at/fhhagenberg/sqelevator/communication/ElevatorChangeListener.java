package at.fhhagenberg.sqelevator.communication;

import at.fhhagenberg.sqelevator.model.Elevator;
import at.fhhagenberg.sqelevator.model.ElevatorSystem;

public interface ElevatorChangeListener {
    void update(ElevatorSystem elevatorSystem, int elevatorId);
}
