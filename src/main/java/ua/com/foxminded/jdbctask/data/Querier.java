package ua.com.foxminded.jdbctask.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Querier {
    
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
       DataGenerator dg = new DataGenerator();
       Querier q = new Querier();
       Connection connection = dg.getConnection();
       
       int n = 20;
       q.getGroupsStudentCountLessThan(connection, n);
       
       String courseName = "Physics";
       int courseId = q.getCourseId(connection, courseName);
       q.getStudentsRelatedToCourse(connection, courseId);
       
       
    }

    void getGroupsStudentCountLessThan(Connection connection, int n) throws SQLException {
        PreparedStatement groupsStudentCountLessThan = connection.prepareStatement(SqlQueryConstants.GROUPS_STUDENT_COUNT_NOT_MORE_THAN);
        groupsStudentCountLessThan.setInt(1,n);
        ResultSet rs = groupsStudentCountLessThan.executeQuery();
        while (rs.next()) {
            String groupId = rs.getString(1);
            String groupName = rs.getString(2);
            String studentCount = rs.getString(3);
            System.out.println("groupId: " + groupId + "     groupName: " + groupName + "         studentCount: " + studentCount);
        }
    }
    
    void getStudentsRelatedToCourse(Connection connection, int courseId) throws SQLException {
        PreparedStatement studentsRelatedToCourse = connection.prepareStatement(SqlQueryConstants.SELECT_STUDENTS_RELATED_TO_GIVEN_COURSE);
        studentsRelatedToCourse.setInt(1,courseId);
        ResultSet rs = studentsRelatedToCourse.executeQuery();
        while (rs.next()) {
            String studentId = rs.getString(1);
            String firstName = rs.getString(2);
            String lastName = rs.getString(3);
            System.out.println("studentId: " + studentId + "     firstName: " + firstName + "         lastName: " + lastName);
        }
    }
    
    int getCourseId(Connection connection, String courseName) throws SQLException {
        PreparedStatement selectCourseId = connection.prepareStatement(SqlQueryConstants.SELECT_COURSE_ID);
        selectCourseId.setString(1, courseName);
        ResultSet rs = selectCourseId.executeQuery();
        rs.next();
        int course_id = rs.getInt(1);
        return course_id;
        
    }
}
