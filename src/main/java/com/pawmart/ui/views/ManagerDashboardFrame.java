package com.pawmart.ui.views;

import com.pawmart.database.DatabaseConfig;
import com.pawmart.database.DatabaseConnection;
import com.pawmart.model.Category;
import com.pawmart.model.DashboardStats;
import com.pawmart.model.Product;
import com.pawmart.model.Sale;
import com.pawmart.model.User;
import com.pawmart.service.AuthService;
import com.pawmart.service.CategoryService;
import com.pawmart.service.ProductService;
import com.pawmart.service.ReportService;
import com.pawmart.service.UserService;
import com.pawmart.ui.components.DangerButton;
import com.pawmart.ui.components.ModernPasswordField;
import com.pawmart.ui.components.ModernTable;
import com.pawmart.ui.components.ModernTextField;
import com.pawmart.ui.components.PawIconRenderer;
import com.pawmart.ui.components.PrimaryButton;
import com.pawmart.ui.components.RoundedPanel;
import com.pawmart.ui.components.SecondaryButton;
import com.pawmart.ui.components.StatCard;
import com.pawmart.ui.theme.PawMartTheme;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
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
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Premium Store Manager Dashboard for PawMart Pet Shop Management System.
 */
public class ManagerDashboardFrame extends JFrame {

    private final User currentManager;
    private final UserService userService = new UserService();
    private final ProductService productService = new ProductService();
    private final CategoryService categoryService = new CategoryService();
    private final ReportService reportService = new ReportService();

    private JPanel contentCards;
    private CardLayout cardLayout;
    private final List<JButton> navButtons = new ArrayList<>();

    // Dashboard Cards & Charts
    private StatCard cardTotalProducts;
    private StatCard cardTotalCashiers;
    private StatCard cardTotalSales;
    private StatCard cardTotalRevenue;
    private DefaultTableModel recentSalesModel;
    private JPanel chartContainer;
    private JPanel lowStockContainer;

    // Cashiers Table
    private ModernTable tblCashiers;
    private DefaultTableModel cashiersModel;
    private ModernTextField txtSearchCashiers;

    // Products Table
    private ModernTable tblProducts;
    private DefaultTableModel productsModel;
    private ModernTextField txtSearchProducts;
    private JComboBox<CategoryItem> cmbCategoryFilter;

    // Categories Table
    private ModernTable tblCategories;
    private DefaultTableModel categoriesModel;

    // Sales Table
    private ModernTable tblSales;
    private DefaultTableModel salesModel;
    private ModernTextField txtSearchSales;

    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public ManagerDashboardFrame(User manager) {
        this.currentManager = manager;
        setTitle("PawMart - Store Manager Dashboard (" + manager.getFullName() + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1240, 780);
        setMinimumSize(new Dimension(1080, 680));
        setLocationRelativeTo(null);
        getContentPane().setBackground(PawMartTheme.BG_CREAM);
        setLayout(new BorderLayout());

        initUi();
        loadDashboardStats();
    }

    private void initUi() {
        // Left Deep Teal Navigation Sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // Center Content Area (CardLayout)
        cardLayout = new CardLayout();
        contentCards = new JPanel(cardLayout);
        contentCards.setBackground(PawMartTheme.BG_CREAM);
        contentCards.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        contentCards.add(createDashboardPanel(), "DASHBOARD");
        contentCards.add(createCashiersPanel(), "CASHIERS");
        contentCards.add(createProductsPanel(), "PRODUCTS");
        contentCards.add(createCategoriesPanel(), "CATEGORIES");
        contentCards.add(createInventoryPanel(), "INVENTORY");
        contentCards.add(createSalesPanel(), "SALES");
        contentCards.add(createReportsPanel(), "REPORTS");
        contentCards.add(createSettingsPanel(), "SETTINGS");

        add(contentCards, BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(PawMartTheme.TEAL_PRIMARY);
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 14, 20, 14));

        // Top Brand Header
        JPanel topBox = new JPanel();
        topBox.setOpaque(false);
        topBox.setLayout(new BoxLayout(topBox, BoxLayout.Y_AXIS));

        JPanel brandRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brandRow.setOpaque(false);

        JLabel logoLabel = loadScaledImage("/images/logo.png", 38, 38);
        if (logoLabel == null) {
            logoLabel = new JLabel(PawIconRenderer.createPawIcon(32, Color.WHITE));
        }

        JPanel brandText = new JPanel();
        brandText.setOpaque(false);
        brandText.setLayout(new BoxLayout(brandText, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("PawMart");
        title.setFont(PawMartTheme.FONT_TITLE_MEDIUM);
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("MANAGER SUITE");
        subtitle.setFont(PawMartTheme.FONT_SMALL_BOLD);
        subtitle.setForeground(PawMartTheme.AMBER_ACCENT);

        brandText.add(title);
        brandText.add(subtitle);

        brandRow.add(logoLabel);
        brandRow.add(brandText);
        topBox.add(brandRow);
        topBox.add(Box.createVerticalStrut(18));

        // Manager Profile Card
        JPanel profileCard = new RoundedPanel(12, PawMartTheme.TEAL_DARK, null, false);
        profileCard.setLayout(new BorderLayout(8, 0));
        profileCard.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        profileCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        JLabel avatar = new JLabel(PawIconRenderer.createPawIcon(22, PawMartTheme.EMERALD_PRIMARY));
        JPanel profInfo = new JPanel();
        profInfo.setOpaque(false);
        profInfo.setLayout(new BoxLayout(profInfo, BoxLayout.Y_AXIS));

        JLabel profName = new JLabel(currentManager.getFullName());
        profName.setFont(PawMartTheme.FONT_BODY_BOLD);
        profName.setForeground(Color.WHITE);

        JLabel profRole = new JLabel("Store Manager");
        profRole.setFont(PawMartTheme.FONT_SMALL);
        profRole.setForeground(PawMartTheme.TEXT_LIGHT);

        profInfo.add(profName);
        profInfo.add(profRole);

        profileCard.add(avatar, BorderLayout.WEST);
        profileCard.add(profInfo, BorderLayout.CENTER);
        topBox.add(profileCard);
        topBox.add(Box.createVerticalStrut(20));

        sidebar.add(topBox, BorderLayout.NORTH);

        // Center Navigation Links
        JPanel navMenu = new JPanel();
        navMenu.setOpaque(false);
        navMenu.setLayout(new BoxLayout(navMenu, BoxLayout.Y_AXIS));

        addNavItem(navMenu, "Dashboard", "DASHBOARD", true);
        addNavItem(navMenu, "Cashiers", "CASHIERS", false);
        addNavItem(navMenu, "Products", "PRODUCTS", false);
        addNavItem(navMenu, "Categories", "CATEGORIES", false);
        addNavItem(navMenu, "Inventory Alerts", "INVENTORY", false);
        addNavItem(navMenu, "Sales Ledger", "SALES", false);
        addNavItem(navMenu, "Reports & Charts", "REPORTS", false);
        addNavItem(navMenu, "Database Settings", "SETTINGS", false);

        sidebar.add(navMenu, BorderLayout.CENTER);

        // Bottom Logout Button
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

            // Lazy refresh corresponding data
            if ("DASHBOARD".equals(cardKey)) loadDashboardStats();
            else if ("CASHIERS".equals(cardKey)) loadCashiers();
            else if ("PRODUCTS".equals(cardKey)) loadProducts();
            else if ("CATEGORIES".equals(cardKey)) loadCategories();
            else if ("INVENTORY".equals(cardKey)) loadInventory();
            else if ("SALES".equals(cardKey)) loadSales();
            else if ("REPORTS".equals(cardKey)) loadReports();
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
    // 1. DASHBOARD VIEW
    // =========================================================================
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(14, 14));
        panel.setOpaque(false);

        // Header Title
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Store Performance Overview");
        title.setFont(PawMartTheme.FONT_TITLE_LARGE);
        title.setForeground(PawMartTheme.TEXT_DARK);

        JLabel subtitle = new JLabel("Live database statistics, inventory health & recent sales");
        subtitle.setFont(PawMartTheme.FONT_BODY);
        subtitle.setForeground(PawMartTheme.TEXT_MUTED);

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        panel.add(header, BorderLayout.NORTH);

        // Center Content: Stats + Charts + Recent Sales
        JPanel center = new JPanel(new BorderLayout(14, 14));
        center.setOpaque(false);

        // 4 KPI Stat Cards
        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 14, 14));
        statsGrid.setOpaque(false);

        cardTotalProducts = new StatCard("TOTAL PRODUCTS", "...", "Active in catalog", "cart",
                new Color(230, 240, 242), PawMartTheme.TEAL_PRIMARY);
        cardTotalCashiers = new StatCard("ACTIVE CASHIERS", "...", "Authorized staff", "paw",
                new Color(209, 250, 229), PawMartTheme.EMERALD_PRIMARY);
        cardTotalSales = new StatCard("TOTAL ORDERS", "...", "Completed sales", "bone",
                new Color(254, 243, 199), PawMartTheme.AMBER_ACCENT);
        cardTotalRevenue = new StatCard("TOTAL REVENUE", "$0.00", "Gross earnings", "paw",
                new Color(254, 242, 232), PawMartTheme.ORANGE_ACCENT);

        statsGrid.add(cardTotalProducts);
        statsGrid.add(cardTotalCashiers);
        statsGrid.add(cardTotalSales);
        statsGrid.add(cardTotalRevenue);
        center.add(statsGrid, BorderLayout.NORTH);

        // Lower Grid: Left Chart + Right Recent Sales & Low Stock
        JPanel lowerGrid = new JPanel(new GridLayout(1, 2, 14, 14));
        lowerGrid.setOpaque(false);

        // Chart Card
        JPanel chartCard = new RoundedPanel(14, Color.WHITE, PawMartTheme.CARD_BORDER, true);
        chartCard.setLayout(new BorderLayout());
        chartCard.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        JLabel chartTitle = new JLabel("Monthly Revenue Trend");
        chartTitle.setFont(PawMartTheme.FONT_SUBTITLE);
        chartTitle.setForeground(PawMartTheme.TEXT_DARK);
        chartCard.add(chartTitle, BorderLayout.NORTH);

        chartContainer = new JPanel(new BorderLayout());
        chartContainer.setOpaque(false);
        chartCard.add(chartContainer, BorderLayout.CENTER);
        lowerGrid.add(chartCard);

        // Right Recent Sales Card
        JPanel rightCard = new RoundedPanel(14, Color.WHITE, PawMartTheme.CARD_BORDER, true);
        rightCard.setLayout(new BorderLayout(8, 8));
        rightCard.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        JLabel recentTitle = new JLabel("Recent Store Transactions");
        recentTitle.setFont(PawMartTheme.FONT_SUBTITLE);
        recentTitle.setForeground(PawMartTheme.TEXT_DARK);
        rightCard.add(recentTitle, BorderLayout.NORTH);

        recentSalesModel = new DefaultTableModel(new String[]{"Receipt #", "Cashier", "Amount", "Payment", "Date"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        ModernTable tblRecent = new ModernTable(recentSalesModel);
        rightCard.add(ModernTable.createScrollPane(tblRecent), BorderLayout.CENTER);

        lowerGrid.add(rightCard);

        center.add(lowerGrid, BorderLayout.CENTER);
        panel.add(center, BorderLayout.CENTER);

        return panel;
    }

    private void loadDashboardStats() {
        SwingWorker<DashboardStats, Void> worker = new SwingWorker<>() {
            @Override
            protected DashboardStats doInBackground() throws Exception {
                return reportService.getDashboardStats();
            }

            @Override
            protected void done() {
                try {
                    DashboardStats stats = get();
                    cardTotalProducts.setValue(String.valueOf(stats.getTotalProducts()));
                    cardTotalCashiers.setValue(String.valueOf(stats.getTotalCashiers()));
                    cardTotalSales.setValue(String.valueOf(stats.getTotalSalesCount()));
                    cardTotalRevenue.setValue("$" + stats.getTotalRevenue());

                    // Populate Recent Sales
                    recentSalesModel.setRowCount(0);
                    if (stats.getRecentSales() != null) {
                        for (Sale s : stats.getRecentSales()) {
                            recentSalesModel.addRow(new Object[]{
                                    s.getReceiptNumber(),
                                    s.getCashierName(),
                                    "$" + s.getGrandTotal(),
                                    s.getPaymentMethod(),
                                    s.getCreatedAt() != null ? DATE_FMT.format(s.getCreatedAt()) : ""
                            });
                        }
                    }

                    // Render JFreeChart
                    renderMonthlyChart(stats.getMonthlyRevenue());

                } catch (Exception ex) {
                    System.err.println("[ManagerDashboard] Load stats failed: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void renderMonthlyChart(Map<String, BigDecimal> monthlyData) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (monthlyData != null && !monthlyData.isEmpty()) {
            for (Map.Entry<String, BigDecimal> entry : monthlyData.entrySet()) {
                dataset.addValue(entry.getValue().doubleValue(), "Revenue ($)", entry.getKey());
            }
        } else {
            dataset.addValue(120.0, "Revenue ($)", "Oct 2026");
        }

        JFreeChart chart = ChartFactory.createBarChart(
                null,
                "Month",
                "Revenue ($)",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(250, 252, 255));
        plot.setOutlinePaint(null);
        plot.setRangeGridlinePaint(new Color(226, 232, 240));

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, PawMartTheme.EMERALD_PRIMARY);
        renderer.setDrawBarOutline(false);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setOpaque(false);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        chartContainer.removeAll();
        chartContainer.add(chartPanel, BorderLayout.CENTER);
        chartContainer.revalidate();
        chartContainer.repaint();
    }

    // =========================================================================
    // 2. CASHIERS MANAGEMENT VIEW
    // =========================================================================
    private JPanel createCashiersPanel() {
        JPanel panel = new JPanel(new BorderLayout(14, 14));
        panel.setOpaque(false);

        // Header + Search + Action
        JPanel topBar = new JPanel(new BorderLayout(10, 0));
        topBar.setOpaque(false);

        JPanel titleBox = new JPanel(new GridLayout(2, 1));
        titleBox.setOpaque(false);
        JLabel title = new JLabel("Cashier Accounts Management");
        title.setFont(PawMartTheme.FONT_TITLE_LARGE);
        title.setForeground(PawMartTheme.TEXT_DARK);
        JLabel sub = new JLabel("Manage authorized POS staff, credentials, and account statuses");
        sub.setFont(PawMartTheme.FONT_BODY);
        sub.setForeground(PawMartTheme.TEXT_MUTED);
        titleBox.add(title);
        titleBox.add(sub);
        topBar.add(titleBox, BorderLayout.WEST);

        // Search & Add Button Row
        JPanel actionBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionBox.setOpaque(false);

        txtSearchCashiers = new ModernTextField("Search cashiers by name, user, email...");
        txtSearchCashiers.setPreferredSize(new Dimension(280, 38));
        txtSearchCashiers.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                searchCashiers();
            }
        });

        PrimaryButton btnAddCashier = new PrimaryButton("+ Add Cashier");
        btnAddCashier.setPreferredSize(new Dimension(140, 38));
        btnAddCashier.addActionListener(e -> showAddCashierDialog());

        actionBox.add(txtSearchCashiers);
        actionBox.add(btnAddCashier);
        topBar.add(actionBox, BorderLayout.EAST);

        panel.add(topBar, BorderLayout.NORTH);

        // Cashiers JTable
        cashiersModel = new DefaultTableModel(new String[]{"ID", "Full Name", "Username", "Email", "Status", "Registered On"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblCashiers = new ModernTable(cashiersModel);

        JPanel tableCard = new RoundedPanel(14, Color.WHITE, PawMartTheme.CARD_BORDER, true);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        tableCard.add(ModernTable.createScrollPane(tblCashiers), BorderLayout.CENTER);

        // Bottom Controls for selected cashier
        JPanel bottomControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        bottomControls.setOpaque(false);

        SecondaryButton btnEdit = new SecondaryButton("Edit Details");
        btnEdit.addActionListener(e -> editSelectedCashier());

        SecondaryButton btnToggleStatus = new SecondaryButton("Toggle Status");
        btnToggleStatus.addActionListener(e -> toggleCashierStatus());

        SecondaryButton btnResetPass = new SecondaryButton("Reset Password");
        btnResetPass.addActionListener(e -> resetCashierPassword());

        bottomControls.add(btnResetPass);
        bottomControls.add(btnToggleStatus);
        bottomControls.add(btnEdit);
        tableCard.add(bottomControls, BorderLayout.SOUTH);

        panel.add(tableCard, BorderLayout.CENTER);

        return panel;
    }

    private void loadCashiers() {
        SwingWorker<List<User>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<User> doInBackground() throws Exception {
                return userService.getAllCashiers();
            }

            @Override
            protected void done() {
                try {
                    List<User> list = get();
                    populateCashiersTable(list);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ManagerDashboardFrame.this, "Error loading cashiers: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void searchCashiers() {
        String q = txtSearchCashiers.getText().trim();
        try {
            List<User> results = userService.searchCashiers(q);
            populateCashiersTable(results);
        } catch (Exception ex) {
            System.err.println("Search cashiers error: " + ex.getMessage());
        }
    }

    private void populateCashiersTable(List<User> list) {
        cashiersModel.setRowCount(0);
        if (list != null) {
            for (User u : list) {
                cashiersModel.addRow(new Object[]{
                        u.getId(),
                        u.getFullName(),
                        u.getUsername(),
                        u.getEmail(),
                        u.getStatus(),
                        u.getCreatedAt() != null ? DATE_FMT.format(u.getCreatedAt()) : ""
                });
            }
        }
    }

    private void showAddCashierDialog() {
        JDialog dlg = new JDialog(this, "Add New Cashier", true);
        dlg.setSize(440, 480);
        dlg.setLocationRelativeTo(this);
        dlg.getContentPane().setBackground(PawMartTheme.BG_CREAM);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        p.setOpaque(false);

        ModernTextField txtName = addField(p, "Full Name", "e.g. John Doe");
        ModernTextField txtUser = addField(p, "Username", "e.g. jdoe");
        ModernTextField txtEmail = addField(p, "Email", "e.g. jdoe@pawmart.com");

        p.add(new JLabel("Password (min 6 chars)"));
        ModernPasswordField txtPass = new ModernPasswordField("Enter password");
        p.add(txtPass);
        p.add(Box.createVerticalStrut(10));

        p.add(new JLabel("Confirm Password"));
        ModernPasswordField txtConf = new ModernPasswordField("Re-enter password");
        p.add(txtConf);
        p.add(Box.createVerticalStrut(20));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btns.setOpaque(false);
        SecondaryButton btnCancel = new SecondaryButton("Cancel");
        btnCancel.addActionListener(e -> dlg.dispose());

        PrimaryButton btnSave = new PrimaryButton("Save Cashier");
        btnSave.addActionListener(e -> {
            try {
                userService.createCashier(
                        txtName.getText(),
                        txtUser.getText(),
                        txtEmail.getText(),
                        new String(txtPass.getPassword()),
                        new String(txtConf.getPassword())
                );
                JOptionPane.showMessageDialog(dlg, "Cashier account created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
                loadCashiers();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        btns.add(btnCancel);
        btns.add(btnSave);
        p.add(btns);

        dlg.add(p);
        dlg.setVisible(true);
    }

    private void editSelectedCashier() {
        int row = tblCashiers.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a cashier from the table.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int id = (int) cashiersModel.getValueAt(row, 0);
        String name = (String) cashiersModel.getValueAt(row, 1);
        String username = (String) cashiersModel.getValueAt(row, 2);
        String email = (String) cashiersModel.getValueAt(row, 3);
        String status = (String) cashiersModel.getValueAt(row, 4);

        JDialog dlg = new JDialog(this, "Edit Cashier: " + username, true);
        dlg.setSize(440, 400);
        dlg.setLocationRelativeTo(this);
        dlg.getContentPane().setBackground(PawMartTheme.BG_CREAM);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        p.setOpaque(false);

        ModernTextField txtName = addField(p, "Full Name", "");
        txtName.setText(name);

        ModernTextField txtUser = addField(p, "Username", "");
        txtUser.setText(username);

        ModernTextField txtEmail = addField(p, "Email", "");
        txtEmail.setText(email);

        p.add(new JLabel("Account Status"));
        JComboBox<String> cmbStatus = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE"});
        cmbStatus.setSelectedItem(status);
        p.add(cmbStatus);
        p.add(Box.createVerticalStrut(22));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btns.setOpaque(false);
        SecondaryButton btnCancel = new SecondaryButton("Cancel");
        btnCancel.addActionListener(e -> dlg.dispose());

        PrimaryButton btnSave = new PrimaryButton("Update");
        btnSave.addActionListener(e -> {
            try {
                userService.updateCashier(id, txtName.getText(), txtUser.getText(), txtEmail.getText(), (String) cmbStatus.getSelectedItem());
                JOptionPane.showMessageDialog(dlg, "Cashier details updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
                loadCashiers();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, ex.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        btns.add(btnCancel);
        btns.add(btnSave);
        p.add(btns);

        dlg.add(p);
        dlg.setVisible(true);
    }

    private void toggleCashierStatus() {
        int row = tblCashiers.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a cashier from the table.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int id = (int) cashiersModel.getValueAt(row, 0);
        String currentStatus = (String) cashiersModel.getValueAt(row, 4);
        boolean makeActive = "INACTIVE".equalsIgnoreCase(currentStatus);

        try {
            if (makeActive) {
                userService.activateCashier(id);
                JOptionPane.showMessageDialog(this, "Cashier activated.", "Status Changed", JOptionPane.INFORMATION_MESSAGE);
            } else {
                userService.deactivateCashier(id);
                JOptionPane.showMessageDialog(this, "Cashier deactivated. Sales records remain preserved.", "Status Changed", JOptionPane.INFORMATION_MESSAGE);
            }
            loadCashiers();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error toggling status: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetCashierPassword() {
        int row = tblCashiers.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a cashier from the table.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int id = (int) cashiersModel.getValueAt(row, 0);
        String username = (String) cashiersModel.getValueAt(row, 2);

        JDialog dlg = new JDialog(this, "Reset Password for " + username, true);
        dlg.setSize(380, 260);
        dlg.setLocationRelativeTo(this);
        dlg.getContentPane().setBackground(PawMartTheme.BG_CREAM);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        p.setOpaque(false);

        p.add(new JLabel("New Password (min 6 characters)"));
        ModernPasswordField txtNew = new ModernPasswordField("Enter new password");
        p.add(txtNew);
        p.add(Box.createVerticalStrut(10));

        p.add(new JLabel("Confirm Password"));
        ModernPasswordField txtConf = new ModernPasswordField("Confirm new password");
        p.add(txtConf);
        p.add(Box.createVerticalStrut(18));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btns.setOpaque(false);

        SecondaryButton btnCancel = new SecondaryButton("Cancel");
        btnCancel.addActionListener(e -> dlg.dispose());

        PrimaryButton btnSave = new PrimaryButton("Save Password");
        btnSave.addActionListener(e -> {
            try {
                userService.resetPassword(id, new String(txtNew.getPassword()), new String(txtConf.getPassword()));
                JOptionPane.showMessageDialog(dlg, "Password reset successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, ex.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        btns.add(btnCancel);
        btns.add(btnSave);
        p.add(btns);

        dlg.add(p);
        dlg.setVisible(true);
    }

    // =========================================================================
    // 3. PRODUCTS MANAGEMENT VIEW
    // =========================================================================
    private JPanel createProductsPanel() {
        JPanel panel = new JPanel(new BorderLayout(14, 14));
        panel.setOpaque(false);

        // Header + Filter Bar
        JPanel topBar = new JPanel(new BorderLayout(10, 0));
        topBar.setOpaque(false);

        JPanel titleBox = new JPanel(new GridLayout(2, 1));
        titleBox.setOpaque(false);
        JLabel title = new JLabel("Pet Product & Inventory Catalog");
        title.setFont(PawMartTheme.FONT_TITLE_LARGE);
        title.setForeground(PawMartTheme.TEXT_DARK);
        JLabel sub = new JLabel("Create, update, pricing, stock levels & threshold monitoring");
        sub.setFont(PawMartTheme.FONT_BODY);
        sub.setForeground(PawMartTheme.TEXT_MUTED);
        titleBox.add(title);
        titleBox.add(sub);
        topBar.add(titleBox, BorderLayout.WEST);

        // Filter / Search / Add Controls
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controls.setOpaque(false);

        cmbCategoryFilter = new JComboBox<>();
        cmbCategoryFilter.setPreferredSize(new Dimension(160, 38));
        cmbCategoryFilter.addActionListener(e -> filterProducts());

        txtSearchProducts = new ModernTextField("Search products...");
        txtSearchProducts.setPreferredSize(new Dimension(200, 38));
        txtSearchProducts.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                filterProducts();
            }
        });

        PrimaryButton btnAddProduct = new PrimaryButton("+ Add Product");
        btnAddProduct.setPreferredSize(new Dimension(140, 38));
        btnAddProduct.addActionListener(e -> showAddProductDialog());

        controls.add(cmbCategoryFilter);
        controls.add(txtSearchProducts);
        controls.add(btnAddProduct);
        topBar.add(controls, BorderLayout.EAST);

        panel.add(topBar, BorderLayout.NORTH);

        // Products Table
        productsModel = new DefaultTableModel(new String[]{"ID", "Product Name", "Category", "Price ($)", "Stock Qty", "Low Threshold", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblProducts = new ModernTable(productsModel);

        JPanel tableCard = new RoundedPanel(14, Color.WHITE, PawMartTheme.CARD_BORDER, true);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        tableCard.add(ModernTable.createScrollPane(tblProducts), BorderLayout.CENTER);

        // Bottom Product Controls
        JPanel bottomControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        bottomControls.setOpaque(false);

        SecondaryButton btnEdit = new SecondaryButton("Edit Product");
        btnEdit.addActionListener(e -> editSelectedProduct());

        DangerButton btnDelete = new DangerButton("Delete / Deactivate");
        btnDelete.addActionListener(e -> deleteSelectedProduct());

        bottomControls.add(btnEdit);
        bottomControls.add(btnDelete);
        tableCard.add(bottomControls, BorderLayout.SOUTH);

        panel.add(tableCard, BorderLayout.CENTER);

        return panel;
    }

    private void loadProducts() {
        // Load categories into dropdown
        try {
            List<Category> cats = categoryService.getAllCategories();
            cmbCategoryFilter.removeAllItems();
            cmbCategoryFilter.addItem(new CategoryItem(0, "All Categories"));
            for (Category c : cats) {
                cmbCategoryFilter.addItem(new CategoryItem(c.getId(), c.getName()));
            }
        } catch (Exception ignored) {}

        filterProducts();
    }

    private void filterProducts() {
        CategoryItem selectedCat = (CategoryItem) cmbCategoryFilter.getSelectedItem();
        Integer catId = selectedCat != null && selectedCat.id > 0 ? selectedCat.id : null;
        String query = txtSearchProducts != null ? txtSearchProducts.getText().trim() : "";

        SwingWorker<List<Product>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Product> doInBackground() throws Exception {
                return productService.searchProducts(query, catId, false);
            }

            @Override
            protected void done() {
                try {
                    List<Product> list = get();
                    productsModel.setRowCount(0);
                    for (Product p : list) {
                        productsModel.addRow(new Object[]{
                                p.getId(),
                                p.getName(),
                                p.getCategoryName(),
                                "$" + p.getPrice(),
                                p.getQuantity(),
                                p.getLowStockThreshold(),
                                p.getStatus()
                        });
                    }
                } catch (Exception ex) {
                    System.err.println("Load products failed: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void showAddProductDialog() {
        ProductFormDialog dlg = new ProductFormDialog(this, null);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            filterProducts();
        }
    }

    private void editSelectedProduct() {
        int row = tblProducts.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a product from the table.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int id = (int) productsModel.getValueAt(row, 0);
        try {
            Product p = productService.getProductById(id);
            if (p != null) {
                ProductFormDialog dlg = new ProductFormDialog(this, p);
                dlg.setVisible(true);
                if (dlg.isSaved()) {
                    filterProducts();
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedProduct() {
        int row = tblProducts.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a product from the table.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int id = (int) productsModel.getValueAt(row, 0);
        String name = (String) productsModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete/deactivate '" + name + "'?\n"
                        + "Note: If this product has past sales records, it will be safely deactivated to preserve sales history.",
                "Confirm Removal",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                productService.deleteOrDeactivateProduct(id);
                JOptionPane.showMessageDialog(this, "Product updated/removed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                filterProducts();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting product: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // =========================================================================
    // 4. CATEGORIES MANAGEMENT VIEW
    // =========================================================================
    private JPanel createCategoriesPanel() {
        JPanel panel = new JPanel(new BorderLayout(14, 14));
        panel.setOpaque(false);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        JLabel title = new JLabel("Product Categories");
        title.setFont(PawMartTheme.FONT_TITLE_LARGE);
        title.setForeground(PawMartTheme.TEXT_DARK);
        topBar.add(title, BorderLayout.WEST);

        PrimaryButton btnAddCat = new PrimaryButton("+ Add Category");
        btnAddCat.addActionListener(e -> showAddCategoryDialog());
        topBar.add(btnAddCat, BorderLayout.EAST);

        panel.add(topBar, BorderLayout.NORTH);

        categoriesModel = new DefaultTableModel(new String[]{"ID", "Category Name", "Description"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblCategories = new ModernTable(categoriesModel);

        JPanel tableCard = new RoundedPanel(14, Color.WHITE, PawMartTheme.CARD_BORDER, true);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        tableCard.add(ModernTable.createScrollPane(tblCategories), BorderLayout.CENTER);

        panel.add(tableCard, BorderLayout.CENTER);
        return panel;
    }

    private void loadCategories() {
        SwingWorker<List<Category>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Category> doInBackground() throws Exception {
                return categoryService.getAllCategories();
            }

            @Override
            protected void done() {
                try {
                    List<Category> list = get();
                    categoriesModel.setRowCount(0);
                    for (Category c : list) {
                        categoriesModel.addRow(new Object[]{c.getId(), c.getName(), c.getDescription()});
                    }
                } catch (Exception ex) {
                    System.err.println("Load categories error: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void showAddCategoryDialog() {
        String name = JOptionPane.showInputDialog(this, "Enter Category Name:", "New Category", JOptionPane.PLAIN_MESSAGE);
        if (name != null && !name.trim().isEmpty()) {
            String desc = JOptionPane.showInputDialog(this, "Enter Description:", "New Category Description", JOptionPane.PLAIN_MESSAGE);
            try {
                categoryService.addCategory(name, desc);
                JOptionPane.showMessageDialog(this, "Category added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadCategories();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    // =========================================================================
    // 5. INVENTORY & LOW STOCK ALERTS VIEW
    // =========================================================================
    private JPanel createInventoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(14, 14));
        panel.setOpaque(false);

        JLabel title = new JLabel("Low Stock Inventory & Restock Hub");
        title.setFont(PawMartTheme.FONT_TITLE_LARGE);
        title.setForeground(PawMartTheme.TEXT_DARK);
        panel.add(title, BorderLayout.NORTH);

        DefaultTableModel lowStockModel = new DefaultTableModel(new String[]{"ID", "Product Name", "Category", "Current Stock", "Threshold", "Action Needed"}, 0);
        ModernTable tblLow = new ModernTable(lowStockModel);

        JPanel card = new RoundedPanel(14, Color.WHITE, PawMartTheme.CARD_BORDER, true);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        card.add(ModernTable.createScrollPane(tblLow), BorderLayout.CENTER);

        panel.add(card, BorderLayout.CENTER);

        // Async load
        SwingWorker<List<Product>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Product> doInBackground() throws Exception {
                return productService.getLowStockProducts(true);
            }

            @Override
            protected void done() {
                try {
                    List<Product> list = get();
                    lowStockModel.setRowCount(0);
                    for (Product p : list) {
                        String urgency = p.getQuantity() == 0 ? "OUT OF STOCK - RESTOCK ASAP" : "LOW STOCK - RESTOCK SOON";
                        lowStockModel.addRow(new Object[]{
                                p.getId(), p.getName(), p.getCategoryName(), p.getQuantity(), p.getLowStockThreshold(), urgency
                        });
                    }
                } catch (Exception ignored) {}
            }
        };
        worker.execute();

        return panel;
    }

    private void loadInventory() {
        // Will refresh inventory panel contents
    }

    // =========================================================================
    // 6. SALES LEDGER & INVOICES VIEW
    // =========================================================================
    private JPanel createSalesPanel() {
        JPanel panel = new JPanel(new BorderLayout(14, 14));
        panel.setOpaque(false);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        JLabel title = new JLabel("Store Sales Ledger & Historical Receipts");
        title.setFont(PawMartTheme.FONT_TITLE_LARGE);
        title.setForeground(PawMartTheme.TEXT_DARK);
        topBar.add(title, BorderLayout.WEST);

        txtSearchSales = new ModernTextField("Search by receipt number...");
        txtSearchSales.setPreferredSize(new Dimension(240, 38));
        txtSearchSales.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                filterSales();
            }
        });
        topBar.add(txtSearchSales, BorderLayout.EAST);
        panel.add(topBar, BorderLayout.NORTH);

        salesModel = new DefaultTableModel(new String[]{"ID", "Receipt #", "Cashier", "Subtotal", "Discount", "Grand Total", "Payment", "Date"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblSales = new ModernTable(salesModel);

        JPanel card = new RoundedPanel(14, Color.WHITE, PawMartTheme.CARD_BORDER, true);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        card.add(ModernTable.createScrollPane(tblSales), BorderLayout.CENTER);

        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        bottomBar.setOpaque(false);

        PrimaryButton btnViewReceipt = new PrimaryButton("View Receipt & Print");
        btnViewReceipt.addActionListener(e -> viewSelectedReceipt());
        bottomBar.add(btnViewReceipt);

        card.add(bottomBar, BorderLayout.SOUTH);
        panel.add(card, BorderLayout.CENTER);

        return panel;
    }

    private void loadSales() {
        filterSales();
    }

    private void filterSales() {
        String q = txtSearchSales != null ? txtSearchSales.getText().trim() : "";
        SwingWorker<List<Sale>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Sale> doInBackground() throws Exception {
                return reportService.filterSales(q, null, null, null);
            }

            @Override
            protected void done() {
                try {
                    List<Sale> list = get();
                    salesModel.setRowCount(0);
                    for (Sale s : list) {
                        salesModel.addRow(new Object[]{
                                s.getId(),
                                s.getReceiptNumber(),
                                s.getCashierName(),
                                "$" + s.getSubtotal(),
                                "-$" + s.getDiscountAmount(),
                                "$" + s.getGrandTotal(),
                                s.getPaymentMethod(),
                                s.getCreatedAt() != null ? DATE_FMT.format(s.getCreatedAt()) : ""
                        });
                    }
                } catch (Exception ex) {
                    System.err.println("Load sales error: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void viewSelectedReceipt() {
        int row = tblSales.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a sales record from the table.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int saleId = (int) salesModel.getValueAt(row, 0);
        try {
            Sale s = reportService.getSaleDetails(saleId);
            if (s != null) {
                new ReceiptPreviewDialog(this, s).setVisible(true);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error viewing receipt: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================================
    // 7. REPORTS & CHARTS VIEW
    // =========================================================================
    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(14, 14));
        panel.setOpaque(false);

        JLabel title = new JLabel("Analytics & Business Intelligence");
        title.setFont(PawMartTheme.FONT_TITLE_LARGE);
        title.setForeground(PawMartTheme.TEXT_DARK);
        panel.add(title, BorderLayout.NORTH);

        JPanel chartsSplit = new JPanel(new GridLayout(1, 2, 14, 14));
        chartsSplit.setOpaque(false);

        // Chart 1: Monthly Sales
        JPanel pnlBar = new RoundedPanel(14, Color.WHITE, PawMartTheme.CARD_BORDER, true);
        pnlBar.setLayout(new BorderLayout());
        pnlBar.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        JLabel lblBar = new JLabel("Revenue Growth (Monthly)");
        lblBar.setFont(PawMartTheme.FONT_SUBTITLE);
        pnlBar.add(lblBar, BorderLayout.NORTH);

        JPanel barHolder = new JPanel(new BorderLayout());
        pnlBar.add(barHolder, BorderLayout.CENTER);
        chartsSplit.add(pnlBar);

        // Chart 2: Category Distribution Pie Chart
        JPanel pnlPie = new RoundedPanel(14, Color.WHITE, PawMartTheme.CARD_BORDER, true);
        pnlPie.setLayout(new BorderLayout());
        pnlPie.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        JLabel lblPie = new JLabel("Product Catalog Share by Category");
        lblPie.setFont(PawMartTheme.FONT_SUBTITLE);
        pnlPie.add(lblPie, BorderLayout.NORTH);

        JPanel pieHolder = new JPanel(new BorderLayout());
        pnlPie.add(pieHolder, BorderLayout.CENTER);
        chartsSplit.add(pnlPie);

        panel.add(chartsSplit, BorderLayout.CENTER);

        // Load data into charts
        SwingWorker<DashboardStats, Void> worker = new SwingWorker<>() {
            @Override
            protected DashboardStats doInBackground() throws Exception {
                return reportService.getDashboardStats();
            }

            @Override
            protected void done() {
                try {
                    DashboardStats stats = get();

                    // Bar Chart
                    DefaultCategoryDataset bDataset = new DefaultCategoryDataset();
                    if (stats.getMonthlyRevenue() != null) {
                        for (Map.Entry<String, BigDecimal> e : stats.getMonthlyRevenue().entrySet()) {
                            bDataset.addValue(e.getValue().doubleValue(), "Revenue ($)", e.getKey());
                        }
                    }
                    JFreeChart bChart = ChartFactory.createBarChart(null, "Month", "Revenue ($)", bDataset);
                    bChart.setBackgroundPaint(Color.WHITE);
                    barHolder.removeAll();
                    barHolder.add(new ChartPanel(bChart), BorderLayout.CENTER);
                    barHolder.revalidate();

                    // Pie Chart
                    DefaultPieDataset pDataset = new DefaultPieDataset();
                    if (stats.getCategoryDistribution() != null) {
                        for (Map.Entry<String, Integer> e : stats.getCategoryDistribution().entrySet()) {
                            pDataset.setValue(e.getKey(), e.getValue());
                        }
                    }
                    JFreeChart pChart = ChartFactory.createPieChart(null, pDataset, true, true, false);
                    pChart.setBackgroundPaint(Color.WHITE);
                    PiePlot pPlot = (PiePlot) pChart.getPlot();
                    pPlot.setBackgroundPaint(Color.WHITE);
                    pPlot.setOutlinePaint(null);

                    pieHolder.removeAll();
                    pieHolder.add(new ChartPanel(pChart), BorderLayout.CENTER);
                    pieHolder.revalidate();

                } catch (Exception ignored) {}
            }
        };
        worker.execute();

        return panel;
    }

    private void loadReports() {
        // Refresh reports if needed
    }

    // =========================================================================
    // 8. SETTINGS VIEW
    // =========================================================================
    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(14, 14));
        panel.setOpaque(false);

        JLabel title = new JLabel("Database & Environment Configuration");
        title.setFont(PawMartTheme.FONT_TITLE_LARGE);
        title.setForeground(PawMartTheme.TEXT_DARK);
        panel.add(title, BorderLayout.NORTH);

        JPanel card = new RoundedPanel(14, Color.WHITE, PawMartTheme.CARD_BORDER, true);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JLabel info = new JLabel("<html><b>Active MySQL Connection Details</b><br/>"
                + "PawMart connects through JDBC to MySQL via WampServer / phpMyAdmin defaults.<br/>"
                + "JDBC URL: <code>" + DatabaseConfig.getJdbcUrl() + "</code><br/>"
                + "Database: <b>" + DatabaseConfig.getDatabase() + "</b> | User: <b>" + DatabaseConfig.getUsername() + "</b></html>");
        info.setFont(PawMartTheme.FONT_BODY);
        card.add(info);
        card.add(Box.createVerticalStrut(20));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.setOpaque(false);

        PrimaryButton btnTest = new PrimaryButton("Test MySQL Connection");
        btnTest.addActionListener(e -> {
            boolean ok = DatabaseConnection.testConnection();
            if (ok) {
                JOptionPane.showMessageDialog(this, "MySQL Database Connection is HEALTHY and OPERATIONAL!", "Connection Test", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to connect to MySQL database.\nPlease verify that WampServer MySQL service is running.", "Connection Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnRow.add(btnTest);
        card.add(btnRow);

        panel.add(card, BorderLayout.CENTER);
        return panel;
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out of PawMart?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            AuthService.getInstance().logout();
            dispose();
            new LoginFrame().setVisible(true);
        }
    }

    private ModernTextField addField(JPanel p, String lbl, String placeholder) {
        JLabel label = new JLabel(lbl);
        label.setFont(PawMartTheme.FONT_BODY_BOLD);
        p.add(label);
        p.add(Box.createVerticalStrut(4));
        ModernTextField tf = new ModernTextField(placeholder);
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        p.add(tf);
        p.add(Box.createVerticalStrut(10));
        return tf;
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

    /**
     * Inner Dialog for Adding / Editing Products.
     */
    private static class ProductFormDialog extends JDialog {
        private final Product product;
        private boolean saved = false;

        private ModernTextField txtName;
        private JComboBox<CategoryItem> cmbCategory;
        private ModernTextField txtPrice;
        private JSpinner spinQuantity;
        private JSpinner spinThreshold;
        private JTextArea txtDesc;
        private JComboBox<String> cmbStatus;

        ProductFormDialog(JFrame parent, Product productToEdit) {
            super(parent, productToEdit == null ? "Add New Pet Product" : "Edit Product: " + productToEdit.getName(), true);
            this.product = productToEdit != null ? productToEdit : new Product();

            setSize(480, 580);
            setLocationRelativeTo(parent);
            getContentPane().setBackground(PawMartTheme.BG_CREAM);
            setLayout(new BorderLayout());

            initForm();
        }

        private void initForm() {
            JPanel p = new JPanel();
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
            p.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));
            p.setOpaque(false);

            p.add(new JLabel("Product Name"));
            txtName = new ModernTextField("e.g. Royal Canin Adult Dog Food 3kg");
            txtName.setText(product.getName());
            p.add(txtName);
            p.add(Box.createVerticalStrut(10));

            p.add(new JLabel("Category"));
            cmbCategory = new JComboBox<>();
            try {
                List<Category> cats = new CategoryService().getAllCategories();
                CategoryItem sel = null;
                for (Category c : cats) {
                    CategoryItem item = new CategoryItem(c.getId(), c.getName());
                    cmbCategory.addItem(item);
                    if (product.getCategoryId() == c.getId()) {
                        sel = item;
                    }
                }
                if (sel != null) cmbCategory.setSelectedItem(sel);
            } catch (Exception ignored) {}
            p.add(cmbCategory);
            p.add(Box.createVerticalStrut(10));

            p.add(new JLabel("Unit Price ($)"));
            txtPrice = new ModernTextField("e.g. 29.99");
            if (product.getPrice() != null && product.getPrice().compareTo(BigDecimal.ZERO) > 0) {
                txtPrice.setText(product.getPrice().toString());
            }
            p.add(txtPrice);
            p.add(Box.createVerticalStrut(10));

            // Stock & Threshold Row
            JPanel numRow = new JPanel(new GridLayout(1, 2, 10, 0));
            numRow.setOpaque(false);

            JPanel qBox = new JPanel(new BorderLayout());
            qBox.setOpaque(false);
            qBox.add(new JLabel("Current Stock Quantity"), BorderLayout.NORTH);
            spinQuantity = new JSpinner(new SpinnerNumberModel(product.getQuantity(), 0, 99999, 1));
            qBox.add(spinQuantity, BorderLayout.CENTER);

            JPanel tBox = new JPanel(new BorderLayout());
            tBox.setOpaque(false);
            tBox.add(new JLabel("Low Stock Threshold"), BorderLayout.NORTH);
            spinThreshold = new JSpinner(new SpinnerNumberModel(product.getLowStockThreshold() > 0 ? product.getLowStockThreshold() : 5, 0, 9999, 1));
            tBox.add(spinThreshold, BorderLayout.CENTER);

            numRow.add(qBox);
            numRow.add(tBox);
            p.add(numRow);
            p.add(Box.createVerticalStrut(10));

            p.add(new JLabel("Description"));
            txtDesc = new JTextArea(product.getDescription(), 3, 20);
            txtDesc.setLineWrap(true);
            txtDesc.setWrapStyleWord(true);
            p.add(new JScrollPane(txtDesc));
            p.add(Box.createVerticalStrut(10));

            p.add(new JLabel("Status"));
            cmbStatus = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE"});
            cmbStatus.setSelectedItem(product.getStatus() != null ? product.getStatus() : "ACTIVE");
            p.add(cmbStatus);
            p.add(Box.createVerticalStrut(20));

            JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            btns.setOpaque(false);
            SecondaryButton btnCancel = new SecondaryButton("Cancel");
            btnCancel.addActionListener(e -> dispose());

            PrimaryButton btnSave = new PrimaryButton("Save Product");
            btnSave.addActionListener(e -> save());

            btns.add(btnCancel);
            btns.add(btnSave);
            p.add(btns);

            add(new JScrollPane(p), BorderLayout.CENTER);
        }

        private void save() {
            try {
                String name = txtName.getText().trim();
                CategoryItem catItem = (CategoryItem) cmbCategory.getSelectedItem();
                String priceStr = txtPrice.getText().trim();

                if (name.isEmpty()) throw new Exception("Product name is required.");
                if (catItem == null) throw new Exception("Please select a category.");
                BigDecimal price = new BigDecimal(priceStr);

                product.setName(name);
                product.setCategoryId(catItem.id);
                product.setPrice(price);
                product.setQuantity((Integer) spinQuantity.getValue());
                product.setLowStockThreshold((Integer) spinThreshold.getValue());
                product.setDescription(txtDesc.getText().trim());
                product.setStatus((String) cmbStatus.getSelectedItem());

                ProductService ps = new ProductService();
                if (product.getId() > 0) {
                    ps.updateProduct(product);
                    JOptionPane.showMessageDialog(this, "Product updated successfully!", "Saved", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    ps.addProduct(product);
                    JOptionPane.showMessageDialog(this, "Product added successfully!", "Saved", JOptionPane.INFORMATION_MESSAGE);
                }
                saved = true;
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
            }
        }

        public boolean isSaved() {
            return saved;
        }
    }
}
