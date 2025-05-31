package cliemailsystem.exceptions;

public class UsernameNotFoundException extends InvalidUserException {
  public UsernameNotFoundException(String username) {
    super("User with username " + username + " not found");
  }
}
