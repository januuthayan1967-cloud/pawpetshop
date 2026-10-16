package com.pawmart.service;

import com.pawmart.dao.ProductDAO;
import com.pawmart.model.Product;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * Service for Product Management and Inventory Operations.
 */
public class ProductService {

    private final ProductDAO productDAO;

    public ProductService() {
        this.productDAO = new ProductDAO();
    }

    public List<Product> getAllProducts(boolean activeOnly) throws SQLException {
        return productDAO.findAll(activeOnly);
    }

    public Product getProductById(int id) throws SQLException {
        return productDAO.findById(id);
    }

    public List<Product> searchProducts(String query, Integer categoryId, boolean activeOnly) throws SQLException {
        return productDAO.search(query, categoryId, activeOnly);
    }

    public List<Product> getLowStockProducts(boolean activeOnly) throws SQLException {
        return productDAO.findLowStock(activeOnly);
    }

    public void addProduct(Product product) throws ValidationException, SQLException {
        validateProduct(product);
        productDAO.create(product);
    }

    public void updateProduct(Product product) throws ValidationException, SQLException {
        if (product.getId() <= 0) {
            throw new ValidationException("Invalid product ID.");
        }
        validateProduct(product);
        productDAO.update(product);
    }

    public boolean deleteOrDeactivateProduct(int productId) throws SQLException {
        return productDAO.safeDelete(productId);
    }

    public void setProductStatus(int productId, String status) throws SQLException {
        productDAO.setStatus(productId, status);
    }

    private void validateProduct(Product product) throws ValidationException {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new ValidationException("Product name is required.");
        }
        if (product.getCategoryId() <= 0) {
            throw new ValidationException("Please select a valid product category.");
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Product price must be greater than zero.");
        }
        if (product.getQuantity() < 0) {
            throw new ValidationException("Product quantity cannot be negative.");
        }
        if (product.getLowStockThreshold() < 0) {
            throw new ValidationException("Low stock threshold cannot be negative.");
        }
    }

    public static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }
}
