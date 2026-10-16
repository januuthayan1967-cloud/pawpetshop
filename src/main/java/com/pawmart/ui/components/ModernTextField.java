package com.pawmart.ui.components;

import com.pawmart.ui.theme.PawMartTheme;

import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * Modern rounded JTextField with placeholder support and focus highlight.
 */
public class ModernTextField extends JTextField {

    private String placeholder;
    private boolean isFocused = false;
    private int cornerRadius = 10;
    private Color normalBorder = PawMartTheme.CARD_BORDER;
    private Color focusBorder = PawMartTheme.TEAL_PRIMARY;

    public ModernTextField() {
        this("");
    }

    public ModernTextField(String placeholder) {
        this.placeholder = placeholder;
        setFont(PawMartTheme.FONT_BODY);
        setForeground(PawMartTheme.TEXT_DARK);
        setCaretColor(PawMartTheme.TEAL_PRIMARY);
        setOpaque(false);
        setBorder(new EmptyBorder(8, 12, 8, 12));

        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                isFocused = true;
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                isFocused = false;
                repaint();
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
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);

        // Border
        g2.setColor(isFocused ? focusBorder : normalBorder);
        g2.setStroke(new BasicStroke(isFocused ? 1.5f : 1.0f));
        g2.drawRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);

        g2.dispose();
        super.paintComponent(g);

        // Placeholder
        if (getText().isEmpty() && placeholder != null && !placeholder.isEmpty() && !isFocused) {
            Graphics2D gPlaceholder = (Graphics2D) g.create();
            gPlaceholder.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            gPlaceholder.setColor(PawMartTheme.TEXT_LIGHT);
            gPlaceholder.setFont(getFont());
            int textY = (getHeight() - gPlaceholder.getFontMetrics().getHeight()) / 2 + gPlaceholder.getFontMetrics().getAscent();
            gPlaceholder.drawString(placeholder, getInsets().left, textY);
            gPlaceholder.dispose();
        }
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }
}
