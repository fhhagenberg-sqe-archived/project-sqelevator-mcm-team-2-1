package at.fhhagenberg.sqelevator.statemanagement;

import at.fhhagenberg.sqelevator.communication.ElevatorSystemChangeListener;
import at.fhhagenberg.sqelevator.communication.UIActionListener;
import at.fhhagenberg.sqelevator.model.Elevator;
import at.fhhagenberg.sqelevator.model.ElevatorSystem;
import at.fhhagenberg.sqelevator.model.states.ButtonState;
import at.fhhagenberg.sqelevator.model.states.CommittedDirection;
import at.fhhagenberg.sqelevator.model.states.DoorStatus;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.LoggerFactory;
import sqelevator.IElevator;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ElevatorManagement implements UIActionListener {

    public static final String RMI_ERROR = "Error invoking RMI method";

    private IElevator rmiInstance;
    @Getter
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    @Getter
    private ScheduledFuture future = null;
    @Getter
    private ElevatorSystem elevatorSystem;

    private LinkedList<ElevatorSystemChangeListener> listeners = new LinkedList<>();

    public ElevatorManagement(IElevator rmiInstance) {
        this.rmiInstance = rmiInstance;
        this.elevatorSystem = new ElevatorSystem();
        initPolling();
    }

    public ElevatorManagement(IElevator rmiInstance, boolean polling) {
        this.rmiInstance = rmiInstance;
        this.elevatorSystem = new ElevatorSystem();
        if(polling) initPolling();
    }

    private void initPolling() {
        try {
            long period = this.rmiInstance.getClockTick();
            if(period == 0) period = 100;
            this.elevatorSystem.setClockTickRate(period);
            future = scheduler.scheduleAtFixedRate(this::pollElevatorSystem, 1, period, TimeUnit.MILLISECONDS);
        } catch (RemoteException e) {
            LoggerFactory.getLogger(ElevatorManagement.class).error(RMI_ERROR, e);
        }
    }

    public void pollElevatorSystem() {
        try {
            elevatorSystem.setElevatorCount(rmiInstance.getElevatorNum());
            elevatorSystem.setFloorCount(rmiInstance.getFloorNum());
            elevatorSystem.setFloorHeight(rmiInstance.getFloorHeight());

            getFloorButtonStates();
            pollElevators();

            listeners.forEach(listener -> listener.update(elevatorSystem));
        } catch (RemoteException e) {
            LoggerFactory.getLogger(ElevatorManagement.class).error("RMI could not be polled", e);
        }

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

    public void addListener(ElevatorSystemChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public void floorSelected(int elevator, int floor) {
        try {
            this.rmiInstance.setTarget(elevator, floor);
        } catch (RemoteException e) {
            LoggerFactory.getLogger(ElevatorManagement.class).error(RMI_ERROR, e);
        }
    }

    @Override
    public void changeCommittedDirection(int elevator, CommittedDirection direction) {
        //TODO: Christoph, change the logic for this based on the automatic mode
        //Is hardcoded now for the manual mode
        try {
            this.rmiInstance.setCommittedDirection(elevator, direction.getRawValue());
        } catch (RemoteException e) {
            LoggerFactory.getLogger(ElevatorManagement.class).error(RMI_ERROR, e);
        }
    }

    @Override
    public void setServicedFloor(int elevator, int floor, boolean serviced) {
        //TODO: Christoph, implement this
    }
}
