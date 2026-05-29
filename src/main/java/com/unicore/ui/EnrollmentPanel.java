package com.unicore.ui;

import com.unicore.dao.CourseDAO;
import com.unicore.dao.EnrollmentDAO;
import com.unicore.dao.StudentDAO;
import com.unicore.models.Course;
import com.unicore.models.Student;
import com.unicore.utils.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EnrollmentPanel extends JPanel {

    private JComboBox<Object> studentCombo;
    private JComboBox<Object> courseCombo;
    private JTable table;
    private DefaultTableModel model;

    private StudentDAO studentDao = new StudentDAO();
    private CourseDAO courseDao = new CourseDAO();
    private EnrollmentDAO enrollDao = new EnrollmentDAO();

    public EnrollmentPanel() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.BG_DARK);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        buildUI();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIUtils.BG_DARK);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));
        header.add(UIUtils.heading("Enrollments"), BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setBackground(UIUtils.BG_DARK);

        // Enroll form
        JPanel formCard = UIUtils.card(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.fill = GridBagConstraints.HORIZONTAL;

        studentCombo = UIUtils.styledCombo();
        courseCombo = UIUtils.styledCombo();

        for (Student s : studentDao.getAll()) studentCombo.addItem(s);
        for (Course c : courseDao.getAll()) courseCombo.addItem(c);

        JButton enrollBtn = UIUtils.primaryButton("Enroll");
        JButton dropBtn = UIUtils.dangerButton("Drop");

        gc.gridy = 0; gc.gridx = 0; gc.weightx = 0;
        formCard.add(UIUtils.subLabel("Student"), gc);
        gc.gridx = 1; gc.weightx = 1;
        formCard.add(studentCombo, gc);

        gc.gridy = 1; gc.gridx = 0; gc.weightx = 0;
        formCard.add(UIUtils.subLabel("Course"), gc);
        gc.gridx = 1; gc.weightx = 1;
        formCard.add(courseCombo, gc);

        gc.gridy = 2; gc.gridx = 0;
        formCard.add(enrollBtn, gc);
        gc.gridx = 1;
        formCard.add(dropBtn, gc);

        enrollBtn.addActionListener(e -> {
            Student s = (Student) studentCombo.getSelectedItem();
            Course c = (Course) courseCombo.getSelectedItem();
            if (s == null || c == null) return;
            if (enrollDao.enroll(s.getId(), c.getId())) {
                JOptionPane.showMessageDialog(this, "Student enrolled successfully.");
                loadEnrollments(s);
            } else {
                JOptionPane.showMessageDialog(this, "Enrollment failed. Already enrolled?");
            }
        });

        dropBtn.addActionListener(e -> {
            Student s = (Student) studentCombo.getSelectedItem();
            Course c = (Course) courseCombo.getSelectedItem();
            if (s == null || c == null) return;
            if (enrollDao.drop(s.getId(), c.getId())) {
                JOptionPane.showMessageDialog(this, "Course dropped.");
                loadEnrollments(s);
            }
        });

        studentCombo.addActionListener(e -> {
            Student s = (Student) studentCombo.getSelectedItem();
            if (s != null) loadEnrollments(s);
        });

        center.add(formCard, BorderLayout.NORTH);

        // Table
        String[] cols = {"Course Code", "Course Name", "Credits", "Grade", "Marks", "Status"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UIUtils.styleTable(table);

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(UIUtils.BG_CARD);
        tableCard.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER, 1));

        JLabel tableTitle = new JLabel("  Student Enrollments");
        tableTitle.setFont(UIUtils.FONT_HEADING);
        tableTitle.setForeground(UIUtils.TEXT_SUB);
        tableTitle.setBorder(new EmptyBorder(10, 0, 10, 0));
        tableCard.add(tableTitle, BorderLayout.NORTH);
        tableCard.add(UIUtils.styledScroll(table), BorderLayout.CENTER);

        center.add(tableCard, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        if (studentCombo.getItemCount() > 0)
            loadEnrollments((Student) studentCombo.getSelectedItem());
    }

    private void loadEnrollments(Student s) {
        model.setRowCount(0);
        for (String[] row : enrollDao.getStudentEnrollments(s.getId()))
            model.addRow(row);
    }
}
