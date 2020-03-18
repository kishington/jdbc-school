package ua.com.foxminded.jdbctask.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ua.com.foxminded.jdbctask.menu.Dialog;
import ua.com.foxminded.jdbctask.models.Course;
import ua.com.foxminded.jdbctask.models.Group;
import ua.com.foxminded.jdbctask.models.Student;


public class DataGenerator {
    public static final String DATA_GENERATED = "Data generated successfully.";
    private static final Random random = new Random();
    private static final List<String> dropAllTables = new ArrayList<>();
    static {
        dropAllTables.add(SqlQueryConstants.DROP_GROUPS_TABLE);
        dropAllTables.add(SqlQueryConstants.DROP_STUDENTS_TABLE);
        dropAllTables.add(SqlQueryConstants.DROP_COURSES_TABLE);
        dropAllTables.add(SqlQueryConstants.DROP_STUDENTS_COURSES_TABLE);
    }
   
    public String generateData() {
        DatabaseConnectionGetter connectionGetter = new DatabaseConnectionGetter();
        try (Connection connection = connectionGetter.getConnection()) {
            createTables(connection);
            insertGroups(connection);
            insertStudents(connection);
            insertCourses(connection);
            insertStudentsToCoursesRelations(connection);
        } catch (SQLException e) {
            return Dialog.DB_ACCSESS_PROBLEM;
        } catch (IOException e) {
            return Dialog.FILE_ACCSESS_PROBLEM;
        } catch (Exception e) {
            return Dialog.PROGRAM_ERROR;
        }
        return DATA_GENERATED;
    }
    
    private void createTables(Connection connection) throws SQLException, IOException {
        try (Statement stmt = connection.createStatement()) {
            for(String dropTable: dropAllTables) {
                stmt.executeUpdate(dropTable);
            }
            String createAllTables = fileToString(SqlQueryConstants.CREATE_ALL_TABLES_PATH);
            stmt.executeUpdate(createAllTables);
        }
    }
    
    private void insertGroups(Connection connection) throws SQLException {
        Group group = new Group();
        try (PreparedStatement insertGroup = connection.prepareStatement(SqlQueryConstants.INSERT_GROUP)) {
            for (int groupId = 0; groupId < Assigner.NUMBER_OF_GROUPS; groupId++) {
                setRandomGroupName(group);
                String groupName = group.getName();
                insertGroup.setInt(1, groupId);
                insertGroup.setString(2, groupName);
                insertGroup.executeUpdate();
            }
        }
    }
  
    private void insertStudents(Connection connection) throws SQLException {
        Assigner dataGenerator = new Assigner();
        int[][] studentsToGroupsDistribution = dataGenerator.assignStudentsToGroups();
        try (PreparedStatement insertAssignedStudent = connection
                .prepareStatement(SqlQueryConstants.INSERT_ASSIGNED_STUDENT);
                PreparedStatement insertNotAssignedStudent = connection
                        .prepareStatement(SqlQueryConstants.INSERT_NOT_ASSIGNED_STUDENT)) {
            for (int studentId = 0; studentId < Assigner.NUMBER_OF_STUDENTS; studentId++) {
                String firstName = getRandomFirstName();
                String lastName = getRandomLastName();
                boolean studentAssignedToGroup = studentsToGroupsDistribution[studentId][0] == Assigner.STUDENT_ASSIGNED;
                if (studentAssignedToGroup) {
                    int groupId = studentsToGroupsDistribution[studentId][1];
                    insertAssignedStudent.setInt(1, studentId);
                    insertAssignedStudent.setInt(2, groupId);
                    insertAssignedStudent.setString(3, firstName);
                    insertAssignedStudent.setString(4, lastName);
                    insertAssignedStudent.executeUpdate();
                } else {
                    insertNotAssignedStudent.setInt(1, studentId);
                    insertNotAssignedStudent.setString(2, firstName);
                    insertNotAssignedStudent.setString(3, lastName);
                    insertNotAssignedStudent.executeUpdate();
                }
            }
        }
    }
    
    private void insertCourses(Connection connection) throws SQLException {
        try (PreparedStatement insertCourse = connection.prepareStatement(SqlQueryConstants.INSERT_COURSE)) {
            int numberOfCourses = Course.getTotalNumberOfAvailableCourses();
            List<String> availableCourses = Course.getAvailableCourses();
            for (int courseId = 0; courseId < numberOfCourses; courseId++) {
                String courseName = availableCourses.get(courseId);
                insertCourse.setInt(1, courseId);
                insertCourse.setString(2, courseName);
                insertCourse.executeUpdate();
            }
        }
    } 
    
    private void insertStudentsToCoursesRelations(Connection connection) throws SQLException {
        Assigner assigner = new Assigner();
        int[][] studentsCourses = assigner.assignCoursesToStudents();
        try (PreparedStatement insertStudentCourseRelation = connection
                .prepareStatement(SqlQueryConstants.INSERT_STUDENT_COURSE_RELATION)) {
            for (int studentId = 0; studentId < Assigner.NUMBER_OF_STUDENTS; studentId++) {
                int[] coursesIds = studentsCourses[studentId];
                int numberOfCourses = coursesIds.length;
                insertStudentCourseRelation.setInt(1, studentId);
                for (int courseNumber = 0; courseNumber < numberOfCourses; courseNumber++) {
                    int courseId = coursesIds[courseNumber];
                    insertStudentCourseRelation.setInt(2, courseId);
                    insertStudentCourseRelation.executeUpdate();
                }
            }
        }
    }
    
    private String fileToString(String filePath) throws IOException {
        StringBuilder output = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(filePath)))) {
            in.lines().forEach(line -> output.append(line + "\n"));
        } 
        return output.toString();
    }
    
    private String getRandomFirstName() {
        int index = random.nextInt(Student.firstNames.size());
        return Student.firstNames.get(index);
    }
    
    private String getRandomLastName() {
        int index = random.nextInt(Student.lastNames.size());
        return Student.lastNames.get(index);
    }
    
    private void setRandomGroupName(Group group) {
        final String letters = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder groupName = new StringBuilder();
        
        int index = random.nextInt(letters.length());
        char randomChar = letters.charAt(index);
        groupName.append(randomChar);        
        index = random.nextInt(letters.length());
        randomChar = letters.charAt(index);
        groupName.append(randomChar);
       
        groupName.append('-');
        
        int randomDigit = random.nextInt(10);
        groupName.append(randomDigit);
        randomDigit = random.nextInt(10);
        groupName.append(randomDigit);
        
        String groupNameString = groupName.toString();
        group.setName(groupNameString);
    } 
}
