package at.fhhagenberg.sqelevator.connection;

import sqelevator.IElevator;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class RMIElevatorServiceFetcher {

    private static IElevator rmiInstance;

    private RMIElevatorServiceFetcher() { }

    public static IElevator getElevatorService() throws RemoteException, NotBoundException, MalformedURLException {
        if(rmiInstance!=null) return rmiInstance;
        rmiInstance = (IElevator) Naming.lookup("rmi://localhost/ElevatorSim");
        return rmiInstance;
    }
}
