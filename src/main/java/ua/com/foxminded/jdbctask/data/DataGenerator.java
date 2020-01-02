package ua.com.foxminded.jdbctask.data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import ua.com.foxminded.jdbctask.university.Course;
import ua.com.foxminded.jdbctask.university.Group;
import ua.com.foxminded.jdbctask.university.Student;

public class DataGenerator {
    private static final String DB_PROPERTIES_PATH = "src/main/resources/config.properties";
    private static final Random random = new Random();
   
    public void generateData() throws SQLException, IOException {
        try (Connection connection = getConnection()) {
            createTables(connection);
            insertGroups(connection);
            insertStudents(connection);
            insertCourses(connection);
            insertStudentsToCoursesRelations(connection);
        }
    }

    public Connection getConnection() throws IOException, SQLException {
        String jdbcDriver = null;
        String dbUrl = null;
        String user = null;
        String password = null;
        try (InputStream input = new FileInputStream(DB_PROPERTIES_PATH)) {
            Properties properties = new Properties();
            properties.load(input);
            jdbcDriver = properties.getProperty("jdbcDriver");
            dbUrl = properties.getProperty("dbUrl");
            user = properties.getProperty("dbUser");
            password = properties.getProperty("dbPassword");
        }
        Connection connection = null;
        try {
            Class.forName(jdbcDriver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        connection = DriverManager.getConnection(dbUrl, user, password);
        return connection;
    }
    
    private void createTables(Connection connection) throws SQLException, IOException {
        try (Statement stmt = connection.createStatement()) {
            String dropAllTables = SqlQueryConstants.DROP_GROUPS_TABLE + SqlQueryConstants.DROP_STUDENTS_TABLE
                    + SqlQueryConstants.DROP_COURSES_TABLE + SqlQueryConstants.DROP_STUDENTS_COURSES_TABLE;
            stmt.executeUpdate(dropAllTables);
            for (int i = 0; i < SqlQueryConstants.NUMBER_OF_TABLES; i++) {
                String createTable = fileToString(SqlQueryConstants.TABLES_TO_CREATE_PATHS[i]);
                stmt.executeUpdate(createTable);
            }
        }
    }
    
    private void insertGroups(Connection connection) throws SQLException {
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
  
    private void insertStudents(Connection connection) throws SQLException {
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
    
    private String fileToString(String filePath) throws IOException {
        StringBuilder output = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(filePath)))) {
            in.lines().forEach(line -> output.append(line + "\n"));
        } 
        return output.toString();
    }
    
    private void setRandomFullName(Student student) {
        int index = random.nextInt(Student.FIRST_NAMES.length);
        String firstName = Student.FIRST_NAMES[index];
        student.setFirstName(firstName); 
        
        index = random.nextInt(Student.LAST_NAMES.length);
        String lastName = Student.LAST_NAMES[index];
        student.setLastName(lastName); 
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
