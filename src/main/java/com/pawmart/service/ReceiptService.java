package com.pawmart.service;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.pawmart.model.Sale;
import com.pawmart.model.SaleItem;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Service for Receipt Formatting, OpenPDF export, and Hardware Printing.
 */
public class ReceiptService {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Generates a beautifully formatted text receipt for display and console/dot-matrix printing.
     */
    public String generateReceiptText(Sale sale) {
        StringBuilder sb = new StringBuilder();
        String dateStr = sale.getCreatedAt() != null ? DATE_FORMAT.format(sale.getCreatedAt()) : DATE_FORMAT.format(new Date());

        sb.append("========================================================\n");
        sb.append("               PAWMART PET SHOP & CARE                 \n");
        sb.append("         124 Pet Paradise Blvd, Animal City             \n");
        sb.append("              Tel: +1 (800) 555-PAWS                   \n");
        sb.append("========================================================\n");
        sb.append(String.format(" Receipt No : %-40s\n", sale.getReceiptNumber()));
        sb.append(String.format(" Date/Time  : %-40s\n", dateStr));
        sb.append(String.format(" Cashier        : %-35s\n", sale.getCashierName() != null ? sale.getCashierName() : "Store Staff"));
        sb.append(String.format(" Payment Method : %-35s\n", com.pawmart.ui.theme.PawMartTheme.formatPaymentMethod(sale.getPaymentMethod())));
        sb.append("--------------------------------------------------------\n");
        sb.append(String.format(" %-26s %5s %9s %10s\n", "Item", "Qty", "Price", "Total"));
        sb.append("--------------------------------------------------------\n");

        if (sale.getItems() != null) {
            for (SaleItem item : sale.getItems()) {
                String name = item.getProductName();
                if (name.length() > 25) {
                    name = name.substring(0, 22) + "...";
                }
                sb.append(String.format(" %-26s %5d %9.2f %10.2f\n",
                        name, item.getQuantity(), item.getUnitPrice(), item.getLineTotal()));
            }
        }

        sb.append("--------------------------------------------------------\n");
        sb.append(String.format(" Subtotal                                      $%8.2f\n", sale.getSubtotal()));
        sb.append(String.format(" Discount                                     -$%8.2f\n", sale.getDiscountAmount()));
        sb.append("========================================================\n");
        sb.append(String.format(" GRAND TOTAL                                   $%8.2f\n", sale.getGrandTotal()));
        if ("CASH".equalsIgnoreCase(sale.getPaymentMethod()) && sale.getAmountReceived() != null) {
            sb.append(String.format(" Cash Tendered                                 $%8.2f\n", sale.getAmountReceived()));
            if (sale.getChangeAmount() != null) {
                sb.append(String.format(" Change Due                                    $%8.2f\n", sale.getChangeAmount()));
            }
        }
        sb.append("========================================================\n");
        sb.append("             Thank you for shopping at PawMart!         \n");
        sb.append("               Woofs, Purrs & Warm Tails!               \n");
        sb.append("        Returns accepted within 14 days with receipt    \n");
        sb.append("========================================================\n");

        return sb.toString();
    }

    /**
     * Exports a formal PDF receipt using OpenPDF.
     * @param sale completed sale object
     * @param targetFile destination PDF file
     */
    public void exportToPdf(Sale sale, File targetFile) throws Exception {
        Document document = new Document(PageSize.A5, 36, 36, 36, 36);
        PdfWriter.getInstance(document, new FileOutputStream(targetFile));
        document.open();

        // Palette
        Color primaryTeal = new Color(15, 76, 92);
        Color accentOrange = new Color(242, 100, 25);
        Color textDark = new Color(30, 41, 59);

        Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD, primaryTeal);
        Font subtitleFont = new Font(Font.HELVETICA, 10, Font.NORMAL, new Color(100, 116, 139));
        Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
        Font bodyFont = new Font(Font.HELVETICA, 9, Font.NORMAL, textDark);
        Font boldFont = new Font(Font.HELVETICA, 9, Font.BOLD, textDark);
        Font totalFont = new Font(Font.HELVETICA, 14, Font.BOLD, accentOrange);

        // Header Title
        Paragraph pTitle = new Paragraph("PAWMART PET SHOP", titleFont);
        pTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(pTitle);

        Paragraph pSubtitle = new Paragraph("Premium Pet Nutrition, Toys, Accessories & Care\nTel: +1 (800) 555-PAWS | www.pawmartpetshop.com", subtitleFont);
        pSubtitle.setAlignment(Element.ALIGN_CENTER);
        pSubtitle.setSpacingAfter(15);
        document.add(pSubtitle);

        // Receipt Meta Table
        PdfPTable metaTable = new PdfPTable(2);
        metaTable.setWidthPercentage(100);
        metaTable.setSpacingAfter(12);

        String dateStr = sale.getCreatedAt() != null ? DATE_FORMAT.format(sale.getCreatedAt()) : DATE_FORMAT.format(new Date());

        addMetaCell(metaTable, "Receipt No: " + sale.getReceiptNumber(), bodyFont, Element.ALIGN_LEFT);
        addMetaCell(metaTable, "Date: " + dateStr, bodyFont, Element.ALIGN_RIGHT);
        addMetaCell(metaTable, "Cashier: " + (sale.getCashierName() != null ? sale.getCashierName() : "Store Staff"), bodyFont, Element.ALIGN_LEFT);
        addMetaCell(metaTable, "Payment: " + com.pawmart.ui.theme.PawMartTheme.formatPaymentMethod(sale.getPaymentMethod()), bodyFont, Element.ALIGN_RIGHT);

        document.add(metaTable);

        // Line Items Table
        PdfPTable table = new PdfPTable(new float[]{4.5f, 1.2f, 2.0f, 2.3f});
        table.setWidthPercentage(100);

        // Headers
        addHeaderCell(table, "Product Description", headerFont, primaryTeal);
        addHeaderCell(table, "Qty", headerFont, primaryTeal);
        addHeaderCell(table, "Price", headerFont, primaryTeal);
        addHeaderCell(table, "Total", headerFont, primaryTeal);

        // Data Rows
        boolean alternate = false;
        if (sale.getItems() != null) {
            for (SaleItem item : sale.getItems()) {
                Color rowBg = alternate ? new Color(248, 250, 252) : Color.WHITE;
                alternate = !alternate;

                addRowCell(table, item.getProductName(), bodyFont, Element.ALIGN_LEFT, rowBg);
                addRowCell(table, String.valueOf(item.getQuantity()), bodyFont, Element.ALIGN_CENTER, rowBg);
                addRowCell(table, "$" + item.getUnitPrice().toString(), bodyFont, Element.ALIGN_RIGHT, rowBg);
                addRowCell(table, "$" + item.getLineTotal().toString(), bodyFont, Element.ALIGN_RIGHT, rowBg);
            }
        }
        document.add(table);

        // Totals Section
        Paragraph pSpace = new Paragraph(" ");
        pSpace.setSpacingAfter(8);
        document.add(pSpace);

        PdfPTable totalsTable = new PdfPTable(new float[]{6f, 4f});
        totalsTable.setWidthPercentage(100);

        addMetaCell(totalsTable, "Subtotal:", boldFont, Element.ALIGN_RIGHT);
        addMetaCell(totalsTable, "$" + sale.getSubtotal(), boldFont, Element.ALIGN_RIGHT);

        addMetaCell(totalsTable, "Discount:", boldFont, Element.ALIGN_RIGHT);
        addMetaCell(totalsTable, "-$" + sale.getDiscountAmount(), boldFont, Element.ALIGN_RIGHT);

        addMetaCell(totalsTable, "GRAND TOTAL:", totalFont, Element.ALIGN_RIGHT);
        addMetaCell(totalsTable, "$" + sale.getGrandTotal(), totalFont, Element.ALIGN_RIGHT);

        if ("CASH".equalsIgnoreCase(sale.getPaymentMethod()) && sale.getAmountReceived() != null) {
            addMetaCell(totalsTable, "Cash Tendered:", boldFont, Element.ALIGN_RIGHT);
            addMetaCell(totalsTable, "$" + sale.getAmountReceived(), boldFont, Element.ALIGN_RIGHT);
            if (sale.getChangeAmount() != null) {
                addMetaCell(totalsTable, "Change Due:", boldFont, Element.ALIGN_RIGHT);
                addMetaCell(totalsTable, "$" + sale.getChangeAmount(), boldFont, Element.ALIGN_RIGHT);
            }
        }

        document.add(totalsTable);

        // Footer Message
        Paragraph pFooter = new Paragraph("\nThank you for shopping at PawMart!\nWoofs, Purrs & Warm Tails!", subtitleFont);
        pFooter.setAlignment(Element.ALIGN_CENTER);
        pFooter.setSpacingBefore(15);
        document.add(pFooter);

        document.close();
    }

    private void addHeaderCell(PdfPTable table, String text, Font font, Color bg) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bg);
        cell.setPadding(6);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private void addRowCell(PdfPTable table, String text, Font font, int alignment, Color bg) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bg);
        cell.setPadding(6);
        cell.setHorizontalAlignment(alignment);
        table.addCell(cell);
    }

    private void addMetaCell(PdfPTable table, String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(2);
        table.addCell(cell);
    }

    /**
     * Prints the receipt to the default or selected printer.
     */
    public boolean printReceipt(Sale sale) {
        PrinterJob job = PrinterJob.getPrinterJob();
        String receiptContent = generateReceiptText(sale);

        job.setPrintable(new Printable() {
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                if (pageIndex > 0) {
                    return NO_SUCH_PAGE;
                }
                Graphics2D g2d = (Graphics2D) graphics;
                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                g2d.setFont(new java.awt.Font(java.awt.Font.MONOSPACED, java.awt.Font.PLAIN, 9));

                int y = 20;
                for (String line : receiptContent.split("\n")) {
                    g2d.drawString(line, 10, y);
                    y += 12;
                }
                return PAGE_EXISTS;
            }
        });

        if (job.printDialog()) {
            try {
                job.print();
                return true;
            } catch (PrinterException e) {
                System.err.println("[ReceiptService] Print error: " + e.getMessage());
            }
        }
        return false;
    }
}
