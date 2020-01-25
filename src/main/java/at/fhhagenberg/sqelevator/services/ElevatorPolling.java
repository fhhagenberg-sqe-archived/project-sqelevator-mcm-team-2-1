package at.fhhagenberg.sqelevator.services;

import at.fhhagenberg.sqelevator.controller.ElevatorController;

import java.rmi.RemoteException;

public class ElevatorPolling implements Runnable {

    private ElevatorController controller;

    public ElevatorPolling(ElevatorController control) {
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