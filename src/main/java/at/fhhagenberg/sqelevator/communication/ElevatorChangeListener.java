package at.fhhagenberg.sqelevator.communication;

import at.fhhagenberg.sqelevator.model.ElevatorSystem;

/**
 * This class notifies when an elevator has been changed. Also includes the index of the elevator in question.
 *
 * In fact, this interface is just a minimal extension of the {@link ElevatorSystemChangeListener}. It can also pass the
 * id of a certain elevator to the implementation.
 * Alternatively, the listener could also hold the id as a member and use {@link ElevatorSystemChangeListener} directly.
 * Nevertheless, this was seen as a better separation of concerns.
 *
 * @author Martin Schneglberger
 */
public interface ElevatorChangeListener {
    /**
     * Gets called when the state of an elevator changed
     *
     * @param elevatorSystem Overall elevator system
     * @param elevatorId elevator in question
     */
    void update(ElevatorSystem elevatorSystem, int elevatorId);
}
