package com.pawmart.dao;

import com.pawmart.database.DatabaseConnection;
import com.pawmart.model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Pet Shop Products and Inventory.
 */
public class ProductDAO {

    public List<Product> findAll(boolean activeOnly) throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = """
            SELECT p.*, c.name AS category_name
            FROM products p
            LEFT JOIN categories c ON p.category_id = c.id
        """ + (activeOnly ? " WHERE p.status = 'ACTIVE' " : "") + " ORDER BY p.name ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public Product findById(int id) throws SQLException {
        String sql = """
            SELECT p.*, c.name AS category_name
            FROM products p
            LEFT JOIN categories c ON p.category_id = c.id
            WHERE p.id = ?
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public List<Product> findByCategory(int categoryId, boolean activeOnly) throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = """
            SELECT p.*, c.name AS category_name
            FROM products p
            LEFT JOIN categories c ON p.category_id = c.id
            WHERE p.category_id = ?
        """ + (activeOnly ? " AND p.status = 'ACTIVE' " : "") + " ORDER BY p.name ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public List<Product> search(String keyword, Integer categoryId, boolean activeOnly) throws SQLException {
        List<Product> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT p.*, c.name AS category_name
            FROM products p
            LEFT JOIN categories c ON p.category_id = c.id
            WHERE 1=1
        """);

        if (activeOnly) {
            sql.append(" AND p.status = 'ACTIVE'");
        }
        if (categoryId != null && categoryId > 0) {
            sql.append(" AND p.category_id = ?");
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (p.name LIKE ? OR p.description LIKE ?)");
        }
        sql.append(" ORDER BY p.name ASC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (categoryId != null && categoryId > 0) {
                ps.setInt(paramIndex++, categoryId);
            }
            if (keyword != null && !keyword.trim().isEmpty()) {
                String pattern = "%" + keyword.trim() + "%";
                ps.setString(paramIndex++, pattern);
                ps.setString(paramIndex++, pattern);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public List<Product> findLowStock(boolean activeOnly) throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = """
            SELECT p.*, c.name AS category_name
            FROM products p
            LEFT JOIN categories c ON p.category_id = c.id
            WHERE p.quantity <= p.low_stock_threshold
        """ + (activeOnly ? " AND p.status = 'ACTIVE' " : "") + " ORDER BY p.quantity ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public boolean create(Product p) throws SQLException {
        String sql = """
            INSERT INTO products (name, category_id, price, quantity, description, image_path, low_stock_threshold, status)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getName());
            ps.setInt(2, p.getCategoryId());
            ps.setBigDecimal(3, p.getPrice());
            ps.setInt(4, p.getQuantity());
            ps.setString(5, p.getDescription());
            ps.setString(6, p.getImagePath());
            ps.setInt(7, p.getLowStockThreshold());
            ps.setString(8, p.getStatus() != null ? p.getStatus() : "ACTIVE");
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        p.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean update(Product p) throws SQLException {
        String sql = """
            UPDATE products
            SET name = ?, category_id = ?, price = ?, quantity = ?, description = ?,
                image_path = ?, low_stock_threshold = ?, status = ?
            WHERE id = ?
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setInt(2, p.getCategoryId());
            ps.setBigDecimal(3, p.getPrice());
            ps.setInt(4, p.getQuantity());
            ps.setString(5, p.getDescription());
            ps.setString(6, p.getImagePath());
            ps.setInt(7, p.getLowStockThreshold());
            ps.setString(8, p.getStatus());
            ps.setInt(9, p.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean hasSalesHistory(int productId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM sale_items WHERE product_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Safely deletes a product if no sales reference it, otherwise deactivates it.
     * @return true if deleted or deactivated successfully
     */
    public boolean safeDelete(int productId) throws SQLException {
        if (hasSalesHistory(productId)) {
            // Soft delete by deactivating to preserve sales history
            return setStatus(productId, "INACTIVE");
        } else {
            String sql = "DELETE FROM products WHERE id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, productId);
                return ps.executeUpdate() > 0;
            }
        }
    }

    public boolean setStatus(int productId, String status) throws SQLException {
        String sql = "UPDATE products SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, productId);
            return ps.executeUpdate() > 0;
        }
    }

    public int countTotalProducts() throws SQLException {
        String sql = "SELECT COUNT(*) FROM products WHERE status = 'ACTIVE'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public int countLowStockProducts() throws SQLException {
        String sql = "SELECT COUNT(*) FROM products WHERE status = 'ACTIVE' AND quantity <= low_stock_threshold";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private Product mapRow(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setCategoryId(rs.getInt("category_id"));
        p.setCategoryName(rs.getString("category_name"));
        p.setPrice(rs.getBigDecimal("price"));
        p.setQuantity(rs.getInt("quantity"));
        p.setDescription(rs.getString("description"));
        p.setImagePath(rs.getString("image_path"));
        p.setLowStockThreshold(rs.getInt("low_stock_threshold"));
        p.setStatus(rs.getString("status"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        p.setUpdatedAt(rs.getTimestamp("updated_at"));
        return p;
    }
}
