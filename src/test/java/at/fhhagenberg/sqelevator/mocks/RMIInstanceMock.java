package at.fhhagenberg.sqelevator.mocks;

import at.fhhagenberg.sqelevator.model.Elevator;
import at.fhhagenberg.sqelevator.model.states.CommittedDirection;
import at.fhhagenberg.sqelevator.model.states.DoorStatus;
import sqelevator.IElevator;

import java.rmi.RemoteException;
import java.util.HashMap;

public class RMIInstanceMock implements IElevator {

    private Elevator elevator1;

    public RMIInstanceMock() {
        elevator1 = Elevator.builder()
                .id(0)
                .acceleration(10)
                .speed(0)
                .capacity(10)
                .floor(0)
                .position(0)
                .weight(10)
                .doorStatus(DoorStatus.OPEN)
                .target(0)
                .buttons(new HashMap<>())
                .committedDirection(CommittedDirection.UNCOMMITTED)
                .build();
    }
    @Override
    public int getCommittedDirection(int elevatorNumber) throws RemoteException {
        return elevator1.getCommittedDirection().getRawValue();
    }

    @Override
    public int getElevatorAccel(int elevatorNumber) throws RemoteException {
        return elevator1.getAcceleration();
    }

    @Override
    public boolean getElevatorButton(int elevatorNumber, int floor) throws RemoteException {
        return floor%2==0;
    }

    @Override
    public int getElevatorDoorStatus(int elevatorNumber) throws RemoteException {
        return elevator1.getDoorStatus().getRawValue();
    }

    @Override
    public int getElevatorFloor(int elevatorNumber) throws RemoteException {
        return elevator1.getFloor();
    }

    @Override
    public int getElevatorNum() throws RemoteException {
        return 1;
    }

    @Override
    public int getElevatorPosition(int elevatorNumber) throws RemoteException {
        return elevator1.getPosition();
    }

    @Override
    public int getElevatorSpeed(int elevatorNumber) throws RemoteException {
        return elevator1.getSpeed();
    }

    @Override
    public int getElevatorWeight(int elevatorNumber) throws RemoteException {
        return elevator1.getWeight();
    }

    @Override
    public int getElevatorCapacity(int elevatorNumber) throws RemoteException {
        return elevator1.getCapacity();
    }

    @Override
    public boolean getFloorButtonDown(int floor) throws RemoteException {
        return floor%2==0;
    }

    @Override
    public boolean getFloorButtonUp(int floor) throws RemoteException {
        return floor%2!=0;
    }

    @Override
    public int getFloorHeight() throws RemoteException {
        return 10;
    }

    @Override
    public int getFloorNum() throws RemoteException {
        return 10;
    }

    @Override
    public boolean getServicesFloors(int elevatorNumber, int floor) throws RemoteException {
        return false;
    }

    @Override
    public int getTarget(int elevatorNumber) throws RemoteException {
        return 0;
    }

    @Override
    public void setCommittedDirection(int elevatorNumber, int direction) throws RemoteException {

    }

    @Override
    public void setServicesFloors(int elevatorNumber, int floor, boolean service) throws RemoteException {

    }

    @Override
    public void setTarget(int elevatorNumber, int target) throws RemoteException {

    }

    @Override
    public long getClockTick() throws RemoteException {
        return 100;
    }
}
