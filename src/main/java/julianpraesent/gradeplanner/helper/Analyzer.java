package julianpraesent.gradeplanner.helper;

import julianpraesent.gradeplanner.model.Course;
import julianpraesent.gradeplanner.model.GradeEnum;
import julianpraesent.gradeplanner.model.WeightedCourse;

import java.util.ArrayList;
import java.util.Comparator;

public class Analyzer {

    /**
     * analyzes a list of courses and makes suggestions in order to reach a certain weighted grade average
     * @param courses
     * @return
     */
    public static ArrayList<Course> analyzeCourses(ArrayList<Course> courses, double targetAverage) throws Exception {
        ArrayList<WeightedCourse> weightedCourses = new ArrayList<>();

        for (Course course: courses) {
            if(gradeEnumToInt(course.getGrade()) == 0) course.setGrade(GradeEnum.NICHT_GENUEGEND);
            weightedCourses.add(new WeightedCourse(course, course.getEcts() * gradeEnumToInt(course.getGrade())));
        }

        weightedCourses.sort(Comparator.comparing(WeightedCourse::getValue));

        if(calcWeightedAverage(weightedCourses) <= targetAverage) return weightedCourseToCourse(weightedCourses);
        else return optimizeCourses(weightedCourses, targetAverage);
    }

    /**
     * optimizes a list of courses in order to hit a certain target average in the easiest way possible
     * @param weightedCourses
     * @param targetAverage
     * @return
     */
    private static ArrayList<Course> optimizeCourses(ArrayList<WeightedCourse> weightedCourses, double targetAverage) throws Exception {
        if(calcWeightedAverage(weightedCourses) <= targetAverage) return weightedCourseToCourse(weightedCourses);
        WeightedCourse improvedWeightedCourse = null;

        // TODO: check if loop can be simplified
        for(WeightedCourse weightedCourse: weightedCourses) {
            if(improvedWeightedCourse != null) break;
            if(weightedCourse.getCourse().isLocked()) continue;
            improvedWeightedCourse = improveWeightedCourse(weightedCourses.get(weightedCourses.size()-1));
            weightedCourses.set(weightedCourses.size()-1, improvedWeightedCourse);
        }
        // if no course can be improved
        if(improvedWeightedCourse == null) throw new Exception("All courses are already optimized, target average is unreachable!");
        return optimizeCourses(weightedCourses, targetAverage);
    }

    /**
     * improves the grade of the given WeightedCourse by one if possible. Return null if course cannot be improved
     * @param weightedCourse
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

        return WeightedCourse.builder()
                .course(weightedCourse.getCourse())
                .value(weightedCourse.getCourse().getEcts() * gradeEnumToInt(weightedCourse.getCourse().getGrade()))
                .build();
    }

    /**
     * converts a gradeEnum to the according int
     * @param gradeEnum
     * @return int value of gradeEnum
     */
    private static int gradeEnumToInt(GradeEnum gradeEnum) {
        switch (gradeEnum) {
            case SEHR_GUT: return 1;
            case GUT: return 2;
            case BEFRIEDIGEND: return 3;
            case GENUEGEND: return 4;
            case NICHT_GENUEGEND: return 5;
            default: return 0;
        }
    }

    /**
     * calculates the weighted average of a list of courses
     * @param weightedList
     * @return weighted average
     */
    private static double calcWeightedAverage(ArrayList<WeightedCourse> weightedList) {
        int sum = weightedList.stream().mapToInt(WeightedCourse::getValue).sum();
        int ects = 0;

        // TODO: check if loop can be simplified
        for(WeightedCourse weightedCourse: weightedList) {
            ects+= weightedCourse.getCourse().getEcts();
        }

        return (double) sum / ects;
    }

    /**
     * converts ArrayList<WeightedCourse> to ArrayList<Course>
     * @param weightedCourses
     * @return converted list
     */
    private static ArrayList<Course> weightedCourseToCourse(ArrayList<WeightedCourse> weightedCourses) {
        ArrayList<Course> courses = new ArrayList<>();

        weightedCourses.forEach(weightedCourse -> courses.add(weightedCourse.getCourse()));
        return courses;
    }
}
