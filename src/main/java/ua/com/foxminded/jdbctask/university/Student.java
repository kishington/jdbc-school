package ua.com.foxminded.jdbctask.university;

import java.util.Random;

import org.junit.jupiter.api.Test;

public class Student {

    private int id;
    private int groupId;
    private String firstName;
    private String lastName;

    private static int counter = 0;
    private static Random random = new Random();
    private static String[] FIRST_NAMES = { "Imran", "Raj", "Siva", "Suresh", "Muhammad", "Salim", "Vladimir", "John",
            "Oliver", "Ahmed", "Aziz", "Mikhail", "Oleg", "Ramzan", "Victor", "Istvan", "Steven", "Andrew", "Mumtaz",
            "Alexey" };
    private static String[] LAST_NAMES = { "Putin", "Medvedev", "Poroshenko", "Lukashenko", "Zelensky", "Kadyrov",
            "Timoshenko", "Petrov", "Bashyrov", "Lutsenko", "Kobolev", "Abbas", "Abadi", "Antar", "Arian", "Lyashko",
            "Nikulin", "Vitsin", "Morgunov", "Trump" };

    public void setRandomStudent() {
        this.id = counter;
        setRandomFirstName();
        setRandomLastName();
        counter++;
    }
    
    public void setRandomFullName() {
        setRandomFirstName();
        setRandomLastName();
    }

    public int getId() {
        return id;
    }
    
    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setRandomFirstName() {
        int index = random.nextInt(FIRST_NAMES.length);
        this.firstName = FIRST_NAMES[index];
    }

    public String getLastName() {
        return lastName;
    }
    public void setRandomLastName() {
        int index = random.nextInt(LAST_NAMES.length);
        this.lastName = LAST_NAMES[index];
    }
}
