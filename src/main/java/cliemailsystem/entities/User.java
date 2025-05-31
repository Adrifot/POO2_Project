package cliemailsystem.entities;

import cliemailsystem.dao.UserDAO;
import cliemailsystem.exceptions.InvalidUserException;

public class User {
    private int id; // Primary key in DB
    private String username;
    private String password;

    public User() {}

    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User register(UserDAO dao) throws InvalidUserException {
        if (dao.findByUsername(this.username) != null) {
            throw new InvalidUserException("Username is already taken.");
        }

        return dao.save(this);
    }

}