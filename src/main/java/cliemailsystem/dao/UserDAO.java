package cliemailsystem.dao;

import cliemailsystem.db.DatabaseConnection;
import cliemailsystem.entities.User;
import cliemailsystem.exceptions.UserNotFoundException;
import cliemailsystem.exceptions.UsernameNotFoundException;
import cliemailsystem.interfaces.CRUDable;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDAO extends BaseDAO<User> implements CRUDable<User> {

    @Override
    public User save(User user) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());

            statement.executeUpdate();
            System.out.println("Registration successful!");
            return user;

        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) { // PostgreSQL specific for unique constraint violations
                System.err.println("Username is already taken.");
            } else {
                System.err.println("Failed to save user: " + e.getMessage());
            }
            return null;
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
                throw new UserNotFoundException(id);
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

    public Map<Integer, String> getUserIdToUsername() {
        String sql = "SELECT id, username FROM users";
        Map<Integer, String> idToUsernameMap = new HashMap<>();

        try (
                Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                idToUsernameMap.put(id, username);
            }

            return idToUsernameMap;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch user by username: " + e.getMessage());
        }
    }

    public Map<String, Integer> getUsernameToUserIdMap() {
        Map<Integer, String> idToUsername = getUserIdToUsername();
        Map<String, Integer> usernameToId = new HashMap<>();

        for (Map.Entry<Integer, String> entry : idToUsername.entrySet()) {
            usernameToId.put(entry.getValue(), entry.getKey());
        }

        return usernameToId;
    }

    public int findUserIdByUsername(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            } else {
                throw new UsernameNotFoundException(username);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve user ID: " + e.getMessage(), e);
        }
    }

    public String findUsernameById(int userId) {
        String sql = "SELECT username FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("username");
            } else {
                throw new UserNotFoundException(userId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve username: " + e.getMessage(), e);
        }
    }


}
