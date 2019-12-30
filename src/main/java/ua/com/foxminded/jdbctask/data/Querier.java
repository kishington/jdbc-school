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
    
    void getStudentsRelatedToCourse(Connection connection, String courseName) {
        
    }
}
