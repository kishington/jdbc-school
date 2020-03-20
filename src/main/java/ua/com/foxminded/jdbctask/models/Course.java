package ua.com.foxminded.jdbctask.models;

import java.util.ArrayList;
import java.util.List;

public class Course {
    private Course() {

    }

    private static final List<String> courses = new ArrayList<>();
    static {
        courses.add("Maths");
        courses.add("Biology");
        courses.add("Zoology");
        courses.add("Botany");
        courses.add("Physics");
        courses.add("Chemistry");
        courses.add("Astronomy");
        courses.add("Economics");
        courses.add("Physiology");
        courses.add("Econometrics");
    }

    public static List<String> getAvailableCourses() {
        return new ArrayList<>(courses);
    }

    public static int getTotalNumberOfAvailableCourses() {
        return courses.size();
    }
}
