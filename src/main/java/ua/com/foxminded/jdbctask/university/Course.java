package ua.com.foxminded.jdbctask.university;

import java.util.ArrayList;
import java.util.List;

public class Course {
    private int id;
    private String name;
    private String description;

    public static List<String> courses = new ArrayList<>();
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
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    
    public static int getTotalNumberOfAvailableCourses() {
        return courses.size();
    }
}
