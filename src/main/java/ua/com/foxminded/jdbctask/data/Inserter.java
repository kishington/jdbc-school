package ua.com.foxminded.jdbctask.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import ua.com.foxminded.jdbctask.university.Group;
import ua.com.foxminded.jdbctask.university.Student;

public class Inserter {

    static final String JDBC_DRIVER = "org.postgresql.Driver";
    static final String DB_URL = "jdbc:postgresql://localhost:5432/university";

    static final String USER = "Sasha";
    static final String PASSWORD = "password";
    
    static final String CREATE_GROUPS_TABLE_PATH = "/create_groups_table.sql";
    static final String CREATE_COURSES_TABLE_PATH = "/create_courses_table.sql";
    static final String CREATE_STUDENTS_TABLE_PATH = "/create_students_table.sql";
    
    static final String DROP_GROUPS_TABLE = "DROP TABLE IF EXISTS groups;";
    static final String DROP_COURSES_TABLE = "DROP TABLE IF EXISTS courses;";
    static final String DROP_STUDENTS_TABLE = "DROP TABLE IF EXISTS students;";
    
    public String fileToString(String filePath) throws IOException {
        StringBuilder output = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(filePath)))) {
            in.lines().forEach(line -> output.append(line + "\n"));
        } 
        return output.toString();
    }

    public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException {
        Inserter appStarter = new Inserter();
        appStarter.createTables();
        appStarter.insertGroups();
        appStarter.insertStudents();
        System.out.println("Succes");
    }
    
    public void insertGroups() throws SQLException, ClassNotFoundException {
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            stmt = conn.createStatement();
            Group group = new Group();
            
            for (int i = 0; i < DataGenerator.NUMBER_OF_GROUPS; i++) {
                group.setRandomGroup();
                int groupId = group.getId();
                String groupName = group.getName();
                String sql = "INSERT INTO groups VALUES ('" + groupId + "', '" + groupName + "');";
                stmt.executeUpdate(sql);
            }

        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }
    
    @Test
    void test() {
        DataGenerator dg = new DataGenerator();
        int[][] studentsToGroupsDistribution = dg.assignStudentsToGroups();
        
        Map<Integer, List<Integer>> groups = new HashMap<>();
        for (int groupId = 0; groupId < 10; groupId++) {
            List<Integer> students = new ArrayList<>();
            groups.put(groupId, students);
        }

        for (int studentId = 0; studentId < 200; studentId++) {
            int assigned = studentsToGroupsDistribution[studentId][0];
            int groupId = studentsToGroupsDistribution[studentId][1];
            if (assigned == 1) {
                List<Integer> students = groups.get(groupId);
                students.add(studentId);
                groups.put(groupId, students);
            }
        }
        Set< Map.Entry< Integer,List<Integer>> > st = groups.entrySet();    
        
        for (Map.Entry< Integer,List<Integer>> en:st) 
        { 
            System.out.println("groupId: " + en.getKey()+"   students: " + en.getValue().size()); 
            System.out.println(en.getValue() + "\n");
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
            DataGenerator dataGenerator = new DataGenerator();
            int[][] studentsToGroupsDistribution = dataGenerator.assignStudentsToGroups();
            for(int studentId = 0; studentId < DataGenerator.NUMBER_OF_STUDENTS; studentId++) {
                student.setRandomFullName();
                String firstName = student.getFirstName();
                String lastName = student.getLastName();
                boolean studentAssignedToGroup = studentsToGroupsDistribution[studentId][0] == DataGenerator.STUDENT_ASSIGNED;
                if (studentAssignedToGroup) {
                    int groupId = studentsToGroupsDistribution[studentId][1];
                    String sql = "INSERT INTO students VALUES ('" + studentId + "', '" + groupId + "', '" + firstName + "', '" + lastName + "');";
                    stmt.executeUpdate(sql);
                    System.out.println(firstName + " " + lastName);
                } else {
                    String sql = "INSERT INTO students (student_id, first_name, last_name) VALUES ('" + studentId + "', '" + firstName + "', '" + lastName + "');";
                    stmt.executeUpdate(sql);
                }
            }
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }
    
    public void createTables() throws SQLException, IOException, ClassNotFoundException {
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            stmt = conn.createStatement();
            
            stmt.executeUpdate(DROP_GROUPS_TABLE);
            String sql = fileToString(CREATE_GROUPS_TABLE_PATH);
            stmt.executeUpdate(sql);
            
            stmt.executeUpdate(DROP_COURSES_TABLE);
            sql = fileToString(CREATE_COURSES_TABLE_PATH);
            stmt.executeUpdate(sql);
            
            stmt.executeUpdate(DROP_STUDENTS_TABLE);
            sql = fileToString(CREATE_STUDENTS_TABLE_PATH);
            stmt.executeUpdate(sql);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }
}
