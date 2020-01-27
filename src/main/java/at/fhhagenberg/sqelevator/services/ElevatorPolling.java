package at.fhhagenberg.sqelevator.services;

import at.fhhagenberg.sqelevator.statemanagement.ElevatorManagement;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;

public class ElevatorPolling implements Runnable {

    private ElevatorManagement controller;

    public ElevatorPolling(ElevatorManagement control) {
        this.controller = control;
    }

    @Override
    public void run() {
        try {
            this.controller.pollElevatorSystem();
        } catch (RemoteException e) {
            LoggerFactory.getLogger(ElevatorPolling.class).error("RMI could not be polled", e);
        }
    }
}