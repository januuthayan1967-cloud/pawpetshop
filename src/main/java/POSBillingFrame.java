import com.pawmart.model.CartItem;
import com.pawmart.model.Product;
import com.pawmart.model.Role;
import com.pawmart.model.Sale;
import com.pawmart.model.User;
import com.pawmart.service.AuthService;
import com.pawmart.service.CheckoutService;
import com.pawmart.service.ProductService;
import com.pawmart.ui.theme.PawMartTheme;
import com.pawmart.ui.views.ReceiptPreviewDialog;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * PawMart Pet Shop Management System - POS Billing Terminal.
 * Fully editable in Apache NetBeans GUI Builder (Design View).
 */
public class POSBillingFrame extends javax.swing.JFrame {

    private final ProductService productService = new ProductService();
    private final CheckoutService checkoutService = new CheckoutService();
    private final List<CartItem> cartItems = new ArrayList<>();
    private List<Product> catalogProducts = new ArrayList<>();

    /**
     * Creates new form POSBillingFrame
     */
    public POSBillingFrame() {
        PawMartTheme.setupFlatLaf();
        initComponents();
        headerPanel.setOpaque(false);
        setSize(1140, 720);
        setLocationRelativeTo(null);

        // Scaled background photography
        PawMartTheme.applyScaledBackground(lblBackground, "/images/backgrounds/pos_billing_background.jpg");
        lblBackground.setBounds(0, 0, 1140, 720);

        // Translucent cards
        PawMartTheme.styleTranslucentCard(catalogCard, 18, new java.awt.Color(255, 255, 255, 240), PawMartTheme.CARD_BORDER);
        PawMartTheme.styleTranslucentCard(cartCard, 18, new java.awt.Color(255, 255, 255, 240), PawMartTheme.CARD_BORDER);
        PawMartTheme.styleTranslucentCard(summaryPanel, 14, new java.awt.Color(250, 247, 242, 245), PawMartTheme.CARD_BORDER);

        // Buttons Styling
        PawMartTheme.styleButton(btnAddToCart, PawMartTheme.EMERALD_PRIMARY, java.awt.Color.WHITE, 12);
        PawMartTheme.styleButton(btnCheckout, PawMartTheme.EMERALD_PRIMARY, java.awt.Color.WHITE, 12);
        PawMartTheme.styleButton(btnClearCart, PawMartTheme.STATUS_DANGER, java.awt.Color.WHITE, 10);
        PawMartTheme.styleButton(btnBack, PawMartTheme.ORANGE_ACCENT, java.awt.Color.WHITE, 12);
        PawMartTheme.styleButton(btnPOSSearch, java.awt.Color.WHITE, PawMartTheme.TEAL_PRIMARY, 10);

        applyTableStyling();
        loadCatalog();
    }

    private void applyTableStyling() {
        PawMartTheme.styleTable(tblCatalogPOS);
        if (tblCatalogPOS.getColumnModel().getColumnCount() >= 4) {
            tblCatalogPOS.getColumnModel().getColumn(0).setPreferredWidth(50);
            tblCatalogPOS.getColumnModel().getColumn(0).setMaxWidth(65);
            tblCatalogPOS.getColumnModel().getColumn(1).setPreferredWidth(230);
            tblCatalogPOS.getColumnModel().getColumn(2).setPreferredWidth(90);
            tblCatalogPOS.getColumnModel().getColumn(3).setPreferredWidth(75);

            javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            tblCatalogPOS.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
            tblCatalogPOS.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
            tblCatalogPOS.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        }

        PawMartTheme.styleTable(tblCart);
        if (tblCart.getColumnModel().getColumnCount() >= 4) {
            tblCart.getColumnModel().getColumn(0).setPreferredWidth(190);
            tblCart.getColumnModel().getColumn(1).setPreferredWidth(80);
            tblCart.getColumnModel().getColumn(2).setPreferredWidth(55);
            tblCart.getColumnModel().getColumn(3).setPreferredWidth(85);

            javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            tblCart.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
            tblCart.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
            tblCart.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        }
    }

    private void loadCatalog() {
        try {
            catalogProducts = productService.getAllProducts(true);
            populateCatalogTable(catalogProducts);
        } catch (Exception ex) {
            System.err.println("[POSBillingFrame] Error loading catalog: " + ex.getMessage());
        }
    }

    private void populateCatalogTable(List<Product> products) {
        DefaultTableModel model = (DefaultTableModel) tblCatalogPOS.getModel();
        model.setRowCount(0);
        for (Product p : products) {
            model.addRow(new Object[]{
                p.getId(),
                p.getName(),
                String.format("$%.2f", p.getPrice().doubleValue()),
                p.getQuantity()
            });
        }
    }

    private void updateCartDisplay() {
        DefaultTableModel model = (DefaultTableModel) tblCart.getModel();
        model.setRowCount(0);

        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            model.addRow(new Object[]{
                item.getProduct().getName(),
                String.format("$%.2f", item.getProduct().getPrice().doubleValue()),
                item.getQuantity(),
                String.format("$%.2f", item.getLineTotal().doubleValue())
            });
            subtotal = subtotal.add(item.getLineTotal());
        }

        BigDecimal tax = subtotal.multiply(new BigDecimal("0.08")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(tax);

        lblSubtotalVal.setText(String.format("$%.2f", subtotal.doubleValue()));
        lblTaxVal.setText(String.format("$%.2f", tax.doubleValue()));
        lblTotalVal.setText(String.format("$%.2f", total.doubleValue()));
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
        catalogCard = new javax.swing.JPanel();
        lblCatalogHeader = new javax.swing.JLabel();
        txtSearchPOS = new javax.swing.JTextField();
        btnPOSSearch = new javax.swing.JButton();
        scrollCatalogPOS = new javax.swing.JScrollPane();
        tblCatalogPOS = new javax.swing.JTable();
        btnAddToCart = new javax.swing.JButton();
        cartCard = new javax.swing.JPanel();
        lblCartHeader = new javax.swing.JLabel();
        btnClearCart = new javax.swing.JButton();
        scrollCart = new javax.swing.JScrollPane();
        tblCart = new javax.swing.JTable();
        summaryPanel = new javax.swing.JPanel();
        lblSubtotalText = new javax.swing.JLabel();
        lblSubtotalVal = new javax.swing.JLabel();
        lblTaxText = new javax.swing.JLabel();
        lblTaxVal = new javax.swing.JLabel();
        lblTotalText = new javax.swing.JLabel();
        lblTotalVal = new javax.swing.JLabel();
        btnCheckout = new javax.swing.JButton();
        btnBack = new javax.swing.JButton();
        lblBackground = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("PawMart - POS Billing Terminal");
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
        lblHeaderIcon.setBounds(35, 18, 45, 48);

        lblHeaderTitle.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblHeaderTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblHeaderTitle.setText("PawMart POS Billing Terminal");
        headerPanel.add(lblHeaderTitle);
        lblHeaderTitle.setBounds(90, 18, 550, 32);

        lblHeaderSub.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblHeaderSub.setForeground(new java.awt.Color(209, 231, 221));
        lblHeaderSub.setText("Fast cashier checkout, cart management, and instant receipt printing");
        headerPanel.add(lblHeaderSub);
        lblHeaderSub.setBounds(90, 50, 550, 20);

        getContentPane().add(headerPanel);
        headerPanel.setBounds(0, 0, 1120, 90);

        catalogCard.setBackground(new java.awt.Color(255, 255, 255));
        catalogCard.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(226, 217, 204), 1, true));
        catalogCard.setLayout(null);

        lblCatalogHeader.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblCatalogHeader.setForeground(new java.awt.Color(15, 76, 92));
        lblCatalogHeader.setText("1. Select Pet Supplies");
        catalogCard.add(lblCatalogHeader);
        lblCatalogHeader.setBounds(20, 15, 280, 24);

        txtSearchPOS.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        txtSearchPOS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchPOSActionPerformed(evt);
            }
        });
        catalogCard.add(txtSearchPOS);
        txtSearchPOS.setBounds(20, 48, 360, 36);

        btnPOSSearch.setBackground(new java.awt.Color(5, 150, 105));
        btnPOSSearch.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnPOSSearch.setForeground(new java.awt.Color(255, 255, 255));
        btnPOSSearch.setText("🔍 Find");
        btnPOSSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPOSSearchActionPerformed(evt);
            }
        });
        catalogCard.add(btnPOSSearch);
        btnPOSSearch.setBounds(390, 48, 110, 36);

        tblCatalogPOS.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        tblCatalogPOS.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Product", "Price", "Stock"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblCatalogPOS.setRowHeight(28);
        scrollCatalogPOS.setViewportView(tblCatalogPOS);

        catalogCard.add(scrollCatalogPOS);
        scrollCatalogPOS.setBounds(20, 96, 480, 395);

        btnAddToCart.setBackground(new java.awt.Color(15, 76, 92));
        btnAddToCart.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnAddToCart.setForeground(new java.awt.Color(255, 255, 255));
        btnAddToCart.setText("➕ Add Selected Item to Cart");
        btnAddToCart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddToCartActionPerformed(evt);
            }
        });
        catalogCard.add(btnAddToCart);
        btnAddToCart.setBounds(20, 502, 480, 40);

        getContentPane().add(catalogCard);
        catalogCard.setBounds(40, 110, 520, 555);

        cartCard.setBackground(new java.awt.Color(255, 255, 255));
        cartCard.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(226, 217, 204), 1, true));
        cartCard.setLayout(null);

        lblCartHeader.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblCartHeader.setForeground(new java.awt.Color(15, 76, 92));
        lblCartHeader.setText("2. Customer Cart");
        cartCard.add(lblCartHeader);
        lblCartHeader.setBounds(20, 15, 250, 24);

        btnClearCart.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnClearCart.setForeground(new java.awt.Color(242, 100, 25));
        btnClearCart.setText("🗑 Empty Cart");
        btnClearCart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearCartActionPerformed(evt);
            }
        });
        cartCard.add(btnClearCart);
        btnClearCart.setBounds(360, 12, 120, 30);

        tblCart.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        tblCart.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product", "Price", "Qty", "Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblCart.setRowHeight(28);
        scrollCart.setViewportView(tblCart);

        cartCard.add(scrollCart);
        scrollCart.setBounds(20, 48, 460, 255);

        summaryPanel.setBackground(new java.awt.Color(250, 247, 242));
        summaryPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(226, 217, 204), 1, true));
        summaryPanel.setLayout(null);

        lblSubtotalText.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblSubtotalText.setForeground(new java.awt.Color(16, 34, 56));
        lblSubtotalText.setText("Subtotal:");
        summaryPanel.add(lblSubtotalText);
        lblSubtotalText.setBounds(20, 12, 120, 20);

        lblSubtotalVal.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblSubtotalVal.setForeground(new java.awt.Color(16, 34, 56));
        lblSubtotalVal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblSubtotalVal.setText("$0.00");
        summaryPanel.add(lblSubtotalVal);
        lblSubtotalVal.setBounds(300, 12, 140, 20);

        lblTaxText.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblTaxText.setForeground(new java.awt.Color(16, 34, 56));
        lblTaxText.setText("Tax (8%):");
        summaryPanel.add(lblTaxText);
        lblTaxText.setBounds(20, 40, 120, 20);

        lblTaxVal.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblTaxVal.setForeground(new java.awt.Color(16, 34, 56));
        lblTaxVal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTaxVal.setText("$0.00");
        summaryPanel.add(lblTaxVal);
        lblTaxVal.setBounds(300, 40, 140, 20);

        lblTotalText.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblTotalText.setForeground(new java.awt.Color(15, 76, 92));
        lblTotalText.setText("Total Due:");
        summaryPanel.add(lblTotalText);
        lblTotalText.setBounds(20, 75, 120, 28);

        lblTotalVal.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblTotalVal.setForeground(new java.awt.Color(5, 150, 105));
        lblTotalVal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotalVal.setText("$0.00");
        summaryPanel.add(lblTotalVal);
        lblTotalVal.setBounds(260, 72, 180, 32);

        cartCard.add(summaryPanel);
        summaryPanel.setBounds(20, 315, 460, 120);

        btnCheckout.setBackground(new java.awt.Color(5, 150, 105));
        btnCheckout.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnCheckout.setForeground(new java.awt.Color(255, 255, 255));
        btnCheckout.setText("✔ Complete Sale & Print Receipt");
        btnCheckout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCheckoutActionPerformed(evt);
            }
        });
        cartCard.add(btnCheckout);
        btnCheckout.setBounds(20, 445, 460, 45);

        btnBack.setBackground(new java.awt.Color(251, 139, 36));
        btnBack.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnBack.setForeground(new java.awt.Color(255, 255, 255));
        btnBack.setText("⬅ Close POS Terminal");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });
        cartCard.add(btnBack);
        btnBack.setBounds(20, 500, 460, 40);

        getContentPane().add(cartCard);
        cartCard.setBounds(580, 110, 500, 555);

        lblBackground.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/backgrounds/pos_terminal_bg.png"))); // NOI18N
        getContentPane().add(lblBackground);
        lblBackground.setBounds(0, 0, 1120, 700);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txtSearchPOSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchPOSActionPerformed
        btnPOSSearchActionPerformed(evt);
    }//GEN-LAST:event_txtSearchPOSActionPerformed

    private void btnPOSSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPOSSearchActionPerformed
        String query = txtSearchPOS.getText().trim();
        try {
            List<Product> filtered = productService.searchProducts(query.isEmpty() ? null : query, null, true);
            populateCatalogTable(filtered);
        } catch (Exception ex) {
            System.err.println("[POSBillingFrame] Search error: " + ex.getMessage());
        }
    }//GEN-LAST:event_btnPOSSearchActionPerformed

    private void btnAddToCartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddToCartActionPerformed
        int selectedRow = tblCatalogPOS.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an item from the left table to add to the cart.", "Select Item", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int productId = (Integer) tblCatalogPOS.getValueAt(selectedRow, 0);
        Product selectedProduct = null;
        for (Product p : catalogProducts) {
            if (p.getId() == productId) {
                selectedProduct = p;
                break;
            }
        }

        if (selectedProduct == null) return;

        if (selectedProduct.getQuantity() <= 0) {
            JOptionPane.showMessageDialog(this, "Selected product is currently out of stock!", "Out of Stock", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if already in cart
        CartItem existing = null;
        for (CartItem ci : cartItems) {
            if (ci.getProduct().getId() == productId) {
                existing = ci;
                break;
            }
        }

        if (existing != null) {
            if (existing.getQuantity() + 1 > selectedProduct.getQuantity()) {
                JOptionPane.showMessageDialog(this, "Cannot add more. Exceeds available stock (" + selectedProduct.getQuantity() + ").", "Stock Limit", JOptionPane.WARNING_MESSAGE);
                return;
            }
            existing.setQuantity(existing.getQuantity() + 1);
        } else {
            cartItems.add(new CartItem(selectedProduct, 1));
        }

        updateCartDisplay();
    }//GEN-LAST:event_btnAddToCartActionPerformed

    private void btnClearCartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearCartActionPerformed
        cartItems.clear();
        updateCartDisplay();
    }//GEN-LAST:event_btnClearCartActionPerformed

    private void btnCheckoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCheckoutActionPerformed
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "The shopping cart is empty. Add products before checking out.", "Empty Cart", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User current = AuthService.getInstance().getCurrentUser();
        if (current == null) {
            current = new User(1, "Cashier Staff", "cashier", "cashier@pawmart.com", "", Role.CASHIER, "ACTIVE");
        }

        try {
            Sale completedSale = checkoutService.processCheckout(current, cartItems, BigDecimal.ZERO, "CASH");
            JOptionPane.showMessageDialog(this,
                "✓ Sale completed successfully!\nReceipt Number: " + completedSale.getReceiptNumber() + "\nTotal: $" + completedSale.getGrandTotal(),
                "Checkout Completed",
                JOptionPane.INFORMATION_MESSAGE);

            // Open Receipt Preview
            ReceiptPreviewDialog dialog = new ReceiptPreviewDialog(this, completedSale);
            dialog.setVisible(true);

            cartItems.clear();
            updateCartDisplay();
            loadCatalog();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Checkout error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnCheckoutActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnBackActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        PawMartTheme.setupFlatLaf();
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new POSBillingFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddToCart;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnCheckout;
    private javax.swing.JButton btnClearCart;
    private javax.swing.JButton btnPOSSearch;
    private javax.swing.JPanel cartCard;
    private javax.swing.JPanel catalogCard;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JLabel lblBackground;
    private javax.swing.JLabel lblCartHeader;
    private javax.swing.JLabel lblCatalogHeader;
    private javax.swing.JLabel lblHeaderIcon;
    private javax.swing.JLabel lblHeaderSub;
    private javax.swing.JLabel lblHeaderTitle;
    private javax.swing.JLabel lblSubtotalText;
    private javax.swing.JLabel lblSubtotalVal;
    private javax.swing.JLabel lblTaxText;
    private javax.swing.JLabel lblTaxVal;
    private javax.swing.JLabel lblTotalText;
    private javax.swing.JLabel lblTotalVal;
    private javax.swing.JScrollPane scrollCart;
    private javax.swing.JScrollPane scrollCatalogPOS;
    private javax.swing.JPanel summaryPanel;
    private javax.swing.JTable tblCart;
    private javax.swing.JTable tblCatalogPOS;
    private javax.swing.JTextField txtSearchPOS;
    // End of variables declaration//GEN-END:variables
}
