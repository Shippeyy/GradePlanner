package julianpraesent.gradeplanner;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import julianpraesent.gradeplanner.helper.AppConstants;

public class Main extends Application {


    public static void main(String[] args) {
        // TODO: remove
        /*
        ArrayList<Course> courses = DataHandler.loadFile(AppConstants.PATH);

        try {
            ArrayList<Course> optimizedCourses = Analyzer.analyzeCourses(courses, 1);
            DataHandler.writeFile(optimizedCourses, AppConstants.PATH);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        */
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // TODO: implement GUI
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/main.fxml"));
        primaryStage.setTitle(AppConstants.TITLE + " - " + AppConstants.VERSION);
        primaryStage.setScene(new Scene(root, 900, 600));

        primaryStage.show();

    }
}
