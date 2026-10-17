package com.pawmart.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a completed sales transaction.
 */
public class Sale {
    private int id;
    private String receiptNumber;
    private int cashierId;
    private String cashierName;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal grandTotal;
    private String paymentMethod; // 'CASH', 'CREDIT_CARD', 'DEBIT_CARD', 'MOBILE_PAY'
    private BigDecimal amountReceived;
    private BigDecimal changeAmount;
    private Timestamp createdAt;
    private List<SaleItem> items = new ArrayList<>();

    public Sale() {
        this.subtotal = BigDecimal.ZERO;
        this.discountAmount = BigDecimal.ZERO;
        this.grandTotal = BigDecimal.ZERO;
        this.paymentMethod = "CASH";
    }

    public Sale(int id, String receiptNumber, int cashierId, String cashierName, BigDecimal subtotal,
                BigDecimal discountAmount, BigDecimal grandTotal, String paymentMethod, Timestamp createdAt) {
        this.id = id;
        this.receiptNumber = receiptNumber;
        this.cashierId = cashierId;
        this.cashierName = cashierName;
        this.subtotal = subtotal;
        this.discountAmount = discountAmount;
        this.grandTotal = grandTotal;
        this.paymentMethod = paymentMethod;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public int getCashierId() {
        return cashierId;
    }

    public void setCashierId(int cashierId) {
        this.cashierId = cashierId;
    }

    public String getCashierName() {
        return cashierName;
    }

    public void setCashierName(String cashierName) {
        this.cashierName = cashierName;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(BigDecimal grandTotal) {
        this.grandTotal = grandTotal;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getAmountReceived() {
        return amountReceived;
    }

    public void setAmountReceived(BigDecimal amountReceived) {
        this.amountReceived = amountReceived;
    }

    public BigDecimal getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(BigDecimal changeAmount) {
        this.changeAmount = changeAmount;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public List<SaleItem> getItems() {
        return items;
    }

    public void setItems(List<SaleItem> items) {
        this.items = items;
    }

    public void addItem(SaleItem item) {
        this.items.add(item);
    }
}
