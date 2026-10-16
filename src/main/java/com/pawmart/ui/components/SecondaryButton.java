package com.pawmart.ui.components;

import com.pawmart.ui.theme.PawMartTheme;

import javax.swing.JButton;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Secondary outlined/light action button.
 */
public class SecondaryButton extends JButton {

    private Color borderColor;
    private Color hoverBgColor;
    private boolean isHovered = false;
    private int cornerRadius = 12;

    public SecondaryButton(String text) {
        super(text);
        this.borderColor = PawMartTheme.CARD_BORDER;
        this.hoverBgColor = new Color(241, 245, 249);

        setFont(PawMartTheme.FONT_BODY_BOLD);
        setForeground(PawMartTheme.TEXT_DARK);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    isHovered = true;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (isEnabled()) {
                    isHovered = false;
                    repaint();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Background
        if (isHovered && isEnabled()) {
            g2.setColor(hoverBgColor);
            g2.fillRoundRect(0, 0, w, h, cornerRadius, cornerRadius);
        } else {
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, w, h, cornerRadius, cornerRadius);
        }

        // Border
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);

        g2.dispose();
        super.paintComponent(g);
    }
}
