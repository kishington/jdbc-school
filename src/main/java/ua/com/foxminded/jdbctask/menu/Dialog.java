package ua.com.foxminded.jdbctask.menu;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import ua.com.foxminded.jdbctask.data.DataGenerator;
import ua.com.foxminded.jdbctask.data.DatabaseConnectionGetter;
import ua.com.foxminded.jdbctask.data.Querier;
import ua.com.foxminded.jdbctask.models.Course;

public class Dialog {
    public static final String DB_ACCSESS_PROBLEM = "Sorry, problem with database access.";
    public static final String FILE_ACCSESS_PROBLEM = "Sorry, problem with file access.";
    public static final String CONTACT_SUPPORT = "Try to reload the program or contact support.";
    public static final String PROGRAM_ERROR = "Program error!";
    private static final String STUDENT_NOT_IN_DATABASE = "This student is not on the database.";
    private static final String SELECT_COURSE = "Select one of the folowing courses:";

    private static final String MAIN_MENU = "Choose one of the following:\n"
            + "a. Find all groups with less or equals student count\n"
            + "b. Find all students related to course with given name\n"
            + "c. Add new student\n"
            + "d. Delete student by student_id\n" + "e. Assign a student to a course (from a list)\n"
            + "f. Remove the student from one of his or her courses\n"
            + "g. Exit the program";
    private static final String ENTER_STUDENT_ID = "Enter student_id: ";
    private static final String NO_SUCH_COURSE = "No such course is available.";
    private static final String NO_SUCH_MENU = "No such menu available.";
    private static final String WANT_TO_CONTINUE = "Do you want to continue (yes/no)?";
    private static final String BYE = "Thank you!\nBye!";

    private Querier querier = new Querier();
    DatabaseConnectionGetter connectionGetter = new DatabaseConnectionGetter();

    public void start(String dataGenerationResult) {
        switch (dataGenerationResult) {
        case DB_ACCSESS_PROBLEM:
            System.out.println(Dialog.DB_ACCSESS_PROBLEM);
            System.out.println(Dialog.CONTACT_SUPPORT);
            break;
        case FILE_ACCSESS_PROBLEM:
            System.out.println(Dialog.FILE_ACCSESS_PROBLEM);
            System.out.println(Dialog.CONTACT_SUPPORT);
            break;
        case PROGRAM_ERROR:
            System.out.println(Dialog.PROGRAM_ERROR);
            System.out.println(Dialog.CONTACT_SUPPORT);
            break;
        case DataGenerator.DATA_GENERATED:
            Scanner scanner = new Scanner(System.in);
            try (Connection connection = connectionGetter.getConnection()) {
                showMainMenu(connection, scanner);
            } catch (SQLException e) {
                System.out.println(DB_ACCSESS_PROBLEM);
                System.out.println(CONTACT_SUPPORT);
            } catch (IOException e) {
                System.out.println(FILE_ACCSESS_PROBLEM);
                System.out.println(CONTACT_SUPPORT);
            } catch (Exception e) {
                System.out.println(PROGRAM_ERROR);
                System.out.println(CONTACT_SUPPORT);
            } finally {
                scanner.close();
            }
            break;
        }
    }

    private void showMainMenu(Connection connection, Scanner scanner) {
        boolean wantExitProgram = false;
        String option = MAIN_MENU;
        while (!wantExitProgram) {
            switch (option) {
            case MAIN_MENU:
                System.out.println(MAIN_MENU);
                option = scanner.nextLine();
                break;
            case "a":
                option = displayGroupsStudentCountNoMoreThan(connection, scanner);
                break;
            case "b":
                option = displayStudentsRelatedToCourse(connection, scanner);
                break;
            case "c":
                option = addNewStudent(connection, scanner);
                break;
            case "d":
                option = deleteStudentById(connection, scanner);
                break;
            case "e":
                option = assignStudentToCourse(connection, scanner);
                break;
            case "f":
                option = removeStudentFromCourse(connection, scanner);
                break;
            case "g":
                wantExitProgram = true;
                System.out.println(BYE);
                break;
            case NO_SUCH_MENU:
                System.out.println(NO_SUCH_MENU);
                option = askToContinueProgram(scanner);
                break;
            default:
                option = NO_SUCH_MENU;
                break;
            }
        }
    }

    private String displayGroupsStudentCountNoMoreThan(Connection connection, Scanner scanner) {
        System.out.print("Enter student count: ");
        try {
            int studentCount = scanner.nextInt();
            System.out.println();
            printGroupsStudentCountLessThan(connection, studentCount);
            System.out.println();
            scanner.nextLine();
            return askToContinueProgram(scanner);
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Integer number expected.");
            scanner.nextLine();
            return askToContinueProgram(scanner);
        } catch (SQLException e) {
            System.out.println(DB_ACCSESS_PROBLEM);
            scanner.nextLine();
            return askToContinueProgram(scanner);
        }
    }

    private String displayStudentsRelatedToCourse(Connection connection, Scanner scanner) {
        try {
            System.out.println(SELECT_COURSE);
            displayAvailableCourses();

            String courseName = scanner.nextLine();
            printStudentsRelatedToCourse(connection, courseName);
            return askToContinueProgram(scanner);
        } catch (SQLException e) {
            System.out.println(DB_ACCSESS_PROBLEM);
            scanner.nextLine();
            return askToContinueProgram(scanner);
        }
    }

    private String addNewStudent(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter first name: ");
            String firstName = scanner.nextLine();
            System.out.print("Enter last name: ");
            String lastName = scanner.nextLine();
            querier.addNewStudent(connection, firstName, lastName);
            System.out.println("Student added to the database.");
            return askToContinueProgram(scanner);
        } catch (SQLException e) {
            System.out.println(DB_ACCSESS_PROBLEM);
            scanner.nextLine();
            return askToContinueProgram(scanner);
        }
    }

    private String deleteStudentById(Connection connection, Scanner scanner) {
        System.out.println(ENTER_STUDENT_ID);
        try {
            int studentId = scanner.nextInt();
            if (!querier.isStudentAvailable(connection, studentId)) {
                System.out.println(STUDENT_NOT_IN_DATABASE);
                scanner.nextLine();
                return askToContinueProgram(scanner);
            } else {
                querier.deleteStudent(connection, studentId);
                System.out.println("This student has been deleted from the database.");
                scanner.nextLine();
                return askToContinueProgram(scanner);
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Integer number expected.");
            scanner.nextLine();
            return askToContinueProgram(scanner);
        } catch (SQLException e) {
            System.out.println(DB_ACCSESS_PROBLEM);
            scanner.nextLine();
            return askToContinueProgram(scanner);
        }
    }

    private String assignStudentToCourse(Connection connection, Scanner scanner) {
        System.out.println(ENTER_STUDENT_ID);
        try {
            int studentId = scanner.nextInt();
            if (!querier.isStudentAvailable(connection, studentId)) {
                System.out.println(STUDENT_NOT_IN_DATABASE);
                scanner.nextLine();
                return askToContinueProgram(scanner);
            } else {
                System.out.println(SELECT_COURSE);
                scanner.nextLine();
                displayAvailableCourses();
                String courseName = scanner.nextLine();
                int assignmentResult = querier.assignStudentToCourse(connection, studentId, courseName);
                displayStudentToCourseAssignementResult(assignmentResult);
                return askToContinueProgram(scanner);
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Integer number expected.");
            scanner.nextLine();
            return askToContinueProgram(scanner);
        } catch (SQLException e) {
            System.out.println(DB_ACCSESS_PROBLEM);
            scanner.nextLine();
            return askToContinueProgram(scanner);
        }
    }

    private void displayStudentToCourseAssignementResult(int removalResult) {
        switch (removalResult) {
        case Querier.COURSE_NOT_AVAILABLE:
            System.out.println(NO_SUCH_COURSE);
            break;
        case Querier.STUDENT_ALREADY_ASSIGNED:
            System.out.println("Student is already assigned to this course.");
            break;
        case Querier.STUDENT_ASSIGNED_SUCCESSFULLY:
            System.out.println("The student has been assigned to selected course.");
            break;
        }
    }

    private String removeStudentFromCourse(Connection connection, Scanner scanner) {
        System.out.println(ENTER_STUDENT_ID);
        try {
            int studentId = scanner.nextInt();
            if (!querier.isStudentAvailable(connection, studentId)) {
                System.out.println(STUDENT_NOT_IN_DATABASE);
                scanner.nextLine();
                return askToContinueProgram(scanner);
            } else {
                System.out.println(SELECT_COURSE);
                scanner.nextLine();
                displayAvailableCourses();
                String courseName = scanner.nextLine();
                int removalResult = querier.removeStudentFromCourse(connection, studentId, courseName);
                displayStudentFromCourseRemovalResult(removalResult);
                return askToContinueProgram(scanner);
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Integer number expected.");
            scanner.nextLine();
            return askToContinueProgram(scanner);
        } catch (SQLException e) {
            System.out.println(DB_ACCSESS_PROBLEM);
            scanner.nextLine();
            return askToContinueProgram(scanner);
        }
    }

    private void displayStudentFromCourseRemovalResult(int removalResult) {
        switch (removalResult) {
        case Querier.COURSE_NOT_AVAILABLE:
            System.out.println(NO_SUCH_COURSE);
            break;
        case Querier.STUDENT_NOT_ASSIGNED_TO_COURSE:
            System.out.println("The student is not assigned to this course.");
            break;
        case Querier.STUDENT_REMOVED_FROM_COURSE:
            System.out.println("The student has been removed from selected course.");
            break;
        }
    }

    private String askToContinueProgram(Scanner scanner) {
        System.out.println(WANT_TO_CONTINUE);
        String answer = scanner.nextLine();
        if (answer.equals("yes")) {
            return MAIN_MENU;
        } else {
            if (answer.equals("no")) {
                return "g";
            } else {
                return NO_SUCH_MENU;
            }
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
        if (availableCourses.contains(courseName)) {
            int courseId = availableCourses.indexOf(courseName);
            String students = querier.getStudentsRelatedToCourse(connection, courseId);
            System.out.println(students);
        } else {
            System.out.println(NO_SUCH_COURSE);
        }
    }

    public void displayAvailableCourses() {
        List<String> availableCourses = Course.getAvailableCourses();
        for (String courseName : availableCourses) {
            System.out.print(courseName + "  ");
        }
        System.out.println();
    }
}
