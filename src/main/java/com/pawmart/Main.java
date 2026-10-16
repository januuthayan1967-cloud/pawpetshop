package com.pawmart;

import com.pawmart.database.DatabaseInitializer;
import com.pawmart.ui.theme.PawMartTheme;

import javax.swing.SwingUtilities;

/**
 * Main application launcher for PawMart Pet Shop Management System.
 */
public class Main {

    public static void main(String[] args) {
        // 1. Initialize Modern FlatLaf Look and Feel with PawMart styling
        PawMartTheme.setupFlatLaf();

        // 2. Launch Login Frame on the Swing Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            // Asynchronously verify & initialize database tables
            new Thread(() -> {
                try {
                    DatabaseInitializer.initializeDatabase();
                } catch (Exception e) {
                    System.err.println("[PawMart] Initial database check: " + e.getMessage());
                }
            }).start();

            try {
                javax.swing.JFrame loginScreen = (javax.swing.JFrame) Class.forName("login").getDeclaredConstructor().newInstance();
                loginScreen.setVisible(true);
            } catch (Exception e) {
                System.err.println("[PawMart] Launch error: " + e.getMessage());
            }
        });
    }
}
