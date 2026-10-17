package com.pawmart.test;

import com.pawmart.database.DatabaseConnection;
import com.pawmart.database.DatabaseInitializer;
import com.pawmart.model.Role;
import com.pawmart.model.User;
import com.pawmart.service.AuthService;
import com.pawmart.ui.theme.PawMartTheme;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Diagnostic utility to instantiate, inspect, and photograph every screen in PawMart.
 */
public class ScreenVisualInspector {

    public static void main(String[] args) throws Exception {
        System.out.println("=== PAWMART SCREEN VISUAL INSPECTOR ===");
        
        // 1. Setup Theme and DB
        PawMartTheme.setupFlatLaf();
        DatabaseConnection.getConnection();
        DatabaseInitializer.initializeDatabase();

        // Ensure session exists
        User managerUser = AuthService.getInstance().login("admin", "admin123");
        System.out.println("Authenticated manager session: " + managerUser.getUsername());

        File screenshotDir = new File("target/screenshots");
        if (!screenshotDir.exists()) {
            screenshotDir.mkdirs();
        }

        // List of all screens to test
        String[] screenClassNames = {
            "login",
            "Managerpage",
            "Dashboard",
            "Addpetsupplies",
            "Updatepetsupplies",
            "Searchpetsupplies",
            "Viewpetsupllies",
            "createcashieraccounts",
            "POSBillingFrame",
            "ReportsSalesFrame",
            "com.pawmart.ui.views.ManagerDashboardFrame",
            "com.pawmart.ui.views.CashierDashboardFrame",
            "com.pawmart.ui.views.LoginFrame"
        };

        for (String className : screenClassNames) {
            inspectScreen(className, screenshotDir, managerUser);
        }

        System.out.println("\n=== VISUAL INSPECTION COMPLETED ===");
    }

    private static void inspectScreen(String className, File screenshotDir, User user) {
        System.out.println("\n------------------------------------------------------------");
        System.out.println("INSPECTING SCREEN: " + className);
        System.out.println("------------------------------------------------------------");

        try {
            Class<?> clazz = Class.forName(className);
            JFrame frame = null;

            if (className.contains("ManagerDashboardFrame") || className.contains("CashierDashboardFrame")) {
                Constructor<?> ctor = clazz.getDeclaredConstructor(User.class);
                frame = (JFrame) ctor.newInstance(user);
            } else {
                Constructor<?> ctor = clazz.getDeclaredConstructor();
                frame = (JFrame) ctor.newInstance();
            }

            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setVisible(true);

            // Let layout and components settle
            Thread.sleep(400);

            int width = frame.getWidth();
            int height = frame.getHeight();
            System.out.printf("Window Title: '%s', Bounds: %dx%d\n", frame.getTitle(), width, height);

            // Check Content Pane bounds and children
            Container contentPane = frame.getContentPane();
            System.out.printf("ContentPane Size: %dx%d, Layout: %s\n",
                    contentPane.getWidth(), contentPane.getHeight(),
                    contentPane.getLayout() != null ? contentPane.getLayout().getClass().getSimpleName() : "null");

            List<String> issues = new ArrayList<>();
            inspectContainer(contentPane, "", issues);

            if (issues.isEmpty()) {
                System.out.println("  ✓ No component bounds or clipping issues detected.");
            } else {
                System.out.println("  ⚠ ISSUES DETECTED (" + issues.size() + "):");
                for (String issue : issues) {
                    System.out.println("    - " + issue);
                }
            }

            // Capture screenshot at default size
            captureScreenshot(frame, new File(screenshotDir, simpleName(className) + "_default.png"));

            // Test standard resolutions
            int[][] resolutions = {
                {1366, 768},
                {1440, 900},
                {1920, 1080}
            };

            for (int[] res : resolutions) {
                frame.setSize(res[0], res[1]);
                frame.validate();
                Thread.sleep(150);
                captureScreenshot(frame, new File(screenshotDir, simpleName(className) + "_" + res[0] + "x" + res[1] + ".png"));
            }

            frame.dispose();

        } catch (Throwable t) {
            System.err.println("Error inspecting " + className + ": " + t.getMessage());
            t.printStackTrace();
        }
    }

    private static String simpleName(String fqcn) {
        int idx = fqcn.lastIndexOf('.');
        return idx >= 0 ? fqcn.substring(idx + 1) : fqcn;
    }

    private static void inspectContainer(Container parent, String indent, List<String> issues) {
        Component[] children = parent.getComponents();
        int parentW = parent.getWidth();
        int parentH = parent.getHeight();

        for (int i = 0; i < children.length; i++) {
            Component child = children[i];
            Rectangle r = child.getBounds();
            String name = child.getName() != null ? child.getName() : child.getClass().getSimpleName();

            // Check if bounds exceed parent
            if (parentW > 0 && r.x + r.width > parentW + 5) {
                issues.add(String.format("Component '%s' [%s] exceeds parent width (%d): x=%d + w=%d = %d",
                        name, child.getClass().getSimpleName(), parentW, r.x, r.width, r.x + r.width));
            }
            if (parentH > 0 && r.y + r.height > parentH + 5) {
                issues.add(String.format("Component '%s' [%s] exceeds parent height (%d): y=%d + h=%d = %d",
                        name, child.getClass().getSimpleName(), parentH, r.y, r.height, r.y + r.height));
            }

            // Check if text is clipped in JButton or JLabel
            if (child instanceof JButton btn) {
                FontMetrics fm = btn.getFontMetrics(btn.getFont());
                if (btn.getText() != null && !btn.getText().isEmpty()) {
                    int textW = fm.stringWidth(btn.getText());
                    if (textW > r.width - 10 && r.width > 0) {
                        issues.add(String.format("Button text clipped on '%s': text '%s' width=%d exceeds button w=%d",
                                name, btn.getText(), textW, r.width));
                    }
                }
            } else if (child instanceof JLabel lbl) {
                if (lbl.getText() != null && !lbl.getText().isEmpty() && !lbl.getText().startsWith("<html>")) {
                    FontMetrics fm = lbl.getFontMetrics(lbl.getFont());
                    int textW = fm.stringWidth(lbl.getText());
                    if (textW > r.width + 10 && r.width > 0) {
                        issues.add(String.format("Label text clipped on '%s': text '%s' width=%d exceeds label w=%d",
                                name, lbl.getText(), textW, r.width));
                    }
                }
            }

            // Recurse into child panels
            if (child instanceof Container container && !(child instanceof JTable) && !(child instanceof JScrollPane)) {
                inspectContainer(container, indent + "  ", issues);
            }
        }
    }

    private static void captureScreenshot(JFrame frame, File targetFile) {
        try {
            int w = Math.max(100, frame.getWidth());
            int h = Math.max(100, frame.getHeight());
            BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = img.createGraphics();
            frame.paint(g2);
            g2.dispose();
            ImageIO.write(img, "PNG", targetFile);
            System.out.printf("    Saved screenshot: %s (%dx%d)\n", targetFile.getName(), w, h);
        } catch (Exception ex) {
            System.err.println("Failed to capture screenshot: " + ex.getMessage());
        }
    }
}
