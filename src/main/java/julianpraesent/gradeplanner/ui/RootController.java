package julianpraesent.gradeplanner.ui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import julianpraesent.gradeplanner.helper.Analyzer;
import julianpraesent.gradeplanner.helper.AppConstants;
import julianpraesent.gradeplanner.helper.DataHandler;
import julianpraesent.gradeplanner.helper.Helper;
import julianpraesent.gradeplanner.model.Course;
import julianpraesent.gradeplanner.model.GradeEnum;
import julianpraesent.gradeplanner.model.LoglevelEnum;
import julianpraesent.gradeplanner.model.TypeEnum;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RootController {

    @FXML
    private Label lbl_appHeading;

    @FXML
    private ListView<Course> lv_courses;

    @FXML
    private Label lbl_avg;

    @FXML
    private Label lbl_totalEcts;

    @FXML
    private TextArea txta_log;

    @FXML
    private SplitPane splitpane;

    @FXML
    private ChoiceBox<TypeEnum> choicebox_type;

    @FXML
    private ChoiceBox<GradeEnum> choicebox_grade;

    @FXML
    private TextField tf_title;

    @FXML
    private TextField tf_ects;

    @FXML
    private CheckBox chb_lock;

    @FXML
    private CheckBox chb_graded;

    @FXML
    private TextField tf_targetAverage;

    /**
     * initializes the ui; is automatically called as first method by javafx
     */
    public void initialize() {
        this.lbl_appHeading.setText(Helper.getApplicationHeader());

        this.choicebox_type.getItems().addAll(
                TypeEnum.VU,
                TypeEnum.VO,
                TypeEnum.SE,
                TypeEnum.UE,
                TypeEnum.PR
        );

        this.choicebox_grade.getItems().addAll(
                GradeEnum.SEHR_GUT,
                GradeEnum.GUT,
                GradeEnum.BEFRIEDIGEND,
                GradeEnum.GENUEGEND,
                GradeEnum.NICHT_GENUEGEND,
                GradeEnum.ERFOLGREICH_TEILGENOMMEN
        );

        this.tf_targetAverage.setText(AppConstants.DEFAULT_WEIGHTED_GRADE_AVERAGE);

        // adding a numeric filter for a double in the range of [1.0;4.9]
        this.tf_targetAverage.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("^([1-4]([.][0-9]?)?)?")) {
                    tf_targetAverage.setText(oldValue);
                }
            }
        });

        // adding a numeric filter for an integer except 0
        this.tf_ects.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("([1-9]\\d*)?")) {
                    tf_ects.setText(oldValue);
                }
            }
        });
    }

    /**
     * updates the entries of the listview in the ui (existing content will be deleted)
     *
     * @param courses list of courses that shall be displayed
     */
    private void updateListview(ArrayList<Course> courses) {

        this.lv_courses.getItems().clear();
        this.lv_courses.getItems().addAll(courses);
        this.lv_courses.setCellFactory(param -> new ListviewCell());

        Helper.updateMetricLabels(this.lv_courses, this.lbl_avg, this.lbl_totalEcts);
        log("courses have been updated", LoglevelEnum.SUCCESS);
    }

    /**
     * logs a given text with a certain prefix (according to the given loglevel) to the ui
     *
     * @param message message that shall be displayed
     * @param loglevel loglevel for the according log prefix
     */
    @FXML
    private void log(String message, LoglevelEnum loglevel) {
        String prefix = "";

        switch (loglevel) {
            case INFO:
                prefix = AppConstants.PREFIX_LOGLEVEL_INFO;
                break;
            case SUCCESS:
                prefix = AppConstants.PREFIX_LOGLEVEL_SUCCESS;
                break;
            case ERROR:
                prefix = AppConstants.PREFIX_LOGLEVEL_ERROR;
        }
        this.txta_log.appendText(prefix + message + "\n");
    }

    /**
     * analyses the loaded courses
     */
    @FXML
    protected void analyze() {
        ArrayList<Course> courses = new ArrayList<>(this.lv_courses.getItems());

        try {
            log("analysis started", LoglevelEnum.INFO);
            double targetAverage = Double.parseDouble(this.tf_targetAverage.getText());
            ArrayList<Course> optimizedCourses = Analyzer.analyzeCourses(courses, targetAverage);
            log("analysis finished", LoglevelEnum.INFO);
            updateListview(optimizedCourses);
        } catch (NumberFormatException e) {
            log("target grade average has to be a number", LoglevelEnum.ERROR);
        } catch (Exception e) {
            log(e.getMessage(), LoglevelEnum.ERROR);
        }
    }

    /**
     * exports all courses that are displayed in the listview to a path (which is selected by the user)
     */
    @FXML
    protected void exportData() {
        List<Course> courseList = this.lv_courses.getItems();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save data file");

        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(filter);

        try {
            File file = fileChooser.showSaveDialog(this.splitpane.getScene().getWindow());
            log("writing file", LoglevelEnum.INFO);
            DataHandler.writeFile(new ArrayList<>(courseList), file.getPath());
            log("export finished", LoglevelEnum.SUCCESS);
        } catch (Exception e) {
            log(e.getMessage(), LoglevelEnum.ERROR);
        }
    }

    /**
     * imports courses from a path (which is selected by the user)
     */
    @FXML
    protected void importData() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select data file");

        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(filter);

        try {
            File file = fileChooser.showOpenDialog(this.splitpane.getScene().getWindow());
            log("loading selected file", LoglevelEnum.INFO);
            ArrayList<Course> importedCourses = DataHandler.loadFile(file.getPath());
            // in case the user cancelled or closed the file chooser
            if (importedCourses == null) {
                log("file selection cancelled by user", LoglevelEnum.INFO);
                return;
            }
            updateListview(importedCourses);
        } catch (Exception e) {
            log(e.getMessage(), LoglevelEnum.ERROR);
        }
    }

    /**
     * import courses from a TISS export file (TISS is a platform of the Technical University of Vienna)
     */
    @FXML
    protected void importTissData() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select TISS export file");

        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("XLSX files (*.xlsx)", "*.xlsx");
        fileChooser.getExtensionFilters().add(filter);

        try {
            File file = fileChooser.showOpenDialog(this.splitpane.getScene().getWindow());
            log("loading selected file", LoglevelEnum.INFO);
            ArrayList<Course> importedCourses = DataHandler.loadTissXlsx(file.getPath());
            updateListview(importedCourses);
        } catch (Exception e) {
            log(e.getMessage(), LoglevelEnum.ERROR);
        }
    }

    /**
     * saves the selected course and any changes that were made
     */
    @FXML
    protected void saveCourse() {
        ArrayList<Course> courses = new ArrayList<>(this.lv_courses.getItems());
        Course course = this.lv_courses.getSelectionModel().getSelectedItem();
        try {
            course.setTitle(this.tf_title.getText());
            course.setEcts(Integer.parseInt(this.tf_ects.getText()));
            course.setGrade(this.choicebox_grade.getValue());
            course.setGraded(this.chb_graded.isSelected());
            course.setLocked(this.chb_lock.isSelected());
            course.setType(choicebox_type.getSelectionModel().getSelectedItem());
            course.setModified(false);

            courses.remove(this.lv_courses.getSelectionModel().getSelectedItem());
            courses.add(course);
            this.updateListview(courses);

        } catch (Exception e) {
            log("invalid input", LoglevelEnum.ERROR);
        }
    }

    /**
     * displays the details of a course that was clicked on via the listview
     */
    @FXML
    protected void displaySelectedCourse() {
        Course selectedCourse = this.lv_courses.getSelectionModel().getSelectedItem();
        if (selectedCourse == null) return;

        this.tf_title.setText(selectedCourse.getTitle());
        this.tf_ects.setText(Integer.toString(selectedCourse.getEcts()));
        this.choicebox_grade.getSelectionModel().select(selectedCourse.getGrade());
        this.choicebox_type.getSelectionModel().select(selectedCourse.getType());
        this.chb_graded.setSelected(selectedCourse.isGraded());
        this.chb_lock.setSelected(selectedCourse.isLocked());
    }

    /**
     * deletes a selected course
     */
    @FXML
    protected void deleteCourse() {
        ArrayList<Course> courses = new ArrayList<>(this.lv_courses.getItems());
        Course selectedCourse = this.lv_courses.getSelectionModel().getSelectedItem();
        if (selectedCourse != null && courses.contains(selectedCourse)) {
            courses.remove(selectedCourse);
            this.updateListview(courses);
        }
    }

    /**
     * adds a new course to the listview
     */
    @FXML
    protected void addCourse() {
        try {
            Course course = Course.builder()
                    .id(UUID.randomUUID().toString())
                    .title(this.tf_title.getText())
                    .ects(Integer.parseInt(this.tf_ects.getText()))
                    .grade(this.choicebox_grade.getValue())
                    .graded(this.chb_graded.isSelected())
                    .locked(this.chb_lock.isSelected())
                    .type(choicebox_type.getSelectionModel().getSelectedItem())
                    .build();

            ArrayList<Course> courses = new ArrayList<>(this.lv_courses.getItems());
            courses.add(course);

            updateListview(courses);
            log("course was added", LoglevelEnum.SUCCESS);
        } catch (Exception e) {
            log("invalid input", LoglevelEnum.ERROR);
        }

    }
}
