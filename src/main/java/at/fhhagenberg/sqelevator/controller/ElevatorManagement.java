package at.fhhagenberg.sqelevator.controller;

import at.fhhagenberg.sqelevator.communication.ElevatorChangeListener;
import at.fhhagenberg.sqelevator.communication.UIActionListener;
import at.fhhagenberg.sqelevator.model.states.ButtonState;
import at.fhhagenberg.sqelevator.model.states.CommittedDirection;
import at.fhhagenberg.sqelevator.model.Elevator;
import at.fhhagenberg.sqelevator.model.ElevatorSystem;
import at.fhhagenberg.sqelevator.model.states.DoorStatus;
import at.fhhagenberg.sqelevator.services.ElevatorPolling;
import sqelevator.IElevator;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ElevatorManagement implements UIActionListener {

    private IElevator rmiInstance;
    private ElevatorPolling elevatorPolling;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private ElevatorSystem elevatorSystem;

    private LinkedList<ElevatorChangeListener> listeners = new LinkedList<>();

    public ElevatorManagement(IElevator rmiInstance) {
        this.rmiInstance = rmiInstance;
        this.elevatorPolling = new ElevatorPolling(this);
        this.elevatorSystem = new ElevatorSystem();
        initPolling();
    }

    private void initPolling() {
        try {
            long period = this.rmiInstance.getClockTick();
            if(period == 0) period = 100;
            this.elevatorSystem.setClockTickRate(period);
            scheduler.scheduleAtFixedRate(this.elevatorPolling, 1, period, TimeUnit.MILLISECONDS);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void pollElevatorSystem() throws RemoteException {
        elevatorSystem.setElevatorCount(rmiInstance.getElevatorNum());
        elevatorSystem.setFloorCount(rmiInstance.getFloorNum());
        elevatorSystem.setFloorHeight(rmiInstance.getFloorHeight());

        getFloorButtonStates();
        pollElevators();

        //System.out.println(elevatorSystem.getElevators().get(0).getWeight());

        listeners.forEach(listener -> listener.update(elevatorSystem));
    }

    private void getFloorButtonStates() throws RemoteException {
        elevatorSystem.setFloorButtons(new HashMap<>());
        for (int floor = 0; floor < rmiInstance.getFloorNum(); floor++) {
            boolean isUp = rmiInstance.getFloorButtonUp(floor);
            boolean isDown = rmiInstance.getFloorButtonDown(floor);
            ButtonState state;

            if(isUp && isDown) state = ButtonState.BOTH;
            else if (isUp) state = ButtonState.UP;
            else if (isDown) state = ButtonState.DOWN;
            else state = ButtonState.UNSET;

            elevatorSystem.getFloorButtons().put(floor, state);
        }
    }

    private void pollElevators() throws RemoteException {
        elevatorSystem.setElevators(new HashMap<>());
        for(int i = 0; i < elevatorSystem.getElevatorCount(); i++) {
            Elevator tempElevator = pollElevator(i);
            pollButtonsForElevator(tempElevator);
            elevatorSystem.getElevators().put(i, tempElevator);
        }
    }

    private Elevator pollElevator(int i) throws RemoteException {
        Elevator tempElevator = new Elevator();
        tempElevator.setId(i);
        tempElevator.setFloor(rmiInstance.getElevatorFloor(i));
        tempElevator.setPosition(rmiInstance.getElevatorPosition(i));
        tempElevator.setTarget(rmiInstance.getTarget(i));
        tempElevator.setCommittedDirection(CommittedDirection.fromInteger(rmiInstance.getCommittedDirection(i)));
        tempElevator.setDoorStatus(DoorStatus.fromInteger(rmiInstance.getElevatorDoorStatus(i)));
        tempElevator.setSpeed(rmiInstance.getElevatorSpeed(i));
        tempElevator.setAcceleration(rmiInstance.getElevatorAccel(i));
        tempElevator.setCapacity(rmiInstance.getElevatorCapacity(i));
        tempElevator.setWeight(rmiInstance.getElevatorWeight(i));
        return tempElevator;
    }

    private void pollButtonsForElevator(Elevator elevator) throws RemoteException {
        elevator.setButtons(new HashMap<>());
        for(int floor = 0; floor < rmiInstance.getFloorNum(); floor++) {
            elevator.getButtons().put(floor, rmiInstance.getElevatorButton(elevator.getId(), floor));
        }
    }

    public void addListener(ElevatorChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public void floorSelected(int elevator, int floor) {
        try {
            this.rmiInstance.setTarget(elevator, floor);
        } catch (RemoteException e) {
            System.err.println("Could not set elevator target");
            e.printStackTrace();
        }
    }

    @Override
    public void changeCommittedDirection(int elevator, CommittedDirection direction) {
        try {
            this.rmiInstance.setCommittedDirection(elevator, direction.getRawValue());
        } catch (RemoteException e) {
            System.err.println("Could not set committed direction");
            e.printStackTrace();
        }
    }
}
