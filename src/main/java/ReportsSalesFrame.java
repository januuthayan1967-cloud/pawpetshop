import com.pawmart.dao.SaleDAO;
import com.pawmart.model.DashboardStats;
import com.pawmart.model.Role;
import com.pawmart.model.Sale;
import com.pawmart.model.User;
import com.pawmart.service.AuthService;
import com.pawmart.service.ReportService;
import com.pawmart.ui.theme.PawMartTheme;
import com.pawmart.ui.views.ReceiptPreviewDialog;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * PawMart Pet Shop Management System - Sales History & Reports.
 * Fully editable in Apache NetBeans GUI Builder (Design View).
 */
public class ReportsSalesFrame extends javax.swing.JFrame {

    private final SaleDAO saleDAO = new SaleDAO();
    private final ReportService reportService = new ReportService();
    private List<Sale> currentSales = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    /**
     * Creates new form ReportsSalesFrame
     */
    public ReportsSalesFrame() {
        PawMartTheme.setupFlatLaf();
        initComponents();
        setSize(1060, 680);
        setLocationRelativeTo(null);

        // Scaled background photography
        PawMartTheme.applyScaledBackground(lblBackground, "/images/backgrounds/reports_background.jpg");
        lblBackground.setBounds(0, 0, 1060, 680);

        // Translucent stats bar
        PawMartTheme.styleTranslucentCard(statsPanel, 18, new java.awt.Color(255, 255, 255, 240), PawMartTheme.CARD_BORDER);
        statsPanel.setBounds(30, 95, 1000, 75);

        // Layout bounds
        scrollSales.setBounds(30, 180, 1000, 410);
        btnViewReceipt.setBounds(30, 600, 190, 38);
        btnBack.setBounds(230, 600, 150, 38);

        // Buttons Styling
        PawMartTheme.styleButton(btnRefreshSales, PawMartTheme.EMERALD_PRIMARY, java.awt.Color.WHITE, 10);
        PawMartTheme.styleButton(btnViewReceipt, PawMartTheme.EMERALD_PRIMARY, java.awt.Color.WHITE, 12);
        PawMartTheme.styleButton(btnBack, PawMartTheme.ORANGE_ACCENT, java.awt.Color.WHITE, 12);

        applyTableStyling();
        loadReportsData();
    }

    private void applyTableStyling() {
        PawMartTheme.styleTable(tblSales);
        if (tblSales.getColumnModel().getColumnCount() >= 7) {
            tblSales.getColumnModel().getColumn(0).setPreferredWidth(130); // Receipt #
            tblSales.getColumnModel().getColumn(1).setPreferredWidth(140); // Cashier
            tblSales.getColumnModel().getColumn(2).setPreferredWidth(140); // Date
            tblSales.getColumnModel().getColumn(3).setPreferredWidth(95);  // Subtotal
            tblSales.getColumnModel().getColumn(4).setPreferredWidth(85);  // Discount
            tblSales.getColumnModel().getColumn(5).setPreferredWidth(105); // Grand Total
            tblSales.getColumnModel().getColumn(6).setPreferredWidth(125); // Payment

            // Center align Receipt, Date, numeric columns, and payment
            javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            tblSales.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
            tblSales.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
            tblSales.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
            tblSales.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
            tblSales.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
            tblSales.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
        }
    }

    private void loadReportsData() {
        try {
            DashboardStats stats = reportService.getDashboardStats();
            lblStatRevenue.setText(String.format("$%.2f", stats.getTotalRevenue().doubleValue()));
            lblStatTransCount.setText(stats.getTotalSalesCount() + " Sales");

            currentSales = saleDAO.findAll();
            filterAndDisplaySales();
        } catch (Exception ex) {
            System.err.println("[ReportsSalesFrame] Error loading sales reports: " + ex.getMessage());
        }
    }

    private void filterAndDisplaySales() {
        String selectedMethod = (String) comboPaymentFilter.getSelectedItem();
        DefaultTableModel model = (DefaultTableModel) tblSales.getModel();
        model.setRowCount(0);

        for (Sale s : currentSales) {
            if (selectedMethod != null && !"All Payment Methods".equalsIgnoreCase(selectedMethod)) {
                if (!selectedMethod.equalsIgnoreCase(s.getPaymentMethod())) {
                    continue;
                }
            }

            model.addRow(new Object[]{
                s.getReceiptNumber(),
                s.getCashierName() != null ? s.getCashierName() : "Cashier #" + s.getCashierId(),
                s.getCreatedAt() != null ? dateFormat.format(s.getCreatedAt()) : "-",
                String.format("$%.2f", s.getSubtotal().doubleValue()),
                String.format("$%.2f", s.getDiscountAmount().doubleValue()),
                String.format("$%.2f", s.getGrandTotal().doubleValue()),
                s.getPaymentMethod()
            });
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
        statsPanel = new javax.swing.JPanel();
        lblCardRevTitle = new javax.swing.JLabel();
        lblStatRevenue = new javax.swing.JLabel();
        lblCardTransTitle = new javax.swing.JLabel();
        lblStatTransCount = new javax.swing.JLabel();
        lblCardMethodTitle = new javax.swing.JLabel();
        comboPaymentFilter = new javax.swing.JComboBox<>();
        btnRefreshSales = new javax.swing.JButton();
        scrollSales = new javax.swing.JScrollPane();
        tblSales = new javax.swing.JTable();
        btnViewReceipt = new javax.swing.JButton();
        btnBack = new javax.swing.JButton();
        lblBackground = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("PawMart - Sales History & Reports");
        setBackground(new java.awt.Color(250, 247, 242));
        setResizable(false);
        getContentPane().setLayout(null);

        headerPanel.setOpaque(false);
        headerPanel.setLayout(null);

        lblHeaderIcon.setFont(new java.awt.Font("Segoe UI Emoji", 1, 28)); // NOI18N
        lblHeaderIcon.setForeground(new java.awt.Color(242, 100, 25));
        lblHeaderIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHeaderIcon.setText("📊");
        headerPanel.add(lblHeaderIcon);
        lblHeaderIcon.setBounds(25, 18, 50, 48);

        lblHeaderTitle.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblHeaderTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblHeaderTitle.setText("Sales History & Financial Reports");
        headerPanel.add(lblHeaderTitle);
        lblHeaderTitle.setBounds(105, 18, 550, 32);

        lblHeaderSub.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblHeaderSub.setForeground(new java.awt.Color(209, 231, 221));
        lblHeaderSub.setText("Audit completed transactions, review gross revenue, and reprint receipts");
        headerPanel.add(lblHeaderSub);
        lblHeaderSub.setBounds(105, 50, 650, 20);

        getContentPane().add(headerPanel);
        headerPanel.setBounds(0, 0, 1024, 90);

        statsPanel.setBackground(new java.awt.Color(255, 255, 255));
        statsPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(226, 217, 204), 1, true));
        statsPanel.setLayout(null);

        lblCardRevTitle.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblCardRevTitle.setForeground(new java.awt.Color(5, 150, 105));
        lblCardRevTitle.setText("TOTAL REVENUE");
        statsPanel.add(lblCardRevTitle);
        lblCardRevTitle.setBounds(25, 12, 180, 16);

        lblStatRevenue.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblStatRevenue.setForeground(new java.awt.Color(5, 150, 105));
        lblStatRevenue.setText("$482.50");
        statsPanel.add(lblStatRevenue);
        lblStatRevenue.setBounds(25, 32, 180, 32);

        lblCardTransTitle.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblCardTransTitle.setForeground(new java.awt.Color(15, 76, 92));
        lblCardTransTitle.setText("TRANSACTIONS");
        statsPanel.add(lblCardTransTitle);
        lblCardTransTitle.setBounds(240, 12, 180, 16);

        lblStatTransCount.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblStatTransCount.setForeground(new java.awt.Color(15, 76, 92));
        lblStatTransCount.setText("8 Sales");
        statsPanel.add(lblStatTransCount);
        lblStatTransCount.setBounds(240, 32, 180, 32);

        lblCardMethodTitle.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblCardMethodTitle.setForeground(new java.awt.Color(107, 114, 128));
        lblCardMethodTitle.setText("FILTER PAYMENT");
        statsPanel.add(lblCardMethodTitle);
        lblCardMethodTitle.setBounds(455, 12, 180, 16);

        comboPaymentFilter.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        comboPaymentFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All Payment Methods", "CASH", "CREDIT_CARD", "DEBIT_CARD", "MOBILE_PAY" }));
        comboPaymentFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboPaymentFilterActionPerformed(evt);
            }
        });
        statsPanel.add(comboPaymentFilter);
        comboPaymentFilter.setBounds(455, 32, 220, 34);

        btnRefreshSales.setBackground(new java.awt.Color(5, 150, 105));
        btnRefreshSales.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnRefreshSales.setForeground(new java.awt.Color(255, 255, 255));
        btnRefreshSales.setText("🔄 Refresh");
        btnRefreshSales.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshSalesActionPerformed(evt);
            }
        });
        statsPanel.add(btnRefreshSales);
        btnRefreshSales.setBounds(740, 20, 145, 38);

        getContentPane().add(statsPanel);
        statsPanel.setBounds(55, 110, 914, 75);

        tblSales.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        tblSales.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Receipt #", "Cashier", "Date & Time", "Subtotal", "Discount", "Grand Total", "Payment"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblSales.setRowHeight(28);
        scrollSales.setViewportView(tblSales);

        getContentPane().add(scrollSales);
        scrollSales.setBounds(55, 195, 914, 345);

        btnViewReceipt.setBackground(new java.awt.Color(5, 150, 105));
        btnViewReceipt.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnViewReceipt.setForeground(new java.awt.Color(255, 255, 255));
        btnViewReceipt.setText("📄 View / Print Selected Receipt");
        btnViewReceipt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewReceiptActionPerformed(evt);
            }
        });
        getContentPane().add(btnViewReceipt);
        btnViewReceipt.setBounds(55, 552, 280, 44);

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

        lblBackground.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/backgrounds/reports_bg.png"))); // NOI18N
        getContentPane().add(lblBackground);
        lblBackground.setBounds(0, 0, 1024, 616);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void comboPaymentFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboPaymentFilterActionPerformed
        filterAndDisplaySales();
    }//GEN-LAST:event_comboPaymentFilterActionPerformed

    private void btnRefreshSalesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshSalesActionPerformed
        loadReportsData();
    }//GEN-LAST:event_btnRefreshSalesActionPerformed

    private void btnViewReceiptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewReceiptActionPerformed
        int selectedRow = tblSales.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a transaction row to view or print its receipt.", "Select Row", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String receiptNum = (String) tblSales.getValueAt(selectedRow, 0);
        try {
            Sale sale = saleDAO.findByReceiptNumber(receiptNum);
            if (sale != null) {
                ReceiptPreviewDialog dialog = new ReceiptPreviewDialog(this, sale);
                dialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Receipt not found in database.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error fetching receipt: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnViewReceiptActionPerformed

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
                new ReportsSalesFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnRefreshSales;
    private javax.swing.JButton btnViewReceipt;
    private javax.swing.JComboBox<String> comboPaymentFilter;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JLabel lblBackground;
    private javax.swing.JLabel lblCardMethodTitle;
    private javax.swing.JLabel lblCardRevTitle;
    private javax.swing.JLabel lblCardTransTitle;
    private javax.swing.JLabel lblHeaderIcon;
    private javax.swing.JLabel lblHeaderSub;
    private javax.swing.JLabel lblHeaderTitle;
    private javax.swing.JLabel lblStatRevenue;
    private javax.swing.JLabel lblStatTransCount;
    private javax.swing.JScrollPane scrollSales;
    private javax.swing.JPanel statsPanel;
    private javax.swing.JTable tblSales;
    // End of variables declaration//GEN-END:variables
}
