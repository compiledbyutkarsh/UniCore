package com.unicore.ui;

import com.unicore.utils.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {

    private JPanel contentPanel;
    private String adminName;

    public MainFrame(String adminName) {
        this.adminName = adminName;
        setTitle("UniCore — Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(UIUtils.BG_DARK);
        setContentPane(root);

        root.add(buildSidebar(), BorderLayout.WEST);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(UIUtils.BG_DARK);
        root.add(contentPanel, BorderLayout.CENTER);

        showDashboard();
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(UIUtils.BG_PANEL);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UIUtils.BORDER));

        // Top — logo
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 18));
        logoPanel.setBackground(UIUtils.BG_PANEL);
        logoPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIUtils.BORDER));
        JLabel logo = new JLabel("🎓  UniCore");
        logo.setFont(new Font("Dialog", Font.BOLD, 15));
        logo.setForeground(UIUtils.TEXT_PRIMARY);
        logoPanel.add(logo);
        sidebar.add(logoPanel, BorderLayout.NORTH);

        // Middle — nav
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBackground(UIUtils.BG_PANEL);
        nav.setBorder(new EmptyBorder(8, 0, 8, 0));

        String[][] items = {
            {"🏠", "Dashboard"},
            {"👨‍🎓", "Students"},
            {"👨‍🏫", "Faculty"},
            {"📚", "Courses"},
            {"🏛", "Departments"},
            {"📋", "Enrollments"},
            {"📊", "Grades"}
        };

        for (String[] item : items) {
            nav.add(navButton(item[0], item[1]));
        }

        sidebar.add(nav, BorderLayout.CENTER);

        // Bottom — admin + logout
        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setBackground(UIUtils.BG_PANEL);
        bottom.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIUtils.BORDER));

        JPanel adminInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        adminInfo.setBackground(UIUtils.BG_PANEL);
        JLabel adminLbl = new JLabel("👤  " + adminName);
        adminLbl.setFont(new Font("Dialog", Font.PLAIN, 12));
        adminLbl.setForeground(UIUtils.TEXT_SUB);
        adminInfo.add(adminLbl);
        bottom.add(adminInfo);

        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        logoutPanel.setBackground(UIUtils.BG_PANEL);
        JButton logoutBtn = UIUtils.ghostButton("← Logout");
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        logoutPanel.add(logoutBtn);
        bottom.add(logoutPanel);

        sidebar.add(bottom, BorderLayout.SOUTH);
        return sidebar;
    }

    private JButton navButton(String icon, String label) {
        JButton btn = new JButton(icon + "  " + label);
        btn.setFont(new Font("Dialog", Font.PLAIN, 13));
        btn.setForeground(UIUtils.TEXT_SUB);
        btn.setBackground(UIUtils.BG_PANEL);
        btn.setBorder(new EmptyBorder(12, 20, 12, 20));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(220, 46));
        btn.setMinimumSize(new Dimension(220, 46));
        btn.setPreferredSize(new Dimension(220, 46));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(UIUtils.BG_CARD);
                btn.setForeground(UIUtils.TEXT_PRIMARY);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(UIUtils.BG_PANEL);
                btn.setForeground(UIUtils.TEXT_SUB);
            }
        });

        btn.addActionListener(e -> {
            switch (label) {
                case "Dashboard"   -> showDashboard();
                case "Students"    -> showPanel(new StudentPanel());
                case "Faculty"     -> showPanel(new FacultyPanel());
                case "Courses"     -> showPanel(new CoursePanel());
                case "Departments" -> showPanel(new DepartmentPanel());
                case "Enrollments" -> showPanel(new EnrollmentPanel());
                case "Grades"      -> showPanel(new GradePanel());
            }
        });

        return btn;
    }

    private void showPanel(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showDashboard() {
        showPanel(new DashboardPanel());
    }
}
