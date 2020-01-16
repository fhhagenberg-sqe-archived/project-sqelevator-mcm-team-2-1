package at.fhhagenberg.sqelevator.connection;

import sqelevator.IElevator;

import java.rmi.Naming;

public class RMIElevatorServiceFetcher {

    private static IElevator rmiInstance;

    private RMIElevatorServiceFetcher() { }


    public static IElevator getElevatorService() throws Exception {
        if(rmiInstance!=null) return rmiInstance;
        rmiInstance = (IElevator) Naming.lookup("rmi://localhost/ElevatorSim");
        return rmiInstance;
    }
}
