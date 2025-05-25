package cliemailsystem.dao;

import cliemailsystem.db.DatabaseConnection;
import cliemailsystem.entities.User;
import cliemailsystem.interfaces.CrudRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO extends BaseDAO<User> implements CrudRepository<User> {

    @Override
    public User save(User user) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

        // Use a try-with-resources block to ensure connection is closed
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Set the query parameters
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());

            // Execute the query
            statement.executeUpdate();
            System.out.println("Registration successful!");
            return user;

        } catch (SQLException e) {
            // Handle unique constraint violation for username
            if ("23505".equals(e.getSQLState())) { // PostgreSQL specific for unique constraint violations
                System.err.println("Username is already taken.");
            } else {
                System.err.println("Failed to save user: " + e.getMessage());
            }
            return null; // Return null on failure
        }
    }

    @Override
    public User findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                logAction("SELECT user by ID: " + id);
                return user;
            } else {
                throw new RuntimeException("User with ID: " + id + " was not found.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch user by ID: " + e.getMessage());
        }
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        List<User> users = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                users.add(user);
            }

            logAction("SELECT all users");
            return users;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch users: " + e.getMessage());
        }
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE users SET username = ?, password = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setInt(3, user.getId());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                logAction("UPDATE user with ID: " + user.getId());
                return user;
            } else {
                throw new RuntimeException("User with ID: " + user.getId() + " could not be updated.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update user: " + e.getMessage());
        }
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                logAction("DELETE user by ID: " + id);
            } else {
                throw new RuntimeException("Could not delete user with ID: " + id);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete user: " + e.getMessage());
        }
    }

    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password")
                );
                logAction("SELECT user by username: " + username);
                return user;
            } else {
                return null; // No user found with the given username
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch user by username: " + e.getMessage());
        }
    }

}
