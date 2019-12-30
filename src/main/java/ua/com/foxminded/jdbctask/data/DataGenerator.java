package ua.com.foxminded.jdbctask.data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Random;

import ua.com.foxminded.jdbctask.university.Course;
import ua.com.foxminded.jdbctask.university.Group;
import ua.com.foxminded.jdbctask.university.Student;

public class DataGenerator {
    
    private static Random random = new Random();
   
    public void generateData() throws SQLException, IOException {
        try (Connection connection = getConnection()){
            createTables(connection);
            insertGroups(connection);
            insertStudents(connection);
            insertCourses(connection);
            insertStudentsToCoursesRelations(connection);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    Connection getConnection() throws ClassNotFoundException {
        String jdbcDriver = null;
        String dbUrl = null;
        String user = null;
        String password = null;
        try (InputStream input = new FileInputStream("src/main/resources/config.properties")) {
            Properties properties = new Properties();
            properties.load(input);
            jdbcDriver = properties.getProperty("jdbcDriver");
            dbUrl = properties.getProperty("dbUrl");
            user = properties.getProperty("dbUser");
            password = properties.getProperty("dbPassword");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Connection connection = null;
        try {
            Class.forName(jdbcDriver);
            connection = DriverManager.getConnection(dbUrl, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        } 
        return connection;
    }
    
    public void createTables(Connection connection) throws SQLException, IOException {
        try (Statement stmt = connection.createStatement()) {

            stmt.executeUpdate(SqlQueryConstants.DROP_GROUPS_TABLE);
            String sql = fileToString(SqlQueryConstants.CREATE_GROUPS_TABLE_PATH);
            stmt.executeUpdate(sql);

            stmt.executeUpdate(SqlQueryConstants.DROP_STUDENTS_TABLE);
            sql = fileToString(SqlQueryConstants.CREATE_STUDENTS_TABLE_PATH);
            stmt.executeUpdate(sql);

            stmt.executeUpdate(SqlQueryConstants.DROP_COURSES_TABLE);
            sql = fileToString(SqlQueryConstants.CREATE_COURSES_TABLE_PATH);
            stmt.executeUpdate(sql);

            stmt.executeUpdate(SqlQueryConstants.DROP_STUDENTS_COURSES_TABLE);
            sql = fileToString(SqlQueryConstants.CREATE_STUDENTS_COURSES_TABLE_PATH);
            stmt.executeUpdate(sql);
        }
    }
    
    public void insertGroups(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            Group group = new Group();      
            for (int i = 0; i < Assigner.NUMBER_OF_GROUPS; i++) {
                group.setRandomGroup();
                int groupId = group.getId();
                String groupName = group.getName();
                String sql = "INSERT INTO groups VALUES ('" + groupId + "', '" + groupName + "');";
                stmt.executeUpdate(sql);
            }
        } 
    }
  
    public void insertStudents(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()){
      
            Student student = new Student();
            Assigner dataGenerator = new Assigner();
            int[][] studentsToGroupsDistribution = dataGenerator.assignStudentsToGroups();
            for(int studentId = 0; studentId < Assigner.NUMBER_OF_STUDENTS; studentId++) {
                student.setRandomFullName();
                String firstName = student.getFirstName();
                String lastName = student.getLastName();
                boolean studentAssignedToGroup = studentsToGroupsDistribution[studentId][0] == Assigner.STUDENT_ASSIGNED;
                if (studentAssignedToGroup) {
                    int groupId = studentsToGroupsDistribution[studentId][1];
                    String sql = "INSERT INTO students VALUES ('" + studentId + "', '" + groupId + "', '" + firstName + "', '" + lastName + "');";
                    stmt.executeUpdate(sql);
                } else {
                    String sql = "INSERT INTO students (student_id, first_name, last_name) VALUES ('" + studentId + "', '" + firstName + "', '" + lastName + "');";
                    stmt.executeUpdate(sql);
                }
            }
        }
    }
    
    void insertCourses(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()){ 
            int numberOfCourses = Course.courses.size();
            
            for(int courseId = 0; courseId < numberOfCourses; courseId++) {
                String courseName = Course.courses.get(courseId);
                String sql = "INSERT INTO courses (course_id, course_name) VALUES ('" + courseId + "', '" + courseName + "');";
                stmt.executeUpdate(sql);
            } 
        }
    }
    
    
    void insertStudentsToCoursesRelations(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()){
            Assigner assigner = new Assigner();
            int[][] studentsCourses = assigner.assignCoursesToStudents();
            
            for (int studentId = 0; studentId < Assigner.NUMBER_OF_STUDENTS; studentId++) {
                int[] coursesIds = studentsCourses[studentId];
                int numberOfCourses = coursesIds.length;
                for(int courseNumber = 0; courseNumber < numberOfCourses; courseNumber++) {
                    int courseId = coursesIds[courseNumber];
                    String sql = "INSERT INTO students_courses VALUES ('" + studentId + "', '" + courseId + "');";
                    stmt.executeUpdate(sql);
                }
            }
        }
    }
    
    public String fileToString(String filePath) throws IOException {
        StringBuilder output = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(filePath)))) {
            in.lines().forEach(line -> output.append(line + "\n"));
        } 
        return output.toString();
    }
}
