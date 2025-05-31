package cliemailsystem.exceptions;

public class EmailSystemException extends RuntimeException {
    public EmailSystemException(String message) {
      super(message);
    }

    public EmailSystemException(String message, Throwable cause) {
        super(message, cause);
    }
}
