package cliemailsystem.exceptions;

public class UserNotFoundException extends InvalidUserException {
    public UserNotFoundException(int userId) {
        super("User with id " + userId + " not found");
    }
}
