import com.pawmart.model.Category;
import com.pawmart.model.Product;
import com.pawmart.model.Role;
import com.pawmart.model.User;
import com.pawmart.service.AuthService;
import com.pawmart.service.CategoryService;
import com.pawmart.service.ProductService;
import com.pawmart.ui.theme.PawMartTheme;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 * PawMart Pet Shop Management System - Search Pet Supplies.
 * Fully editable in Apache NetBeans GUI Builder (Design View).
 */
public class Searchpetsupplies extends javax.swing.JFrame {

    private final ProductService productService = new ProductService();
    private final CategoryService categoryService = new CategoryService();

    /**
     * Creates new form Searchpetsupplies
     */
    public Searchpetsupplies() {
        PawMartTheme.setupFlatLaf();
        initComponents();
        setSize(1060, 680);
        setLocationRelativeTo(null);

        // Scaled background photography
        PawMartTheme.applyScaledBackground(lblBackground, "/images/backgrounds/search_pet_supplies_background.jpg");
        lblBackground.setBounds(0, 0, 1060, 680);

        // Filter Card styling
        PawMartTheme.styleTranslucentCard(filterPanel, 16, new java.awt.Color(255, 255, 255, 240), PawMartTheme.CARD_BORDER);
        filterPanel.setBounds(30, 95, 1000, 70);

        // Table & Scroll bounds
        scrollSearchResults.setBounds(30, 175, 1000, 415);
        btnBack.setBounds(30, 600, 190, 38);
        lblResultCount.setBounds(240, 600, 500, 38);
        lblResultCount.setForeground(java.awt.Color.WHITE);
        lblResultCount.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));

        // Paw vector brand icon
        lblHeaderIcon.setText("");
        lblHeaderIcon.setIcon(PawMartTheme.createPawIcon(28, 28, PawMartTheme.ORANGE_ACCENT));

        // Buttons Styling
        PawMartTheme.styleButton(btnSearch, PawMartTheme.EMERALD_PRIMARY, java.awt.Color.WHITE, 10);
        PawMartTheme.styleButton(btnReset, java.awt.Color.WHITE, PawMartTheme.TEAL_PRIMARY, 10);
        PawMartTheme.styleButton(btnBack, PawMartTheme.ORANGE_ACCENT, java.awt.Color.WHITE, 12);

        applyTableStyling();
        loadCategories();
        performSearch();
    }

    private void applyTableStyling() {
        PawMartTheme.styleTable(tblSearchResults);
        if (tblSearchResults.getColumnModel().getColumnCount() >= 6) {
            tblSearchResults.getColumnModel().getColumn(0).setPreferredWidth(55); // ID
            tblSearchResults.getColumnModel().getColumn(0).setMaxWidth(75);
            tblSearchResults.getColumnModel().getColumn(1).setPreferredWidth(320); // Product Name
            tblSearchResults.getColumnModel().getColumn(2).setPreferredWidth(160); // Category
            tblSearchResults.getColumnModel().getColumn(3).setPreferredWidth(100); // Price
            tblSearchResults.getColumnModel().getColumn(4).setPreferredWidth(100); // Stock
            tblSearchResults.getColumnModel().getColumn(5).setPreferredWidth(130); // Status

            // Center align ID, Price, Stock
            javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            tblSearchResults.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
            tblSearchResults.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
            tblSearchResults.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

            // Styled Status column renderer
            tblSearchResults.getColumnModel().getColumn(5).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
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

    private void loadCategories() {
        try {
            List<Category> categories = categoryService.getAllCategories();
            comboFilterCategory.removeAllItems();
            comboFilterCategory.addItem("All Categories");
            for (Category cat : categories) {
                comboFilterCategory.addItem(cat.getName());
            }
        } catch (Exception ex) {
            System.err.println("[Searchpetsupplies] Error loading categories: " + ex.getMessage());
        }
    }

    private void performSearch() {
        String query = txtSearchQuery.getText().trim();
        String selectedCategory = (String) comboFilterCategory.getSelectedItem();

        Integer categoryId = null;
        if (selectedCategory != null && !"All Categories".equalsIgnoreCase(selectedCategory)) {
            try {
                List<Category> allCats = categoryService.getAllCategories();
                for (Category c : allCats) {
                    if (c.getName().equalsIgnoreCase(selectedCategory)) {
                        categoryId = c.getId();
                        break;
                    }
                }
            } catch (Exception ignored) {}
        }

        try {
            List<Product> results = productService.searchProducts(query.isEmpty() ? null : query, categoryId, true);
            DefaultTableModel model = (DefaultTableModel) tblSearchResults.getModel();
            model.setRowCount(0);

            for (Product p : results) {
                String status = p.getQuantity() <= p.getLowStockThreshold() ? "Low Stock" : "In Stock";
                model.addRow(new Object[]{
                    p.getId(),
                    p.getName(),
                    p.getCategoryName() != null ? p.getCategoryName() : "General",
                    String.format("$%.2f", p.getPrice().doubleValue()),
                    p.getQuantity() + " units",
                    status
                });
            }

            lblResultCount.setText(results.size() + " pet supplies found in catalog");
        } catch (Exception ex) {
            System.err.println("[Searchpetsupplies] Search error: " + ex.getMessage());
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
        filterPanel = new javax.swing.JPanel();
        lblFilterCategory = new javax.swing.JLabel();
        comboFilterCategory = new javax.swing.JComboBox<>();
        txtSearchQuery = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();
        scrollSearchResults = new javax.swing.JScrollPane();
        tblSearchResults = new javax.swing.JTable();
        btnBack = new javax.swing.JButton();
        lblResultCount = new javax.swing.JLabel();
        lblBackground = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("PawMart - Search Pet Supplies & Inventory");
        setBackground(new java.awt.Color(250, 247, 242));
        setResizable(false);
        getContentPane().setLayout(null);

        headerPanel.setOpaque(false);
        headerPanel.setLayout(null);

        lblHeaderIcon.setFont(new java.awt.Font("Segoe UI Emoji", 1, 28)); // NOI18N
        lblHeaderIcon.setForeground(new java.awt.Color(242, 100, 25));
        lblHeaderIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHeaderIcon.setText("");
        headerPanel.add(lblHeaderIcon);
        lblHeaderIcon.setBounds(25, 18, 50, 48);

        lblHeaderTitle.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblHeaderTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblHeaderTitle.setText("Search Pet Supplies & Inventory");
        headerPanel.add(lblHeaderTitle);
        lblHeaderTitle.setBounds(105, 18, 550, 32);

        lblHeaderSub.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblHeaderSub.setForeground(new java.awt.Color(209, 231, 221));
        lblHeaderSub.setText("Real-time product lookup across all pet categories, prices, and stock");
        headerPanel.add(lblHeaderSub);
        lblHeaderSub.setBounds(105, 50, 650, 20);

        getContentPane().add(headerPanel);
        headerPanel.setBounds(0, 0, 1024, 90);

        filterPanel.setBackground(new java.awt.Color(255, 255, 255));
        filterPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(226, 217, 204), 1, true));
        filterPanel.setLayout(null);

        lblFilterCategory.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblFilterCategory.setForeground(new java.awt.Color(16, 34, 56));
        lblFilterCategory.setText("Category Filter:");
        filterPanel.add(lblFilterCategory);
        lblFilterCategory.setBounds(20, 22, 110, 24);

        comboFilterCategory.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        comboFilterCategory.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All Categories", "Dogs", "Cats", "Birds", "Fish & Aquatic", "Pet Food & Treats", "Toys & Enrichment", "Beds & Furniture", "Healthcare & Wellness" }));
        filterPanel.add(comboFilterCategory);
        comboFilterCategory.setBounds(135, 16, 220, 36);

        txtSearchQuery.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtSearchQuery.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchQueryActionPerformed(evt);
            }
        });
        filterPanel.add(txtSearchQuery);
        txtSearchQuery.setBounds(370, 16, 280, 36);

        btnSearch.setBackground(new java.awt.Color(5, 150, 105));
        btnSearch.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnSearch.setForeground(new java.awt.Color(255, 255, 255));
        btnSearch.setText("Search");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });
        filterPanel.add(btnSearch);
        btnSearch.setBounds(665, 16, 115, 36);

        btnReset.setBackground(new java.awt.Color(255, 255, 255));
        btnReset.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnReset.setForeground(new java.awt.Color(15, 76, 92));
        btnReset.setText("Show All");
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });
        filterPanel.add(btnReset);
        btnReset.setBounds(790, 16, 105, 36);

        getContentPane().add(filterPanel);
        filterPanel.setBounds(55, 110, 914, 68);

        tblSearchResults.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        tblSearchResults.setModel(new javax.swing.table.DefaultTableModel(
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
        tblSearchResults.setRowHeight(28);
        scrollSearchResults.setViewportView(tblSearchResults);

        getContentPane().add(scrollSearchResults);
        scrollSearchResults.setBounds(55, 190, 914, 350);

        btnBack.setBackground(new java.awt.Color(242, 100, 25));
        btnBack.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnBack.setForeground(new java.awt.Color(255, 255, 255));
        btnBack.setText("Back to Dashboard");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });
        getContentPane().add(btnBack);
        btnBack.setBounds(754, 552, 215, 44);

        lblResultCount.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblResultCount.setForeground(new java.awt.Color(15, 76, 92));
        lblResultCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblResultCount.setText("Live database results");
        getContentPane().add(lblResultCount);
        lblResultCount.setBounds(55, 555, 600, 35);

        lblBackground.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/backgrounds/search_bg.png"))); // NOI18N
        getContentPane().add(lblBackground);
        lblBackground.setBounds(0, 0, 1024, 616);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtSearchQueryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchQueryActionPerformed
        performSearch();
    }//GEN-LAST:event_txtSearchQueryActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        performSearch();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        txtSearchQuery.setText("");
        if (comboFilterCategory.getItemCount() > 0) {
            comboFilterCategory.setSelectedIndex(0);
        }
        performSearch();
    }//GEN-LAST:event_btnResetActionPerformed

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
                new Searchpetsupplies().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnSearch;
    private javax.swing.JComboBox<String> comboFilterCategory;
    private javax.swing.JPanel filterPanel;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JLabel lblBackground;
    private javax.swing.JLabel lblFilterCategory;
    private javax.swing.JLabel lblHeaderIcon;
    private javax.swing.JLabel lblHeaderSub;
    private javax.swing.JLabel lblHeaderTitle;
    private javax.swing.JLabel lblResultCount;
    private javax.swing.JScrollPane scrollSearchResults;
    private javax.swing.JTable tblSearchResults;
    private javax.swing.JTextField txtSearchQuery;
    // End of variables declaration//GEN-END:variables
}
