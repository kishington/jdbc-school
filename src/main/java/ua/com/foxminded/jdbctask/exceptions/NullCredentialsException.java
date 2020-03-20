package ua.com.foxminded.jdbctask.exceptions;

public class NullCredentialsException extends RuntimeException {

    private static final long serialVersionUID = -8801213805243350716L;
    
    public NullCredentialsException(String message) {
        super(message);
    }

}

