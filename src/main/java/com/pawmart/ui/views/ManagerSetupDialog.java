package com.pawmart.ui.views;

import com.pawmart.database.DatabaseInitializer;
import com.pawmart.ui.components.ModernPasswordField;
import com.pawmart.ui.components.ModernTextField;
import com.pawmart.ui.components.PrimaryButton;
import com.pawmart.ui.components.RoundedPanel;
import com.pawmart.ui.components.SecondaryButton;
import com.pawmart.ui.theme.PawMartTheme;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

/**
 * Dialog for initial First-Run Store Manager Setup.
 */
public class ManagerSetupDialog extends JDialog {

    private ModernTextField txtFullName;
    private ModernTextField txtUsername;
    private ModernTextField txtEmail;
    private ModernPasswordField txtPassword;
    private ModernPasswordField txtConfirmPassword;

    public ManagerSetupDialog(JFrame parent) {
        super(parent, "PawMart - First Run Manager Setup", true);
        setSize(460, 520);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(PawMartTheme.BG_CREAM);
        setLayout(new BorderLayout());

        initComponents();
    }

    private void initComponents() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(PawMartTheme.BG_CREAM);
        wrapper.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JPanel card = new RoundedPanel(16, Color.WHITE, PawMartTheme.CARD_BORDER, true);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel title = new JLabel("Create Manager Account");
        title.setFont(PawMartTheme.FONT_TITLE_MEDIUM);
        title.setForeground(PawMartTheme.TEAL_PRIMARY);

        JLabel sub = new JLabel("Initial setup for store owner or head manager");
        sub.setFont(PawMartTheme.FONT_SMALL);
        sub.setForeground(PawMartTheme.TEXT_MUTED);

        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(sub);
        card.add(Box.createVerticalStrut(20));

        txtFullName = addFormField(card, "Full Name", "e.g. Sarah Jenkins");
        txtUsername = addFormField(card, "Username", "e.g. manager");
        txtEmail = addFormField(card, "Email Address", "e.g. manager@pawmart.com");

        card.add(new JLabel("Password (min 6 characters)"));
        card.add(Box.createVerticalStrut(4));
        txtPassword = new ModernPasswordField("Enter strong password");
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        card.add(txtPassword);
        card.add(Box.createVerticalStrut(10));

        card.add(new JLabel("Confirm Password"));
        card.add(Box.createVerticalStrut(4));
        txtConfirmPassword = new ModernPasswordField("Re-enter password");
        txtConfirmPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        card.add(txtConfirmPassword);
        card.add(Box.createVerticalStrut(22));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        SecondaryButton btnCancel = new SecondaryButton("Cancel");
        btnCancel.addActionListener(e -> dispose());

        PrimaryButton btnCreate = new PrimaryButton("Create Manager");
        btnCreate.addActionListener(e -> submitManager());

        btnRow.add(btnCancel);
        btnRow.add(btnCreate);
        card.add(btnRow);

        wrapper.add(card, BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);
    }

    private ModernTextField addFormField(JPanel parent, String labelText, String placeholder) {
        JLabel label = new JLabel(labelText);
        label.setFont(PawMartTheme.FONT_BODY_BOLD);
        label.setForeground(PawMartTheme.TEXT_DARK);
        parent.add(label);
        parent.add(Box.createVerticalStrut(4));

        ModernTextField field = new ModernTextField(placeholder);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        parent.add(field);
        parent.add(Box.createVerticalStrut(10));
        return field;
    }

    private void submitManager() {
        String name = txtFullName.getText().trim();
        String user = txtUsername.getText().trim();
        String email = txtEmail.getText().trim();
        String pass = new String(txtPassword.getPassword());
        String conf = new String(txtConfirmPassword.getPassword());

        if (name.isEmpty() || user.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (pass.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!pass.equals(conf)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean ok = DatabaseInitializer.createInitialManager(name, user, email, pass);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Manager account created successfully!\nYou may now sign in.", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to create account. Username or email may already be in use.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
