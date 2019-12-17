package ua.com.foxminded.jdbctask.university;

import java.util.Random;

public class Student {

    private int id;
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

    void setRandomStudent() {
        this.id = counter;
        setRandomFirstName();
        setRandomLastName();
        counter++;
    }

    public int getId() {
        return id;
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
        this.firstName = LAST_NAMES[index];
    }
}
