package com.pawmart.service;

import com.pawmart.dao.CategoryDAO;
import com.pawmart.model.Category;

import java.sql.SQLException;
import java.util.List;

/**
 * Service for Product Category management.
 */
public class CategoryService {

    private final CategoryDAO categoryDAO;

    public CategoryService() {
        this.categoryDAO = new CategoryDAO();
    }

    public List<Category> getAllCategories() throws SQLException {
        return categoryDAO.findAll();
    }

    public Category getCategoryById(int id) throws SQLException {
        return categoryDAO.findById(id);
    }

    public Category addCategory(String name, String description) throws ValidationException, SQLException {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Category name is required.");
        }
        Category cat = new Category(0, name.trim(), description != null ? description.trim() : "");
        categoryDAO.create(cat);
        return cat;
    }

    public void updateCategory(int id, String name, String description) throws ValidationException, SQLException {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Category name is required.");
        }
        Category cat = new Category(id, name.trim(), description != null ? description.trim() : "");
        categoryDAO.update(cat);
    }

    public boolean deleteCategory(int id) throws SQLException {
        return categoryDAO.delete(id);
    }

    public static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }
}
