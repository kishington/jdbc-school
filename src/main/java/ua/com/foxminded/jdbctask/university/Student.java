package ua.com.foxminded.jdbctask.university;

public class Student {

    private int id;
    private int groupId;
    private String firstName;
    private String lastName;


    public static final String[] FIRST_NAMES = { "Imran", "Raj", "Siva", "Suresh", "Muhammad", "Salim", "Vladimir", "John",
            "Oliver", "Ahmed", "Aziz", "Mikhail", "Oleg", "Ramzan", "Victor", "Istvan", "Steven", "Andrew", "Mumtaz",
            "Alexey" };
    public static final String[] LAST_NAMES = { "Putin", "Medvedev", "Poroshenko", "Lukashenko", "Zelensky", "Kadyrov",
            "Timoshenko", "Petrov", "Bashyrov", "Lutsenko", "Kobolev", "Abbas", "Abadi", "Antar", "Arian", "Lyashko",
            "Nikulin", "Vitsin", "Morgunov", "Trump" };    

    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
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
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
