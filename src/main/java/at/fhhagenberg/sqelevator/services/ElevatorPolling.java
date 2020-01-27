package at.fhhagenberg.sqelevator.services;

import at.fhhagenberg.sqelevator.statemanagement.ElevatorManagement;

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
            e.printStackTrace();
        }
    }
}