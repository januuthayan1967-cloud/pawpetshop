package com.pawmart.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Encapsulates live database dashboard metrics.
 */
public class DashboardStats {
    private int totalProducts;
    private int totalCashiers;
    private int totalSalesCount;
    private BigDecimal totalRevenue;
    private int lowStockCount;
    private List<Sale> recentSales;
    private List<Product> lowStockProducts;
    private Map<String, BigDecimal> monthlyRevenue; // Month label -> Amount
    private Map<String, Integer> categoryDistribution; // Category name -> Product count

    public DashboardStats() {
        this.totalRevenue = BigDecimal.ZERO;
    }

    public int getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(int totalProducts) {
        this.totalProducts = totalProducts;
    }

    public int getTotalCashiers() {
        return totalCashiers;
    }

    public void setTotalCashiers(int totalCashiers) {
        this.totalCashiers = totalCashiers;
    }

    public int getTotalSalesCount() {
        return totalSalesCount;
    }

    public void setTotalSalesCount(int totalSalesCount) {
        this.totalSalesCount = totalSalesCount;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue != null ? totalRevenue : BigDecimal.ZERO;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public int getLowStockCount() {
        return lowStockCount;
    }

    public void setLowStockCount(int lowStockCount) {
        this.lowStockCount = lowStockCount;
    }

    public List<Sale> getRecentSales() {
        return recentSales;
    }

    public void setRecentSales(List<Sale> recentSales) {
        this.recentSales = recentSales;
    }

    public List<Product> getLowStockProducts() {
        return lowStockProducts;
    }

    public void setLowStockProducts(List<Product> lowStockProducts) {
        this.lowStockProducts = lowStockProducts;
    }

    public Map<String, BigDecimal> getMonthlyRevenue() {
        return monthlyRevenue;
    }

    public void setMonthlyRevenue(Map<String, BigDecimal> monthlyRevenue) {
        this.monthlyRevenue = monthlyRevenue;
    }

    public Map<String, Integer> getCategoryDistribution() {
        return categoryDistribution;
    }

    public void setCategoryDistribution(Map<String, Integer> categoryDistribution) {
        this.categoryDistribution = categoryDistribution;
    }
}
