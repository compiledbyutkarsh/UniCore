package com.unicore.ui;

import com.unicore.dao.CourseDAO;
import com.unicore.dao.EnrollmentDAO;
import com.unicore.models.Course;
import com.unicore.utils.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GradePanel extends JPanel {

    private JComboBox<Object> courseCombo;
    private JTable table;
    private DefaultTableModel model;
    private CourseDAO courseDao = new CourseDAO();
    private EnrollmentDAO enrollDao = new EnrollmentDAO();

    public GradePanel() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.BG_DARK);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        buildUI();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIUtils.BG_DARK);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));
        header.add(UIUtils.heading("Grades"), BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setBackground(UIUtils.BG_DARK);

        // Course selector
        JPanel topCard = UIUtils.card(new FlowLayout(FlowLayout.LEFT, 12, 8));
        courseCombo = UIUtils.styledCombo();
        courseCombo.setPreferredSize(new Dimension(300, 36));
        for (Course c : courseDao.getAll()) courseCombo.addItem(c);
        courseCombo.addActionListener(e -> loadGrades());

        JButton loadBtn = UIUtils.primaryButton("Load");
        loadBtn.addActionListener(e -> loadGrades());
        topCard.add(UIUtils.subLabel("Select Course:"));
        topCard.add(courseCombo);
        topCard.add(loadBtn);
        center.add(topCard, BorderLayout.NORTH);

        // Table
        String[] cols = {"Student ID", "Name", "Grade", "Marks", "Status"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UIUtils.styleTable(table);

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(UIUtils.BG_CARD);
        tableCard.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER, 1));
        tableCard.add(UIUtils.styledScroll(table), BorderLayout.CENTER);

        // Grade input
        JPanel gradeInput = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        gradeInput.setBackground(UIUtils.BG_CARD);

        JTextField marksField = UIUtils.styledField("");
        marksField.setPreferredSize(new Dimension(100, 34));
        JComboBox<String> gradeCombo = new JComboBox<>(new String[]{"A+","A","B+","B","C+","C","D","F"});
        gradeCombo.setBackground(UIUtils.BG_DARK);
        gradeCombo.setForeground(UIUtils.TEXT_PRIMARY);
        gradeCombo.setFont(UIUtils.FONT_BODY);

        JButton saveGrade = UIUtils.primaryButton("Save Grade");
        saveGrade.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select a student row first."); return; }
            Course c = (Course) courseCombo.getSelectedItem();
            if (c == null) return;

            String sid = (String) model.getValueAt(row, 0);
            String grade = (String) gradeCombo.getSelectedItem();
            double marks;
            try { marks = Double.parseDouble(marksField.getText().trim()); }
            catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Enter valid marks."); return; }

            // get student id from DB by student_id string
            try {
                var con = com.unicore.database.DBConnection.getConnection();
                var ps = con.prepareStatement("SELECT id FROM students WHERE student_id=?");
                ps.setString(1, sid);
                var rs = ps.executeQuery();
                if (rs.next()) {
                    int studentId = rs.getInt(1);
                    if (enrollDao.updateGrade(studentId, c.getId(), grade, marks)) {
                        JOptionPane.showMessageDialog(this, "Grade saved.");
                        loadGrades();
                    }
                }
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        });

        gradeInput.add(UIUtils.subLabel("Marks:"));
        gradeInput.add(marksField);
        gradeInput.add(UIUtils.subLabel("Grade:"));
        gradeInput.add(gradeCombo);
        gradeInput.add(saveGrade);
        tableCard.add(gradeInput, BorderLayout.SOUTH);

        center.add(tableCard, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        if (courseCombo.getItemCount() > 0) loadGrades();
    }

    private void loadGrades() {
        Course c = (Course) courseCombo.getSelectedItem();
        if (c == null) return;
        model.setRowCount(0);
        for (String[] row : enrollDao.getCourseEnrollments(c.getId()))
            model.addRow(row);
    }
}
