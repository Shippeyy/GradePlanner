package julianpraesent.gradeplanner.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import julianpraesent.gradeplanner.helper.Analyzer;
import julianpraesent.gradeplanner.helper.AppConstants;
import julianpraesent.gradeplanner.helper.DataHandler;
import julianpraesent.gradeplanner.model.Course;
import julianpraesent.gradeplanner.model.LoglevelEnum;

import java.util.ArrayList;
import java.util.List;

public class Controller {

    private ObservableList<String> listViewItems = FXCollections.observableArrayList();

    @FXML
    private ListView<Course> lv_courses;

    @FXML
    private Label lbl_avg;

    @FXML
    private Label lbl_totalEcts;

    @FXML
    private CheckBox chb_lock;

    @FXML
    private CheckBox chb_graded;

    @FXML
    private ChoiceBox choicebox_type;

    @FXML
    private TextField tf_title;

    @FXML
    private TextField tf_ects;

    @FXML
    private TextField tf_grade;

    @FXML
    private TextArea txta_log;

    /**
     * exports all courses that are displayed in the listview to a fixed path
     *
     * @param event
     */
    @FXML
    protected void exportCourses(ActionEvent event) {
        List<Course> courseList = this.lv_courses.getItems();
        try {
            DataHandler.writeFile(new ArrayList<>(courseList), AppConstants.PATH);
        } catch (Exception e) {
            log(e.getMessage(), LoglevelEnum.ERROR);
        }

    }

    /**
     * imports courses from a fixed path
     * @param event
     */
    @FXML
    protected void importCourses(ActionEvent event) {
        try {
            ArrayList<Course> importedCourses = DataHandler.loadFile(AppConstants.PATH);
            updateListview(importedCourses);
        } catch (Exception e) {
            log(e.getMessage(), LoglevelEnum.ERROR);
        }
    }

    /**
     * updates the entries of the listview in the ui (existing content will be deleted)
     *
     * @param courses
     */
    private void updateListview(ArrayList<Course> courses) {
        this.lv_courses.getItems().clear();
        this.lv_courses.getItems().addAll(courses);

        this.lv_courses.setCellFactory(param -> new ListCell<Course>() {
            protected void updateItem(Course course, boolean empty) {
                super.updateItem(course, empty);

                if (empty || course == null || course.getTitle() == null) setText(null);
                else setText(course.getTitle());
            }
        });

        log("courses have been updated", LoglevelEnum.SUCCESS);
    }

    @FXML
    protected void createCourse(ActionEvent event) {
        // TODO: implement method
    }

    /**
     * analyses the loaded courses
     *
     * @param event
     */
    @FXML
    protected void anaylzeCourses(ActionEvent event) {
        ArrayList<Course> courses = new ArrayList<>(this.lv_courses.getItems());

        try {
            log("analysis started", LoglevelEnum.INFO);
            ArrayList<Course> optimizedCourses = Analyzer.analyzeCourses(courses, 1);
            log("analysis finished", LoglevelEnum.INFO);
            updateListview(optimizedCourses);
        } catch (Exception e) {
            log(e.getMessage(), LoglevelEnum.ERROR);
        }
    }

    /**
     * logs a given text with a certain prefix (according to the given loglevel) to the ui
     *
     * @param message
     * @param loglevel
     */
    @FXML
    private void log(String message, LoglevelEnum loglevel) {
        String prefix = "";

        switch (loglevel) {
            case INFO:
                prefix = "INFO - ";
                break;
            case SUCCESS:
                prefix = "SUCCESS - ";
                break;
            case ERROR:
                prefix = "ERROR - ";
        }
        this.txta_log.appendText(prefix + message + "\n");
    }

    /**
     * quits the application
     * @param event
     */
    @FXML
    protected void quit(ActionEvent event) {
        System.exit(0);
    }
}
