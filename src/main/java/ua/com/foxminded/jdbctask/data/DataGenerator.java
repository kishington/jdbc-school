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
        Group group = new Group();
        try (PreparedStatement insertGroup = connection.prepareStatement(SqlQueryConstants.INSERT_GROUP)) {
            for (int groupId = 0; groupId < Assigner.getNumberOfGroups(); groupId++) {
                setRandomGroupName(group);
                String groupName = group.getName();
                insertGroup.setInt(1, groupId);
                insertGroup.setString(2, groupName);
                insertGroup.executeUpdate();
            }
        }
    }
  
    public void insertStudents(Connection connection) throws SQLException {
        Student student = new Student();
        Assigner dataGenerator = new Assigner();
        int[][] studentsToGroupsDistribution = dataGenerator.assignStudentsToGroups();
        try (PreparedStatement insertAssignedStudent = connection
                .prepareStatement(SqlQueryConstants.INSERT_ASSIGNED_STUDENT);
                PreparedStatement insertNotAssignedStudent = connection
                        .prepareStatement(SqlQueryConstants.INSERT_NOT_ASSIGNED_STUDENT)) {
            for (int studentId = 0; studentId < Assigner.getNumberOfStudents(); studentId++) {
                setRandomFullName(student);
                String firstName = student.getFirstName();
                String lastName = student.getLastName();
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
    
    void insertCourses(Connection connection) throws SQLException {
        try (PreparedStatement insertCourse = connection.prepareStatement(SqlQueryConstants.INSERT_COURSE)) {
            int numberOfCourses = Course.courses.size();
            for (int courseId = 0; courseId < numberOfCourses; courseId++) {
                String courseName = Course.courses.get(courseId);
                insertCourse.setInt(1, courseId);
                insertCourse.setString(2, courseName);
                insertCourse.executeUpdate();
            }
        }
    } 
    
    void insertStudentsToCoursesRelations(Connection connection) throws SQLException {
        Assigner assigner = new Assigner();
        int[][] studentsCourses = assigner.assignCoursesToStudents();
        try (PreparedStatement insertStudentCourseRelation = connection
                .prepareStatement(SqlQueryConstants.INSERT_STUDENT_COURSE_RELATION)) {
            for (int studentId = 0; studentId < Assigner.getNumberOfStudents(); studentId++) {
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
    
    public String fileToString(String filePath) throws IOException {
        StringBuilder output = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(filePath)))) {
            in.lines().forEach(line -> output.append(line + "\n"));
        } 
        return output.toString();
    }
    
    public void setRandomFullName(Student student) {
        int index = random.nextInt(Student.FIRST_NAMES.length);
        String firstName = Student.FIRST_NAMES[index];
        student.setFirstName(firstName); 
        
        index = random.nextInt(Student.LAST_NAMES.length);
        String lastName = Student.LAST_NAMES[index];
        student.setLastName(lastName); 
    }
    
    public void setRandomGroupName(Group group) {
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
