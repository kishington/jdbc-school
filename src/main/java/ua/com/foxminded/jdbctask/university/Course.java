package ua.com.foxminded.jdbctask.university;

public class Course {
    private int id;
    private String name;
    private String description;
    
    private static final String[] COURSES = { "Maths", "Biology", "Zoology", "Botany", "Physics", "Chemistry",
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
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
