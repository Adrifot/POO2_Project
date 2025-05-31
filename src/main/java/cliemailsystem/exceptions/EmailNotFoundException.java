package cliemailsystem.exceptions;

public class EmailNotFoundException extends EmailSystemException {
    public EmailNotFoundException(String message) {
        super(message);
    }
}