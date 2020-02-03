package at.fhhagenberg.sqelevator.connection;

import sqelevator.IElevator;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * This singleton class is used to request an open rmi connection to the remote RMI server.
 *
 * @author Martin Schneglberger
 */
public class RMIElevatorServiceFetcher {

    private static IElevator rmiInstance;

    private RMIElevatorServiceFetcher() { }

    /**
     * Returns a rmi connection to the remote RMI server
     * @return instance connected to the RMI server
     */
    public static IElevator getElevatorService() throws RemoteException, NotBoundException, MalformedURLException {
        if(rmiInstance!=null) return rmiInstance;
        rmiInstance = (IElevator) Naming.lookup("rmi://localhost/ElevatorSim");
        return rmiInstance;
    }
}
