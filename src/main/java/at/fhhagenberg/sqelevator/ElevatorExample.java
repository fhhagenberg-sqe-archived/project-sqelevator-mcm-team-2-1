package at.fhhagenberg.sqelevator;

import at.fhhagenberg.sqelevator.connection.RMIElevatorServiceFetcher;
import at.fhhagenberg.sqelevator.controller.ElevatorManagement;
import at.fhhagenberg.sqelevator.gui.ElevatorController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sqelevator.IElevator;


public class ElevatorExample extends Application {

  @Override
  public void start(Stage primaryStage) throws Exception {
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("elevator.fxml"));


    try {
      IElevator rmiInstance = RMIElevatorServiceFetcher.getElevatorService();
      ElevatorManagement management = new ElevatorManagement(rmiInstance);

      Parent root = (Parent)fxmlLoader.load();
      ElevatorController controller = fxmlLoader.<ElevatorController>getController();
      controller.setUiActionListener(management);
      management.addListener(controller);

      primaryStage.setTitle("Elevator Control 2000");
      primaryStage.setScene(new Scene(root, 1600, 900));
      primaryStage.show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    launch();
    // check this: https://stackoverflow.com/questions/52682195/how-to-get-javafx-and-java-11-working-in-intellij-idea
    // problem was that the debugging did not work when using the maven javafx:run job, even if I started the job to be debugged
    // thus, if you wish to debug the application, follow this guide
  }
}