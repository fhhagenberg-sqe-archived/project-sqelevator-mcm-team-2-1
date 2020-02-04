package at.fhhagenberg.sqelevator.statemanagement;

import at.fhhagenberg.sqelevator.communication.ElevatorSystemChangeListener;
import at.fhhagenberg.sqelevator.communication.UIActionListener;
import at.fhhagenberg.sqelevator.model.Elevator;
import at.fhhagenberg.sqelevator.model.ElevatorSystem;
import at.fhhagenberg.sqelevator.model.states.ButtonState;
import at.fhhagenberg.sqelevator.model.states.CommittedDirection;
import at.fhhagenberg.sqelevator.model.states.DoorStatus;
import javafx.scene.control.Button;
import lombok.Getter;
import org.slf4j.LoggerFactory;
import sqelevator.IElevator;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * This class contains the management functionality of the elevator program.
 * It can be constructed to poll the RMI interface on a regular basis
 * By acting as a UIActionListener, it can also listen for UI events and forward requests to the RMI Server
 *
 * @see UIActionListener
 * @author Martin Schneglberger
 */
public class ElevatorManagement implements UIActionListener {

    public static final String RMI_ERROR = "Error invoking RMI method";

    private IElevator rmiInstance;
    @Getter
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    @Getter
    private ScheduledFuture future = null;
    @Getter
    private ElevatorSystem elevatorSystem;

    private Boolean autoActive;

    private LinkedList<ElevatorSystemChangeListener> listeners = new LinkedList<>();

    /**
     * Sets up the ElevatorManagement. Polling gets turned on automatically
     *
     * @param rmiInstance RMI instance
     */
    public ElevatorManagement(IElevator rmiInstance) {
        this.rmiInstance = rmiInstance;
        this.elevatorSystem = new ElevatorSystem();
        this.autoActive = false;
        initPolling();
    }

    /**
     * Additional constructor which allows to enable or disable the automatic polling
     *
     * @param rmiInstance RMI instance
     * @param polling true if the RMI server should be polled on a regular basis
     */
    public ElevatorManagement(IElevator rmiInstance, boolean polling) {
        this.rmiInstance = rmiInstance;
        this.elevatorSystem = new ElevatorSystem();
        this.autoActive = false;
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

    /**
     * Requests latest updates from the RMI Server and saves them in the {@link #elevatorSystem} member
     */
    public void pollElevatorSystem() {
        try {
            elevatorSystem.setElevatorCount(rmiInstance.getElevatorNum());
            elevatorSystem.setFloorCount(rmiInstance.getFloorNum());
            elevatorSystem.setFloorHeight(rmiInstance.getFloorHeight());

            getFloorButtonStates();
            pollElevators();

            listeners.forEach(listener -> listener.update(elevatorSystem));

            setNextAutoModeActions();
        } catch (RemoteException e) {
            LoggerFactory.getLogger(ElevatorManagement.class).error("RMI could not be polled", e);
        }

    }

    private void setNextAutoModeActions() throws RemoteException {
        //if(automode) --> how do we get this?
        for(int ele = 0; ele < rmiInstance.getElevatorNum();  ele++) {
            Elevator actElevator = elevatorSystem.getElevators().get(ele);


            //if(actElevator.isAutomaticModeActive()) {
            //if(elevatorSystem.getElevators().get(ele).isAutomaticModeActive()){
            if(autoActive){
                /*for (int floor = 0; floor < rmiInstance.getFloorNum(); floor++) {
                    //test floor for up or down already done before in getFloorButton States
                    boolean isUp = rmiInstance.getFloorButtonUp(floor);
                    boolean isDown = rmiInstance.getFloorButtonDown(floor);

                        //rmiInstance.
                        ButtonState stateFloor = elevatorSystem.getFloorButtons().get(floor);
                        rmiInstance.get;
                        elevatorSystem.getElevators().get(0).;
                }
                elevatorSystem.getElevators()
                elevatorSystem.getFloorButtons().
                */
                int actualFloor = actElevator.getFloor();
                CommittedDirection actualDirection = actElevator.getCommittedDirection();

                if(actualDirection==CommittedDirection.UNCOMMITTED){
                    actualDirection = CommittedDirection.UP;
                }
                if(actElevator.getFloor() <= 0){
                    actualDirection = CommittedDirection.UP;
                }else if(actElevator.getFloor() >= elevatorSystem.getFloorCount()-1){
                    actualDirection = CommittedDirection.DOWN;
                }


                //find next floor
                int nextFloor = getNextFloor(actElevator, elevatorSystem, actualFloor, actualDirection);
                if(nextFloor == Integer.MAX_VALUE){
                    if(actualDirection == CommittedDirection.UP){
                        actualDirection = CommittedDirection.DOWN;
                    }else {
                        actualDirection = CommittedDirection.UP;
                    }
                    nextFloor = getNextFloor(actElevator, elevatorSystem, actualFloor, actualDirection);
                }

                //send the elevator to it's selected floor
                if (nextFloor != Integer.MAX_VALUE && nextFloor >= 0){
                    //actElevator.setCommittedDirection(actualDirection);
                    changeCommittedDirection(actElevator.getId(), actualDirection);
                    //actElevator.setFloor(nextFloor);
                    floorSelected(actElevator.getId(), nextFloor);
                    System.out.println("Set next floor to " + nextFloor + "; Direction: " + actualDirection.getPrintValue());
                }else {
                    actElevator.setCommittedDirection(CommittedDirection.UNCOMMITTED);
                    System.out.println("Set Direction to: " + actualDirection.getPrintValue() + "; nextFloor: " + nextFloor);
                }
            }
        }

    }

    private int getNextFloor(Elevator actElevator, ElevatorSystem elevatorSystem,
                             int actualFloor, CommittedDirection direction){
        int nextFloor = Integer.MAX_VALUE;

        /*
        get next choosen and next wishing to go into the same direction - set this one as new goal
                    (or if full capacity:weight just the next choosen)
        */
        int endFloor = -1;
        int plusMinus = -1;
        if(direction == CommittedDirection.UP){
            endFloor = elevatorSystem.getFloorCount();
            plusMinus = 1;
        }

        actualFloor += plusMinus;

        while (nextFloor == Integer.MAX_VALUE && actualFloor != endFloor){
            //if not full --> get the next floor in the direction, where someone wants to go the same direction
            //if(actElevator.getWeight() >= (actElevator.getCapacity()*0.9)){
                ButtonState bs = elevatorSystem.getFloorButtons().get(actualFloor);
                if(     (bs == ButtonState.BOTH)
                        || ((bs==ButtonState.DOWN)&&(direction==CommittedDirection.DOWN))
                        || ((bs==ButtonState.UP)&&(direction==CommittedDirection.UP))){
                    nextFloor = actualFloor;
                }
            //}

            //if not already set and the button for this floor is pressed in the elevator --> set next floor to it
            if((nextFloor == Integer.MAX_VALUE) && actElevator.getButtons().get(actualFloor)){
                nextFloor = actualFloor;
            }

            actualFloor += plusMinus;
        }

        return nextFloor;
    }

	/**
     * Loads the states of the UP/Down buttons of each floor
     *
     * @throws RemoteException Gets thrown if no connection to the RMI server is possible
     */
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

    /**
     * Loads the states of all elevators
     *
     * @throws RemoteException Gets thrown if no connection to the RMI server is possible
     */
    private void pollElevators() throws RemoteException {
        elevatorSystem.setElevators(new HashMap<>());
        for(int i = 0; i < elevatorSystem.getElevatorCount(); i++) {
            Elevator tempElevator = pollElevator(i);
            pollButtonsForElevator(tempElevator);
            elevatorSystem.getElevators().put(i, tempElevator);
        }
    }

    /**
     * Loads the status of a single elevator
     *
     * @param i Index of the elevator in question
     * @return Elevator in current state
     *
     * @throws RemoteException Gets thrown if no connection to the RMI server is possible
     */
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

    /**
     * Loads the state of the buttons of a certain elevator (requested floors)
     *
     * @throws RemoteException Gets thrown if no connection to the RMI server is possible
     */
    private void pollButtonsForElevator(Elevator elevator) throws RemoteException {
        elevator.setButtons(new HashMap<>());
        for(int floor = 0; floor < rmiInstance.getFloorNum(); floor++) {
            elevator.getButtons().put(floor, rmiInstance.getElevatorButton(elevator.getId(), floor));
        }
    }

    /**
     * Adds a listener which should get notified when new system updates got polled
     *
     * @param listener Object which should get informed about new updates
     */
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
    public void setAutoMode(int elevator, boolean autoEnabled) {
        autoActive = autoEnabled;
        elevatorSystem.getElevators().get(elevator).setAutomaticModeActive(autoEnabled);
    }
}
