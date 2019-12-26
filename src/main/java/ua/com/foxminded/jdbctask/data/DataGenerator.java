package ua.com.foxminded.jdbctask.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import ua.com.foxminded.jdbctask.university.Course;
import ua.com.foxminded.jdbctask.university.Group;
import ua.com.foxminded.jdbctask.university.Student;

public class DataGenerator {

    static final String JDBC_DRIVER = "org.postgresql.Driver";
    static final String DB_URL = "jdbc:postgresql://localhost:5432/university";

    static final String USER = "Sasha";
    static final String PASSWORD = "password";
    
    static final String CREATE_GROUPS_TABLE_PATH = "/create_groups_table.sql";
    static final String CREATE_COURSES_TABLE_PATH = "/create_courses_table.sql";
    static final String CREATE_STUDENTS_TABLE_PATH = "/create_students_table.sql";
    static final String CREATE_STUDENTS_COURSES_TABLE_PATH = "/create_students_courses_table.sql";
    
    static final String CREATE_STUDENTS_COURSES_TABLE = "CREATE TABLE students_courses (\n" + 
                                                            "student_id integer REFERENCES students,\n" + 
                                                            "course_id integer REFERENCES courses,\n" + 
                                                            "PRIMARY KEY (student_id, course_id)\n" + 
                                                        ");";
    static final String DROP_GROUPS_TABLE = "DROP TABLE IF EXISTS groups;";
    static final String DROP_COURSES_TABLE = "DROP TABLE IF EXISTS courses;";
    static final String DROP_STUDENTS_TABLE = "DROP TABLE IF EXISTS students;";
    static final String DROP_STUDENTS_COURSES_TABLE = "DROP TABLE IF EXISTS students_courses";

    public void generateData() throws SQLException, IOException {
        try {
            createTables();
            insertGroups();
            insertStudents();
            insertCourses();
            insertStudentsToCoursesRelations();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public void createTables() throws SQLException, IOException, ClassNotFoundException {
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            stmt = conn.createStatement();
            
            stmt.executeUpdate(DROP_STUDENTS_COURSES_TABLE);

            stmt.executeUpdate(DROP_GROUPS_TABLE);
            String sql = fileToString(CREATE_GROUPS_TABLE_PATH);
            stmt.executeUpdate(sql);
            
            stmt.executeUpdate(DROP_STUDENTS_TABLE);
            sql = fileToString(CREATE_STUDENTS_TABLE_PATH);
            stmt.executeUpdate(sql);
            
            stmt.executeUpdate(DROP_COURSES_TABLE);
            sql = fileToString(CREATE_COURSES_TABLE_PATH);
            stmt.executeUpdate(sql);
            
            sql = fileToString(CREATE_STUDENTS_COURSES_TABLE_PATH);
            stmt.executeUpdate(sql);
            //stmt.executeUpdate(CREATE_STUDENTS_COURSES_TABLE);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } finally {
                    if (conn != null) {
                        conn.close();
                    }
                }
            }
        }
    }
    
    public void insertGroups() throws SQLException, ClassNotFoundException {
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            stmt = conn.createStatement();
            Group group = new Group();
            
            for (int i = 0; i < Assigner.NUMBER_OF_GROUPS; i++) {
                group.setRandomGroup();
                int groupId = group.getId();
                String groupName = group.getName();
                String sql = "INSERT INTO groups VALUES ('" + groupId + "', '" + groupName + "');";
                stmt.executeUpdate(sql);
            }

        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } finally {
                    if (conn != null) {
                        conn.close();
                    }
                }
            }
        }
    }
  
    public void insertStudents() throws ClassNotFoundException, SQLException {
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            stmt = conn.createStatement();
            
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
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } finally {
                    if (conn != null) {
                        conn.close();
                    }
                }
            }
        }
    }
    
    void insertCourses() throws SQLException, ClassNotFoundException {
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            stmt = conn.createStatement();
   
            int numberOfCourses = Course.COURSES.length;
            
            for(int courseId = 0; courseId < numberOfCourses; courseId++) {
                String courseName = Course.COURSES[courseId];
                String sql = "INSERT INTO courses (course_id, course_name) VALUES ('" + courseId + "', '" + courseName + "');";
                stmt.executeUpdate(sql);
            }
            
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } finally {
                    if (conn != null) {
                        conn.close();
                    }
                }
            }
        }
    }
    
    void insertStudentsToCoursesRelations() throws SQLException, ClassNotFoundException {
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            stmt = conn.createStatement();
           
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
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } finally {
                    if (conn != null) {
                        conn.close();
                    }
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
