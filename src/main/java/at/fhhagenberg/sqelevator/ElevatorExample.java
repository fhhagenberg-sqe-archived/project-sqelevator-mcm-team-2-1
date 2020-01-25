package at.fhhagenberg.sqelevator;

import at.fhhagenberg.sqelevator.connection.RMIElevatorServiceFetcher;
import at.fhhagenberg.sqelevator.controller.ElevatorController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sqelevator.IElevator;

import java.rmi.RemoteException;


public class ElevatorExample extends Application {

  @Override
  public void start(Stage primaryStage) throws Exception {
    Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("elevator_control.fxml"));
    primaryStage.setTitle("Elevator Control 2000");
    primaryStage.setScene(new Scene(root, 800, 500));
    primaryStage.show();
  }

  public static void main(String[] args) {
    try {
      IElevator rmiInstance = RMIElevatorServiceFetcher.getElevatorService();
      ElevatorController controller = new ElevatorController(rmiInstance);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}