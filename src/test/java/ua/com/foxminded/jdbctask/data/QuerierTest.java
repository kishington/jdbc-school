package ua.com.foxminded.jdbctask.data;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jdbctask.exceptions.DataGenerationException;
import jdbctask.exceptions.DatabaseConnectionException;

class QuerierTest {
    private static final Querier querier = new Querier();
    private static final DataGenerator dataGenerator = new DataGenerator();
    private static Connection connection;

    @BeforeAll
    private static void init() throws DatabaseConnectionException {
        DatabaseConnectionGetter dbConnGetter = new DatabaseConnectionGetter();
        connection = dbConnGetter.getConnection();
    }

    @BeforeEach
    public void initEach() throws DataGenerationException {
        dataGenerator.generateData();
    }

    @AfterAll
    private static void cleanUp() throws SQLException {
        connection.close();
    }

    @Test
    void testRemoveStudentFromCourse_courseNotAvailable() throws SQLException {
        int expected = Constants.COURSE_NOT_AVAILABLE;
        int actual = querier.removeStudentFromCourse(connection, 0, "Chess");
        assertEquals(expected, actual);
    }

    @Test
    void testRemoveStudentFromCourse_removalSuccess() throws SQLException {
        assignStudentToCourse(connection, 0, 1);
        int expected = Constants.STUDENT_REMOVED_FROM_COURSE;
        int actual = querier.removeStudentFromCourse(connection, 0, "Biology");
        assertEquals(expected, actual);
    }

    @Test
    void testAssignStudentToCourse_courseNotAvailable() throws SQLException {
        int expected = Constants.COURSE_NOT_AVAILABLE;
        int actual = querier.assignStudentToCourse(connection, 0, "Chess");
        assertEquals(expected, actual);
    }

    @Test
    void testAssignStudentToCourse_alreadyAssigned() throws SQLException {
        querier.assignStudentToCourse(connection, 0, "Biology");
        int expected = Constants.STUDENT_ALREADY_ASSIGNED;
        int actual = querier.assignStudentToCourse(connection, 0, "Biology");
        assertEquals(expected, actual);
    }

    @Test
    void testAssignStudentToCourse_AssignedSuccessfully() throws SQLException {
        querier.assignStudentToCourse(connection, 0, "Maths");
        String selectStudentToCourseRelation = "SELECT * FROM students_courses WHERE student_id = 0 AND course_id = 0;";
        try (PreparedStatement statement = connection.prepareStatement(selectStudentToCourseRelation)) {
            try (ResultSet rs = statement.executeQuery()) {
                assertTrue(rs.next());
            }
        }
    }

    @Test
    void testGetStudentCourses() throws SQLException {
        assignStudentToCourse(connection, 5, 2);
        assignStudentToCourse(connection, 5, 4);
        assignStudentToCourse(connection, 5, 8);
        List<Integer> studentCourses = querier.getStudentCourses(connection, 5);
        assertTrue(studentCourses.contains(2));
        assertTrue(studentCourses.contains(4));
        assertTrue(studentCourses.contains(8));
    }

    @Test
    void testAddNewStudent() throws SQLException {
        querier.addNewStudent(connection, "Ion", "Petrescu");
        String selectStudent = "SELECT * FROM students WHERE first_name = 'Ion' AND last_name = 'Petrescu';";
        try (PreparedStatement statement = connection.prepareStatement(selectStudent)) {
            try (ResultSet rs = statement.executeQuery()) {
                assertTrue(rs.next());
            }
        }
    }

    @Test
    void testDeleteStudent() throws SQLException {
        querier.deleteStudent(connection, 0);
        String selectStudent = SqlQueryConstants.SELECT_STUDENT_BY_ID;
        try (PreparedStatement statement = connection.prepareStatement(selectStudent)) {
            statement.setInt(1, 0);
            try (ResultSet rs = statement.executeQuery()) {
                assertFalse(rs.next());
            }
        }
    }

    @Test
    void testIsStudentAvailable_whenHeIs() throws SQLException {
        boolean studentAvailable = querier.isStudentAvailable(connection, 6);
        assertTrue(studentAvailable);
    }

    @Test
    void testIsStudentAvailable_whenHeIsNot() throws SQLException {
        String deleteStudent = "DELETE FROM students WHERE student_id = 6;";
        try (PreparedStatement statement = connection.prepareStatement(deleteStudent)) {
            statement.executeUpdate();
        }
        boolean studentAvailable = querier.isStudentAvailable(connection, 6);
        assertFalse(studentAvailable);
    }

    @Test
    void testGetGroupsStudentCountLessThan() throws SQLException {
        removeAllStudentsFromGroups();

        assignStudentsToGroup(0, 0, 10);
        assignStudentsToGroup(1, 10, 20);
        int fromStudentId = 20;
        for (int groupId = 2; groupId < Constants.NUMBER_OF_GROUPS; groupId++) {
            int numberOfStudents = 15;
            assignStudentsToGroup(groupId, fromStudentId, fromStudentId + numberOfStudents);
            fromStudentId += numberOfStudents;
        }

        insertGroupName("xxxx", 0);
        insertGroupName("xxxx", 1);

        String expected = "group id: 0     group name: xxxx       student count: 10   \n"
                        + "group id: 1     group name: xxxx       student count: 10   \n";
        String actual = querier.getGroupsStudentCountLessThan(connection, 11);
        assertEquals(expected, actual);
    }

    @Test
    void testGetEmptyGroupsIds() throws SQLException {
        removeAllStudentsFromGroups();
        List<Integer> emptyGroupsIds = querier.getEmptyGroupsIds(connection);
        for (int i = 0; i < emptyGroupsIds.size(); i++) {
            int expectedGroupId = i;
            int actualGroupId = emptyGroupsIds.get(i);
            assertEquals(expectedGroupId, actualGroupId);
        }
    }

    @Test
    void testGetGroupIdToNameMap() throws SQLException {
        char groupName = 'a';
        for (int groupId = 0; groupId < Constants.NUMBER_OF_GROUPS; groupId++) {
            insertGroupName(Character.toString(groupName), groupId);
            groupName++;
        }
        Map<Integer, String> groupIdToNameMapActual = querier.getGroupIdToNameMap(connection);

        Map<Integer, String> groupIdToNameMapExpected = new HashMap<>();
        groupName = 'a';
        for (int groupId = 0; groupId < Constants.NUMBER_OF_GROUPS; groupId++) {
            groupIdToNameMapExpected.put(groupId, Character.toString(groupName));
            groupName++;
        }

        assertTrue(groupIdToNameMapActual.equals(groupIdToNameMapExpected));
    }

    @Test
    void testGetStudentsRelatedToCourse() throws SQLException {
        String truncateStudentsCoursesTable = "TRUNCATE students_courses;";
        try (PreparedStatement statement = connection.prepareStatement(truncateStudentsCoursesTable)) {
            statement.executeUpdate();
        }
        int nameTag = 1;
        for (int studentId = 0; studentId < 4; studentId++) {
            insertStudentName(studentId, "Petr" + nameTag, "Petrov" + nameTag);
            assignStudentToCourse(connection, studentId, 0);
            nameTag++;
        }

        String expected = "student id: 0           first name: Petr1         last name: Petrov1        \n"
                + "student id: 1           first name: Petr2         last name: Petrov2        \n"
                + "student id: 2           first name: Petr3         last name: Petrov3        \n"
                + "student id: 3           first name: Petr4         last name: Petrov4        \n";
        String actual = querier.getStudentsRelatedToCourse(connection, 0);
        assertEquals(expected, actual);
    }

    @Test
    void testGetCourseId() throws SQLException {
        String[] courses = new String[] { "Maths", "Biology", "Zoology", "Botany", "Physics", "Chemistry", "Astronomy",
                "Economics", "Physiology", "Econometrics" };
        for (int i = 0; i < courses.length; i++) {
            int expectedCourseId = i;
            int actualCourseId = querier.getCourseId(connection, courses[i]);
            assertEquals(expectedCourseId, actualCourseId);
        }
    }

    private void removeAllStudentsFromGroups() throws SQLException {
        String removeStudentsFromGroups = "UPDATE students SET group_id = NULL;";
        try (PreparedStatement statement = connection.prepareStatement(removeStudentsFromGroups)) {
            statement.executeUpdate();
        }
    }

    private void assignStudentsToGroup(int groupId, int fromStudentId, int toStudentId) throws SQLException {
        String assignStudents = "UPDATE students SET group_id = ? WHERE student_id >= ? AND student_id < ?;";
        try (PreparedStatement statement = connection.prepareStatement(assignStudents)) {
            statement.setInt(1, groupId);
            statement.setInt(2, fromStudentId);
            statement.setInt(3, toStudentId);
            statement.executeUpdate();
        }
    }

    private void insertGroupName(String groupName, int groupId) throws SQLException {
        String setGroupName = "UPDATE groups SET group_name = ? WHERE group_id = ?;";
        try (PreparedStatement statement = connection.prepareStatement(setGroupName)) {
            statement.setString(1, groupName);
            statement.setInt(2, groupId);
            statement.executeUpdate();
        }
    }

    private void insertStudentName(int studentId, String firstName, String lastName) throws SQLException {
        String setStudentName = "UPDATE students SET first_name = ?, last_name = ? WHERE student_id = ?;";
        try (PreparedStatement statement = connection.prepareStatement(setStudentName)) {
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setInt(3, studentId);
            statement.executeUpdate();
        }
    }
    
    private void assignStudentToCourse(Connection connection, int studentId, int courseId) throws SQLException {
        try (PreparedStatement insertStudentCourseRelation = 
                connection.prepareStatement(SqlQueryConstants.INSERT_STUDENT_COURSE_RELATION)) {
            insertStudentCourseRelation.setInt(1, studentId);
            insertStudentCourseRelation.setInt(2, courseId);
            insertStudentCourseRelation.executeUpdate();
        }
    }
}
