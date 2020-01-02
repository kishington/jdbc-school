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

    public void addNewStudent(Connection connection, String firstName, String lastName) throws SQLException {
        try (PreparedStatement insertNewStudent = connection.prepareStatement(SqlQueryConstants.INSERT_STUDENT)) {
            insertNewStudent.setString(1, firstName);
            insertNewStudent.setString(2, lastName);
            insertNewStudent.executeUpdate();
        }
    }

    public void deleteStudent(Connection connection, int studentId) throws SQLException {
        try (PreparedStatement insterNewStudent = connection.prepareStatement(SqlQueryConstants.DELETE_STUDENT_BY_ID)) {
            insterNewStudent.setInt(1, studentId);
            insterNewStudent.executeUpdate();
        }
    }
    
    public boolean isStudentAvailable(Connection connection, int studentId) throws SQLException {
        boolean studentAvailable;
        try (PreparedStatement selectStudentById = connection.prepareStatement(SqlQueryConstants.SELECT_STUDENT_BY_ID)) {
            selectStudentById.setInt(1, studentId);
            try(ResultSet rs = selectStudentById.executeQuery()) {
                studentAvailable = rs.next();
            }
        }
        return studentAvailable;
    }
    
    public String getGroupsStudentCountLessThan(Connection connection, int n) throws SQLException {
        StringBuilder output = new StringBuilder();
        if(n < 0) {
            return output.toString();
        }
        try (PreparedStatement groupsStudentCountLessThan = connection
                .prepareStatement(SqlQueryConstants.SELECT_GROUPS_STUDENT_COUNT_NOT_MORE_THAN)) {
            groupsStudentCountLessThan.setInt(1, n);
            try (ResultSet rs = groupsStudentCountLessThan.executeQuery()) {
                while (rs.next()) {
                    int groupId = rs.getInt(1);
                    String groupName = rs.getString(2);
                    int studentCount = rs.getInt(3);
                    String line = String.format("%1$-15s %2$-22s %3$-20s\n","group id: " + groupId, "group name: " + groupName, "student count: " + studentCount);
                    output.append(line);
                }
            }
        }
        List<Integer> emptyGroupsIds = getEmptyGroupsIds(connection);
        Map<Integer, String> groupIdToNameMap = getGroupIdToNameMap(connection);
        for(Integer groupId: emptyGroupsIds) {
            String groupName = groupIdToNameMap.get(groupId);
            String line = String.format("%1$-15s %2$-22s %3$-20s\n","group id: " + groupId, "group name: " + groupName, "student count: " + 0);
            output.append(line);
        }
        return output.toString();
    }
    public List<Integer> getEmptyGroupsIds(Connection connection) throws SQLException {
        List<Integer> groupsWithStudents = new ArrayList<>();
        List<Integer> groupsWithoutStudents = new ArrayList<>();
        try (PreparedStatement groupsByStudentCount = connection
                .prepareStatement(SqlQueryConstants.SELECT_GROUPS_STUDENT_COUNT)) {
            try (ResultSet rs = groupsByStudentCount.executeQuery()) {
                while (rs.next()) {
                    int groupId = rs.getInt(1);
                    if (!rs.wasNull()) {
                        groupsWithStudents.add(groupId);
                    }
                }
            }
        }
        for(int groupId = 0; groupId < Assigner.getNumberOfGroups(); groupId++) {
            if (!groupsWithStudents.contains(groupId)) {
                groupsWithoutStudents.add(groupId);
            }
        }
        return groupsWithoutStudents;
    }
    
    Map<Integer, String> getGroupIdToNameMap(Connection connection) throws SQLException {
        Map<Integer, String> groupIdToNameMap = new HashMap<>();
        try (PreparedStatement selectGroups = connection.prepareStatement(SqlQueryConstants.SELECT_GROUPS)) {
            try (ResultSet rs = selectGroups.executeQuery()) {
                while(rs.next()) {
                    int groupId = rs.getInt(1);
                    String groupName = rs.getString(2);
                    groupIdToNameMap.put(groupId, groupName);
                }
            }
        }
        return groupIdToNameMap;
    }

    public String getStudentsRelatedToCourse(Connection connection, int courseId) throws SQLException {
        StringBuilder output = new StringBuilder();
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
                    String line = String.format("%1$-23s %2$-25s %3$-25s \n","student id: " + studentId, "first name: " + firstName, "last name: " + lastName);
                    output.append(line);
                    //System.out.println(print);
                }
            }
        }
        return output.toString();
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
