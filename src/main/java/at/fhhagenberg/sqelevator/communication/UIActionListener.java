package at.fhhagenberg.sqelevator.communication;

import at.fhhagenberg.sqelevator.model.states.CommittedDirection;

/**
 * This interface is used to notify about any actions that can be triggered by the UI
 *
 * @author Martin Schneglberger
 */
public interface UIActionListener {
    /**
     * A certain elevator has been sent to a specific floor
     * @param elevator elevator in question
     * @param floor target floor
     */
    void floorSelected(int elevator, int floor);

    /**
     * The committed direction of an elevator has been changed
     *
     * @param elevator elevator in question
     * @param direction new direction
     */
    void changeCommittedDirection(int elevator, CommittedDirection direction);
    void setAutoMode(int elevator, boolean autoEnabled);
}
