package julianpraesent.gradeplanner;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import julianpraesent.gradeplanner.helper.Helper;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/root.fxml"));
        primaryStage.setTitle(Helper.getApplicationHeader());
        primaryStage.setScene(new Scene(root, 900, 600));

        primaryStage.show();

    }
}
