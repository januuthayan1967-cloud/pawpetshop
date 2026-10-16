package com.pawmart.service;

import com.pawmart.dao.SaleDAO;
import com.pawmart.model.CartItem;
import com.pawmart.model.Sale;
import com.pawmart.model.SaleItem;
import com.pawmart.model.User;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Service orchestrating POS checkout, receipt number generation, and transactional processing.
 */
public class CheckoutService {

    private final SaleDAO saleDAO;
    private final Random random = new Random();

    public CheckoutService() {
        this.saleDAO = new SaleDAO();
    }

    public Sale processCheckout(User cashier, List<CartItem> cartItems, BigDecimal discountAmount, String paymentMethod)
            throws CheckoutException, SQLException {

        if (cashier == null) {
            throw new CheckoutException("No authenticated cashier session found.");
        }
        if (cartItems == null || cartItems.isEmpty()) {
            throw new CheckoutException("Shopping cart is empty.");
        }

        // Validate quantities and calculate subtotal
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            if (item.getQuantity() <= 0) {
                throw new CheckoutException("Invalid quantity for product: " + item.getProduct().getName());
            }
            if (item.getQuantity() > item.getProduct().getQuantity()) {
                throw new CheckoutException("Insufficient stock for " + item.getProduct().getName()
                        + " (Available: " + item.getProduct().getQuantity() + ", In Cart: " + item.getQuantity() + ")");
            }
            subtotal = subtotal.add(item.getLineTotal());
        }

        if (discountAmount == null) {
            discountAmount = BigDecimal.ZERO;
        }
        if (discountAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new CheckoutException("Discount cannot be negative.");
        }
        if (discountAmount.compareTo(subtotal) > 0) {
            throw new CheckoutException("Discount cannot exceed subtotal ($" + subtotal + ").");
        }

        BigDecimal grandTotal = subtotal.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            paymentMethod = "CASH";
        }

        String receiptNumber = generateReceiptNumber();

        Sale sale = new Sale();
        sale.setReceiptNumber(receiptNumber);
        sale.setCashierId(cashier.getId());
        sale.setCashierName(cashier.getFullName());
        sale.setSubtotal(subtotal.setScale(2, RoundingMode.HALF_UP));
        sale.setDiscountAmount(discountAmount.setScale(2, RoundingMode.HALF_UP));
        sale.setGrandTotal(grandTotal);
        sale.setPaymentMethod(paymentMethod.toUpperCase());

        // Execute atomic transaction
        saleDAO.executeCheckout(sale, cartItems);

        // Populate items in returned sale for instant receipt preview
        for (CartItem ci : cartItems) {
            SaleItem si = new SaleItem();
            si.setSaleId(sale.getId());
            si.setProductId(ci.getProduct().getId());
            si.setProductName(ci.getProduct().getName());
            si.setUnitPrice(ci.getUnitPrice());
            si.setQuantity(ci.getQuantity());
            si.setLineTotal(ci.getLineTotal());
            sale.addItem(si);
        }

        return sale;
    }

    private synchronized String generateReceiptNumber() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String datePart = sdf.format(new Date());
        int randomPart = 1000 + random.nextInt(9000);
        return "REC-" + datePart + "-" + randomPart;
    }

    public static class CheckoutException extends Exception {
        public CheckoutException(String message) {
            super(message);
        }
    }
}
