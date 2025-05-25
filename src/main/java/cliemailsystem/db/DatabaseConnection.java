package cliemailsystem.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/email_system";
    private static final String USER = "postgres";
    private static final String PASSWORD = "adminadmin24";
    private static Connection connection;

    private DatabaseConnection() {}

    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connection to PostgreSQL database established.");
            } catch (SQLException e) {
                throw new RuntimeException("Failed to connect to the database: " + e.getMessage());
            }
        }
        return connection;
    }
}