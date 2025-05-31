package cliemailsystem.exceptions;

public class InvalidUserException extends EmailSystemException {
    public InvalidUserException(String message) {
        super(message);
    }
}