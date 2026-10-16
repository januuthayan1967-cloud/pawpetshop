package com.pawmart.service;

import com.pawmart.dao.ProductDAO;
import com.pawmart.dao.SaleDAO;
import com.pawmart.dao.UserDAO;
import com.pawmart.model.DashboardStats;
import com.pawmart.model.Sale;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Service for Analytics, Manager Reports, and Cashier History.
 */
public class ReportService {

    private final SaleDAO saleDAO;
    private final ProductDAO productDAO;
    private final UserDAO userDAO;

    public ReportService() {
        this.saleDAO = new SaleDAO();
        this.productDAO = new ProductDAO();
        this.userDAO = new UserDAO();
    }

    public DashboardStats getDashboardStats() throws SQLException {
        DashboardStats stats = new DashboardStats();
        stats.setTotalProducts(productDAO.countTotalProducts());
        stats.setTotalCashiers(userDAO.countCashiers());
        stats.setTotalSalesCount(saleDAO.getTotalSalesCount());
        stats.setTotalRevenue(saleDAO.getTotalRevenue());
        stats.setLowStockCount(productDAO.countLowStockProducts());
        stats.setRecentSales(saleDAO.findRecent(8));
        stats.setLowStockProducts(productDAO.findLowStock(true));
        stats.setMonthlyRevenue(saleDAO.getMonthlyRevenue());
        stats.setCategoryDistribution(saleDAO.getCategoryDistribution());
        return stats;
    }

    public List<Sale> getAllSales() throws SQLException {
        return saleDAO.findAll();
    }

    public List<Sale> getCashierSales(int cashierId) throws SQLException {
        return saleDAO.findByCashierId(cashierId);
    }

    public List<Sale> filterSales(String receiptNumber, Integer cashierId, Date startDate, Date endDate) throws SQLException {
        return saleDAO.filterSales(receiptNumber, cashierId, startDate, endDate);
    }

    public Sale getSaleDetails(int saleId) throws SQLException {
        return saleDAO.findById(saleId);
    }

    public Sale getSaleByReceipt(String receiptNumber) throws SQLException {
        return saleDAO.findByReceiptNumber(receiptNumber);
    }
}
