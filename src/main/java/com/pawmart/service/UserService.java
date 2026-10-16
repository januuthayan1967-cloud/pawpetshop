package com.pawmart.service;

import com.pawmart.dao.UserDAO;
import com.pawmart.model.Role;
import com.pawmart.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Service for Manager to manage Cashier accounts.
 */
public class UserService {

    private final UserDAO userDAO;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public UserService() {
        this.userDAO = new UserDAO();
    }

    public List<User> getAllCashiers() throws SQLException {
        return userDAO.findAllCashiers();
    }

    public List<User> searchCashiers(String query) throws SQLException {
        if (query == null || query.trim().isEmpty()) {
            return getAllCashiers();
        }
        return userDAO.searchCashiers(query);
    }

    public User createCashier(String fullName, String username, String email, String password, String confirmPassword)
            throws ValidationException, SQLException {

        validateFields(fullName, username, email);

        if (password == null || password.length() < 6) {
            throw new ValidationException("Password must be at least 6 characters long.");
        }
        if (!password.equals(confirmPassword)) {
            throw new ValidationException("Passwords do not match.");
        }

        if (userDAO.isUsernameTaken(username.trim(), 0)) {
            throw new ValidationException("Username '" + username + "' is already taken.");
        }
        if (userDAO.isEmailTaken(email.trim(), 0)) {
            throw new ValidationException("Email '" + email + "' is already registered.");
        }

        String hash = BCrypt.hashpw(password, BCrypt.gensalt(12));
        User user = new User(0, fullName.trim(), username.trim(), email.trim(), hash, Role.CASHIER, "ACTIVE");
        userDAO.create(user);
        return user;
    }

    public void updateCashier(int id, String fullName, String username, String email, String status)
            throws ValidationException, SQLException {

        validateFields(fullName, username, email);

        if (userDAO.isUsernameTaken(username.trim(), id)) {
            throw new ValidationException("Username '" + username + "' is already taken by another user.");
        }
        if (userDAO.isEmailTaken(email.trim(), id)) {
            throw new ValidationException("Email '" + email + "' is already in use by another user.");
        }

        User user = userDAO.findById(id);
        if (user == null) {
            throw new ValidationException("Cashier account not found.");
        }

        user.setFullName(fullName.trim());
        user.setUsername(username.trim());
        user.setEmail(email.trim());
        user.setStatus(status != null ? status : "ACTIVE");
        userDAO.update(user);
    }

    public void resetPassword(int id, String newPassword, String confirmPassword)
            throws ValidationException, SQLException {
        if (newPassword == null || newPassword.length() < 6) {
            throw new ValidationException("New password must be at least 6 characters long.");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new ValidationException("Passwords do not match.");
        }
        String hash = BCrypt.hashpw(newPassword, BCrypt.gensalt(12));
        userDAO.updatePassword(id, hash);
    }

    public void deactivateCashier(int id) throws SQLException {
        userDAO.setStatus(id, "INACTIVE");
    }

    public void activateCashier(int id) throws SQLException {
        userDAO.setStatus(id, "ACTIVE");
    }

    private void validateFields(String fullName, String username, String email) throws ValidationException {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new ValidationException("Full name is required.");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("Username is required.");
        }
        if (username.trim().length() < 3) {
            throw new ValidationException("Username must be at least 3 characters long.");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email is required.");
        }
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new ValidationException("Please enter a valid email address.");
        }
    }

    public static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }
}
