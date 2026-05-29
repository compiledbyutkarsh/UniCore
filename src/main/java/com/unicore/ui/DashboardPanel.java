package com.unicore.ui;

import com.unicore.database.DBConnection;
import com.unicore.utils.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class DashboardPanel extends JPanel {

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.BG_DARK);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        buildUI();
    }

    private void buildUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIUtils.BG_DARK);
        header.setBorder(new EmptyBorder(0, 0, 24, 0));

        JLabel title = UIUtils.heading("Dashboard");
        JLabel sub = UIUtils.subLabel("Welcome back — here's an overview");
        JPanel headerText = new JPanel();
        headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));
        headerText.setBackground(UIUtils.BG_DARK);
        headerText.add(title);
        headerText.add(Box.createVerticalStrut(4));
        headerText.add(sub);
        header.add(headerText, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // Stats cards
        JPanel cards = new JPanel(new GridLayout(1, 4, 16, 0));
        cards.setBackground(UIUtils.BG_DARK);

        cards.add(statCard("👨‍🎓", "Students", getCount("SELECT COUNT(*) FROM students WHERE status='active'"), UIUtils.ACCENT));
        cards.add(statCard("👨‍🏫", "Faculty", getCount("SELECT COUNT(*) FROM faculty WHERE status='active'"), new Color(168, 85, 247)));
        cards.add(statCard("📚", "Courses", getCount("SELECT COUNT(*) FROM courses"), new Color(20, 184, 166)));
        cards.add(statCard("🏛️", "Departments", getCount("SELECT COUNT(*) FROM departments"), new Color(234, 179, 8)));

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(UIUtils.BG_DARK);
        center.add(cards, BorderLayout.NORTH);
        center.add(Box.createVerticalStrut(24));

        // Recent students table
        JPanel tableSection = new JPanel(new BorderLayout());
        tableSection.setBackground(UIUtils.BG_CARD);
        tableSection.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIUtils.BORDER, 1),
            new EmptyBorder(16, 16, 16, 16)
        ));

        JLabel tableTitle = new JLabel("Recent Students");
        tableTitle.setFont(UIUtils.FONT_HEADING);
        tableTitle.setForeground(UIUtils.TEXT_PRIMARY);
        tableTitle.setBorder(new EmptyBorder(0, 0, 12, 0));

        String[] cols = {"Student ID", "Name", "Department", "Semester", "CGPA", "Status"};
        Object[][] data = getRecentStudents();

        JTable table = new JTable(data, cols) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        UIUtils.styleTable(table);

        tableSection.add(tableTitle, BorderLayout.NORTH);
        tableSection.add(UIUtils.styledScroll(table), BorderLayout.CENTER);

        JPanel tableWrap = new JPanel(new BorderLayout());
        tableWrap.setBackground(UIUtils.BG_DARK);
        tableWrap.setBorder(new EmptyBorder(24, 0, 0, 0));
        tableWrap.add(tableSection);

        center.add(tableWrap, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
    }

    private JPanel statCard(String icon, String label, int count, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UIUtils.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIUtils.BORDER, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));

        JLabel countLabel = new JLabel(String.valueOf(count));
        countLabel.setFont(new Font("SF Pro Display", Font.BOLD, 32));
        countLabel.setForeground(accent);

        JLabel nameLabel = UIUtils.subLabel(label);

        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setBackground(UIUtils.BG_CARD);
        text.add(countLabel);
        text.add(Box.createVerticalStrut(2));
        text.add(nameLabel);

        card.add(iconLabel, BorderLayout.NORTH);
        card.add(text, BorderLayout.CENTER);
        return card;
    }

    private int getCount(String sql) {
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return 0;
    }

    private Object[][] getRecentStudents() {
        String sql = "SELECT s.student_id, CONCAT(s.first_name,' ',s.last_name), d.name, s.semester, s.cgpa, s.status FROM students s LEFT JOIN departments d ON s.department_id=d.id ORDER BY s.id DESC LIMIT 8";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            java.util.List<Object[]> rows = new java.util.ArrayList<>();
            while (rs.next()) {
                rows.add(new Object[]{
                    rs.getString(1), rs.getString(2), rs.getString(3),
                    rs.getInt(4), rs.getDouble(5), rs.getString(6)
                });
            }
            return rows.toArray(new Object[0][]);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return new Object[0][0];
    }
}
