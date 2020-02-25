package ua.com.foxminded.jdbctask.menu;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import ua.com.foxminded.jdbctask.data.DataGenerator;
import ua.com.foxminded.jdbctask.data.Querier;
import ua.com.foxminded.jdbctask.university.Course;

public class Dialog {
    private static final String MAIN_MENU_MESSAGE = "Choose one of the following:\n" + "a. Find all groups with less or equals student count\n"
            + "b. Find all students related to course with given name\n" + "c. Add new student\n"
            + "d. Delete student by student_id\n" + "e. Assign a student to a course (from a list)\n"
            + "f. Remove the student from one of his or her courses\n"
            + "g. Exit the program";
    private static final String NO_SUCH_MENU = "No such menu available.";
    private static final String WANT_TO_CONTINUE =  "Do you want to continue (yes/no)?";
    private static final String BYE = "Bye!";

    private Querier querier = new Querier();
    private static DataGenerator dataGenerator = new DataGenerator();    

    public void start() throws SQLException, IOException {
        Scanner scanner = new Scanner(System.in);
        try(Connection connection = dataGenerator.getConnection()) {
            showMainMenu(connection, scanner);
        }
        scanner.close();
    }
    
    private void showMainMenu(Connection connection, Scanner scanner) throws SQLException {
        System.out.println(MAIN_MENU_MESSAGE);
        String option = scanner.nextLine();
        switch (option) {
        case "a":
            findGroupsStudentCountNoMoreThan(connection, scanner);
            break;
        case "b":
            findStudentsRelatedToCourse(connection, scanner);
            break;
        case "c":
            addNewStudent(connection, scanner);
            break;
        case "d":
            deleteStudentById(connection, scanner);
            break;
        case "e":
            assignStudentToCourse(connection, scanner);
            break;
        case "f":
            removeStudentFromCourse(connection, scanner);
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
    
    private void findGroupsStudentCountNoMoreThan(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter student count: ");
        int studentCount;
        try {
            studentCount  = scanner.nextInt();
            System.out.println();
            printGroupsStudentCountLessThan(connection, studentCount);
            System.out.println();
            scanner.nextLine();
            doYouWantToContinue(connection, scanner);
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Integer number expected.");
            scanner.nextLine();
            doYouWantToContinue(connection, scanner);
        }
    }
    
    private void findStudentsRelatedToCourse(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Select one of the folowing courses:");
        displayAvailableCourses();
        
        String courseName = scanner.nextLine();
        printStudentsRelatedToCourse(connection, courseName);
        doYouWantToContinue(connection, scanner);
    }
    
    private void addNewStudent(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();
        querier.addNewStudent(connection, firstName, lastName);
        System.out.println("Student added to the database.");
        doYouWantToContinue(connection, scanner);
    }
    
    private void deleteStudentById(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Enter student_id: ");
        try {
            int studentId = scanner.nextInt();
            if (!querier.isStudentAvailable(connection, studentId)) {
                System.out.println("This student is not on the database.");
                scanner.nextLine();
                doYouWantToContinue(connection, scanner);
            } else {
                querier.deleteStudent(connection, studentId);
                System.out.println("This student has been deleted from the database.");
                scanner.nextLine();
                doYouWantToContinue(connection, scanner);
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Integer number expected.");
            scanner.nextLine();
            doYouWantToContinue(connection, scanner);
        }
    }
    
    private void assignStudentToCourse(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Enter student_id: ");
        try {
            int studentId = scanner.nextInt();
            if (!querier.isStudentAvailable(connection, studentId)) {
                System.out.println("This student is not on the database.");
                scanner.nextLine();
                doYouWantToContinue(connection, scanner);
            } else {
                System.out.println("Select one of the folowing courses:");
                scanner.nextLine();
                displayAvailableCourses();
                String courseName = scanner.nextLine();
                List<String> availableCourses = Course.getAvailableCourses();
                if(!availableCourses.contains(courseName)) {
                    System.out.println("No such course is available.");
                    doYouWantToContinue(connection, scanner);
                } else {
                    querier.assignStudentToCourse(connection, studentId, courseName);
                    doYouWantToContinue(connection, scanner);
                }
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Integer number expected.");
            scanner.nextLine();
            doYouWantToContinue(connection, scanner);
        }
    }
    
    private void removeStudentFromCourse(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Enter student_id: ");
        try {
            int studentId = scanner.nextInt();
            if (!querier.isStudentAvailable(connection, studentId)) {
                System.out.println("This student is not on the database.");
                scanner.nextLine();
                doYouWantToContinue(connection, scanner);
            } else {
                System.out.println("Select one of the folowing courses:");
                scanner.nextLine();
                displayAvailableCourses();
                String courseName = scanner.nextLine();
                List<String> availableCourses = Course.getAvailableCourses();
                if(!availableCourses.contains(courseName)) {
                    System.out.println("No such course is available.");
                    doYouWantToContinue(connection, scanner);
                } else {
                    querier.removeStudentFromCourse(connection, studentId, courseName);
                    doYouWantToContinue(connection, scanner);
                }
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Integer number expected.");
            scanner.nextLine();
            doYouWantToContinue(connection, scanner);
        }
    }

    private void doYouWantToContinue(Connection connection, Scanner scanner) throws SQLException {  
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
    
    private void printGroupsStudentCountLessThan(Connection connection, int n) throws SQLException {
        String groups = querier.getGroupsStudentCountLessThan(connection, n);
        if (groups.length() == 0) {
            System.out.println("There is no groups with student count equals or less than " + n);
        } else {
            System.out.print(groups);
        }
    }
    
    private void printStudentsRelatedToCourse(Connection connection, String courseName) throws SQLException {
        List<String> availableCourses = Course.getAvailableCourses();
        if(availableCourses.contains(courseName)) {
            int courseId = availableCourses.indexOf(courseName);
            String students = querier.getStudentsRelatedToCourse(connection, courseId);
            System.out.println(students);
        } else {
            System.out.println("No such course is available.");
        }
    }

    public void displayAvailableCourses() {
        List<String> availableCourses = Course.getAvailableCourses();
        for (String courseName: availableCourses) {
            System.out.print(courseName + "  ");
        }
        System.out.println();
    }
}
