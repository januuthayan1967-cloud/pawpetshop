/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Legacy DatabaseConnection compatibility adapter.
 * Delegates seamlessly to modern PawMart database connection provider.
 */
public class DatabaseConnection {

    // Get a connection to the database
    public static Connection getConnection() throws SQLException {
        return com.pawmart.database.DatabaseConnection.getConnection();
    }
}
