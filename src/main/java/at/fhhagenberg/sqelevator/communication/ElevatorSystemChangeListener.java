package at.fhhagenberg.sqelevator.communication;

import at.fhhagenberg.sqelevator.model.ElevatorSystem;

/**
 * This interface is used to notify about changes in the elevator system
 *
 * @author Martin Schneglberger
 */
public interface ElevatorSystemChangeListener {
    /**
     * Gets called whenever the elevator system has been updated
     *
     * @param system new elevator system representation
     * @see ElevatorSystem
     */
    void update(ElevatorSystem system);
}
