package at.fhhagenberg.sqelevator.communication;

import at.fhhagenberg.sqelevator.model.ElevatorSystem;

public interface ElevatorSystemChangeListener {
    void update(ElevatorSystem system);
}
