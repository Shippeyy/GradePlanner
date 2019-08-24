package julianpraesent.gradeplanner.helper;

import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import julianpraesent.gradeplanner.model.Course;
import julianpraesent.gradeplanner.model.GradeEnum;
import julianpraesent.gradeplanner.model.WeightedCourse;

import java.util.ArrayList;

/**
 * Helper class that outsources rather complex calculations that are used globally (shall improve code readability)
 */
public class Helper {

    /**
     * updates the data which is displayed by the metric labels (bottom of the ui)
     *
     * @param listView             listview containing all the courses
     * @param weightedAverageLabel label which displays the weighted grade average
     * @param totalEctsLabel       label which displays the total amount of ects
     */
    public static void updateMetricLabels(ListView<Course> listView, Label weightedAverageLabel, Label totalEctsLabel) {
        ArrayList<Course> courses = new ArrayList<>(listView.getItems());
        ArrayList<WeightedCourse> weightedCourses = weighCourses(courses);

        weightedAverageLabel.setText(AppConstants.PREFIX_WEIGHTED_AVERAGE + calcWeightedAverage(weightedCourses));
        totalEctsLabel.setText(AppConstants.PREFIX_TOTAL_ECTS + weightedCourses.stream().mapToInt(WeightedCourse::getEcts).sum());
    }

    /**
     * converts a gradeEnum to the according int
     *
     * @param gradeEnum
     * @return int value of gradeEnum
     */
    public static int gradeEnumToInt(GradeEnum gradeEnum) {
        switch (gradeEnum) {
            case SEHR_GUT:
                return 1;
            case GUT:
                return 2;
            case BEFRIEDIGEND:
                return 3;
            case GENUEGEND:
                return 4;
            case NICHT_GENUEGEND:
                return 5;
            default:
                return 0;
        }
    }

    /**
     * converts an integer to the according gradeEnum
     *
     * @param number
     * @return
     */
    public static GradeEnum intToGradeEnum(int number) {
        switch (number) {
            case 1:
                return GradeEnum.SEHR_GUT;
            case 2:
                return GradeEnum.GUT;
            case 3:
                return GradeEnum.BEFRIEDIGEND;
            case 4:
                return GradeEnum.GENUEGEND;
            case 5:
                return GradeEnum.NICHT_GENUEGEND;
            default:
                return null;
        }
    }

    /**
     * calculates the weighted average of a list of courses
     *
     * @param weightedList list of weighted courses of which the average shall be calculated
     * @return weighted average
     */
    public static double calcWeightedAverage(ArrayList<WeightedCourse> weightedList) {
        int sum = weightedList.stream().mapToInt(WeightedCourse::getValue).sum();
        int ects = weightedList.stream().mapToInt(WeightedCourse::getEcts).sum();

        return (double) sum / ects;
    }

    /**
     * weights a list of given courses by calculating their amount of ects * grade
     *
     * @param courses list of courses which shall be weighed
     * @return list of weighted courses
     */
    public static ArrayList<WeightedCourse> weighCourses(ArrayList<Course> courses) {
        ArrayList<WeightedCourse> weightedCourses = new ArrayList<>();

        for (Course course : courses) {
            if (gradeEnumToInt(course.getGrade()) == 0) course.setGrade(GradeEnum.NICHT_GENUEGEND);
            weightedCourses.add(new WeightedCourse(course, course.getEcts() * gradeEnumToInt(course.getGrade())));
        }

        return weightedCourses;
    }
}
