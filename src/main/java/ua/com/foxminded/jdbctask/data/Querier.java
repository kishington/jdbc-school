package ua.com.foxminded.jdbctask.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Querier {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        DataGenerator dg = new DataGenerator();
        Querier q = new Querier();
        try (Connection connection = dg.getConnection()) {

            int n = 20;
            q.getGroupsStudentCountLessThan(connection, n);

            String courseName = "Physics";
            int courseId = q.getCourseId(connection, courseName);
            q.getStudentsRelatedToCourse(connection, courseId);

            q.addNewStudent(connection, "Mihail", "Iarov");

            int studentId = 200;
            q.deleteStudent(connection, studentId);
            
            List<Integer> courses = q.getStudentsCourses(connection, 0);
            System.out.println(courses);
        }
    }
    
    void assignStudentToCourse(Connection connection, int studentId, String courseName) throws SQLException {
        int courseId = getCourseId(connection, courseName);

        List<Integer> studentCourses = getStudentsCourses(connection, studentId);
        if (studentCourses.contains(courseId)) {
            System.out.println("Student already assigned to this course");
        } else {
            
        }
    }    
    
    List<Integer> getStudentsCourses(Connection connection, int studentId) throws SQLException {
        List<Integer> courses = new ArrayList<>();
        try (PreparedStatement selectStudentsCourses = connection.prepareStatement(SqlQueryConstants.SELECT_STUDENTS_COURSES)) {
            selectStudentsCourses.setInt(1, studentId);
            try(ResultSet rs = selectStudentsCourses.executeQuery()) {
                while (rs.next()) {
                    int courseId = rs.getInt(1);
                    courses.add(courseId);
                }
            }
        }
        return courses;
    }

    void addNewStudent(Connection connection, String firstName, String lastName) throws SQLException {
        try (PreparedStatement insterNewStudent = connection.prepareStatement(SqlQueryConstants.INSERT_STUDENT)) {
            insterNewStudent.setString(1, firstName);
            insterNewStudent.setString(2, lastName);
            insterNewStudent.executeUpdate();
        }
    }

    void deleteStudent(Connection connection, int studentId) throws SQLException {
        try (PreparedStatement insterNewStudent = connection.prepareStatement(SqlQueryConstants.DELETE_STUDENT_BY_ID)) {
            insterNewStudent.setInt(1, studentId);
            insterNewStudent.executeUpdate();
        }
    }

    void getGroupsStudentCountLessThan(Connection connection, int n) throws SQLException {
        try (PreparedStatement groupsStudentCountLessThan = connection
                .prepareStatement(SqlQueryConstants.GROUPS_STUDENT_COUNT_NOT_MORE_THAN)) {
            groupsStudentCountLessThan.setInt(1, n);
            try (ResultSet rs = groupsStudentCountLessThan.executeQuery()) {
                while (rs.next()) {
                    String groupId = rs.getString(1);
                    String groupName = rs.getString(2);
                    String studentCount = rs.getString(3);
                    System.out.println("groupId: " + groupId + "     groupName: " + groupName
                            + "         studentCount: " + studentCount);
                }
            }
        }
    }

    void getStudentsRelatedToCourse(Connection connection, int courseId) throws SQLException {
        try (PreparedStatement studentsRelatedToCourse = connection
                .prepareStatement(SqlQueryConstants.SELECT_STUDENTS_RELATED_TO_GIVEN_COURSE)) {
            studentsRelatedToCourse.setInt(1, courseId);
            try (ResultSet rs = studentsRelatedToCourse.executeQuery()) {
                while (rs.next()) {
                    String studentId = rs.getString(1);
                    String firstName = rs.getString(2);
                    String lastName = rs.getString(3);
                    System.out.println("studentId: " + studentId + "     firstName: " + firstName
                            + "         lastName: " + lastName);
                }
            }
        }
    }

    int getCourseId(Connection connection, String courseName) throws SQLException {
        try (PreparedStatement selectCourseId = connection.prepareStatement(SqlQueryConstants.SELECT_COURSE_ID)) {
            selectCourseId.setString(1, courseName);
            try (ResultSet rs = selectCourseId.executeQuery()) {
                rs.next();
                int course_id = rs.getInt(1);
                return course_id;
            }
        }
    }
}
