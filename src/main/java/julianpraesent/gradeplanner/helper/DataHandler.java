package julianpraesent.gradeplanner.helper;

import julianpraesent.gradeplanner.model.Course;
import julianpraesent.gradeplanner.model.GradeEnum;
import julianpraesent.gradeplanner.model.TypeEnum;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

public class DataHandler {

    /**
     * reads a csv file from a given path and converts it into a list of courses
     * @param path path of the input file
     * @return list of loaded courses
     */
    public static ArrayList<Course> loadFile(String path) {
        ArrayList<Course> courses = new ArrayList<>();

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

            return courses;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * writes a csv file to a given path with a list of courses
     * @throws IOException if the given file does not exist
     * @param courses list of courses that shall be written to the specified file
     * @param path path of the export file
     */
    public static void writeFile(ArrayList<Course> courses, String path) throws IOException {

        FileWriter fileWriter = new FileWriter(path);
        PrintWriter printWriter = new PrintWriter(fileWriter);

        for (Course course : courses) {
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

    /**
     * reads the certificate XLSX export from the TISS website and converts it into a list of courses
     * @param path path of the input file
     * @return list of loaded courses
     * @throws IOException if the given file does not exist
     */
    public static ArrayList<Course> loadTissXlsx(String path) throws IOException {
        Workbook workbook = WorkbookFactory.create(new File(path));
        Sheet sheet = workbook.getSheetAt(0);
        ArrayList<Course> courses = new ArrayList<>();

        sheet.forEach(row -> {
            // skipping meta rows (rows that don´t contain relevant data)
            if (
                    row.getRowNum() == 0 ||
                            row.getRowNum() == 1 ||
                            row.getRowNum() == 2
            ) return;

            Course helper = Course.builder()
                    .id(UUID.randomUUID().toString())
                    .title(row.getCell(0).getStringCellValue())
                    .type(Helper.stringToTypeEnum(row.getCell(1).getStringCellValue()))
                    .ects((int) row.getCell(3).getNumericCellValue())
                    .grade(Helper.intToGradeEnum((int) row.getCell(5).getNumericCellValue()))
                    .graded(true)
                    .locked(false)
                    .build();

            courses.add(helper);
        });

        return courses;
    }
}
