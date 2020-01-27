package at.fhhagenberg.sqelevator.communication;

import at.fhhagenberg.sqelevator.model.states.CommittedDirection;

public interface UIActionListener {
    void floorSelected(int elevator, int floor);
    void changeCommittedDirection(int elevator, CommittedDirection direction);
}
