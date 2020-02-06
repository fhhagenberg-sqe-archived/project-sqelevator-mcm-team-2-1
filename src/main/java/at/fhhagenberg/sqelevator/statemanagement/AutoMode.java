package at.fhhagenberg.sqelevator.statemanagement;

import at.fhhagenberg.sqelevator.communication.UIActionListener;
import at.fhhagenberg.sqelevator.model.Elevator;
import at.fhhagenberg.sqelevator.model.ElevatorSystem;
import at.fhhagenberg.sqelevator.model.states.ButtonState;
import at.fhhagenberg.sqelevator.model.states.CommittedDirection;
import sqelevator.IElevator;

import java.rmi.RemoteException;

/**
 * This class adds an automatic mode to the elevator program.
 * It checks the pressed buttons and decides where to go next
 *
 * @author Christoph Obermayr
 */
public class AutoMode {
    ElevatorManagement management;
    IElevator rmiInstance;

    /**
     * Constructor needs the elevator management and elevator-interface
     *
     * @param management
     * @param rmiInstance
     */
    public AutoMode(ElevatorManagement management, IElevator rmiInstance) {
        this.management = management;
        this.rmiInstance = rmiInstance;
    }

    /**
     * Set the next actions (direction and floor) for every elevator in the system which works in auto-mode
     *
     * @throws RemoteException
     */
    public void setNextAutoModeActions() throws RemoteException {
        for (int ele = 0; ele < rmiInstance.getElevatorNum(); ele++) {
            Elevator actElevator = management.getElevatorSystem().getElevators().get(ele);

            if (management.getAutoActive()) {

                int actualFloor = actElevator.getFloor();
                CommittedDirection actualDirection = actElevator.getCommittedDirection();

                if (actualDirection == CommittedDirection.UNCOMMITTED) {
                    actualDirection = CommittedDirection.UP;
                }
                if (actElevator.getFloor() <= 0) {
                    actualDirection = CommittedDirection.UP;
                } else if (actElevator.getFloor() >= management.getElevatorSystem().getFloorCount() - 1) {
                    actualDirection = CommittedDirection.DOWN;
                }


                //find next floor
                int nextFloor = getNextFloor(actElevator, management.getElevatorSystem(), actualFloor, actualDirection);
                if (nextFloor == Integer.MAX_VALUE) {
                    if (actualDirection == CommittedDirection.UP) {
                        actualDirection = CommittedDirection.DOWN;
                        //actualFloor = elevatorSystem.getFloorCount();
                    } else {
                        actualDirection = CommittedDirection.UP;
                        //actualFloor = 0;
                    }
                    nextFloor = getNextFloor(actElevator, management.getElevatorSystem(), actualFloor, actualDirection);
                }

                //send the elevator to it's selected floor
                if (nextFloor != Integer.MAX_VALUE && nextFloor >= 0) {
                    management.changeCommittedDirection(actElevator.getId(), actualDirection);
                    management.floorSelected(actElevator.getId(), nextFloor);
                    System.out.println("Set next floor to " + nextFloor + "; Direction: " + actualDirection.getPrintValue());
                } else {
                    actElevator.setCommittedDirection(CommittedDirection.UNCOMMITTED);
                    System.out.println("Set Direction to: " + actualDirection.getPrintValue() + "; nextFloor: " + nextFloor);
                }
            }
        }

    }

    /**
     * Calculates the next floor to go to based on the pressed buttons and the actual load
     *
     * @param actElevator
     * @param elevatorSystem
     * @param actualFloor
     * @param direction
     * @return
     */
    private int getNextFloor(Elevator actElevator, ElevatorSystem elevatorSystem,
                             int actualFloor, CommittedDirection direction) {
        int nextFloor = Integer.MAX_VALUE;

        /*
        get next choosen and next wishing to go into the same direction - set this one as new goal
                    (or if full capacity:weight just the next choosen)
        */
        int endFloor = -1;
        int plusMinus = -1;
        if (direction == CommittedDirection.UP) {
            endFloor = elevatorSystem.getFloorCount();
            plusMinus = 1;
        }

        actualFloor += plusMinus;

        nextFloor = searchNextFloorLoop(nextFloor, actualFloor, endFloor, plusMinus, direction, elevatorSystem, actElevator);


        if (nextFloor == Integer.MAX_VALUE) {
            //still no next floor --> look if the next floor is from the end into the other direction
            plusMinus *= -1;
            if (direction == CommittedDirection.UP) {
                actualFloor = elevatorSystem.getFloorCount();
                direction = CommittedDirection.DOWN;
                endFloor = -1;
            } else {
                actualFloor = -1;
                direction = CommittedDirection.UP;
                endFloor = elevatorSystem.getFloorCount();
            }
            nextFloor = searchNextFloorLoop(nextFloor, actualFloor, endFloor, plusMinus, direction, elevatorSystem, actElevator);
        }

        return nextFloor;
    }

    /**
     * Loop until the next floor to search for
     *
     * @param nextFloor
     * @param actualFloor
     * @param endFloor
     * @param plusMinus
     * @param direction
     * @param elevatorSystem
     * @param actElevator
     * @return
     */
    public int searchNextFloorLoop(int nextFloor, int actualFloor, int endFloor, int plusMinus,
                                   CommittedDirection direction, ElevatorSystem elevatorSystem, Elevator actElevator) {
        while (nextFloor == Integer.MAX_VALUE && actualFloor != endFloor) {
            //if not full --> get the next floor in the direction, where someone wants to go the same direction
            //if(actElevator.getWeight() < (actElevator.getCapacity()*80)){
            ButtonState bs = elevatorSystem.getFloorButtons().get(actualFloor);
            if ((bs == ButtonState.BOTH)
                    || ((bs == ButtonState.DOWN) && (direction == CommittedDirection.DOWN))
                    || ((bs == ButtonState.UP) && (direction == CommittedDirection.UP))
            ) {
                nextFloor = actualFloor;
            }
            //}

            //if not already set and the button for this floor is pressed in the elevator --> set next floor to it
            if ((nextFloor == Integer.MAX_VALUE) && actElevator.getButtons().get(actualFloor)) {
                nextFloor = actualFloor;
            }

            actualFloor += plusMinus;
        }
        return nextFloor;
    }
}