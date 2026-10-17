package com.pawmart.ui.theme;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Design system, color tokens, and GUI styling engine for PawMart Pet Shop Management System.
 * Fully compatible with Apache NetBeans GUI Builder.
 */
public class PawMartTheme {

    // Backgrounds & Cards
    public static final Color BG_CREAM = new Color(250, 247, 242);
    public static final Color BG_CARD_TRANSLUCENT = new Color(255, 255, 255, 235);
    public static final Color BG_CARD_CREAM_TRANSLUCENT = new Color(250, 247, 242, 230);
    public static final Color CARD_WHITE = Color.WHITE;
    public static final Color CARD_BORDER = new Color(226, 217, 204);
    public static final Color BORDER_LIGHT = new Color(226, 232, 240);

    // Deep Teal Palette (Primary Brand)
    public static final Color TEAL_PRIMARY = new Color(15, 76, 92);
    public static final Color TEAL_DARK = new Color(9, 47, 57);
    public static final Color TEAL_HOVER = new Color(24, 88, 104);
    public static final Color TEAL_SELECTED = new Color(29, 106, 125);
    public static final Color TEAL_LIGHT = new Color(230, 240, 242);

    // Emerald Green Palette (Actions / Buttons)
    public static final Color EMERALD_PRIMARY = new Color(5, 150, 105);
    public static final Color EMERALD_HOVER = new Color(4, 120, 87);
    public static final Color EMERALD_LIGHT = new Color(209, 250, 229);

    // Warm Orange / Amber Accents
    public static final Color ORANGE_ACCENT = new Color(242, 100, 25);
    public static final Color AMBER_ACCENT = new Color(251, 139, 36);
    public static final Color ORANGE_LIGHT = new Color(254, 242, 232);

    // Neutral Text Colors
    public static final Color TEXT_DARK = new Color(16, 34, 56);
    public static final Color TEXT_MUTED = new Color(100, 116, 139);
    public static final Color TEXT_LIGHT = new Color(148, 163, 184);

    // Status Colors
    public static final Color STATUS_ACTIVE = new Color(16, 185, 129);
    public static final Color STATUS_INACTIVE = new Color(148, 163, 184);
    public static final Color STATUS_WARNING = new Color(245, 158, 11);
    public static final Color STATUS_DANGER = new Color(239, 68, 68);

    // Modern Fonts (Segoe UI with fallbacks)
    public static final Font FONT_BRAND_LOGO = new Font("Segoe UI", Font.BOLD, 38);
    public static final Font FONT_HEADER_TITLE = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_TITLE_LARGE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_TITLE_MEDIUM = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_SMALL_BOLD = new Font("Segoe UI", Font.BOLD, 11);

    // Image Caches
    private static final Map<String, BufferedImage> ORIGINAL_IMAGES = new ConcurrentHashMap<>();
    private static final Map<String, ImageIcon> SCALED_ICONS = new ConcurrentHashMap<>();

    /**
     * Installs FlatLaf modern Look & Feel with PawMart theme configurations.
     */
    public static void setupFlatLaf() {
        try {
            FlatLightLaf.setup();

            // Customize global FlatLaf properties
            UIManager.put("Button.arc", 14);
            UIManager.put("Component.arc", 12);
            UIManager.put("ProgressBar.arc", 10);
            UIManager.put("TextComponent.arc", 12);
            UIManager.put("ScrollBar.showButtons", false);
            UIManager.put("ScrollBar.width", 10);
            UIManager.put("ScrollBar.thumbArc", 10);

            UIManager.put("Table.selectionBackground", new Color(209, 231, 221));
            UIManager.put("Table.selectionForeground", TEAL_PRIMARY);
            UIManager.put("Table.rowHeight", 34);
            UIManager.put("Table.gridColor", new Color(240, 240, 240));

            UIManager.put("Component.focusWidth", 1);
            UIManager.put("Component.innerFocusWidth", 0);
        } catch (Exception e) {
            System.err.println("[PawMartTheme] Could not initialize FlatLaf: " + e.getMessage());
        }
    }

    /**
     * Safely load and scale background photography using aspect-ratio-preserving
     * crop-to-fill scaling. Caches images and dynamically updates on resize.
     */
    public static void applyScaledBackground(JLabel label, String resourcePath) {
        if (label == null || resourcePath == null || resourcePath.trim().isEmpty()) {
            return;
        }

        // Initial setup
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);

        // Helper to update scaled image icon
        Runnable updateIcon = () -> {
            int w = label.getWidth();
            int h = label.getHeight();
            if (w <= 0 || h <= 0) {
                Dimension pref = label.getPreferredSize();
                w = pref != null && pref.width > 0 ? pref.width : 1024;
                h = pref != null && pref.height > 0 ? pref.height : 640;
            }

            ImageIcon scaledIcon = getScaledIcon(resourcePath, w, h);
            if (scaledIcon != null) {
                label.setIcon(scaledIcon);
            }
        };

        // Run immediately if component has size, or post to event queue
        if (label.getWidth() > 0 && label.getHeight() > 0) {
            updateIcon.run();
        } else {
            SwingUtilities.invokeLater(updateIcon);
        }

        // Attach resize listener to maintain crisp crop-to-fill scaling
        label.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateIcon.run();
            }
        });

        // Automatically adapt to parent resizing (e.g. 1366x768, 1440x900, 1920x1080)
        label.addHierarchyListener(e -> {
            Container parent = label.getParent();
            if (parent != null) {
                try {
                    parent.setComponentZOrder(label, parent.getComponentCount() - 1);
                } catch (Exception ignored) {}

                parent.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent ce) {
                        int pw = parent.getWidth();
                        int ph = parent.getHeight();
                        if (pw > 0 && ph > 0) {
                            label.setBounds(0, 0, pw, ph);
                        }
                    }
                });
                int pw = parent.getWidth();
                int ph = parent.getHeight();
                if (pw > 0 && ph > 0) {
                    label.setBounds(0, 0, pw, ph);
                }
            }
        });
    }

    /**
     * Retrieve or compute scaled image icon using crop-to-fill.
     */
    public static ImageIcon getScaledIcon(String resourcePath, int targetW, int targetH) {
        if (targetW <= 0 || targetH <= 0) return null;
        String cacheKey = resourcePath + "@" + targetW + "x" + targetH;
        ImageIcon cached = SCALED_ICONS.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        BufferedImage orig = ORIGINAL_IMAGES.computeIfAbsent(resourcePath, path -> {
            try {
                URL res = PawMartTheme.class.getResource(path);
                if (res != null) {
                    return ImageIO.read(res);
                }
                InputStream is = PawMartTheme.class.getResourceAsStream(path);
                if (is != null) {
                    return ImageIO.read(is);
                }
            } catch (Exception ex) {
                System.err.println("[PawMartTheme] Could not load image " + path + ": " + ex.getMessage());
            }
            return null;
        });

        if (orig == null) {
            // Graceful fallback: render a sleek gradient
            BufferedImage fallback = new BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = fallback.createGraphics();
            g2.setPaint(new GradientPaint(0, 0, BG_CREAM, targetW, targetH, new Color(230, 240, 242)));
            g2.fillRect(0, 0, targetW, targetH);
            g2.dispose();
            ImageIcon icon = new ImageIcon(fallback);
            SCALED_ICONS.put(cacheKey, icon);
            return icon;
        }

        // Calculate crop-to-fill dimensions (no stretching, preserves aspect ratio)
        int srcW = orig.getWidth();
        int srcH = orig.getHeight();
        double targetAspect = (double) targetW / targetH;
        double srcAspect = (double) srcW / srcH;

        int cropW, cropH, cropX, cropY;
        if (srcAspect > targetAspect) {
            cropH = srcH;
            cropW = (int) Math.round(srcH * targetAspect);
            cropX = (srcW - cropW) / 2;
            cropY = 0;
        } else {
            cropW = srcW;
            cropH = (int) Math.round(srcW / targetAspect);
            cropX = 0;
            cropY = (srcH - cropH) / 2;
        }

        cropW = Math.max(1, Math.min(cropW, srcW - cropX));
        cropH = Math.max(1, Math.min(cropH, srcH - cropY));

        BufferedImage cropped = orig.getSubimage(cropX, cropY, cropW, cropH);
        Image scaled = cropped.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(scaled);
        SCALED_ICONS.put(cacheKey, icon);
        return icon;
    }

    /**
     * Styles a form card or content panel with modern translucent rounded styling.
     */
    public static void styleTranslucentCard(JPanel panel, int radius, Color bgColor, Color borderColor) {
        if (panel == null) return;
        panel.setOpaque(false);
        Color bg = bgColor != null ? bgColor : BG_CARD_TRANSLUCENT;
        Color border = borderColor != null ? borderColor : CARD_BORDER;
        panel.setBackground(bg);
        String bgHex = toHex(bg);
        String borderHex = toHex(border);
        panel.putClientProperty(FlatClientProperties.STYLE,
                "arc: " + radius + "; " +
                "background: #" + bgHex + "; " +
                "border: 1,1,1,1,#" + borderHex
        );
        panel.setBorder(new javax.swing.border.LineBorder(border, 1, true));
    }

    /**
     * Default translucent card styling (warm translucent white, radius 20, soft border).
     */
    public static void styleTranslucentCard(JPanel panel) {
        styleTranslucentCard(panel, 20, BG_CARD_TRANSLUCENT, CARD_BORDER);
    }

    /**
     * Styles a statistics card with rounded corners, white/translucent background, and accent border.
     */
    public static void styleStatCard(JPanel card, Color accentColor) {
        if (card == null) return;
        card.setOpaque(false);
        Color border = accentColor != null ? accentColor : CARD_BORDER;
        Color bg = new Color(255, 255, 255, 240);
        card.setBackground(bg);
        String borderHex = toHex(border);
        card.putClientProperty(FlatClientProperties.STYLE,
                "arc: 16; " +
                "background: #FFFFFFEE; " +
                "border: 1,1,1,1,#" + borderHex
        );
        card.setBorder(new javax.swing.border.LineBorder(border, 1, true));
    }

    /**
     * Formats database payment method enum to user-friendly label.
     */
    public static String formatPaymentMethod(String dbMethod) {
        if (dbMethod == null) return "Cash";
        switch (dbMethod.toUpperCase().trim()) {
            case "CREDIT_CARD": return "Credit Card";
            case "DEBIT_CARD": return "Debit Card";
            case "MOBILE_PAY": return "Mobile Payment";
            case "CASH":
            default: return "Cash";
        }
    }

    /**
     * Maps user-facing payment method label to database enum string.
     */
    public static String toDbPaymentMethod(String displayMethod) {
        if (displayMethod == null) return "CASH";
        switch (displayMethod.toLowerCase().trim()) {
            case "credit card":
            case "credit_card": return "CREDIT_CARD";
            case "debit card":
            case "debit_card": return "DEBIT_CARD";
            case "mobile payment":
            case "mobile_pay": return "MOBILE_PAY";
            case "cash":
            default: return "CASH";
        }
    }

    /**
     * Comprehensive modern table styling (Deep teal header, zebra rows, padded cells, centered status).
     */
    public static void styleTable(JTable table) {
        if (table == null) return;

        JTableHeader header = table.getTableHeader();
        if (header != null) {
            header.setFont(new Font("Segoe UI", Font.BOLD, 12));
            header.setBackground(TEAL_PRIMARY);
            header.setForeground(Color.WHITE);
            header.setPreferredSize(new Dimension(header.getPreferredSize().width, 36));
        }

        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(32);
        table.setSelectionBackground(new Color(209, 231, 221));
        table.setSelectionForeground(TEAL_PRIMARY);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(241, 245, 249));

        // Default alternating row zebra renderer
        TableCellRenderer defaultRenderer = table.getDefaultRenderer(Object.class);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isSel, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, isSel, hasFocus, row, col);
                if (!isSel) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                    c.setForeground(TEXT_DARK);
                }
                if (c instanceof JLabel) {
                    JLabel lbl = (JLabel) c;
                    lbl.setBorder(new EmptyBorder(0, 8, 0, 8));
                }
                return c;
            }
        });
    }

    /**
     * Styles primary / secondary / danger buttons with hover cursor and rounded arc.
     */
    public static void styleButton(JButton btn, Color bg, Color fg, int arc) {
        if (btn == null) return;
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: " + arc);
    }

    private static String toHex(Color c) {
        if (c == null) return "FFFFFF";
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();
        int a = c.getAlpha();
        if (a < 255) {
            return String.format("%02x%02x%02x%02x", r, g, b, a);
        }
        return String.format("%02x%02x%02x", r, g, b);
    }

    public static Border createCardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER, 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        );
    }

    public static javax.swing.Icon createPawIcon(int size, Color color) {
        return com.pawmart.ui.components.PawIconRenderer.createPawIcon(size, color);
    }

    public static javax.swing.Icon createPawIcon(int width, int height, Color color) {
        return com.pawmart.ui.components.PawIconRenderer.createPawIcon(Math.min(width, height), color);
    }
}
