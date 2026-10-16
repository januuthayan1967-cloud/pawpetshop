package com.pawmart.ui.views;

import com.pawmart.database.DatabaseInitializer;
import com.pawmart.model.Role;
import com.pawmart.model.User;
import com.pawmart.service.AuthService;
import com.pawmart.ui.components.ModernPasswordField;
import com.pawmart.ui.components.ModernTextField;
import com.pawmart.ui.components.PawIconRenderer;
import com.pawmart.ui.components.PrimaryButton;
import com.pawmart.ui.components.RoundedPanel;
import com.pawmart.ui.components.SecondaryButton;
import com.pawmart.ui.theme.PawMartTheme;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

/**
 * Premium Role-Based Login Screen for PawMart Pet Shop Management System.
 */
public class LoginFrame extends JFrame {

    private ModernTextField txtUsername;
    private ModernPasswordField txtPassword;
    private PrimaryButton btnLogin;
    private SecondaryButton btnClear;
    private JLabel lblStatus;

    public LoginFrame() {
        setTitle("PawMart Pet Shop Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 580);
        setMinimumSize(new Dimension(840, 520));
        setLocationRelativeTo(null);
        getContentPane().setBackground(PawMartTheme.BG_CREAM);
        setLayout(new BorderLayout());

        initComponents();

        // Check if database has any manager accounts on startup
        checkFirstRunManager();
    }

    private void initComponents() {
        // Main split wrapper
        JPanel mainWrapper = new JPanel(new BorderLayout());
        mainWrapper.setBackground(PawMartTheme.BG_CREAM);
        mainWrapper.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        // Two-column layout container
        JPanel splitPanel = new RoundedPanel(20, Color.WHITE, PawMartTheme.CARD_BORDER, true);
        splitPanel.setLayout(new BorderLayout());

        // Left Panel: Pet Photography & Branding Showcase
        JPanel leftHeroPanel = createHeroPanel();
        splitPanel.add(leftHeroPanel, BorderLayout.WEST);

        // Right Panel: Rounded Modern Login Card
        JPanel rightLoginPanel = createLoginFormPanel();
        splitPanel.add(rightLoginPanel, BorderLayout.CENTER);

        mainWrapper.add(splitPanel, BorderLayout.CENTER);
        add(mainWrapper, BorderLayout.CENTER);
    }

    private JPanel createHeroPanel() {
        JPanel heroPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Warm gradient background
                g2.setColor(new Color(245, 240, 230));
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Decorative paw prints
                g2.setColor(new Color(15, 76, 92, 18));
                PawIconRenderer.drawPawPrint(g2, 20, 30, 48, new Color(15, 76, 92, 22));
                PawIconRenderer.drawPawPrint(g2, getWidth() - 70, 70, 36, new Color(242, 100, 25, 25));
                PawIconRenderer.drawPawPrint(g2, getWidth() - 90, getHeight() - 110, 54, new Color(15, 76, 92, 20));
                PawIconRenderer.drawPawPrint(g2, 35, getHeight() - 80, 40, new Color(5, 150, 105, 25));

                g2.dispose();
            }
        };
        heroPanel.setPreferredSize(new Dimension(420, 500));
        heroPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        // Top Branding
        JPanel brandHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brandHeader.setOpaque(false);

        JLabel lblLogo = loadScaledImage("/images/logo.png", 46, 46);
        if (lblLogo == null) {
            lblLogo = new JLabel(PawIconRenderer.createPawIcon(36, PawMartTheme.TEAL_PRIMARY));
        }

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("PawMart");
        title.setFont(PawMartTheme.FONT_TITLE_LARGE);
        title.setForeground(PawMartTheme.TEAL_PRIMARY);

        JLabel subtitle = new JLabel("PET SHOP & CARE SYSTEM");
        subtitle.setFont(PawMartTheme.FONT_SMALL_BOLD);
        subtitle.setForeground(PawMartTheme.ORANGE_ACCENT);

        titleBlock.add(title);
        titleBlock.add(subtitle);

        brandHeader.add(lblLogo);
        brandHeader.add(titleBlock);

        heroPanel.add(brandHeader, BorderLayout.NORTH);

        // Center Hero Pet Image
        JLabel heroImage = loadScaledImage("/images/hero_pets.png", 360, 260);
        if (heroImage != null) {
            heroImage.setHorizontalAlignment(SwingConstants.CENTER);
            heroPanel.add(heroImage, BorderLayout.CENTER);
        } else {
            JLabel fallback = new JLabel("Happy Pets, Happy Smiles", SwingConstants.CENTER);
            fallback.setFont(PawMartTheme.FONT_TITLE_MEDIUM);
            fallback.setForeground(PawMartTheme.TEAL_PRIMARY);
            heroPanel.add(fallback, BorderLayout.CENTER);
        }

        // Bottom Slogan & Highlights
        JPanel footerBox = new JPanel();
        footerBox.setOpaque(false);
        footerBox.setLayout(new BoxLayout(footerBox, BoxLayout.Y_AXIS));

        JLabel quote = new JLabel("Your All-in-One Pet Shop & POS Partner");
        quote.setFont(PawMartTheme.FONT_BODY_BOLD);
        quote.setForeground(PawMartTheme.TEXT_DARK);
        quote.setAlignmentX(CENTER_ALIGNMENT);

        JLabel quoteSub = new JLabel("Smart inventory, speedy POS billing & sales analytics");
        quoteSub.setFont(PawMartTheme.FONT_SMALL);
        quoteSub.setForeground(PawMartTheme.TEXT_MUTED);
        quoteSub.setAlignmentX(CENTER_ALIGNMENT);

        footerBox.add(quote);
        footerBox.add(Box.createVerticalStrut(4));
        footerBox.add(quoteSub);

        heroPanel.add(footerBox, BorderLayout.SOUTH);

        return heroPanel;
    }

    private JPanel createLoginFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(40, 48, 40, 48));

        // Welcome Header
        JLabel lblWelcome = new JLabel("Welcome Back!");
        lblWelcome.setFont(PawMartTheme.FONT_TITLE_LARGE);
        lblWelcome.setForeground(PawMartTheme.TEXT_DARK);

        JLabel lblSub = new JLabel("Sign in to your authorized PawMart account");
        lblSub.setFont(PawMartTheme.FONT_BODY);
        lblSub.setForeground(PawMartTheme.TEXT_MUTED);

        formPanel.add(lblWelcome);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(lblSub);
        formPanel.add(Box.createVerticalStrut(28));

        // Username Field
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(PawMartTheme.FONT_BODY_BOLD);
        lblUser.setForeground(PawMartTheme.TEXT_DARK);
        formPanel.add(lblUser);
        formPanel.add(Box.createVerticalStrut(6));

        txtUsername = new ModernTextField("Enter your username");
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        formPanel.add(txtUsername);
        formPanel.add(Box.createVerticalStrut(18));

        // Password Field
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(PawMartTheme.FONT_BODY_BOLD);
        lblPass.setForeground(PawMartTheme.TEXT_DARK);
        formPanel.add(lblPass);
        formPanel.add(Box.createVerticalStrut(6));

        txtPassword = new ModernPasswordField("Enter your password");
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        formPanel.add(txtPassword);
        formPanel.add(Box.createVerticalStrut(8));

        // Status / Error message label
        lblStatus = new JLabel(" ");
        lblStatus.setFont(PawMartTheme.FONT_SMALL_BOLD);
        lblStatus.setForeground(PawMartTheme.STATUS_DANGER);
        formPanel.add(lblStatus);
        formPanel.add(Box.createVerticalStrut(14));

        // Buttons Row
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        btnPanel.setOpaque(false);
        btnPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        btnLogin = new PrimaryButton("  Sign In  ");
        btnLogin.setPreferredSize(new Dimension(160, 42));
        btnLogin.addActionListener(e -> performLogin());

        btnClear = new SecondaryButton("Clear");
        btnClear.setPreferredSize(new Dimension(90, 42));
        btnClear.addActionListener(e -> clearFields());

        btnPanel.add(btnLogin);
        btnPanel.add(btnClear);
        formPanel.add(btnPanel);

        // Enter key submits form
        KeyAdapter enterKeyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        };
        txtUsername.addKeyListener(enterKeyAdapter);
        txtPassword.addKeyListener(enterKeyAdapter);

        formPanel.add(Box.createVerticalGlue());

        // First Run Manager Setup Footer Link
        JLabel lblSetupLink = new JLabel("First time running? Setup Manager Account");
        lblSetupLink.setFont(PawMartTheme.FONT_SMALL);
        lblSetupLink.setForeground(PawMartTheme.TEAL_PRIMARY);
        lblSetupLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblSetupLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new ManagerSetupDialog(LoginFrame.this).setVisible(true);
            }
        });
        formPanel.add(lblSetupLink);

        return formPanel;
    }

    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty()) {
            lblStatus.setText("Please enter your username.");
            txtUsername.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            lblStatus.setText("Please enter your password.");
            txtPassword.requestFocus();
            return;
        }

        lblStatus.setForeground(PawMartTheme.TEAL_PRIMARY);
        lblStatus.setText("Authenticating credentials...");
        btnLogin.setEnabled(false);

        // Background authentication via SwingWorker
        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() throws Exception {
                return AuthService.getInstance().login(username, password);
            }

            @Override
            protected void done() {
                btnLogin.setEnabled(true);
                try {
                    User user = get();
                    lblStatus.setText(" ");
                    dispose(); // Close login window

                    // Redirect strictly based on database role
                    if (user.getRole() == Role.MANAGER) {
                        new ManagerDashboardFrame(user).setVisible(true);
                    } else {
                        new CashierDashboardFrame(user).setVisible(true);
                    }
                } catch (Exception ex) {
                    String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    lblStatus.setForeground(PawMartTheme.STATUS_DANGER);
                    lblStatus.setText(msg);
                }
            }
        };
        worker.execute();
    }

    private void clearFields() {
        txtUsername.setText("");
        txtPassword.setText("");
        lblStatus.setText(" ");
        txtUsername.requestFocus();
    }

    private void checkFirstRunManager() {
        SwingWorker<Boolean, Void> checkWorker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                DatabaseInitializer.initializeDatabase();
                return DatabaseInitializer.hasManagerAccount();
            }

            @Override
            protected void done() {
                try {
                    boolean hasMgr = get();
                    if (!hasMgr) {
                        int opt = JOptionPane.showConfirmDialog(LoginFrame.this,
                                "No Store Manager account was detected in the database.\n"
                                        + "Would you like to setup your primary Manager account now?",
                                "First-Run Manager Setup",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.INFORMATION_MESSAGE);
                        if (opt == JOptionPane.YES_OPTION) {
                            new ManagerSetupDialog(LoginFrame.this).setVisible(true);
                        }
                    }
                } catch (Exception ignored) {}
            }
        };
        checkWorker.execute();
    }

    private JLabel loadScaledImage(String resourcePath, int targetW, int targetH) {
        try {
            URL url = getClass().getResource(resourcePath);
            if (url != null) {
                ImageIcon original = new ImageIcon(url);
                Image img = original.getImage().getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                return new JLabel(new ImageIcon(img));
            }
        } catch (Exception ignored) {}
        return null;
    }
}
