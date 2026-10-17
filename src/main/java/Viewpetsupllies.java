import com.pawmart.model.Product;
import com.pawmart.model.Role;
import com.pawmart.model.User;
import com.pawmart.service.AuthService;
import com.pawmart.service.ProductService;
import com.pawmart.ui.theme.PawMartTheme;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 * PawMart Pet Shop Management System - Complete Catalog View.
 * Fully editable in Apache NetBeans GUI Builder (Design View).
 */
public class Viewpetsupllies extends javax.swing.JFrame {

    private final ProductService productService = new ProductService();

    /**
     * Creates new form Viewpetsupllies
     */
    public Viewpetsupllies() {
        PawMartTheme.setupFlatLaf();
        initComponents();
        setSize(1060, 680);
        setLocationRelativeTo(null);

        // Scaled background photography
        PawMartTheme.applyScaledBackground(lblBackground, "/images/backgrounds/view_pet_supplies_background.jpg");
        lblBackground.setBounds(0, 0, 1060, 680);

        // Translucent summary card styling & layout
        PawMartTheme.styleTranslucentCard(summaryBar, 16, new java.awt.Color(255, 255, 255, 240), PawMartTheme.CARD_BORDER);
        summaryBar.setBounds(30, 95, 1000, 60);
        lblCatalogInfo.setBounds(20, 18, 220, 24);
        lblTotalCount.setBounds(250, 18, 300, 24);
        btnRefresh.setBounds(840, 12, 140, 36);

        // Catalog Table and Back button bounds
        scrollCatalog.setBounds(30, 168, 1000, 420);
        btnBack.setBounds(30, 600, 220, 40);

        // Clean button styling and labels
        btnRefresh.setText("Refresh");
        btnBack.setText("Back to Dashboard");
        lblHeaderIcon.setText("");
        lblHeaderIcon.setIcon(com.pawmart.ui.components.PawIconRenderer.createPawIcon(28, PawMartTheme.ORANGE_ACCENT));

        PawMartTheme.styleButton(btnRefresh, PawMartTheme.EMERALD_PRIMARY, java.awt.Color.WHITE, 12);
        PawMartTheme.styleButton(btnBack, PawMartTheme.ORANGE_ACCENT, java.awt.Color.WHITE, 12);

        applyTableStyling();
        loadCatalog();
    }

    private void applyTableStyling() {
        PawMartTheme.styleTable(tblCatalog);
        if (tblCatalog.getColumnModel().getColumnCount() >= 7) {
            tblCatalog.getColumnModel().getColumn(0).setPreferredWidth(55); // ID
            tblCatalog.getColumnModel().getColumn(0).setMaxWidth(75);
            tblCatalog.getColumnModel().getColumn(1).setPreferredWidth(270); // Product Name
            tblCatalog.getColumnModel().getColumn(2).setPreferredWidth(140); // Category
            tblCatalog.getColumnModel().getColumn(3).setPreferredWidth(90);  // Price
            tblCatalog.getColumnModel().getColumn(4).setPreferredWidth(80);  // Stock
            tblCatalog.getColumnModel().getColumn(5).setPreferredWidth(85);  // Threshold
            tblCatalog.getColumnModel().getColumn(6).setPreferredWidth(120); // Status

            // Center align ID, Price, Stock, Threshold
            javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            tblCatalog.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
            tblCatalog.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
            tblCatalog.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
            tblCatalog.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

            // Styled Status column renderer
            tblCatalog.getColumnModel().getColumn(6).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
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

    private void loadCatalog() {
        try {
            List<Product> products = productService.getAllProducts(false);
            DefaultTableModel model = (DefaultTableModel) tblCatalog.getModel();
            model.setRowCount(0);

            for (Product p : products) {
                String status = p.getQuantity() <= p.getLowStockThreshold() ? "Low Stock" : "In Stock";
                model.addRow(new Object[]{
                    p.getId(),
                    p.getName(),
                    p.getCategoryName() != null ? p.getCategoryName() : "General",
                    String.format("$%.2f", p.getPrice().doubleValue()),
                    p.getQuantity(),
                    p.getLowStockThreshold(),
                    status
                });
            }

            lblTotalCount.setText(products.size() + " items loaded");
        } catch (Exception ex) {
            System.err.println("[Viewpetsupllies] Error loading catalog: " + ex.getMessage());
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

        headerPanel = new javax.swing.JPanel();
        lblHeaderIcon = new javax.swing.JLabel();
        lblHeaderTitle = new javax.swing.JLabel();
        lblHeaderSub = new javax.swing.JLabel();
        summaryBar = new javax.swing.JPanel();
        lblCatalogInfo = new javax.swing.JLabel();
        lblTotalCount = new javax.swing.JLabel();
        btnRefresh = new javax.swing.JButton();
        scrollCatalog = new javax.swing.JScrollPane();
        tblCatalog = new javax.swing.JTable();
        btnBack = new javax.swing.JButton();
        lblBackground = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("PawMart - Pet Supplies Catalog & Inventory");
        setBackground(new java.awt.Color(250, 247, 242));
        setResizable(false);
        getContentPane().setLayout(null);

        headerPanel.setOpaque(false);
        headerPanel.setLayout(null);

        lblHeaderIcon.setFont(new java.awt.Font("Segoe UI Emoji", 1, 28)); // NOI18N
        lblHeaderIcon.setForeground(new java.awt.Color(242, 100, 25));
        lblHeaderIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHeaderIcon.setText("🐾");
        headerPanel.add(lblHeaderIcon);
        lblHeaderIcon.setBounds(25, 18, 50, 48);

        lblHeaderTitle.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblHeaderTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblHeaderTitle.setText("Pet Supplies Catalog & Stock Levels");
        headerPanel.add(lblHeaderTitle);
        lblHeaderTitle.setBounds(105, 18, 550, 32);

        lblHeaderSub.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblHeaderSub.setForeground(new java.awt.Color(209, 231, 221));
        lblHeaderSub.setText("Complete inventory catalog, prices, category tags, and real-time stock levels");
        headerPanel.add(lblHeaderSub);
        lblHeaderSub.setBounds(105, 50, 650, 20);

        getContentPane().add(headerPanel);
        headerPanel.setBounds(0, 0, 1024, 90);

        summaryBar.setBackground(new java.awt.Color(255, 255, 255));
        summaryBar.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(226, 217, 204), 1, true));
        summaryBar.setLayout(null);

        lblCatalogInfo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblCatalogInfo.setForeground(new java.awt.Color(15, 76, 92));
        lblCatalogInfo.setText("Store Supplies Directory");
        summaryBar.add(lblCatalogInfo);
        lblCatalogInfo.setBounds(20, 18, 220, 24);

        lblTotalCount.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblTotalCount.setForeground(new java.awt.Color(5, 150, 105));
        lblTotalCount.setText("12 items loaded");
        summaryBar.add(lblTotalCount);
        lblTotalCount.setBounds(250, 18, 300, 24);

        btnRefresh.setBackground(new java.awt.Color(5, 150, 105));
        btnRefresh.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnRefresh.setForeground(new java.awt.Color(255, 255, 255));
        btnRefresh.setText("🔄 Refresh");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });
        summaryBar.add(btnRefresh);
        btnRefresh.setBounds(770, 12, 125, 36);

        getContentPane().add(summaryBar);
        summaryBar.setBounds(55, 110, 914, 60);

        tblCatalog.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        tblCatalog.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Product Name", "Category", "Price", "Stock Qty", "Threshold", "Stock Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblCatalog.setRowHeight(28);
        scrollCatalog.setViewportView(tblCatalog);

        getContentPane().add(scrollCatalog);
        scrollCatalog.setBounds(55, 180, 914, 360);

        btnBack.setBackground(new java.awt.Color(242, 100, 25));
        btnBack.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnBack.setForeground(new java.awt.Color(255, 255, 255));
        btnBack.setText("⬅ Back to Dashboard");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });
        getContentPane().add(btnBack);
        btnBack.setBounds(754, 552, 215, 44);

        lblBackground.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/backgrounds/catalog_bg.png"))); // NOI18N
        getContentPane().add(lblBackground);
        lblBackground.setBounds(0, 0, 1024, 616);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        loadCatalog();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        User current = AuthService.getInstance().getCurrentUser();
        if (current != null && current.getRole() == Role.CASHIER) {
            new Dashboard().setVisible(true);
        } else {
            new Managerpage().setVisible(true);
        }
        this.dispose();
    }//GEN-LAST:event_btnBackActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        PawMartTheme.setupFlatLaf();
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Viewpetsupllies().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JLabel lblBackground;
    private javax.swing.JLabel lblCatalogInfo;
    private javax.swing.JLabel lblHeaderIcon;
    private javax.swing.JLabel lblHeaderSub;
    private javax.swing.JLabel lblHeaderTitle;
    private javax.swing.JLabel lblTotalCount;
    private javax.swing.JScrollPane scrollCatalog;
    private javax.swing.JPanel summaryBar;
    private javax.swing.JTable tblCatalog;
    // End of variables declaration//GEN-END:variables
}
