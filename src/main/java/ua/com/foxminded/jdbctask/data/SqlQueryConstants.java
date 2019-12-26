package ua.com.foxminded.jdbctask.data;

public class SqlQueryConstants {
    String GROUPS_STUDENT_COUNT_NOT_MORE_THAN = 
            "SELECT\n" + 
            "   group_id,\n" + 
            "   number_of_students\n" + 
            "FROM\n" + 
            "   (SELECT\n" + 
            "        group_id,\n" + 
            "        COUNT (student_id) AS number_of_students\n" + 
            "    FROM\n" + 
            "        students\n" + 
            "    GROUP BY\r\n" + 
            "        group_id) students_in_groups\n" + 
            "WHERE\n" + 
            "    group_id IS NOT NULL AND\n" + 
            "    number_of_students < 40\n" + 
            "ORDER BY\n" + 
            "    group_id;";
    
    String SELECT_STUDENTS_RELATED_TO_GIVEN_COURSE = 
            "SELECT\n" + 
            "    student_id\n" + 
            "FROM\n" + 
            "    student_courses\n" + 
            "WHERE\n" + 
            "    course_id = 5\n" + 
            "ORDER BY\n" + 
            "    student_id;";
    String INSERT_STUDENT = 
            "INSERT INTO students(first_name, last_name)\n" + 
            "VALUES ('Vasiliy', 'Terkin');";
  
    String DELETE_STUDENT_BY_ID = 
            "DELETE FROM students\n" + 
            "WHERE student_id = 201;";
    String REMOVE_STUDENT_FROM_COURSE = 
            "DELETE FROM student_courses" +
            "WHERE student_id = 200 AND course_id = 8;";

}
