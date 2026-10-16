package com.pawmart.ui.components;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Modern rounded container panel with optional border and subtle shadow.
 */
public class RoundedPanel extends JPanel {

    private int cornerRadius;
    private Color backgroundColor;
    private Color borderColor;
    private boolean hasShadow;

    public RoundedPanel(int radius) {
        this(radius, Color.WHITE, new Color(232, 226, 216), true);
    }

    public RoundedPanel(int radius, Color backgroundColor) {
        this(radius, backgroundColor, null, false);
    }

    public RoundedPanel(int radius, Color backgroundColor, Color borderColor, boolean hasShadow) {
        super();
        this.cornerRadius = radius;
        this.backgroundColor = backgroundColor;
        this.borderColor = borderColor;
        this.hasShadow = hasShadow;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int offset = hasShadow ? 2 : 0;
        int drawW = width - (hasShadow ? 4 : 1);
        int drawH = height - (hasShadow ? 4 : 1);

        // Subtle drop shadow
        if (hasShadow) {
            g2.setColor(new Color(0, 0, 0, 12));
            g2.fillRoundRect(offset + 1, offset + 2, drawW, drawH, cornerRadius, cornerRadius);
            g2.setColor(new Color(0, 0, 0, 6));
            g2.fillRoundRect(offset, offset + 1, drawW, drawH, cornerRadius, cornerRadius);
        }

        // Background
        g2.setColor(backgroundColor != null ? backgroundColor : getBackground());
        g2.fillRoundRect(offset, offset, drawW, drawH, cornerRadius, cornerRadius);

        // Border
        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(offset, offset, drawW - 1, drawH - 1, cornerRadius, cornerRadius);
        }

        g2.dispose();
        super.paintComponent(g);
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        repaint();
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        repaint();
    }
}
