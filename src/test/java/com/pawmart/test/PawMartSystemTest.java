package com.pawmart.test;

import com.pawmart.database.DatabaseConnection;
import com.pawmart.database.DatabaseInitializer;
import com.pawmart.model.CartItem;
import com.pawmart.model.Category;
import com.pawmart.model.DashboardStats;
import com.pawmart.model.Product;
import com.pawmart.model.Role;
import com.pawmart.model.Sale;
import com.pawmart.model.User;
import com.pawmart.service.AuthService;
import com.pawmart.service.CategoryService;
import com.pawmart.service.CheckoutService;
import com.pawmart.service.ProductService;
import com.pawmart.service.ReceiptService;
import com.pawmart.service.ReportService;
import com.pawmart.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Automated Test Suite for PawMart Pet Shop Management System.
 * Tests Database, Authentication, Product Management, POS Checkout Transactions, Reports & Receipts.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PawMartSystemTest {

    private static AuthService authService;
    private static UserService userService;
    private static ProductService productService;
    private static CategoryService categoryService;
    private static CheckoutService checkoutService;
    private static ReportService reportService;
    private static ReceiptService receiptService;

    @BeforeAll
    public static void setUp() {
        assertTrue(DatabaseConnection.testConnection(), "MySQL Database Connection must be operational.");
        assertTrue(DatabaseInitializer.initializeDatabase(), "Database schema must be successfully initialized.");

        authService = AuthService.getInstance();
        userService = new UserService();
        productService = new ProductService();
        categoryService = new CategoryService();
        checkoutService = new CheckoutService();
        reportService = new ReportService();
        receiptService = new ReceiptService();
    }

    @Test
    @Order(1)
    public void testDatabaseConnectionAndTables() {
        assertTrue(DatabaseConnection.testConnection());
        assertTrue(DatabaseInitializer.hasManagerAccount());
    }

    @Test
    @Order(2)
    public void testAuthValidManagerLogin() throws Exception {
        User manager = authService.login("admin", "admin123");
        assertNotNull(manager);
        assertEquals("admin", manager.getUsername());
        assertEquals(Role.MANAGER, manager.getRole());
        assertTrue(manager.isActive());
        assertTrue(manager.isManager());
    }

    @Test
    @Order(3)
    public void testAuthValidCashierLogin() throws Exception {
        User cashier = authService.login("cashier", "cashier123");
        assertNotNull(cashier);
        assertEquals("cashier", cashier.getUsername());
        assertEquals(Role.CASHIER, cashier.getRole());
        assertTrue(cashier.isActive());
        assertFalse(cashier.isManager());
    }

    @Test
    @Order(4)
    public void testAuthInvalidPassword() {
        assertThrows(AuthService.AuthenticationException.class, () -> {
            authService.login("admin", "wrongpassword");
        });
    }

    @Test
    @Order(5)
    public void testAuthNonExistentUser() {
        assertThrows(AuthService.AuthenticationException.class, () -> {
            authService.login("nonexistent_user_999", "admin123");
        });
    }

    @Test
    @Order(6)
    public void testCategoryListingAndCreation() throws Exception {
        List<Category> categories = categoryService.getAllCategories();
        assertNotNull(categories);
        assertFalse(categories.isEmpty(), "Categories should contain seed pet categories.");

        // Test adding a custom category
        String uniqueCatName = "Aquatics & Fish " + System.currentTimeMillis();
        Category newCat = categoryService.addCategory(uniqueCatName, "Fish food, aquariums, filters");
        assertNotNull(newCat);
        assertTrue(newCat.getId() > 0);
    }

    @Test
    @Order(7)
    public void testProductValidationAndCreation() throws Exception {
        // Validation: price <= 0 should fail
        Product invalidPrice = new Product();
        invalidPrice.setName("Invalid Price Toy");
        invalidPrice.setCategoryId(1);
        invalidPrice.setPrice(BigDecimal.ZERO);
        assertThrows(ProductService.ValidationException.class, () -> {
            productService.addProduct(invalidPrice);
        });

        // Validation: negative quantity should fail
        Product invalidQty = new Product();
        invalidQty.setName("Invalid Qty Food");
        invalidQty.setCategoryId(1);
        invalidQty.setPrice(new BigDecimal("15.00"));
        invalidQty.setQuantity(-5);
        assertThrows(ProductService.ValidationException.class, () -> {
            productService.addProduct(invalidQty);
        });

        // Valid product creation
        Product validProd = new Product();
        validProd.setName("Squeaky Hedgehog Dog Plush " + System.currentTimeMillis());
        validProd.setCategoryId(3); // Pet Toys
        validProd.setPrice(new BigDecimal("12.50"));
        validProd.setQuantity(20);
        validProd.setLowStockThreshold(4);
        validProd.setDescription("Durable squeaky plush toy for puppies and active dogs.");

        productService.addProduct(validProd);
        assertTrue(validProd.getId() > 0);

        Product fetched = productService.getProductById(validProd.getId());
        assertNotNull(fetched);
        assertEquals(20, fetched.getQuantity());
    }

    @Test
    @Order(8)
    public void testPosCheckoutTransactionAndStockDeduction() throws Exception {
        // Authenticate cashier
        User cashier = authService.login("cashier", "cashier123");
        assertNotNull(cashier);

        // Fetch Product 1 (Royal Canin)
        Product p = productService.getProductById(1);
        assertNotNull(p);
        if (p.getQuantity() < 2) {
            p.setQuantity(25);
            productService.updateProduct(p);
            p = productService.getProductById(1);
        }
        int initialStock = p.getQuantity();
        assertTrue(initialStock >= 2, "Product 1 should have enough stock for test checkout.");

        // Create shopping cart with 2 units of Product 1
        List<CartItem> cart = new ArrayList<>();
        cart.add(new CartItem(p, 2));

        BigDecimal discount = new BigDecimal("2.00");
        Sale completedSale = checkoutService.processCheckout(cashier, cart, discount, "CASH");

        assertNotNull(completedSale);
        assertTrue(completedSale.getId() > 0);
        assertNotNull(completedSale.getReceiptNumber());
        assertTrue(completedSale.getReceiptNumber().startsWith("REC-"));

        // Verify subtotal = 2 * 38.50 = 77.00
        assertEquals(new BigDecimal("77.00"), completedSale.getSubtotal());
        assertEquals(new BigDecimal("75.00"), completedSale.getGrandTotal());

        // Verify stock was deducted atomically in MySQL
        Product afterSale = productService.getProductById(1);
        assertEquals(initialStock - 2, afterSale.getQuantity(), "Stock must be reduced by 2 units.");
    }

    @Test
    @Order(9)
    public void testCheckoutOversellingPrevention() throws Exception {
        User cashier = authService.login("cashier", "cashier123");
        Product p = productService.getProductById(9); // Low stock product (stock ~3)

        List<CartItem> cart = new ArrayList<>();
        // Request 9999 units (overselling)
        cart.add(new CartItem(p, 9999));

        assertThrows(CheckoutService.CheckoutException.class, () -> {
            checkoutService.processCheckout(cashier, cart, BigDecimal.ZERO, "CASH");
        });
    }

    @Test
    @Order(10)
    public void testReceiptFormattingAndPdfExport() throws Exception {
        User cashier = authService.login("cashier", "cashier123");
        List<Sale> sales = reportService.getCashierSales(cashier.getId());
        assertFalse(sales.isEmpty(), "Should have completed sales records.");

        Sale s = sales.get(0);
        Sale fullSale = reportService.getSaleDetails(s.getId());

        // Test Text Receipt
        String receiptText = receiptService.generateReceiptText(fullSale);
        assertNotNull(receiptText);
        assertTrue(receiptText.contains("PAWMART PET SHOP"));
        assertTrue(receiptText.contains(fullSale.getReceiptNumber()));

        // Test PDF Export
        File tempPdf = File.createTempFile("pawmart_test_receipt_", ".pdf");
        receiptService.exportToPdf(fullSale, tempPdf);
        assertTrue(tempPdf.exists(), "PDF file must be created on disk.");
        assertTrue(tempPdf.length() > 500, "PDF file must not be empty.");
        tempPdf.deleteOnExit();
    }

    @Test
    @Order(11)
    public void testDashboardStatsAndReports() throws Exception {
        DashboardStats stats = reportService.getDashboardStats();
        assertNotNull(stats);
        assertTrue(stats.getTotalProducts() > 0, "Total products should be > 0.");
        assertTrue(stats.getTotalCashiers() > 0, "Total cashiers should be > 0.");
        assertTrue(stats.getTotalSalesCount() > 0, "Total sales should be > 0.");
        assertTrue(stats.getTotalRevenue().compareTo(BigDecimal.ZERO) > 0, "Total revenue should be > $0.00.");
    }

    @Test
    @Order(12)
    public void testNetBeansGuiBuilderForms() throws Exception {
        String[] formClasses = {
            "login",
            "Managerpage",
            "Dashboard",
            "Addpetsupplies",
            "createcashieraccounts",
            "Searchpetsupplies",
            "Viewpetsupllies",
            "POSBillingFrame",
            "Updatepetsupplies",
            "ReportsSalesFrame"
        };

        for (String className : formClasses) {
            Class<?> clazz = Class.forName(className);
            Object instance = clazz.getDeclaredConstructor().newInstance();
            assertNotNull(instance, "Form " + className + " must instantiate successfully.");
            if (instance instanceof javax.swing.JFrame frame) {
                assertNotNull(frame.getTitle(), "Form " + className + " must have a window title.");
                assertTrue(frame.getWidth() > 0, "Form " + className + " must have non-zero width.");
                assertTrue(frame.getHeight() > 0, "Form " + className + " must have non-zero height.");
                frame.dispose();
            }
        }
    }
}
