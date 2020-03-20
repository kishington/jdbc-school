package ua.com.foxminded.jdbctask.data;

public interface Constants {
    
    public static final String DB_PROPERTIES_PATH = "/config.properties";

    public static final int GROUP_SIZE_LOWER_LIMIT = 10;
    public static final int GROUP_SIZE_UPPER_LIMIT = 30;
    public static final int MIN_NUMBER_OF_COURSES = 1;
    public static final int MAX_NUMBER_OF_COURSES = 3;
    public static final int NUMBER_OF_GROUPS = 10;
    public static final int NUMBER_OF_STUDENTS = 200;
    public static final int STUDENT_ASSIGNED = 1;

    public static final int STUDENT_NOT_ASSIGNED_TO_COURSE = -1;
    public static final int COURSE_NOT_AVAILABLE = 0;
    public static final int STUDENT_REMOVED_FROM_COURSE = 1;
    public static final int STUDENT_ALREADY_ASSIGNED = 2;
    public static final int STUDENT_ASSIGNED_SUCCESSFULLY = 3;

}
