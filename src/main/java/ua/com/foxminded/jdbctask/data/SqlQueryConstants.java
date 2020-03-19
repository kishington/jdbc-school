package ua.com.foxminded.jdbctask.data;

public interface SqlQueryConstants {
    
    public static final String DROP_GROUPS_TABLE = "DROP TABLE IF EXISTS groups CASCADE;";
    public static final String DROP_COURSES_TABLE = "DROP TABLE IF EXISTS courses CASCADE;";
    public static final String DROP_STUDENTS_TABLE = "DROP TABLE IF EXISTS students CASCADE;";
    public static final String DROP_STUDENTS_COURSES_TABLE = "DROP TABLE IF EXISTS students_courses CASCADE";

    public static final String CREATE_ALL_TABLES_PATH = "/create_all_tables.sql";

    public static final String INSERT_STUDENT = "INSERT INTO students(first_name, last_name) VALUES (?, ?);";
    public static final String INSERT_GROUP = "INSERT INTO groups VALUES (?, ?);";
    public static final String INSERT_ASSIGNED_STUDENT = "INSERT INTO students VALUES (?, ?, ?, ?);";
    public static final String INSERT_NOT_ASSIGNED_STUDENT = "INSERT INTO students (student_id, first_name, last_name) VALUES (?, ?, ?);";
    public static final String INSERT_COURSE = "INSERT INTO courses (course_id, course_name) VALUES (?, ?);";
    public static final String INSERT_STUDENT_COURSE_RELATION = "INSERT INTO students_courses VALUES (?, ?) ON CONFLICT DO NOTHING;";

    public static final String REMOVE_STUDENT_FROM_COURSE = 
            "DELETE FROM students_courses\n" +
            "WHERE student_id = ? AND course_id = ?;";
    
    public static final String DELETE_STUDENT_BY_ID = 
            "DELETE FROM students\n" + 
            "WHERE student_id = ?;";
    
    public static final String SELECT_GROUPS = "SELECT * FROM groups";
    
    public static final String SELECT_GROUPS_STUDENT_COUNT = 
            "SELECT\n" + 
            "   group_id,\n" + 
            "   COUNT (student_id)\n" + 
            "FROM\n" + 
            "   students\n" + 
            "GROUP BY\n" + 
            "   group_id\n" + 
            "ORDER BY\n" + 
            "    group_id;";

    public static final String SELECT_GROUPS_STUDENT_COUNT_NOT_MORE_THAN = "SELECT\n" + 
            "    students_in_groups.group_id,\n" + 
            "    groups.group_name,\n" + 
            "    number_of_students\n" + 
            "FROM\n" + 
            "    (SELECT\n" + 
            "        group_id,\n" + 
            "        COUNT (student_id) AS number_of_students\n" + 
            "     FROM\n" + 
            "        students\n" + 
            "     GROUP BY\n" + 
            "        group_id) students_in_groups\n" + 
            "INNER JOIN groups ON students_in_groups.group_id = groups.group_id\n" + 
            "WHERE\n" + 
            "    students_in_groups.group_id IS NOT NULL AND\n" + 
            "    number_of_students < ? \n" + 
            "ORDER BY\n" + 
            "    group_id;";
    
    public static final String SELECT_STUDENT_BY_ID = "SELECT * FROM students WHERE student_id = ?;";
    
    public static final String SELECT_STUDENTS_RELATED_TO_GIVEN_COURSE = 
            "SELECT\n" + 
            "    course_students.student_id,\n" + 
            "    students.first_name,\n" + 
            "    students.last_name\n" + 
            "FROM\n" + 
            "    (SELECT\n" + 
            "        student_id\n" + 
            "    FROM\n" + 
            "        students_courses\n" + 
            "    WHERE\n" + 
            "        students_courses.course_id = ?) course_students\n" + 
            "INNER JOIN students ON course_students.student_id = students.student_id\n" + 
            "ORDER BY\n" + 
            "    course_students.student_id;";
    
    public static final String SELECT_COURSE_ID = 
            "SELECT\n" + 
            "    course_id\n" + 
            "FROM\n" + 
            "    courses\n" + 
            "WHERE\n" + 
            "    course_name = ?;";
    
    public static final String SELECT_STUDENTS_COURSES = 
            "SELECT\n" + 
            "    course_id\n" + 
            "FROM\n" + 
            "    students_courses\n" + 
            "WHERE\n" + 
            "    student_id = ?;";
}
