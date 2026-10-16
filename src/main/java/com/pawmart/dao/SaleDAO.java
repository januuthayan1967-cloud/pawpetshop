package com.pawmart.dao;

import com.pawmart.database.DatabaseConnection;
import com.pawmart.model.CartItem;
import com.pawmart.model.Sale;
import com.pawmart.model.SaleItem;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for Point of Sale transactions, Receipts, and Reports.
 * Implements strict ACID transaction handling for stock deduction and order creation.
 */
public class SaleDAO {

    /**
     * Executes an atomic, transactional checkout.
     * Deducts stock using optimistic concurrency guard (quantity >= requested).
     * Rolls back completely if any product is insufficient or database error occurs.
     */
    public boolean executeCheckout(Sale sale, List<CartItem> cartItems) throws SQLException {
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart cannot be empty for checkout.");
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Begin ACID transaction

            // 1. Deduct stock for each cart item
            String updateStockSql = "UPDATE products SET quantity = quantity - ? WHERE id = ? AND quantity >= ? AND status = 'ACTIVE'";
            try (PreparedStatement psStock = conn.prepareStatement(updateStockSql)) {
                for (CartItem item : cartItems) {
                    psStock.setInt(1, item.getQuantity());
                    psStock.setInt(2, item.getProduct().getId());
                    psStock.setInt(3, item.getQuantity());
                    int updated = psStock.executeUpdate();
                    if (updated == 0) {
                        conn.rollback();
                        throw new SQLException("Insufficient stock or product unavailable for: "
                                + item.getProduct().getName() + " (Requested: " + item.getQuantity() + ")");
                    }
                }
            }

            // 2. Insert into sales table
            String insertSaleSql = """
                INSERT INTO sales (receipt_number, cashier_id, subtotal, discount_amount, grand_total, payment_method)
                VALUES (?, ?, ?, ?, ?, ?)
            """;
            int generatedSaleId = 0;
            try (PreparedStatement psSale = conn.prepareStatement(insertSaleSql, Statement.RETURN_GENERATED_KEYS)) {
                psSale.setString(1, sale.getReceiptNumber());
                psSale.setInt(2, sale.getCashierId());
                psSale.setBigDecimal(3, sale.getSubtotal());
                psSale.setBigDecimal(4, sale.getDiscountAmount());
                psSale.setBigDecimal(5, sale.getGrandTotal());
                psSale.setString(6, sale.getPaymentMethod());
                psSale.executeUpdate();

                try (ResultSet keys = psSale.getGeneratedKeys()) {
                    if (keys.next()) {
                        generatedSaleId = keys.getInt(1);
                        sale.setId(generatedSaleId);
                    } else {
                        conn.rollback();
                        throw new SQLException("Failed to obtain generated sale ID.");
                    }
                }
            }

            // 3. Insert sale_items
            String insertItemSql = """
                INSERT INTO sale_items (sale_id, product_id, product_name, unit_price, quantity, line_total)
                VALUES (?, ?, ?, ?, ?, ?)
            """;
            try (PreparedStatement psItem = conn.prepareStatement(insertItemSql, Statement.RETURN_GENERATED_KEYS)) {
                for (CartItem item : cartItems) {
                    psItem.setInt(1, generatedSaleId);
                    psItem.setInt(2, item.getProduct().getId());
                    psItem.setString(3, item.getProduct().getName());
                    psItem.setBigDecimal(4, item.getUnitPrice());
                    psItem.setInt(5, item.getQuantity());
                    psItem.setBigDecimal(6, item.getLineTotal());
                    psItem.addBatch();
                }
                psItem.executeBatch();
            }

            // Commit transaction
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rbEx) {
                    System.err.println("[SaleDAO] Rollback failed: " + rbEx.getMessage());
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    public Sale findById(int id) throws SQLException {
        String sql = """
            SELECT s.*, u.full_name AS cashier_name
            FROM sales s
            JOIN users u ON s.cashier_id = u.id
            WHERE s.id = ?
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Sale sale = mapRow(rs);
                    sale.setItems(getItemsForSale(sale.getId()));
                    return sale;
                }
            }
        }
        return null;
    }

    public Sale findByReceiptNumber(String receiptNumber) throws SQLException {
        String sql = """
            SELECT s.*, u.full_name AS cashier_name
            FROM sales s
            JOIN users u ON s.cashier_id = u.id
            WHERE s.receipt_number = ?
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, receiptNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Sale sale = mapRow(rs);
                    sale.setItems(getItemsForSale(sale.getId()));
                    return sale;
                }
            }
        }
        return null;
    }

    public List<Sale> findAll() throws SQLException {
        List<Sale> list = new ArrayList<>();
        String sql = """
            SELECT s.*, u.full_name AS cashier_name
            FROM sales s
            JOIN users u ON s.cashier_id = u.id
            ORDER BY s.created_at DESC
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<Sale> findByCashierId(int cashierId) throws SQLException {
        List<Sale> list = new ArrayList<>();
        String sql = """
            SELECT s.*, u.full_name AS cashier_name
            FROM sales s
            JOIN users u ON s.cashier_id = u.id
            WHERE s.cashier_id = ?
            ORDER BY s.created_at DESC
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cashierId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public List<Sale> findRecent(int limit) throws SQLException {
        List<Sale> list = new ArrayList<>();
        String sql = """
            SELECT s.*, u.full_name AS cashier_name
            FROM sales s
            JOIN users u ON s.cashier_id = u.id
            ORDER BY s.created_at DESC
            LIMIT ?
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public List<Sale> filterSales(String receiptQuery, Integer cashierId, java.util.Date startDate, java.util.Date endDate) throws SQLException {
        List<Sale> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT s.*, u.full_name AS cashier_name
            FROM sales s
            JOIN users u ON s.cashier_id = u.id
            WHERE 1=1
        """);

        if (receiptQuery != null && !receiptQuery.trim().isEmpty()) {
            sql.append(" AND s.receipt_number LIKE ?");
        }
        if (cashierId != null && cashierId > 0) {
            sql.append(" AND s.cashier_id = ?");
        }
        if (startDate != null) {
            sql.append(" AND s.created_at >= ?");
        }
        if (endDate != null) {
            sql.append(" AND s.created_at <= ?");
        }
        sql.append(" ORDER BY s.created_at DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (receiptQuery != null && !receiptQuery.trim().isEmpty()) {
                ps.setString(idx++, "%" + receiptQuery.trim() + "%");
            }
            if (cashierId != null && cashierId > 0) {
                ps.setInt(idx++, cashierId);
            }
            if (startDate != null) {
                ps.setTimestamp(idx++, new java.sql.Timestamp(startDate.getTime()));
            }
            if (endDate != null) {
                ps.setTimestamp(idx++, new java.sql.Timestamp(endDate.getTime()));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public List<SaleItem> getItemsForSale(int saleId) throws SQLException {
        List<SaleItem> list = new ArrayList<>();
        String sql = "SELECT * FROM sale_items WHERE sale_id = ? ORDER BY id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, saleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SaleItem item = new SaleItem();
                    item.setId(rs.getInt("id"));
                    item.setSaleId(rs.getInt("sale_id"));
                    item.setProductId(rs.getInt("product_id"));
                    item.setProductName(rs.getString("product_name"));
                    item.setUnitPrice(rs.getBigDecimal("unit_price"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setLineTotal(rs.getBigDecimal("line_total"));
                    list.add(item);
                }
            }
        }
        return list;
    }

    public int getTotalSalesCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM sales";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public BigDecimal getTotalRevenue() throws SQLException {
        String sql = "SELECT COALESCE(SUM(grand_total), 0) FROM sales";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
        }
        return BigDecimal.ZERO;
    }

    public Map<String, BigDecimal> getMonthlyRevenue() throws SQLException {
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        String sql = """
            SELECT DATE_FORMAT(created_at, '%b %Y') AS month_label,
                   SUM(grand_total) AS total_amount
            FROM sales
            GROUP BY DATE_FORMAT(created_at, '%Y-%m'), DATE_FORMAT(created_at, '%b %Y')
            ORDER BY DATE_FORMAT(created_at, '%Y-%m') ASC
            LIMIT 12
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                map.put(rs.getString("month_label"), rs.getBigDecimal("total_amount"));
            }
        }
        return map;
    }

    public Map<String, Integer> getCategoryDistribution() throws SQLException {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = """
            SELECT c.name, COUNT(p.id) AS product_count
            FROM categories c
            LEFT JOIN products p ON c.id = p.category_id AND p.status = 'ACTIVE'
            GROUP BY c.id, c.name
            ORDER BY product_count DESC
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                map.put(rs.getString("name"), rs.getInt("product_count"));
            }
        }
        return map;
    }

    private Sale mapRow(ResultSet rs) throws SQLException {
        Sale s = new Sale();
        s.setId(rs.getInt("id"));
        s.setReceiptNumber(rs.getString("receipt_number"));
        s.setCashierId(rs.getInt("cashier_id"));
        s.setCashierName(rs.getString("cashier_name"));
        s.setSubtotal(rs.getBigDecimal("subtotal"));
        s.setDiscountAmount(rs.getBigDecimal("discount_amount"));
        s.setGrandTotal(rs.getBigDecimal("grand_total"));
        s.setPaymentMethod(rs.getString("payment_method"));
        s.setCreatedAt(rs.getTimestamp("created_at"));
        return s;
    }
}
