package com.pawmart.ui.views;

import com.pawmart.model.CartItem;
import com.pawmart.model.Category;
import com.pawmart.model.Product;
import com.pawmart.model.Sale;
import com.pawmart.model.User;
import com.pawmart.service.AuthService;
import com.pawmart.service.CategoryService;
import com.pawmart.service.CheckoutService;
import com.pawmart.service.ProductService;
import com.pawmart.service.ReportService;
import com.pawmart.ui.components.DangerButton;
import com.pawmart.ui.components.ModernTable;
import com.pawmart.ui.components.ModernTextField;
import com.pawmart.ui.components.PawIconRenderer;
import com.pawmart.ui.components.PrimaryButton;
import com.pawmart.ui.components.RoundedPanel;
import com.pawmart.ui.components.SecondaryButton;
import com.pawmart.ui.theme.PawMartTheme;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Modern Cashier Dashboard and Point of Sale (POS) Billing Terminal for PawMart.
 */
public class CashierDashboardFrame extends JFrame {

    private final User currentCashier;
    private final ProductService productService = new ProductService();
    private final CategoryService categoryService = new CategoryService();
    private final CheckoutService checkoutService = new CheckoutService();
    private final ReportService reportService = new ReportService();

    private CardLayout cardLayout;
    private JPanel contentCards;
    private final List<JButton> navButtons = new ArrayList<>();

    // POS State
    private final List<CartItem> cartItems = new ArrayList<>();
    private DefaultTableModel cartTableModel;
    private DefaultTableModel productCatalogModel;
    private ModernTable tblCart;
    private ModernTable tblProductsPos;

    private JLabel lblSubtotalVal;
    private JLabel lblGrandTotalVal;
    private JLabel lblChangeVal;
    private ModernTextField txtDiscount;
    private ModernTextField txtCashTendered;
    private JComboBox<String> cmbPaymentMethod;
    private JComboBox<CategoryItem> cmbCategoryPos;
    private ModernTextField txtSearchPos;

    // Sales History Table
    private DefaultTableModel historyTableModel;
    private ModernTable tblHistory;

    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public CashierDashboardFrame(User cashier) {
        this.currentCashier = cashier;
        setTitle("PawMart POS Billing Terminal - Cashier: " + cashier.getFullName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1260, 800);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);
        getContentPane().setBackground(PawMartTheme.BG_CREAM);
        setLayout(new BorderLayout());

        initUi();
        loadPosProducts();
    }

    private void initUi() {
        // Deep Teal Navigation Sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // Center Content Area (CardLayout)
        cardLayout = new CardLayout();
        contentCards = new JPanel(cardLayout);
        contentCards.setBackground(PawMartTheme.BG_CREAM);
        contentCards.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        contentCards.add(createPosPanel(), "POS");
        contentCards.add(createCatalogViewPanel(), "CATALOG");
        contentCards.add(createSalesHistoryPanel(), "HISTORY");

        add(contentCards, BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(PawMartTheme.TEAL_PRIMARY);
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 14, 20, 14));

        // Top Brand Header
        JPanel topBox = new JPanel();
        topBox.setOpaque(false);
        topBox.setLayout(new BoxLayout(topBox, BoxLayout.Y_AXIS));

        JPanel brandRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brandRow.setOpaque(false);

        JLabel logoLabel = loadScaledImage("/images/logo.png", 36, 36);
        if (logoLabel == null) {
            logoLabel = new JLabel(PawIconRenderer.createPawIcon(30, Color.WHITE));
        }

        JPanel brandText = new JPanel();
        brandText.setOpaque(false);
        brandText.setLayout(new BoxLayout(brandText, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("PawMart");
        title.setFont(PawMartTheme.FONT_TITLE_MEDIUM);
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("POS TERMINAL");
        subtitle.setFont(PawMartTheme.FONT_SMALL_BOLD);
        subtitle.setForeground(PawMartTheme.AMBER_ACCENT);

        brandText.add(title);
        brandText.add(subtitle);

        brandRow.add(logoLabel);
        brandRow.add(brandText);
        topBox.add(brandRow);
        topBox.add(Box.createVerticalStrut(18));

        // Cashier Profile Card
        JPanel profileCard = new RoundedPanel(12, PawMartTheme.TEAL_DARK, null, false);
        profileCard.setLayout(new BorderLayout(8, 0));
        profileCard.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        profileCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        JLabel avatar = new JLabel(PawIconRenderer.createPawIcon(20, PawMartTheme.EMERALD_PRIMARY));
        JPanel profInfo = new JPanel();
        profInfo.setOpaque(false);
        profInfo.setLayout(new BoxLayout(profInfo, BoxLayout.Y_AXIS));

        JLabel profName = new JLabel(currentCashier.getFullName());
        profName.setFont(PawMartTheme.FONT_BODY_BOLD);
        profName.setForeground(Color.WHITE);

        JLabel profRole = new JLabel("Cashier on Duty");
        profRole.setFont(PawMartTheme.FONT_SMALL);
        profRole.setForeground(PawMartTheme.TEXT_LIGHT);

        profInfo.add(profName);
        profInfo.add(profRole);

        profileCard.add(avatar, BorderLayout.WEST);
        profileCard.add(profInfo, BorderLayout.CENTER);
        topBox.add(profileCard);
        topBox.add(Box.createVerticalStrut(20));

        sidebar.add(topBox, BorderLayout.NORTH);

        // Center Nav Items
        JPanel navMenu = new JPanel();
        navMenu.setOpaque(false);
        navMenu.setLayout(new BoxLayout(navMenu, BoxLayout.Y_AXIS));

        addNavItem(navMenu, "POS Billing", "POS", true);
        addNavItem(navMenu, "Product Catalog", "CATALOG", false);
        addNavItem(navMenu, "My Sales History", "HISTORY", false);

        sidebar.add(navMenu, BorderLayout.CENTER);

        // Bottom Logout
        JPanel bottomBox = new JPanel(new BorderLayout());
        bottomBox.setOpaque(false);

        JButton btnLogout = createNavButton("Logout", false);
        btnLogout.setForeground(new Color(254, 202, 202));
        btnLogout.addActionListener(e -> logout());
        bottomBox.add(btnLogout, BorderLayout.SOUTH);

        sidebar.add(bottomBox, BorderLayout.SOUTH);

        return sidebar;
    }

    private void addNavItem(JPanel container, String text, String cardKey, boolean selected) {
        JButton btn = createNavButton(text, selected);
        btn.addActionListener(e -> {
            for (JButton b : navButtons) {
                b.setBackground(PawMartTheme.TEAL_PRIMARY);
                b.setForeground(new Color(226, 232, 240));
            }
            btn.setBackground(PawMartTheme.TEAL_SELECTED);
            btn.setForeground(Color.WHITE);
            cardLayout.show(contentCards, cardKey);

            if ("POS".equals(cardKey)) loadPosProducts();
            else if ("CATALOG".equals(cardKey)) loadCatalog();
            else if ("HISTORY".equals(cardKey)) loadSalesHistory();
        });
        navButtons.add(btn);
        container.add(btn);
        container.add(Box.createVerticalStrut(4));
    }

    private JButton createNavButton(String text, boolean selected) {
        JButton btn = new JButton(text);
        btn.setFont(PawMartTheme.FONT_BODY_BOLD);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setForeground(selected ? Color.WHITE : new Color(226, 232, 240));
        btn.setBackground(selected ? PawMartTheme.TEAL_SELECTED : PawMartTheme.TEAL_PRIMARY);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(true);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return btn;
    }

    // =========================================================================
    // 1. POINT OF SALE (POS) BILLING TERMINAL
    // =========================================================================
    private JPanel createPosPanel() {
        JPanel panel = new JPanel(new BorderLayout(14, 14));
        panel.setOpaque(false);

        // Two-Column POS Layout (Left: Product Catalog & Search | Right: Shopping Cart & Checkout)
        JPanel splitPos = new JPanel(new GridLayout(1, 2, 14, 14));
        splitPos.setOpaque(false);

        // LEFT: Products Section
        JPanel leftProductSection = createPosLeftProductsSection();
        splitPos.add(leftProductSection);

        // RIGHT: Cart & Checkout Summary
        JPanel rightCartSection = createPosRightCartSection();
        splitPos.add(rightCartSection);

        panel.add(splitPos, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPosLeftProductsSection() {
        JPanel panel = new RoundedPanel(16, Color.WHITE, PawMartTheme.CARD_BORDER, true);
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        // Header + Filter Controls
        JPanel topControls = new JPanel();
        topControls.setOpaque(false);
        topControls.setLayout(new BoxLayout(topControls, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Pet Supplies Catalog");
        title.setFont(PawMartTheme.FONT_TITLE_MEDIUM);
        title.setForeground(PawMartTheme.TEXT_DARK);

        JLabel sub = new JLabel("Select items to add to the customer cart");
        sub.setFont(PawMartTheme.FONT_SMALL);
        sub.setForeground(PawMartTheme.TEXT_MUTED);

        topControls.add(title);
        topControls.add(sub);
        topControls.add(Box.createVerticalStrut(10));

        JPanel searchRow = new JPanel(new BorderLayout(8, 0));
        searchRow.setOpaque(false);

        cmbCategoryPos = new JComboBox<>();
        cmbCategoryPos.setPreferredSize(new Dimension(150, 36));
        cmbCategoryPos.addActionListener(e -> filterPosProducts());

        txtSearchPos = new ModernTextField("Search product name or keyword...");
        txtSearchPos.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                filterPosProducts();
            }
        });

        searchRow.add(cmbCategoryPos, BorderLayout.WEST);
        searchRow.add(txtSearchPos, BorderLayout.CENTER);
        topControls.add(searchRow);

        panel.add(topControls, BorderLayout.NORTH);

        // Products Table
        productCatalogModel = new DefaultTableModel(new String[]{"ID", "Product Name", "Category", "Price ($)", "In Stock"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblProductsPos = new ModernTable(productCatalogModel);
        panel.add(ModernTable.createScrollPane(tblProductsPos), BorderLayout.CENTER);

        // Bottom Add to Cart Bar
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        bottomBar.setOpaque(false);

        PrimaryButton btnAddToCart = new PrimaryButton("+ Add Selected to Cart");
        btnAddToCart.addActionListener(e -> addSelectedProductToCart());
        bottomBar.add(btnAddToCart);

        panel.add(bottomBar, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createPosRightCartSection() {
        JPanel panel = new RoundedPanel(16, Color.WHITE, PawMartTheme.CARD_BORDER, true);
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        // Cart Header
        JPanel cartHeader = new JPanel(new BorderLayout());
        cartHeader.setOpaque(false);

        JLabel title = new JLabel("Current Shopping Cart");
        title.setFont(PawMartTheme.FONT_TITLE_MEDIUM);
        title.setForeground(PawMartTheme.TEXT_DARK);

        JLabel sub = new JLabel("Review items, adjust quantities, apply discounts");
        sub.setFont(PawMartTheme.FONT_SMALL);
        sub.setForeground(PawMartTheme.TEXT_MUTED);

        cartHeader.add(title, BorderLayout.NORTH);
        cartHeader.add(sub, BorderLayout.SOUTH);
        panel.add(cartHeader, BorderLayout.NORTH);

        // Cart Table
        cartTableModel = new DefaultTableModel(new String[]{"Product Name", "Unit Price", "Qty", "Total ($)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblCart = new ModernTable(cartTableModel);
        panel.add(ModernTable.createScrollPane(tblCart), BorderLayout.CENTER);

        // Cart Quantity Controls (+ / - / Remove)
        JPanel cartControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        cartControls.setOpaque(false);

        SecondaryButton btnPlus = new SecondaryButton(" + ");
        btnPlus.setPreferredSize(new Dimension(50, 32));
        btnPlus.addActionListener(e -> adjustCartQuantity(1));

        SecondaryButton btnMinus = new SecondaryButton(" - ");
        btnMinus.setPreferredSize(new Dimension(50, 32));
        btnMinus.addActionListener(e -> adjustCartQuantity(-1));

        DangerButton btnRemove = new DangerButton("Remove Item");
        btnRemove.setPreferredSize(new Dimension(110, 32));
        btnRemove.addActionListener(e -> removeSelectedCartItem());

        SecondaryButton btnClear = new SecondaryButton("Clear Cart");
        btnClear.setPreferredSize(new Dimension(90, 32));
        btnClear.addActionListener(e -> clearCart());

        cartControls.add(btnPlus);
        cartControls.add(btnMinus);
        cartControls.add(btnRemove);
        cartControls.add(btnClear);

        // Summary Card at bottom
        JPanel summaryBox = new JPanel();
        summaryBox.setOpaque(false);
        summaryBox.setLayout(new BoxLayout(summaryBox, BoxLayout.Y_AXIS));
        summaryBox.add(cartControls);
        summaryBox.add(Box.createVerticalStrut(10));

        JPanel totalsCard = new RoundedPanel(12, PawMartTheme.BG_CREAM, PawMartTheme.CARD_BORDER, false);
        totalsCard.setLayout(new BoxLayout(totalsCard, BoxLayout.Y_AXIS));
        totalsCard.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        // Subtotal row
        JPanel subRow = new JPanel(new BorderLayout());
        subRow.setOpaque(false);
        subRow.add(new JLabel("Subtotal:"), BorderLayout.WEST);
        lblSubtotalVal = new JLabel("$0.00");
        lblSubtotalVal.setFont(PawMartTheme.FONT_BODY_BOLD);
        subRow.add(lblSubtotalVal, BorderLayout.EAST);
        totalsCard.add(subRow);
        totalsCard.add(Box.createVerticalStrut(6));

        // Discount input row
        JPanel discRow = new JPanel(new BorderLayout(8, 0));
        discRow.setOpaque(false);
        discRow.add(new JLabel("Discount ($):"), BorderLayout.WEST);
        txtDiscount = new ModernTextField("0.00");
        txtDiscount.setPreferredSize(new Dimension(90, 30));
        txtDiscount.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                calculateTotals();
            }
        });
        discRow.add(txtDiscount, BorderLayout.EAST);
        totalsCard.add(discRow);
        totalsCard.add(Box.createVerticalStrut(8));

        // Payment method row
        JPanel payRow = new JPanel(new BorderLayout(8, 0));
        payRow.setOpaque(false);
        payRow.add(new JLabel("Payment:"), BorderLayout.WEST);
        cmbPaymentMethod = new JComboBox<>(new String[]{"CASH", "CREDIT_CARD", "DEBIT_CARD", "MOBILE_PAY"});
        cmbPaymentMethod.setPreferredSize(new Dimension(140, 30));
        cmbPaymentMethod.addActionListener(e -> calculateTotals());
        payRow.add(cmbPaymentMethod, BorderLayout.EAST);
        totalsCard.add(payRow);
        totalsCard.add(Box.createVerticalStrut(8));

        // Cash Tendered & Change Row
        JPanel cashRow = new JPanel(new BorderLayout(8, 0));
        cashRow.setOpaque(false);
        cashRow.add(new JLabel("Tendered ($):"), BorderLayout.WEST);
        txtCashTendered = new ModernTextField("0.00");
        txtCashTendered.setPreferredSize(new Dimension(90, 30));
        txtCashTendered.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                calculateTotals();
            }
        });
        cashRow.add(txtCashTendered, BorderLayout.EAST);
        totalsCard.add(cashRow);
        totalsCard.add(Box.createVerticalStrut(6));

        JPanel changeRow = new JPanel(new BorderLayout());
        changeRow.setOpaque(false);
        changeRow.add(new JLabel("Change Due:"), BorderLayout.WEST);
        lblChangeVal = new JLabel("$0.00");
        lblChangeVal.setFont(PawMartTheme.FONT_BODY_BOLD);
        lblChangeVal.setForeground(PawMartTheme.STATUS_ACTIVE);
        changeRow.add(lblChangeVal, BorderLayout.EAST);
        totalsCard.add(changeRow);
        totalsCard.add(Box.createVerticalStrut(10));

        // Grand Total row
        JPanel grandRow = new JPanel(new BorderLayout());
        grandRow.setOpaque(false);
        JLabel lblG = new JLabel("GRAND TOTAL:");
        lblG.setFont(PawMartTheme.FONT_TITLE_MEDIUM);
        lblG.setForeground(PawMartTheme.TEAL_PRIMARY);
        grandRow.add(lblG, BorderLayout.WEST);

        lblGrandTotalVal = new JLabel("$0.00");
        lblGrandTotalVal.setFont(PawMartTheme.FONT_TITLE_LARGE);
        lblGrandTotalVal.setForeground(PawMartTheme.ORANGE_ACCENT);
        grandRow.add(lblGrandTotalVal, BorderLayout.EAST);
        totalsCard.add(grandRow);

        summaryBox.add(totalsCard);
        summaryBox.add(Box.createVerticalStrut(12));

        // Complete Checkout Button
        PrimaryButton btnCheckout = new PrimaryButton("Complete Checkout & Print Receipt");
        btnCheckout.setPreferredSize(new Dimension(Integer.MAX_VALUE, 44));
        btnCheckout.addActionListener(e -> performCheckout());
        summaryBox.add(btnCheckout);

        panel.add(summaryBox, BorderLayout.SOUTH);

        return panel;
    }

    private void loadPosProducts() {
        try {
            List<Category> cats = categoryService.getAllCategories();
            cmbCategoryPos.removeAllItems();
            cmbCategoryPos.addItem(new CategoryItem(0, "All Categories"));
            for (Category c : cats) {
                cmbCategoryPos.addItem(new CategoryItem(c.getId(), c.getName()));
            }
        } catch (Exception ignored) {}

        filterPosProducts();
    }

    private void filterPosProducts() {
        CategoryItem sel = (CategoryItem) cmbCategoryPos.getSelectedItem();
        Integer catId = sel != null && sel.id > 0 ? sel.id : null;
        String query = txtSearchPos != null ? txtSearchPos.getText().trim() : "";

        SwingWorker<List<Product>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Product> doInBackground() throws Exception {
                return productService.searchProducts(query, catId, true);
            }

            @Override
            protected void done() {
                try {
                    List<Product> list = get();
                    productCatalogModel.setRowCount(0);
                    for (Product p : list) {
                        productCatalogModel.addRow(new Object[]{
                                p.getId(),
                                p.getName(),
                                p.getCategoryName(),
                                "$" + p.getPrice(),
                                p.getQuantity() > 0 ? String.valueOf(p.getQuantity()) : "OUT OF STOCK"
                        });
                    }
                } catch (Exception ex) {
                    System.err.println("POS filter products error: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void addSelectedProductToCart() {
        int row = tblProductsPos.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a product from the catalog table.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int productId = (int) productCatalogModel.getValueAt(row, 0);

        try {
            Product p = productService.getProductById(productId);
            if (p == null || !p.isActive()) {
                JOptionPane.showMessageDialog(this, "Product is currently unavailable.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (p.getQuantity() <= 0) {
                JOptionPane.showMessageDialog(this, "Product '" + p.getName() + "' is out of stock!", "Out of Stock", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Check if already in cart
            CartItem existing = null;
            for (CartItem ci : cartItems) {
                if (ci.getProduct().getId() == p.getId()) {
                    existing = ci;
                    break;
                }
            }

            if (existing != null) {
                if (existing.getQuantity() + 1 > p.getQuantity()) {
                    JOptionPane.showMessageDialog(this, "Cannot add more. Reached available inventory (" + p.getQuantity() + ").", "Stock Limit", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                existing.setQuantity(existing.getQuantity() + 1);
            } else {
                cartItems.add(new CartItem(p, 1));
            }

            refreshCartTable();
            calculateTotals();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding to cart: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void adjustCartQuantity(int delta) {
        int row = tblCart.getSelectedRow();
        if (row < 0 || row >= cartItems.size()) {
            return;
        }

        CartItem item = cartItems.get(row);
        int newQty = item.getQuantity() + delta;

        if (newQty <= 0) {
            cartItems.remove(row);
        } else if (newQty > item.getProduct().getQuantity()) {
            JOptionPane.showMessageDialog(this, "Requested quantity exceeds available stock (" + item.getProduct().getQuantity() + ").", "Stock Limit", JOptionPane.WARNING_MESSAGE);
            return;
        } else {
            item.setQuantity(newQty);
        }

        refreshCartTable();
        calculateTotals();
    }

    private void removeSelectedCartItem() {
        int row = tblCart.getSelectedRow();
        if (row >= 0 && row < cartItems.size()) {
            cartItems.remove(row);
            refreshCartTable();
            calculateTotals();
        }
    }

    private void clearCart() {
        if (!cartItems.isEmpty()) {
            cartItems.clear();
            refreshCartTable();
            calculateTotals();
        }
    }

    private void refreshCartTable() {
        cartTableModel.setRowCount(0);
        for (CartItem ci : cartItems) {
            cartTableModel.addRow(new Object[]{
                    ci.getProduct().getName(),
                    "$" + ci.getUnitPrice(),
                    ci.getQuantity(),
                    "$" + ci.getLineTotal()
            });
        }
    }

    private void calculateTotals() {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem ci : cartItems) {
            subtotal = subtotal.add(ci.getLineTotal());
        }
        lblSubtotalVal.setText("$" + subtotal.setScale(2, RoundingMode.HALF_UP));

        BigDecimal discount = BigDecimal.ZERO;
        try {
            String dStr = txtDiscount.getText().trim();
            if (!dStr.isEmpty()) {
                discount = new BigDecimal(dStr);
                if (discount.compareTo(BigDecimal.ZERO) < 0) discount = BigDecimal.ZERO;
                if (discount.compareTo(subtotal) > 0) discount = subtotal;
            }
        } catch (Exception ignored) {}

        BigDecimal grandTotal = subtotal.subtract(discount).setScale(2, RoundingMode.HALF_UP);
        lblGrandTotalVal.setText("$" + grandTotal);

        // Change calculation
        try {
            String tenderedStr = txtCashTendered.getText().trim();
            if (!tenderedStr.isEmpty()) {
                BigDecimal tendered = new BigDecimal(tenderedStr);
                BigDecimal change = tendered.subtract(grandTotal);
                if (change.compareTo(BigDecimal.ZERO) >= 0) {
                    lblChangeVal.setText("$" + change.setScale(2, RoundingMode.HALF_UP));
                    lblChangeVal.setForeground(PawMartTheme.STATUS_ACTIVE);
                } else {
                    lblChangeVal.setText("-$" + change.abs().setScale(2, RoundingMode.HALF_UP) + " (Short)");
                    lblChangeVal.setForeground(PawMartTheme.STATUS_DANGER);
                }
            } else {
                lblChangeVal.setText("$0.00");
            }
        } catch (Exception ignored) {
            lblChangeVal.setText("$0.00");
        }
    }

    private void performCheckout() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "The cart is empty. Please add items before checkout.", "Empty Cart", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigDecimal discount = BigDecimal.ZERO;
        try {
            String dStr = txtDiscount.getText().trim();
            if (!dStr.isEmpty()) {
                discount = new BigDecimal(dStr);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid discount amount.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String paymentMethod = (String) cmbPaymentMethod.getSelectedItem();

        try {
            Sale sale = checkoutService.processCheckout(currentCashier, cartItems, discount, paymentMethod);
            clearCart();
            filterPosProducts(); // Refresh live stock numbers

            // Show Receipt Dialog with instant print & PDF export
            new ReceiptPreviewDialog(this, sale).setVisible(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Checkout failed: " + ex.getMessage(), "Checkout Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================================
    // 2. PRODUCT CATALOG VIEW
    // =========================================================================
    private JPanel createCatalogViewPanel() {
        JPanel panel = new JPanel(new BorderLayout(14, 14));
        panel.setOpaque(false);

        JLabel title = new JLabel("Available Pet Supplies Catalog");
        title.setFont(PawMartTheme.FONT_TITLE_LARGE);
        title.setForeground(PawMartTheme.TEXT_DARK);
        panel.add(title, BorderLayout.NORTH);

        DefaultTableModel catModel = new DefaultTableModel(new String[]{"ID", "Product Name", "Category", "Price ($)", "Stock Qty", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        ModernTable tblCat = new ModernTable(catModel);

        JPanel card = new RoundedPanel(14, Color.WHITE, PawMartTheme.CARD_BORDER, true);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        card.add(ModernTable.createScrollPane(tblCat), BorderLayout.CENTER);

        panel.add(card, BorderLayout.CENTER);
        return panel;
    }

    private void loadCatalog() {
        // Reload active products
    }

    // =========================================================================
    // 3. CASHIER SALES HISTORY VIEW
    // =========================================================================
    private JPanel createSalesHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(14, 14));
        panel.setOpaque(false);

        JLabel title = new JLabel("My Transaction History");
        title.setFont(PawMartTheme.FONT_TITLE_LARGE);
        title.setForeground(PawMartTheme.TEXT_DARK);
        panel.add(title, BorderLayout.NORTH);

        historyTableModel = new DefaultTableModel(new String[]{"ID", "Receipt #", "Subtotal", "Discount", "Grand Total", "Payment", "Date/Time"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblHistory = new ModernTable(historyTableModel);

        JPanel card = new RoundedPanel(14, Color.WHITE, PawMartTheme.CARD_BORDER, true);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        card.add(ModernTable.createScrollPane(tblHistory), BorderLayout.CENTER);

        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        bottomBar.setOpaque(false);

        PrimaryButton btnReprint = new PrimaryButton("Reprint / View Receipt");
        btnReprint.addActionListener(e -> reprintSelectedReceipt());
        bottomBar.add(btnReprint);

        card.add(bottomBar, BorderLayout.SOUTH);
        panel.add(card, BorderLayout.CENTER);

        return panel;
    }

    private void loadSalesHistory() {
        SwingWorker<List<Sale>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Sale> doInBackground() throws Exception {
                return reportService.getCashierSales(currentCashier.getId());
            }

            @Override
            protected void done() {
                try {
                    List<Sale> list = get();
                    historyTableModel.setRowCount(0);
                    for (Sale s : list) {
                        historyTableModel.addRow(new Object[]{
                                s.getId(),
                                s.getReceiptNumber(),
                                "$" + s.getSubtotal(),
                                "-$" + s.getDiscountAmount(),
                                "$" + s.getGrandTotal(),
                                s.getPaymentMethod(),
                                s.getCreatedAt() != null ? DATE_FMT.format(s.getCreatedAt()) : ""
                        });
                    }
                } catch (Exception ex) {
                    System.err.println("Load cashier history error: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void reprintSelectedReceipt() {
        int row = tblHistory.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a sales record from your history.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int saleId = (int) historyTableModel.getValueAt(row, 0);
        try {
            Sale s = reportService.getSaleDetails(saleId);
            if (s != null) {
                new ReceiptPreviewDialog(this, s).setVisible(true);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error viewing receipt: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to end your cashier session?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            AuthService.getInstance().logout();
            dispose();
            new LoginFrame().setVisible(true);
        }
    }

    private JLabel loadScaledImage(String resourcePath, int targetW, int targetH) {
        try {
            URL url = getClass().getResource(resourcePath);
            if (url != null) {
                ImageIcon original = new ImageIcon(url);
                Image img = original.getImage().getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                return new JLabel(new ImageIcon(img));
            }
        } catch (Exception ignored) {}
        return null;
    }

    private static class CategoryItem {
        final int id;
        final String name;

        CategoryItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
