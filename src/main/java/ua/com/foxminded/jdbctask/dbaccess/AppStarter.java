package ua.com.foxminded.jdbctask.dbaccess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class AppStarter {

    static final String JDBC_DRIVER = "org.postgresql.Driver";
    static final String DB_URL = "jdbc:postgresql://localhost:5432/university";

    static final String USER = "Sasha";
    static final String PASS = "password";
    
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

    public static void main(String[] args) {
        AppStarter runner = new AppStarter();
        Connection conn = null;
        Statement stmt = null;
        try {
            Class cl = Class.forName(JDBC_DRIVER);
            System.out.println(cl.getName());
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
            String sql = "DROP TABLE IF EXISTS groups;";
            String sql3 = runner.fileToString(CREATE_GROUPS_TABLE_PATH);
            stmt.executeUpdate(sql3);
            

        } catch (SQLException se) {

            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    conn.close();
            } catch (SQLException se) {
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        System.out.println("Succes");
    }
}
