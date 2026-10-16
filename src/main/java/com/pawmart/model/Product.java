package com.pawmart.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Pet shop product model.
 */
public class Product {
    private int id;
    private String name;
    private int categoryId;
    private String categoryName;
    private BigDecimal price;
    private int quantity;
    private String description;
    private String imagePath;
    private int lowStockThreshold;
    private String status; // 'ACTIVE' or 'INACTIVE'
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Product() {
        this.price = BigDecimal.ZERO;
        this.quantity = 0;
        this.lowStockThreshold = 5;
        this.status = "ACTIVE";
    }

    public Product(int id, String name, int categoryId, String categoryName, BigDecimal price, int quantity,
                   String description, String imagePath, int lowStockThreshold, String status) {
        this.id = id;
        this.name = name;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.price = price;
        this.quantity = quantity;
        this.description = description;
        this.imagePath = imagePath;
        this.lowStockThreshold = lowStockThreshold;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getLowStockThreshold() {
        return lowStockThreshold;
    }

    public void setLowStockThreshold(int lowStockThreshold) {
        this.lowStockThreshold = lowStockThreshold;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(status);
    }

    public boolean isLowStock() {
        return quantity <= lowStockThreshold;
    }

    public boolean isOutOfStock() {
        return quantity <= 0;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return name + " ($" + price + ")";
    }
}
