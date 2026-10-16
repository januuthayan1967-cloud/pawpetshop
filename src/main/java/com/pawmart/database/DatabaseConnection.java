package com.pawmart.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Thread-safe MySQL Connection Provider for PawMart.
 */
public class DatabaseConnection {

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("[DatabaseConnection] MySQL Driver not found: " + e.getMessage());
        }
    }

    /**
     * Obtains a fresh MySQL connection to the PawMart database.
     * @return active java.sql.Connection
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(
                    DatabaseConfig.getJdbcUrl(),
                    DatabaseConfig.getUsername(),
                    DatabaseConfig.getPassword()
            );
        } catch (SQLException e) {
            throw new SQLException(
                    "Unable to connect to PawMart MySQL database at " + DatabaseConfig.getHost() + ":" + DatabaseConfig.getPort()
                            + " (" + e.getMessage() + "). Please ensure WampServer/MySQL is running.", e);
        }
    }

    /**
     * Tests whether the MySQL connection is operational.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
