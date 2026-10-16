package com.pawmart.ui.components;

import com.pawmart.ui.theme.PawMartTheme;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Modern KPI / Metric card for the Manager Dashboard.
 */
public class StatCard extends RoundedPanel {

    private final JLabel lblTitle;
    private final JLabel lblValue;
    private final JLabel lblSubtext;
    private final Color iconBgColor;
    private final Color iconColor;
    private final String iconType; // "paw", "cart", "money", "alert", "user"

    public StatCard(String title, String initialValue, String subtext, String iconType, Color iconBgColor, Color iconColor) {
        super(14, Color.WHITE, PawMartTheme.CARD_BORDER, true);
        this.iconType = iconType;
        this.iconBgColor = iconBgColor;
        this.iconColor = iconColor;

        setLayout(new BorderLayout(12, 8));
        setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        setPreferredSize(new Dimension(220, 115));

        // Left Content (Title, Value, Subtext)
        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new javax.swing.BoxLayout(leftPanel, javax.swing.BoxLayout.Y_AXIS));

        lblTitle = new JLabel(title);
        lblTitle.setFont(PawMartTheme.FONT_SMALL_BOLD);
        lblTitle.setForeground(PawMartTheme.TEXT_MUTED);

        lblValue = new JLabel(initialValue);
        lblValue.setFont(PawMartTheme.FONT_TITLE_LARGE);
        lblValue.setForeground(PawMartTheme.TEXT_DARK);

        lblSubtext = new JLabel(subtext);
        lblSubtext.setFont(PawMartTheme.FONT_SMALL);
        lblSubtext.setForeground(PawMartTheme.TEXT_MUTED);

        leftPanel.add(lblTitle);
        leftPanel.add(javax.swing.Box.createVerticalStrut(4));
        leftPanel.add(lblValue);
        leftPanel.add(javax.swing.Box.createVerticalStrut(4));
        leftPanel.add(lblSubtext);

        add(leftPanel, BorderLayout.CENTER);

        // Right Icon Circle Badge
        JPanel iconBadge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = Math.min(getWidth(), getHeight()) - 4;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;

                g2.setColor(iconBgColor);
                g2.fillOval(x, y, size, size);

                // Draw Icon
                int iconInnerSize = size * 5 / 8;
                int ix = x + (size - iconInnerSize) / 2;
                int iy = y + (size - iconInnerSize) / 2;

                if ("paw".equalsIgnoreCase(iconType)) {
                    PawIconRenderer.drawPawPrint(g2, ix, iy, iconInnerSize, iconColor);
                } else if ("cart".equalsIgnoreCase(iconType)) {
                    PawIconRenderer.drawShoppingBag(g2, ix, iy, iconInnerSize, iconColor);
                } else if ("bone".equalsIgnoreCase(iconType)) {
                    PawIconRenderer.drawBone(g2, ix, iy + iconInnerSize / 4, iconInnerSize, iconInnerSize / 2, iconColor);
                } else {
                    PawIconRenderer.drawPawPrint(g2, ix, iy, iconInnerSize, iconColor);
                }

                g2.dispose();
            }
        };
        iconBadge.setOpaque(false);
        iconBadge.setPreferredSize(new Dimension(50, 50));
        add(iconBadge, BorderLayout.EAST);
    }

    public void setValue(String value) {
        lblValue.setText(value);
        lblValue.repaint();
    }

    public void setSubtext(String subtext) {
        lblSubtext.setText(subtext);
        lblSubtext.repaint();
    }
}
