import com.pawmart.model.DashboardStats;
import com.pawmart.model.Product;
import com.pawmart.service.AuthService;
import com.pawmart.service.ProductService;
import com.pawmart.service.ReportService;
import com.pawmart.service.UserService;
import com.pawmart.ui.theme.PawMartTheme;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * PawMart Pet Shop Management System - Store Manager Dashboard Hub.
 * Fully editable in Apache NetBeans GUI Builder (Design View).
 */
public class Managerpage extends javax.swing.JFrame {

    /**
     * Creates new form Managerpage
     */
    public Managerpage() {
        PawMartTheme.setupFlatLaf();
        initComponents();
        mainPanel.setOpaque(false);
        setSize(1060, 680);
        setLocationRelativeTo(null);
        
        // Background crop-to-fill photography
        PawMartTheme.applyScaledBackground(lblBackground, "/images/backgrounds/manager_dashboard_background.jpg");
        lblBackground.setBounds(0, 0, 1060, 680);

        // Paw vector brand logo
        lblBrandLogo.setText("");
        lblBrandLogo.setIcon(PawMartTheme.createPawIcon(36, 36, PawMartTheme.ORANGE_ACCENT));

        // Visual Translucent Stat Cards
        PawMartTheme.styleStatCard(cardProducts, PawMartTheme.CARD_BORDER);
        PawMartTheme.styleStatCard(cardLowStock, PawMartTheme.ORANGE_ACCENT);
        PawMartTheme.styleStatCard(cardSales, PawMartTheme.EMERALD_PRIMARY);
        PawMartTheme.styleStatCard(cardCashiers, PawMartTheme.CARD_BORDER);

        // Action Buttons Styling
        PawMartTheme.styleButton(btnQuickAdd, PawMartTheme.EMERALD_PRIMARY, java.awt.Color.WHITE, 12);
        PawMartTheme.styleButton(btnQuickSearch, java.awt.Color.WHITE, PawMartTheme.TEAL_PRIMARY, 12);
        PawMartTheme.styleButton(btnQuickCashier, java.awt.Color.WHITE, PawMartTheme.TEAL_PRIMARY, 12);
        PawMartTheme.styleButton(btnRefreshData, PawMartTheme.EMERALD_PRIMARY, java.awt.Color.WHITE, 10);
        PawMartTheme.styleButton(btnLogout, PawMartTheme.ORANGE_ACCENT, java.awt.Color.WHITE, 12);

        // Adjust positioning for optimal visibility and breathing room
        scrollTable.setBounds(25, 218, 730, 325);
        btnQuickAdd.setBounds(25, 560, 175, 42);
        btnQuickSearch.setBounds(215, 560, 175, 42);
        btnQuickCashier.setBounds(405, 560, 175, 42);
        btnLogout.setBounds(15, 560, 210, 42);

        applyTableStyling();
        loadDashboardData();
    }

    private void applyTableStyling() {
        PawMartTheme.styleTable(tblInventory);
        if (tblInventory.getColumnModel().getColumnCount() >= 6) {
            tblInventory.getColumnModel().getColumn(0).setPreferredWidth(55); // ID
            tblInventory.getColumnModel().getColumn(0).setMaxWidth(75);
            tblInventory.getColumnModel().getColumn(1).setPreferredWidth(260); // Product Name
            tblInventory.getColumnModel().getColumn(2).setPreferredWidth(140); // Category
            tblInventory.getColumnModel().getColumn(3).setPreferredWidth(90); // Price
            tblInventory.getColumnModel().getColumn(4).setPreferredWidth(80); // Stock
            tblInventory.getColumnModel().getColumn(5).setPreferredWidth(120); // Status

            // Center align ID, Price, Stock
            javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            tblInventory.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
            tblInventory.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
            tblInventory.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

            // Styled Status column renderer
            tblInventory.getColumnModel().getColumn(5).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
                @Override
                public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                    if (!isSelected) {
                        String text = value != null ? value.toString() : "";
                        if (text.contains("Low Stock")) {
                            c.setForeground(PawMartTheme.ORANGE_ACCENT);
                            c.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
                        } else {
                            c.setForeground(PawMartTheme.EMERALD_PRIMARY);
                            c.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
                        }
                    }
                    return c;
                }
            });
        }
    }

    private final ReportService reportService = new ReportService();
    private final ProductService productService = new ProductService();
    private final UserService userService = new UserService();

    /**
     * Load live statistics and products table from MySQL database.
     */
    public void loadDashboardData() {
        try {
            DashboardStats stats = reportService.getDashboardStats();
            lblStatProducts.setText(String.valueOf(stats.getTotalProducts()));
            lblStatLowStock.setText(String.valueOf(stats.getLowStockCount()));
            lblStatSales.setText(String.format("$%.2f", stats.getTotalRevenue().doubleValue()));
            lblStatCashiers.setText(String.valueOf(stats.getTotalCashiers()));

            List<Product> products = productService.getAllProducts(true);
            DefaultTableModel model = (DefaultTableModel) tblInventory.getModel();
            model.setRowCount(0);

            for (Product p : products) {
                String status = p.getQuantity() <= p.getLowStockThreshold() ? "Low Stock" : "In Stock";
                model.addRow(new Object[]{
                    p.getId(),
                    p.getName(),
                    p.getCategoryName() != null ? p.getCategoryName() : "General",
                    String.format("$%.2f", p.getPrice().doubleValue()),
                    p.getQuantity(),
                    status
                });
            }
        } catch (Exception ex) {
            System.err.println("[Managerpage] Error loading dashboard data: " + ex.getMessage());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sidebarPanel = new javax.swing.JPanel();
        lblBrandLogo = new javax.swing.JLabel();
        lblBrandTitle = new javax.swing.JLabel();
        lblBrandSub = new javax.swing.JLabel();
        btnNavAddProduct = new javax.swing.JButton();
        btnNavSearch = new javax.swing.JButton();
        btnNavViewSupplies = new javax.swing.JButton();
        btnNavCashiers = new javax.swing.JButton();
        btnNavPOS = new javax.swing.JButton();
        btnNavReports = new javax.swing.JButton();
        btnLogout = new javax.swing.JButton();
        mainPanel = new javax.swing.JPanel();
        lblHeaderTitle = new javax.swing.JLabel();
        lblHeaderSub = new javax.swing.JLabel();
        btnRefreshData = new javax.swing.JButton();
        cardProducts = new javax.swing.JPanel();
        lblTitleProd = new javax.swing.JLabel();
        lblStatProducts = new javax.swing.JLabel();
        cardLowStock = new javax.swing.JPanel();
        lblTitleLowStock = new javax.swing.JLabel();
        lblStatLowStock = new javax.swing.JLabel();
        cardSales = new javax.swing.JPanel();
        lblTitleSales = new javax.swing.JLabel();
        lblStatSales = new javax.swing.JLabel();
        cardCashiers = new javax.swing.JPanel();
        lblTitleCashiers = new javax.swing.JLabel();
        lblStatCashiers = new javax.swing.JLabel();
        lblTableTitle = new javax.swing.JLabel();
        scrollTable = new javax.swing.JScrollPane();
        tblInventory = new javax.swing.JTable();
        btnQuickAdd = new javax.swing.JButton();
        btnQuickSearch = new javax.swing.JButton();
        btnQuickCashier = new javax.swing.JButton();
        lblBackground = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("PawMart - Manager Dashboard Hub");
        setBackground(new java.awt.Color(250, 247, 242));
        setResizable(false);
        getContentPane().setLayout(null);

        sidebarPanel.setBackground(new java.awt.Color(15, 76, 92));
        sidebarPanel.setLayout(null);

        lblBrandLogo.setFont(new java.awt.Font("Segoe UI Emoji", 1, 36)); // NOI18N
        lblBrandLogo.setForeground(new java.awt.Color(242, 100, 25));
        lblBrandLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBrandLogo.setText("");
        sidebarPanel.add(lblBrandLogo);
        lblBrandLogo.setBounds(10, 20, 220, 40);

        lblBrandTitle.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblBrandTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblBrandTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBrandTitle.setText("PAWMART");
        sidebarPanel.add(lblBrandTitle);
        lblBrandTitle.setBounds(10, 60, 220, 30);

        lblBrandSub.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblBrandSub.setForeground(new java.awt.Color(251, 139, 36));
        lblBrandSub.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBrandSub.setText("MANAGER PORTAL");
        sidebarPanel.add(lblBrandSub);
        lblBrandSub.setBounds(10, 90, 220, 20);

        btnNavAddProduct.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnNavAddProduct.setText("Add Pet Supplies");
        btnNavAddProduct.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        btnNavAddProduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNavAddProductActionPerformed(evt);
            }
        });
        sidebarPanel.add(btnNavAddProduct);
        btnNavAddProduct.setBounds(15, 130, 210, 40);

        btnNavSearch.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnNavSearch.setText("Search Pet Supplies");
        btnNavSearch.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        btnNavSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNavSearchActionPerformed(evt);
            }
        });
        sidebarPanel.add(btnNavSearch);
        btnNavSearch.setBounds(15, 180, 210, 40);

        btnNavViewSupplies.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnNavViewSupplies.setText("View All Supplies");
        btnNavViewSupplies.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        btnNavViewSupplies.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNavViewSuppliesActionPerformed(evt);
            }
        });
        sidebarPanel.add(btnNavViewSupplies);
        btnNavViewSupplies.setBounds(15, 230, 210, 40);

        btnNavCashiers.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnNavCashiers.setText("Cashier Accounts");
        btnNavCashiers.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        btnNavCashiers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNavCashiersActionPerformed(evt);
            }
        });
        sidebarPanel.add(btnNavCashiers);
        btnNavCashiers.setBounds(15, 280, 210, 40);

        btnNavPOS.setBackground(new java.awt.Color(5, 150, 105));
        btnNavPOS.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnNavPOS.setForeground(new java.awt.Color(255, 255, 255));
        btnNavPOS.setText("Full POS Terminal");
        btnNavPOS.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        btnNavPOS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNavPOSActionPerformed(evt);
            }
        });
        sidebarPanel.add(btnNavPOS);
        btnNavPOS.setBounds(15, 330, 210, 40);

        btnNavReports.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnNavReports.setText("Analytics & Reports");
        btnNavReports.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        btnNavReports.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNavReportsActionPerformed(evt);
            }
        });
        sidebarPanel.add(btnNavReports);
        btnNavReports.setBounds(15, 380, 210, 40);

        btnLogout.setBackground(new java.awt.Color(242, 100, 25));
        btnLogout.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnLogout.setForeground(new java.awt.Color(255, 255, 255));
        btnLogout.setText("Logout");
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });
        sidebarPanel.add(btnLogout);
        btnLogout.setBounds(15, 565, 210, 42);

        getContentPane().add(sidebarPanel);
        sidebarPanel.setBounds(0, 0, 240, 640);

        mainPanel.setLayout(null);

        lblHeaderTitle.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblHeaderTitle.setForeground(new java.awt.Color(15, 76, 92));
        lblHeaderTitle.setText("Welcome Back, Store Manager!");
        mainPanel.add(lblHeaderTitle);
        lblHeaderTitle.setBounds(25, 20, 450, 30);

        lblHeaderSub.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblHeaderSub.setForeground(new java.awt.Color(107, 114, 128));
        lblHeaderSub.setText("Store overview, inventory control, and cashier management");
        mainPanel.add(lblHeaderSub);
        lblHeaderSub.setBounds(25, 50, 450, 20);

        btnRefreshData.setBackground(new java.awt.Color(5, 150, 105));
        btnRefreshData.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnRefreshData.setForeground(new java.awt.Color(255, 255, 255));
        btnRefreshData.setText("Refresh");
        btnRefreshData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshDataActionPerformed(evt);
            }
        });
        mainPanel.add(btnRefreshData);
        btnRefreshData.setBounds(605, 22, 130, 36);

        cardProducts.setBackground(new java.awt.Color(255, 255, 255));
        cardProducts.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(226, 217, 204), 1, true));
        cardProducts.setLayout(null);

        lblTitleProd.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblTitleProd.setForeground(new java.awt.Color(107, 114, 128));
        lblTitleProd.setText("TOTAL PRODUCTS");
        cardProducts.add(lblTitleProd);
        lblTitleProd.setBounds(15, 10, 135, 18);

        lblStatProducts.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblStatProducts.setForeground(new java.awt.Color(15, 76, 92));
        lblStatProducts.setText("12");
        cardProducts.add(lblStatProducts);
        lblStatProducts.setBounds(15, 32, 135, 35);

        mainPanel.add(cardProducts);
        cardProducts.setBounds(25, 85, 165, 85);

        cardLowStock.setBackground(new java.awt.Color(255, 255, 255));
        cardLowStock.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(226, 217, 204), 1, true));
        cardLowStock.setLayout(null);

        lblTitleLowStock.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblTitleLowStock.setForeground(new java.awt.Color(242, 100, 25));
        lblTitleLowStock.setText("LOW STOCK ITEMS");
        cardLowStock.add(lblTitleLowStock);
        lblTitleLowStock.setBounds(15, 10, 135, 18);

        lblStatLowStock.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblStatLowStock.setForeground(new java.awt.Color(242, 100, 25));
        lblStatLowStock.setText("2");
        cardLowStock.add(lblStatLowStock);
        lblStatLowStock.setBounds(15, 32, 135, 35);

        mainPanel.add(cardLowStock);
        cardLowStock.setBounds(206, 85, 165, 85);

        cardSales.setBackground(new java.awt.Color(255, 255, 255));
        cardSales.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(226, 217, 204), 1, true));
        cardSales.setLayout(null);

        lblTitleSales.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblTitleSales.setForeground(new java.awt.Color(5, 150, 105));
        lblTitleSales.setText("TOTAL REVENUE");
        cardSales.add(lblTitleSales);
        lblTitleSales.setBounds(15, 10, 135, 18);

        lblStatSales.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblStatSales.setForeground(new java.awt.Color(5, 150, 105));
        lblStatSales.setText("$482.50");
        cardSales.add(lblStatSales);
        lblStatSales.setBounds(15, 32, 135, 35);

        mainPanel.add(cardSales);
        cardSales.setBounds(387, 85, 165, 85);

        cardCashiers.setBackground(new java.awt.Color(255, 255, 255));
        cardCashiers.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(226, 217, 204), 1, true));
        cardCashiers.setLayout(null);

        lblTitleCashiers.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblTitleCashiers.setForeground(new java.awt.Color(107, 114, 128));
        lblTitleCashiers.setText("STAFF CASHIERS");
        cardCashiers.add(lblTitleCashiers);
        lblTitleCashiers.setBounds(15, 10, 135, 18);

        lblStatCashiers.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblStatCashiers.setForeground(new java.awt.Color(15, 76, 92));
        lblStatCashiers.setText("1");
        cardCashiers.add(lblStatCashiers);
        lblStatCashiers.setBounds(15, 32, 135, 35);

        mainPanel.add(cardCashiers);
        cardCashiers.setBounds(568, 85, 165, 85);

        lblTableTitle.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblTableTitle.setForeground(new java.awt.Color(15, 76, 92));
        lblTableTitle.setText("Store Inventory & Supplies Overview");
        mainPanel.add(lblTableTitle);
        lblTableTitle.setBounds(25, 185, 400, 25);

        tblInventory.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        tblInventory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Product Name", "Category", "Price", "Stock", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblInventory.setRowHeight(28);
        scrollTable.setViewportView(tblInventory);

        mainPanel.add(scrollTable);
        scrollTable.setBounds(25, 218, 710, 330);

        btnQuickAdd.setBackground(new java.awt.Color(5, 150, 105));
        btnQuickAdd.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnQuickAdd.setForeground(new java.awt.Color(255, 255, 255));
        btnQuickAdd.setText("Add Product");
        btnQuickAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuickAddActionPerformed(evt);
            }
        });
        mainPanel.add(btnQuickAdd);
        btnQuickAdd.setBounds(25, 565, 170, 42);

        btnQuickSearch.setBackground(new java.awt.Color(255, 255, 255));
        btnQuickSearch.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnQuickSearch.setForeground(new java.awt.Color(15, 76, 92));
        btnQuickSearch.setText("Search Supplies");
        btnQuickSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuickSearchActionPerformed(evt);
            }
        });
        mainPanel.add(btnQuickSearch);
        btnQuickSearch.setBounds(210, 565, 170, 42);

        btnQuickCashier.setBackground(new java.awt.Color(255, 255, 255));
        btnQuickCashier.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnQuickCashier.setForeground(new java.awt.Color(15, 76, 92));
        btnQuickCashier.setText("New Cashier");
        btnQuickCashier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuickCashierActionPerformed(evt);
            }
        });
        mainPanel.add(btnQuickCashier);
        btnQuickCashier.setBounds(395, 565, 170, 42);

        getContentPane().add(mainPanel);
        mainPanel.setBounds(240, 0, 784, 640);

        lblBackground.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/backgrounds/manager_boutique_bg.png"))); // NOI18N
        getContentPane().add(lblBackground);
        lblBackground.setBounds(0, 0, 1024, 640);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnNavAddProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNavAddProductActionPerformed
        Addpetsupplies addForm = new Addpetsupplies();
        addForm.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnNavAddProductActionPerformed

    private void btnNavSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNavSearchActionPerformed
        Searchpetsupplies searchForm = new Searchpetsupplies();
        searchForm.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnNavSearchActionPerformed

    private void btnNavViewSuppliesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNavViewSuppliesActionPerformed
        Viewpetsupllies viewForm = new Viewpetsupllies();
        viewForm.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnNavViewSuppliesActionPerformed

    private void btnNavCashiersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNavCashiersActionPerformed
        createcashieraccounts cashierForm = new createcashieraccounts();
        cashierForm.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnNavCashiersActionPerformed

    private void btnNavPOSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNavPOSActionPerformed
        POSBillingFrame posFrame = new POSBillingFrame();
        posFrame.setVisible(true);
    }//GEN-LAST:event_btnNavPOSActionPerformed

    private void btnNavReportsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNavReportsActionPerformed
        ReportsSalesFrame reportFrame = new ReportsSalesFrame();
        reportFrame.setVisible(true);
    }//GEN-LAST:event_btnNavReportsActionPerformed

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to log out of PawMart?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            AuthService.getInstance().logout();
            new login().setVisible(true);
            this.dispose();
        }
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void btnRefreshDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshDataActionPerformed
        loadDashboardData();
    }//GEN-LAST:event_btnRefreshDataActionPerformed

    private void btnQuickAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuickAddActionPerformed
        btnNavAddProductActionPerformed(evt);
    }//GEN-LAST:event_btnQuickAddActionPerformed

    private void btnQuickSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuickSearchActionPerformed
        btnNavSearchActionPerformed(evt);
    }//GEN-LAST:event_btnQuickSearchActionPerformed

    private void btnQuickCashierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuickCashierActionPerformed
        btnNavCashiersActionPerformed(evt);
    }//GEN-LAST:event_btnQuickCashierActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        PawMartTheme.setupFlatLaf();
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Managerpage().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnNavAddProduct;
    private javax.swing.JButton btnNavCashiers;
    private javax.swing.JButton btnNavPOS;
    private javax.swing.JButton btnNavReports;
    private javax.swing.JButton btnNavSearch;
    private javax.swing.JButton btnNavViewSupplies;
    private javax.swing.JButton btnQuickAdd;
    private javax.swing.JButton btnQuickCashier;
    private javax.swing.JButton btnQuickSearch;
    private javax.swing.JButton btnRefreshData;
    private javax.swing.JPanel cardCashiers;
    private javax.swing.JPanel cardLowStock;
    private javax.swing.JPanel cardProducts;
    private javax.swing.JPanel cardSales;
    private javax.swing.JLabel lblBackground;
    private javax.swing.JLabel lblBrandLogo;
    private javax.swing.JLabel lblBrandSub;
    private javax.swing.JLabel lblBrandTitle;
    private javax.swing.JLabel lblHeaderSub;
    private javax.swing.JLabel lblHeaderTitle;
    private javax.swing.JLabel lblStatCashiers;
    private javax.swing.JLabel lblStatLowStock;
    private javax.swing.JLabel lblStatProducts;
    private javax.swing.JLabel lblStatSales;
    private javax.swing.JLabel lblTableTitle;
    private javax.swing.JLabel lblTitleCashiers;
    private javax.swing.JLabel lblTitleLowStock;
    private javax.swing.JLabel lblTitleProd;
    private javax.swing.JLabel lblTitleSales;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JScrollPane scrollTable;
    private javax.swing.JPanel sidebarPanel;
    private javax.swing.JTable tblInventory;
    // End of variables declaration//GEN-END:variables
}
