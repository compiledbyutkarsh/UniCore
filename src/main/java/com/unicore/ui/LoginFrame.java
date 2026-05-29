package com.unicore.ui;

import com.unicore.database.DBConnection;
import com.unicore.utils.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel errorLabel;

    public LoginFrame() {
        setTitle("UniCore — University Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(440, 620);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIUtils.BG_DARK);
        setContentPane(root);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(UIUtils.BG_DARK);
        center.setBorder(new EmptyBorder(60, 50, 60, 50));

        // Logo
        JLabel logo = new JLabel("🎓");
        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("UniCore");
        title.setFont(new Font("SF Pro Display", Font.BOLD, 28));
        title.setForeground(UIUtils.TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = UIUtils.subLabel("University Management System");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        center.add(logo);
        center.add(Box.createVerticalStrut(12));
        center.add(title);
        center.add(Box.createVerticalStrut(4));
        center.add(subtitle);
        center.add(Box.createVerticalStrut(40));

        // Form
        JPanel form = UIUtils.card(new GridLayout(0, 1, 0, 12));
        form.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.setMaximumSize(new Dimension(340, 999));

        JLabel userLabel = UIUtils.subLabel("Username");
        usernameField = UIUtils.styledField("");
        usernameField.setText("admin");

        JLabel passLabel = UIUtils.subLabel("Password");
        passwordField = new JPasswordField();
        passwordField.setBackground(UIUtils.BG_DARK);
        passwordField.setForeground(UIUtils.TEXT_PRIMARY);
        passwordField.setCaretColor(UIUtils.TEXT_PRIMARY);
        passwordField.setFont(UIUtils.FONT_BODY);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIUtils.BORDER, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));

        errorLabel = new JLabel(" ");
        errorLabel.setForeground(UIUtils.DANGER);
        errorLabel.setFont(UIUtils.FONT_SMALL);

        JButton loginBtn = UIUtils.primaryButton("Sign In");
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(340, 42));

        form.add(userLabel);
        form.add(usernameField);
        form.add(passLabel);
        form.add(passwordField);
        form.add(errorLabel);
        form.add(loginBtn);

        center.add(form);

        JLabel hint = UIUtils.subLabel("Default: admin / admin123");
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(Box.createVerticalStrut(16));
        center.add(hint);

        root.add(center, BorderLayout.CENTER);

        loginBtn.addActionListener(e -> doLogin());
        passwordField.addActionListener(e -> doLogin());

        getRootPane().setDefaultButton(loginBtn);
    }

    private void doLogin() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            errorLabel.setText("Please enter credentials.");
            return;
        }

        String sql = "SELECT * FROM admins WHERE username=? AND password=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                dispose();
                SwingUtilities.invokeLater(() -> new MainFrame(name).setVisible(true));
            } else {
                errorLabel.setText("Invalid username or password.");
                passwordField.setText("");
            }
        } catch (SQLException e) {
            errorLabel.setText("Database error: " + e.getMessage());
        }
    }
}
