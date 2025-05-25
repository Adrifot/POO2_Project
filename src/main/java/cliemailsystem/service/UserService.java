package cliemailsystem.service;

import cliemailsystem.dao.UserDAO;
import cliemailsystem.entities.User;
import cliemailsystem.exceptions.InvalidUserException;
import cliemailsystem.utils.ConsoleUtils;

import java.util.Scanner;

public class UserService {
    private final UserDAO userDAO = new UserDAO();
    private int currentUserId = -1; // Tracks the ID of the logged-in user

    public void registerUser(Scanner scanner) {
        try {
            String username = ConsoleUtils.readUsername(scanner);
            String password = ConsoleUtils.readPassword(scanner);

            User newUser = new User(0, username, password);
            userDAO.save(newUser);

            System.out.println("Registration successful!");
        } catch (Exception e) {
            System.out.println("An error occurred while registering: " + e.getMessage());
        }
    }

    public boolean logIn(Scanner scanner) {
        String username = ConsoleUtils.readUsername(scanner);
        String password = ConsoleUtils.readPassword(scanner);

        User user = userDAO.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            currentUserId = user.getId();
            System.out.println("Login successful. Welcome, " + user.getUsername() + "!");
            return true;
        } else {
            System.out.println("Invalid username or password. Please try again.");
            return false;
        }
    }

    public int getCurrentUserId() {
        return currentUserId;
    }
}