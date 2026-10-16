package com.pawmart.ui.components;

import javax.swing.Icon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Procedural vector graphics renderer for pet-themed icons (paw prints, bones, fish, shopping carts).
 */
public class PawIconRenderer {

    public static void drawPawPrint(Graphics2D g2, int x, int y, int size, Color color) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);

        double s = size / 24.0;

        // Main pad (rounded heart / triangle)
        int padW = (int) (12 * s);
        int padH = (int) (10 * s);
        int padX = x + (int) (6 * s);
        int padY = y + (int) (11 * s);
        g2.fillOval(padX, padY, padW, padH);

        // Toe 1 (Far left)
        g2.fillOval(x + (int) (2 * s), y + (int) (7 * s), (int) (4.5 * s), (int) (6 * s));

        // Toe 2 (Middle left)
        g2.fillOval(x + (int) (6.5 * s), y + (int) (3 * s), (int) (5 * s), (int) (6.5 * s));

        // Toe 3 (Middle right)
        g2.fillOval(x + (int) (12.5 * s), y + (int) (3 * s), (int) (5 * s), (int) (6.5 * s));

        // Toe 4 (Far right)
        g2.fillOval(x + (int) (17.5 * s), y + (int) (7 * s), (int) (4.5 * s), (int) (6 * s));
    }

    public static void drawBone(Graphics2D g2, int x, int y, int width, int height, Color color) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);

        int knobR = height / 2;
        int barH = height / 3;
        int barY = y + (height - barH) / 2;

        // Middle bar
        g2.fillRoundRect(x + knobR / 2, barY, width - knobR, barH, barH, barH);

        // Left knobs
        g2.fillOval(x, y, knobR, knobR);
        g2.fillOval(x, y + height - knobR, knobR, knobR);

        // Right knobs
        g2.fillOval(x + width - knobR, y, knobR, knobR);
        g2.fillOval(x + width - knobR, y + height - knobR, knobR, knobR);
    }

    public static void drawShoppingBag(Graphics2D g2, int x, int y, int size, Color color) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(1.8f));

        int bagX = x + size / 6;
        int bagY = y + size / 3;
        int bagW = size * 2 / 3;
        int bagH = size * 5 / 8;

        // Bag body
        g2.drawRoundRect(bagX, bagY, bagW, bagH, 6, 6);

        // Handle
        int handleW = bagW / 2;
        int handleX = bagX + (bagW - handleW) / 2;
        int handleY = y + size / 8;
        g2.drawArc(handleX, handleY, handleW, size / 3, 0, 180);
    }

    public static Icon createPawIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                drawPawPrint(g2, x, y, size, color);
                g2.dispose();
            }

            @Override
            public int getIconWidth() {
                return size;
            }

            @Override
            public int getIconHeight() {
                return size;
            }
        };
    }

    public static Icon createBoneIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                drawBone(g2, x, y + size / 4, size, size / 2, color);
                g2.dispose();
            }

            @Override
            public int getIconWidth() {
                return size;
            }

            @Override
            public int getIconHeight() {
                return size;
            }
        };
    }
}
