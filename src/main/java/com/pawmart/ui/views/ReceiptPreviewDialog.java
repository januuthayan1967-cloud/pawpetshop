package com.pawmart.ui.views;

import com.pawmart.model.Sale;
import com.pawmart.service.ReceiptService;
import com.pawmart.ui.components.PrimaryButton;
import com.pawmart.ui.components.RoundedPanel;
import com.pawmart.ui.components.SecondaryButton;
import com.pawmart.ui.theme.PawMartTheme;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;

/**
 * Modern modal dialog for viewing receipts, printing, and exporting to PDF.
 */
public class ReceiptPreviewDialog extends JDialog {

    private final Sale sale;
    private final ReceiptService receiptService;
    private JTextArea txtReceipt;

    public ReceiptPreviewDialog(JFrame parent, Sale sale) {
        super(parent, "PawMart - Sales Receipt: " + sale.getReceiptNumber(), true);
        this.sale = sale;
        this.receiptService = new ReceiptService();

        setSize(520, 640);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(PawMartTheme.BG_CREAM);
        setLayout(new BorderLayout());

        initComponents();
    }

    private void initComponents() {
        JPanel wrapper = new JPanel(new BorderLayout(10, 10));
        wrapper.setBackground(PawMartTheme.BG_CREAM);
        wrapper.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        // Top Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Sales Receipt Preview");
        lblTitle.setFont(PawMartTheme.FONT_TITLE_MEDIUM);
        lblTitle.setForeground(PawMartTheme.TEAL_PRIMARY);

        JLabel lblSub = new JLabel("Transaction completed successfully");
        lblSub.setFont(PawMartTheme.FONT_SMALL);
        lblSub.setForeground(PawMartTheme.STATUS_ACTIVE);

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSub, BorderLayout.SOUTH);
        wrapper.add(headerPanel, BorderLayout.NORTH);

        // Center Receipt Card
        JPanel card = new RoundedPanel(14, Color.WHITE, PawMartTheme.CARD_BORDER, true);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtReceipt = new JTextArea();
        txtReceipt.setEditable(false);
        txtReceipt.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        txtReceipt.setBackground(new Color(254, 252, 248));
        txtReceipt.setForeground(PawMartTheme.TEXT_DARK);
        txtReceipt.setText(receiptService.generateReceiptText(sale));
        txtReceipt.setCaretPosition(0);

        JScrollPane scroll = new JScrollPane(txtReceipt);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        card.add(scroll, BorderLayout.CENTER);

        wrapper.add(card, BorderLayout.CENTER);

        // Bottom Actions Row
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottomBar.setOpaque(false);

        SecondaryButton btnClose = new SecondaryButton("Close");
        btnClose.addActionListener(e -> dispose());

        SecondaryButton btnExportPdf = new SecondaryButton("Export PDF");
        btnExportPdf.addActionListener(e -> exportPdf());

        PrimaryButton btnPrint = new PrimaryButton("Print Receipt");
        btnPrint.addActionListener(e -> printReceipt());

        bottomBar.add(btnClose);
        bottomBar.add(btnExportPdf);
        bottomBar.add(btnPrint);

        wrapper.add(bottomBar, BorderLayout.SOUTH);
        add(wrapper, BorderLayout.CENTER);
    }

    private void printReceipt() {
        boolean ok = receiptService.printReceipt(sale);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Receipt sent to printer successfully.", "Printed", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void exportPdf() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(sale.getReceiptNumber() + ".pdf"));
        chooser.setFileFilter(new FileNameExtensionFilter("PDF Documents (*.pdf)", "pdf"));

        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".pdf")) {
                file = new File(file.getParentFile(), file.getName() + ".pdf");
            }
            try {
                receiptService.exportToPdf(sale, file);
                JOptionPane.showMessageDialog(this, "PDF receipt exported successfully to:\n" + file.getAbsolutePath(), "Export Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error exporting PDF: " + e.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
