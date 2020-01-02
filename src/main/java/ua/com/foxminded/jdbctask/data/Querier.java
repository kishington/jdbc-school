package ua.com.foxminded.jdbctask.data;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ua.com.foxminded.jdbctask.university.Course;
import ua.com.foxminded.jdbctask.university.Group;

public class Querier {
    
    private static final String COURSE_NOT_AVAILABLE = "No such course is available.";

    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
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
            
            List<Integer> courses = q.getStudentCourses(connection, 0);
            System.out.println(courses);
            
            q.assignStudentToCourse(connection, 2, "Chemistry");
            
            q.removeStudentFromCourse(connection, 1, "Botany");
        }
    }
    
    void removeStudentFromCourse(Connection connection, int studentId, String courseName) throws SQLException {
        List<Integer> studentCourses = getStudentCourses(connection, studentId);
        List<String> availableCourses = Course.getAvailableCourses();
        boolean courseAvailable = availableCourses.contains(courseName);
        if (!courseAvailable) {
            System.out.println(COURSE_NOT_AVAILABLE);
        } else {
            int courseId = getCourseId(connection, courseName);
            boolean isStudentAssignedToCourse = studentCourses.contains(courseId);
            if (!isStudentAssignedToCourse) {
                System.out.println("Student is not assigned to this course.");
            } else {
                try (PreparedStatement removalOfStudentFromCourse = connection
                        .prepareStatement(SqlQueryConstants.REMOVE_STUDENT_FROM_COURSE)) {
                    removalOfStudentFromCourse.setInt(1, studentId);
                    removalOfStudentFromCourse.setInt(2, courseId);
                    removalOfStudentFromCourse.executeUpdate();
                }
            }
        }
    }
    
    
    void assignStudentToCourse(Connection connection, int studentId, String courseName) throws SQLException {
        List<String> availableCourses = Course.getAvailableCourses();
        boolean courseAvailable = availableCourses.contains(courseName);
        if(!courseAvailable) {
            System.out.println(COURSE_NOT_AVAILABLE);
        } else {
            int courseId = getCourseId(connection, courseName);
            List<Integer> studentCourses = getStudentCourses(connection, studentId);
            if (studentCourses.contains(courseId)) {
                System.out.println("Student is already assigned to this course.");
            } else {
                try (PreparedStatement insertStudentCourseRelation = connection.prepareStatement(SqlQueryConstants.INSERT_STUDENT_COURSE_RELATION)) {
                    insertStudentCourseRelation.setInt(1, studentId);
                    insertStudentCourseRelation.setInt(2, courseId);
                    insertStudentCourseRelation.executeUpdate();
                }        
            }
        }
    }    
    
    List<Integer> getStudentCourses(Connection connection, int studentId) throws SQLException {
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
        try (PreparedStatement insertNewStudent = connection.prepareStatement(SqlQueryConstants.INSERT_STUDENT)) {
            insertNewStudent.setString(1, firstName);
            insertNewStudent.setString(2, lastName);
            insertNewStudent.executeUpdate();
        }
    }

    void deleteStudent(Connection connection, int studentId) throws SQLException {
        try (PreparedStatement insterNewStudent = connection.prepareStatement(SqlQueryConstants.DELETE_STUDENT_BY_ID)) {
            insterNewStudent.setInt(1, studentId);
            insterNewStudent.executeUpdate();
        }
    }

    public Map<Group, Integer> getGroupsStudentCountLessThan(Connection connection, int n) throws SQLException {
        Map<Group, Integer> groups = new HashMap<>();
        try (PreparedStatement groupsStudentCountLessThan = connection
                .prepareStatement(SqlQueryConstants.SELECT_GROUPS_STUDENT_COUNT_NOT_MORE_THAN)) {
            groupsStudentCountLessThan.setInt(1, n);
            try (ResultSet rs = groupsStudentCountLessThan.executeQuery()) {
                while (rs.next()) {
                    Group group = new Group();
                    int groupId = rs.getInt(1);
                    group.setId(groupId);
                    String groupName = rs.getString(2);
                    group.setName(groupName);
                    int studentCount = rs.getInt(3);
                    groups.put(group, Integer.valueOf(studentCount));
                    //String print = String.format("%1$-15s %2$-22s %3$-20s","groupId: " + groupId, "groupName: " + groupName, "studentCount: " + studentCount);
                    //System.out.println(print);
                }
            }
        }
        return groups;
    }

    public void getStudentsRelatedToCourse(Connection connection, int courseId) throws SQLException {
        try (PreparedStatement studentsRelatedToCourse = connection
                .prepareStatement(SqlQueryConstants.SELECT_STUDENTS_RELATED_TO_GIVEN_COURSE)) {
            studentsRelatedToCourse.setInt(1, courseId);
            try (ResultSet rs = studentsRelatedToCourse.executeQuery()) {
                while (rs.next()) {
                    String studentId = rs.getString(1);
                    String firstName = rs.getString(2);
                    String lastName = rs.getString(3);
                    //System.out.println("studentId: " + studentId + "     firstName: " + firstName
                    //        + "         lastName: " + lastName);
                    String print = String.format("%1$10s %2$10s %3$10s","studentId: " + studentId, "firstName: " + firstName, "lastName: " + lastName);
                    System.out.println(print);
                }
            }
        }
    }

    int getCourseId(Connection connection, String courseName) throws SQLException {
        try (PreparedStatement selectCourseId = connection.prepareStatement(SqlQueryConstants.SELECT_COURSE_ID)) {
            selectCourseId.setString(1, courseName);
            try (ResultSet rs = selectCourseId.executeQuery()) {
                rs.next();
                int courseId = rs.getInt(1);
                return courseId;
            }
        }
    }
}
