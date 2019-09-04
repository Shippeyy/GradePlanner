package julianpraesent.gradeplanner.helper;

import julianpraesent.gradeplanner.model.Course;
import julianpraesent.gradeplanner.model.GradeEnum;
import julianpraesent.gradeplanner.model.WeightedCourse;

import java.util.ArrayList;
import java.util.Comparator;

import static julianpraesent.gradeplanner.helper.Helper.*;

public class Analyzer {

    /**
     * analyzes a list of courses and makes suggestions in order to reach a certain weighted grade average
     * @throws Exception
     * @param courses list of courses that shall be analyzed
     * @param targetAverage target weighted grade average that shall be achieved
     * @return optimized list of courses
     */
    public static ArrayList<Course> analyzeCourses(ArrayList<Course> courses, double targetAverage) throws Exception {
        ArrayList<Course> positiveCourses = removeNegativeGrades(courses);
        ArrayList<WeightedCourse> weightedCourses = weighCourses(positiveCourses);

        if(calcWeightedAverage(weightedCourses) <= targetAverage) return weightedCourseToCourse(weightedCourses);
        else return optimizeCourses(weightedCourses, targetAverage);
    }

    /**
     * optimizes a list of courses in order to hit a certain target average in the easiest way possible
     * @throws Exception
     * @param weightedCourses list of weighted courses that shall be optimized
     * @param targetAverage target weighted grade average that shall be achieved
     * @return list of optimized courses
     */
    private static ArrayList<Course> optimizeCourses(ArrayList<WeightedCourse> weightedCourses, double targetAverage) throws Exception {
        // TODO: check if there is a more elegant solution
        weightedCourses.sort(Comparator.comparing(WeightedCourse::getValue));
        for (int i = weightedCourses.size() - 1; i >= 0; i--) {
            WeightedCourse weightedCourse = weightedCourses.get(i);

            if (weightedCourse.getCourse().getGrade() == GradeEnum.SEHR_GUT ||
                    weightedCourse.getCourse().getGrade() == GradeEnum.TEILGENOMMEN_ERFOLGREICH
            ) continue;

            if (calcWeightedAverage(weightedCourses) <= targetAverage) return weightedCourseToCourse(weightedCourses);

            weightedCourse = improveWeightedCourse(weightedCourse);
            if (weightedCourse.getCourse().getGrade() == null)
                throw new Exception("ERROR - a fatal program error has occured, please contact the developer (getImprovedError = null)");
            weightedCourses.set(i, weightedCourse);
            weightedCourses = weighCourses(weightedCourseToCourse(weightedCourses));
            weightedCourses.sort(Comparator.comparing(WeightedCourse::getValue));
            i = weightedCourses.size();
        }
        throw new Exception("All courses are already optimized, target average is unreachable!");
    }

    /**
     * improves all courses that have a negative grade by one grade
     *
     * @param courses list of courses that may contain negative grades
     * @return list of courses without negative grades
     */
    private static ArrayList<Course> removeNegativeGrades(ArrayList<Course> courses) {
        for (Course course : courses) {
            if (course.getGrade() == GradeEnum.NICHT_GENUEGEND) course.setGrade(GradeEnum.GENUEGEND);
        }
        return courses;
    }

    /**
     * improves the grade of the given WeightedCourse by one if possible. Return null if course cannot be improved
     * @param weightedCourse weightedCourse that shall be improved
     * @return improved weighted course
     */
    private static WeightedCourse improveWeightedCourse(WeightedCourse weightedCourse) {

        if(weightedCourse == null) return null;

        switch (weightedCourse.getCourse().getGrade()) {
            case NICHT_GENUEGEND:
                weightedCourse.getCourse().setGrade(GradeEnum.GENUEGEND);
                break;
            case GENUEGEND:
                weightedCourse.getCourse().setGrade(GradeEnum.BEFRIEDIGEND);
                break;
            case BEFRIEDIGEND:
                weightedCourse.getCourse().setGrade(GradeEnum.GUT);
                break;
            case GUT:
                weightedCourse.getCourse().setGrade(GradeEnum.SEHR_GUT);
                break;
            case SEHR_GUT:
                return null;
        }

        WeightedCourse output = WeightedCourse.builder()
                .course(weightedCourse.getCourse())
                .value(weightedCourse.getCourse().getEcts() * gradeEnumToInt(weightedCourse.getCourse().getGrade()))
                .build();

        output.getCourse().setModified(true);

        return output;
    }

    /**
     * converts list of weighted courses to a list of courses
     * @param weightedCourses list of weighted courses
     * @return converted list
     */
    private static ArrayList<Course> weightedCourseToCourse(ArrayList<WeightedCourse> weightedCourses) {
        ArrayList<Course> courses = new ArrayList<>();

        weightedCourses.forEach(weightedCourse -> courses.add(weightedCourse.getCourse()));
        return courses;
    }
}
