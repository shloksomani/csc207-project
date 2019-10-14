package viewer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Viewer extends Application {

  /**
   * Launch the GUI.
   *
   * @param args pass in arguments
   */
  public static void main(String[] args) {
    launch(args);
  }

  /**
   * Start the GUI.
   *
   * @param stage the initial stage of the GUI
   */
  @Override
  public void start(Stage stage) {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("viewer.fxml"));
    Parent root = null;
    try {
      root = loader.load();
    } catch (IOException e) {
      e.printStackTrace();
    }
    ViewerController controller = loader.getController();
    controller.setup(stage);

    Scene scene = new Scene(root, 900, 600);

    stage.setTitle("Tagger");
    stage.setScene(scene);
    stage.show();
  }
}
