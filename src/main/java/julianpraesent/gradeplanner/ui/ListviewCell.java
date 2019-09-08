package julianpraesent.gradeplanner.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import julianpraesent.gradeplanner.model.Course;

import java.io.IOException;

class ListviewCell extends ListCell<Course> {


    @FXML
    private Label lbl_listviewCellText;

    @FXML
    private Circle circle_listviewStatusIndicator;

    @FXML
    private HBox hbox_cell;

    private FXMLLoader loader;

    /**
     * displays the listviewCell fxml data instead of a simple text cell
     *
     * @param course the course that shall be displayed
     * @param empty  indicates wether the cell shall be displayed as empty
     */
    @Override
    protected void updateItem(Course course, boolean empty) {
        super.updateItem(course, empty);

        if (empty || course == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (this.loader == null) {
                this.loader = new FXMLLoader(getClass().getResource("/fxml/listviewCell.fxml"));
                this.loader.setController(this);

                try {
                    this.loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            this.lbl_listviewCellText.setText(getLabelText(course));
            if (course.isModified()) this.circle_listviewStatusIndicator.setFill(Color.ORANGE);
            else {
                switch (course.getGrade()) {
                    case SEHR_GUT:
                        this.circle_listviewStatusIndicator.setFill(Color.LIGHTGREEN);
                        break;

                    case NICHT_GENUEGEND:
                        this.circle_listviewStatusIndicator.setFill(Color.RED);
                        break;

                    default:
                        this.circle_listviewStatusIndicator.setFill(Color.BLUE);
                }
            }

            setText(null);
            setGraphic(this.hbox_cell);
        }
    }

    /**
     * generates the text that shall be displayed on the label
     *
     * @param course course of the listcell
     * @return generated string for label
     */
    private String getLabelText(Course course) {
        return course.getTitle() + " | " +
                course.getEcts() + " ECTS";
    }
}
