package com.pawmart.service;

import com.pawmart.dao.UserDAO;
import com.pawmart.model.Role;
import com.pawmart.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

/**
 * Authentication and Session Management Service for PawMart.
 */
public class AuthService {

    private static AuthService instance;
    private final UserDAO userDAO;
    private User currentUser;

    private AuthService() {
        this.userDAO = new UserDAO();
    }

    public static synchronized AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    /**
     * Authenticates user against MySQL database using BCrypt.
     * @param username user identifier
     * @param plainPassword user entered password
     * @return authenticated User object
     * @throws AuthenticationException on invalid credentials, deactivation, or database error
     */
    public User login(String username, String plainPassword) throws AuthenticationException {
        if (username == null || username.trim().isEmpty()) {
            throw new AuthenticationException("Please enter your username.");
        }
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new AuthenticationException("Please enter your password.");
        }

        try {
            User user = userDAO.findByUsername(username.trim());
            if (user == null) {
                throw new AuthenticationException("Invalid username or password.");
            }

            if (!user.isActive()) {
                throw new AuthenticationException("This account is currently deactivated. Please contact your Store Manager.");
            }

            boolean passwordMatch = false;
            try {
                passwordMatch = BCrypt.checkpw(plainPassword, user.getPasswordHash());
            } catch (Exception e) {
                // If the hash is invalid or plain text in legacy databases, fallback check
                if (plainPassword.equals(user.getPasswordHash())) {
                    passwordMatch = true;
                    // Auto-upgrade to BCrypt
                    String upgradedHash = BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
                    userDAO.updatePassword(user.getId(), upgradedHash);
                }
            }

            if (!passwordMatch) {
                throw new AuthenticationException("Invalid username or password.");
            }

            this.currentUser = user;
            return user;
        } catch (SQLException e) {
            throw new AuthenticationException("Database error during login: " + e.getMessage());
        }
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isManager() {
        return currentUser != null && currentUser.getRole() == Role.MANAGER;
    }

    public boolean isCashier() {
        return currentUser != null && currentUser.getRole() == Role.CASHIER;
    }

    public static class AuthenticationException extends Exception {
        public AuthenticationException(String message) {
            super(message);
        }
    }
}
