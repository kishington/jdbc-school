package ua.com.foxminded.jdbctask.exceptions;

public class DatabaseConnectionException extends Exception {
    
    private static final long serialVersionUID = -6313346737552562765L;
    
    public DatabaseConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
