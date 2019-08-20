package julianpraesent.gradeplanner.helper;

import julianpraesent.gradeplanner.model.Course;
import julianpraesent.gradeplanner.model.GradeEnum;
import julianpraesent.gradeplanner.model.TypeEnum;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class DataHandler {

    /**
     * reads a csv file from a given path and converts it into a list of type Course
     * @param path
     * @return converted csv file
     */
    public static ArrayList<Course> loadFile(String path) {
        ArrayList<Course> courses = new ArrayList<Course>();
        try {
            Scanner scanner = new Scanner(new File(path));
            while (scanner.hasNextLine()) {
                String[] data = scanner.nextLine().split(";");

                Course helper = Course.builder()
                        .id(data[0])
                        .title(data[1])
                        .type(TypeEnum.valueOf(data[2]))
                        .ects(Integer.parseInt(data[3]))
                        .grade(GradeEnum.valueOf(data[4]))
                        .graded(Boolean.parseBoolean(data[5]))
                        .locked(Boolean.parseBoolean(data[6]))
                        .build();

                courses.add(helper);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return courses;
    }

    /**
     * writes a csv file to a given path with a list of courses
     * @param courses
     * @param path
     */
    public static void writeFile(ArrayList<Course> courses, String path) {
        try {
            FileWriter fileWriter = new FileWriter(path);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            for (Course course: courses) {
                StringBuilder exportedCourse = new StringBuilder();

                exportedCourse.append(course.getId());
                exportedCourse.append(";");
                exportedCourse.append(course.getTitle());
                exportedCourse.append(";");
                exportedCourse.append(course.getType());
                exportedCourse.append(";");
                exportedCourse.append(course.getEcts());
                exportedCourse.append(";");
                exportedCourse.append(course.getGrade());
                exportedCourse.append(";");
                exportedCourse.append(course.isGraded());
                exportedCourse.append(";");
                exportedCourse.append(course.isLocked());

                printWriter.println(exportedCourse);
            }

            printWriter.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
