package ua.com.foxminded.jdbctask.university;

import java.util.Random;

public class Course {
    private int id;
    private String name;
    private String description;
    
    private static Random random = new Random();
    
    public static final String[] COURSES = { "Maths", "Biology", "Zoology", "Botany", "Physics", "Chemistry",
            "Astronomy", "Economics", "Physiology", "Econometrics" };

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
    public void setRandomCourse() {
        int id = random.nextInt(COURSES.length);
        this.id = id;
        this.name = COURSES[id];
    }
    
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
