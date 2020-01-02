package ua.com.foxminded.jdbctask.menu;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import ua.com.foxminded.jdbctask.data.DataGenerator;
import ua.com.foxminded.jdbctask.data.Querier;
import ua.com.foxminded.jdbctask.data.SqlQueryConstants;
import ua.com.foxminded.jdbctask.data.Visualiser;
import ua.com.foxminded.jdbctask.university.Course;
import ua.com.foxminded.jdbctask.university.Group;

public class Dialog {
    private static final String MAIN_MENU_MESSAGE = "Choose one of the following:\n" + "a. Find all groups with less or equals student count\n"
            + "b. Find all students related to course with given name\n" + "c. Add new student\n"
            + "d. Delete student by STUDENT_ID\n" + "e. Add a student to the course (from a list)\n"
            + "f. Remove the student from one of his or her courses\n"
            + "g. Exit the programm";
    private static final String NO_SUCH_MENU = "No such menu available.";
    private static final String WANT_TO_CONTINUE =  "Do you want to continue (yes/no)?";
    private static final String BYE = "Bye!";

    Querier querier = new Querier();
    Visualiser visualiser = new Visualiser();
    static DataGenerator dataGenerator = new DataGenerator();    
    
    public static void main(String[] args) throws SQLException, IOException {
        Dialog d = new Dialog();
        Scanner scanner = new Scanner(System.in);
        try(Connection connection = dataGenerator.getConnection()) {
            d.showMainMenu(connection, scanner);
        }
        scanner.close();
    }
    
    void showMainMenu(Connection connection, Scanner scanner) throws SQLException {
        System.out.println(MAIN_MENU_MESSAGE);
        String option = scanner.nextLine();
        switch (option) {
        case "a":
            System.out.print("Enter student count: ");
            int studentCount  = scanner.nextInt();
            System.out.println();
            printGroupsStudentCountLessThan(connection, studentCount);
            System.out.println();
            scanner.nextLine();
            doYouWantToContinue(connection, scanner);
            break;
        case "b":
            System.out.println("Select one of the folowing courses:");
            List<String> availableCourses = Course.getAvailableCourses();
            for (String courseName: availableCourses) {
                System.out.print(courseName + "  ");
            }
            System.out.println();
            String course = scanner.nextLine();
            System.out.println(course);
            break;
        case "c":
            System.out.println("c");
            break;
        case "d":
            System.out.println("d");
            break;
        case "e":
            System.out.println("e");
            break;
        case "f":
            System.out.println("f");
            break;
        case "g":
            System.out.println(BYE);
            break;
        default:
            System.out.println(NO_SUCH_MENU);
            doYouWantToContinue(connection, scanner);
            break;
        }
    }

    void doYouWantToContinue(Connection connection, Scanner scanner) throws SQLException {  
        System.out.println(WANT_TO_CONTINUE);
        String answer = scanner.nextLine();
        switch (answer) {
        case "yes":
            showMainMenu(connection, scanner);
            break;
        case "no":
            System.out.println(BYE);
            break;
        default:
            System.out.println(NO_SUCH_MENU);
            doYouWantToContinue(connection, scanner);
            break;
        }
    }
    
    public void printGroupsStudentCountLessThan(Connection connection, int n) throws SQLException {
        Map<Group, Integer> groups = querier.getGroupsStudentCountLessThan(connection, n);
        // System.out.println(groups);
        if (groups.size() == 0) {
            System.out.println("There is no groups with student count equals or less than " + n);
        } else {
            groups.forEach((group, studentCount) -> {
                int groupId = group.getId();
                String groupName = group.getName();
                String toPrint = String.format("%1$-15s %2$-22s %3$-20s", "groupId: " + groupId,
                        "groupName: " + groupName, "studentCount: " + studentCount);
                System.out.println(toPrint);
            });
        }
    }
    
    public void printStudentsRelatedToCourse(Connection connection, String courseName) {
        List<String> availableCourses = Course.getAvailableCourses();
        if(availableCourses.contains(courseName)) {
            
        } else {
            System.out.println("No such course is available.");
        }
    }
    
//    public void getStudentsRelatedToCourse(Connection connection, int courseId) throws SQLException {
//        try (PreparedStatement studentsRelatedToCourse = connection
//                .prepareStatement(SqlQueryConstants.SELECT_STUDENTS_RELATED_TO_GIVEN_COURSE)) {
//            studentsRelatedToCourse.setInt(1, courseId);
//            try (ResultSet rs = studentsRelatedToCourse.executeQuery()) {
//                while (rs.next()) {
//                    String studentId = rs.getString(1);
//                    String firstName = rs.getString(2);
//                    String lastName = rs.getString(3);
//                    //System.out.println("studentId: " + studentId + "     firstName: " + firstName
//                    //        + "         lastName: " + lastName);
//                    String print = String.format("%1$10s %2$10s %3$10s","studentId: " + studentId, "firstName: " + firstName, "lastName: " + lastName);
//                    System.out.println(print);
//                }
//            }
//        }
//    }

}
