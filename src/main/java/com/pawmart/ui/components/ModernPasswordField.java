package com.pawmart.ui.components;

import com.pawmart.ui.theme.PawMartTheme;

import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Modern rounded JPasswordField with show/hide password toggle icon.
 */
public class ModernPasswordField extends JPasswordField {

    private String placeholder;
    private boolean isFocused = false;
    private boolean passwordVisible = false;
    private int cornerRadius = 10;
    private char defaultEchoChar;

    public ModernPasswordField() {
        this("");
    }

    public ModernPasswordField(String placeholder) {
        this.placeholder = placeholder;
        this.defaultEchoChar = getEchoChar();

        setFont(PawMartTheme.FONT_BODY);
        setForeground(PawMartTheme.TEXT_DARK);
        setCaretColor(PawMartTheme.TEAL_PRIMARY);
        setOpaque(false);
        // Leave room on the right for the eye toggle
        setBorder(new EmptyBorder(8, 12, 8, 36));

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

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int eyeX = getWidth() - 30;
                if (e.getX() >= eyeX && e.getX() <= eyeX + 22) {
                    togglePasswordVisibility();
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int eyeX = getWidth() - 30;
                if (e.getX() >= eyeX && e.getX() <= eyeX + 22) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
                }
            }
        });
    }

    public void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;
        if (passwordVisible) {
            setEchoChar((char) 0);
        } else {
            setEchoChar(defaultEchoChar);
        }
        repaint();
    }

    public boolean isPasswordVisible() {
        return passwordVisible;
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
        g2.setColor(isFocused ? PawMartTheme.TEAL_PRIMARY : PawMartTheme.CARD_BORDER);
        g2.setStroke(new BasicStroke(isFocused ? 1.5f : 1.0f));
        g2.drawRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);

        // Draw Eye icon
        int eyeX = w - 28;
        int eyeY = h / 2 - 7;
        g2.setColor(passwordVisible ? PawMartTheme.TEAL_PRIMARY : PawMartTheme.TEXT_LIGHT);
        g2.setStroke(new BasicStroke(1.4f));

        // Eye shape
        g2.drawArc(eyeX, eyeY + 1, 16, 11, 0, 180);
        g2.drawArc(eyeX, eyeY - 2, 16, 11, 180, 180);
        g2.fillOval(eyeX + 6, eyeY + 3, 5, 5);

        if (passwordVisible) {
            // Draw slash across eye
            g2.drawLine(eyeX - 1, eyeY + 13, eyeX + 17, eyeY - 1);
        }

        g2.dispose();
        super.paintComponent(g);

        // Placeholder
        if (getPassword().length == 0 && placeholder != null && !placeholder.isEmpty() && !isFocused) {
            Graphics2D gp = (Graphics2D) g.create();
            gp.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            gp.setColor(PawMartTheme.TEXT_LIGHT);
            gp.setFont(getFont());
            int textY = (getHeight() - gp.getFontMetrics().getHeight()) / 2 + gp.getFontMetrics().getAscent();
            gp.drawString(placeholder, getInsets().left, textY);
            gp.dispose();
        }
    }
}
