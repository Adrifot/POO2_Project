package cliemailsystem.dao;

import cliemailsystem.audit.AuditLogger;
import cliemailsystem.db.DatabaseConnection;
import cliemailsystem.entities.Email;
import cliemailsystem.interfaces.CRUDable;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmailDAO extends BaseDAO<Email> implements CRUDable<Email> {

    @Override
    public Email save(Email email) {
        String sql = "INSERT INTO emails (from_user_id, to_user_id, created_at, subject, content, status) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, email.getFromUserId());
            stmt.setInt(2, email.getToUserId());
            stmt.setTimestamp(3, Timestamp.valueOf(email.getTimestamp()));
            stmt.setString(4, email.getSubject());
            stmt.setString(5, email.getContent());
            stmt.setString(6, email.getStatus().name());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                email = new Email(email.getFromUserId(), email.getToUserId(), email.getSubject(), email.getContent());
                email.setId(rs.getInt("id"));
            }

            return email;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save email: " + e.getMessage());
        }
    }

    @Override
    public Email findById(int id) {
        String sql = "SELECT * FROM emails WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Email email = new Email(
                        rs.getInt("from_user_id"),
                        rs.getInt("to_user_id"),
                        rs.getString("subject"),
                        rs.getString("content")
                );
                email.setId(rs.getInt("id"));
                email.markAsRead();
                return email;
            } else {
                throw new RuntimeException("Email with ID: " + id + " not found.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch email by ID: " + e.getMessage());
        }
    }

    @Override
    public List<Email> findAll() {
        String sql = "SELECT * FROM emails";
        List<Email> emails = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Email email = new Email(
                        rs.getInt("from_user_id"),
                        rs.getInt("to_user_id"),
                        rs.getString("subject"),
                        rs.getString("content")
                );
                email.setId(rs.getInt("id"));
                emails.add(email);
            }

            return emails;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch emails: " + e.getMessage());
        }
    }

    @Override
    public Email update(Email email) {
        String sql = "UPDATE emails SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email.getStatus().name());
            stmt.setInt(2, email.getId());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                AuditLogger.getInstance().log("Email#" + email.getId() + " updated");
                return email;
            } else {
                throw new RuntimeException("Failed to update email with ID: " + email.getId());
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update email: " + e.getMessage());
        }
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM emails WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted <= 0) {
                throw new RuntimeException("Failed to delete email with ID: " + id);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete email: " + e.getMessage());
        }
    }

    public List<Email> findAllForUser(int userId) {
        String sql = "SELECT * FROM emails WHERE to_user_id = ?";
        List<Email> inbox = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Email email = new Email(
                        rs.getInt("id"),
                        rs.getInt("from_user_id"),
                        rs.getInt("to_user_id"),
                        rs.getString("subject"),
                        rs.getString("content"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                );
                String statusStr = rs.getString("status");
                if (statusStr != null) {
                    try {
                        email.setStatus(Email.EmailStatus.valueOf(statusStr));
                    } catch (IllegalArgumentException e) {
                        email.setStatus(Email.EmailStatus.NEW);
                    }
                } else {
                    email.setStatus(Email.EmailStatus.NEW);
                }

                inbox.add(email);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve emails for user with ID: " + userId, e);
        }

        return inbox;
    }

}

