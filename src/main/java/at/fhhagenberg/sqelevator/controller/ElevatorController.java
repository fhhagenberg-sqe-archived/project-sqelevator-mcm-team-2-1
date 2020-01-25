package at.fhhagenberg.sqelevator.controller;

import at.fhhagenberg.sqelevator.model.ButtonState;
import at.fhhagenberg.sqelevator.model.Elevator;
import at.fhhagenberg.sqelevator.model.ElevatorSystem;
import at.fhhagenberg.sqelevator.services.ElevatorPolling;
import sqelevator.IElevator;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//TODO: Create interface for this
public class ElevatorController {

    private IElevator rmiInstance;
    private ElevatorPolling elevatorPolling;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private ElevatorSystem elevatorSystem;

    public ElevatorController(IElevator rmiInstance) {
        this.rmiInstance = rmiInstance;
        this.elevatorPolling = new ElevatorPolling(this);
        this.elevatorSystem = new ElevatorSystem();
        initPolling();
    }

    private void initPolling() {
        try {
            long period = this.rmiInstance.getClockTick();
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

        for(int i = 0; i < elevatorSystem.getElevatorCount(); i++) {
            Elevator tempElevator = pollElevator(i);
            pollButtonsForElevator(tempElevator);
            elevatorSystem.getElevators().put(i, tempElevator);
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

    private Elevator pollElevator(int i) throws RemoteException {
        Elevator tempElevator = new Elevator();
        tempElevator.setId(i);
        tempElevator.setFloor(rmiInstance.getElevatorFloor(i));
        tempElevator.setPosition(rmiInstance.getElevatorPosition(i));
        tempElevator.setTarget(rmiInstance.getTarget(i));
        tempElevator.setCommittedDirection(rmiInstance.getCommittedDirection(i));
        tempElevator.setDoorStatus(rmiInstance.getElevatorDoorStatus(i));
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
}
