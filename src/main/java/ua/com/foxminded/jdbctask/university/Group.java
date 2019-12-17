package ua.com.foxminded.jdbctask.university;

import java.util.Random;

public class Group {
    private int id;
    private String name;
    
    private static int counter = 0;
    private static Random random = new Random();
    private static final String LETTERS = "abcdefghijklmnopqrstuvwxyz";
    
    public void setRandomCourse() {
        this.id = counter;
        setRandomName();
        counter++;
    }
    
    public int getId() {
        return id;
    }
 
    public String getName() {
        return name;
    }
    public void setRandomName() {
        StringBuilder groupName = new StringBuilder();
        
        int index = random.nextInt(LETTERS.length());
        char randomChar = LETTERS.charAt(index);
        groupName.append(randomChar);        
        index = random.nextInt(LETTERS.length());
        randomChar = LETTERS.charAt(index);
        groupName.append(randomChar);
       
        groupName.append('-');
        
        int randomDigit = random.nextInt(10);
        groupName.append(randomDigit);
        randomDigit = random.nextInt(10);
        groupName.append(randomDigit);
        
        this.name = groupName.toString();
    }
}
