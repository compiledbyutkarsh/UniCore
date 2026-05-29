package com.unicore.ui;

import com.unicore.dao.CourseDAO;
import com.unicore.dao.DepartmentDAO;
import com.unicore.dao.FacultyDAO;
import com.unicore.models.Course;
import com.unicore.models.Department;
import com.unicore.models.Faculty;
import com.unicore.utils.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CoursePanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private CourseDAO dao = new CourseDAO();
    private DepartmentDAO deptDao = new DepartmentDAO();
    private FacultyDAO facDao = new FacultyDAO();

    public CoursePanel() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.BG_DARK);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        buildUI();
        loadData();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIUtils.BG_DARK);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));
        header.add(UIUtils.heading("Courses"), BorderLayout.WEST);

        JButton addBtn = UIUtils.primaryButton("+ Add Course");
        addBtn.addActionListener(e -> showForm(null));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actions.setBackground(UIUtils.BG_DARK);
        actions.add(addBtn);
        header.add(actions, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        String[] cols = {"ID", "Code", "Course Name", "Credits", "Department", "Faculty", "Semester", "Max"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UIUtils.styleTable(table);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setMinWidth(0);

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(UIUtils.BG_CARD);
        tableCard.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER, 1));
        tableCard.add(UIUtils.styledScroll(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        bottom.setBackground(UIUtils.BG_CARD);
        JButton editBtn = UIUtils.ghostButton("✏ Edit");
        editBtn.addActionListener(e -> editSelected());
        JButton delBtn = UIUtils.dangerButton("🗑 Delete");
        delBtn.addActionListener(e -> deleteSelected());
        bottom.add(editBtn);
        bottom.add(delBtn);
        tableCard.add(bottom, BorderLayout.SOUTH);
        add(tableCard, BorderLayout.CENTER);
    }

    private void loadData() {
        model.setRowCount(0);
        for (Course c : dao.getAll())
            model.addRow(new Object[]{
                c.getId(), c.getCourseCode(), c.getCourseName(),
                c.getCredits(), c.getDepartmentName(), c.getFacultyName(),
                c.getSemester(), c.getMaxStudents()
            });
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a course first."); return; }
        int id = (int) model.getValueAt(row, 0);
        for (Course c : dao.getAll()) {
            if (c.getId() == id) { showForm(c); return; }
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a course first."); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this course?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.delete((int) model.getValueAt(row, 0))) loadData();
        }
    }

    private void showForm(Course existing) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            existing == null ? "Add Course" : "Edit Course", true);
        dialog.setSize(460, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIUtils.BG_DARK);
        panel.setBorder(new EmptyBorder(24, 28, 24, 28));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(6, 0, 6, 0);
        gc.gridwidth = 2;
        gc.weightx = 1.0;

        JTextField codeField    = UIUtils.styledField("");
        JTextField nameField    = UIUtils.styledField("");
        JTextField creditsField = UIUtils.styledField("");
        JTextField maxField     = UIUtils.styledField("");
        JComboBox<Object> deptCombo = UIUtils.styledCombo();
        JComboBox<Object> facCombo  = UIUtils.styledCombo();
        JComboBox<Object> semCombo  = UIUtils.styledCombo();

        List<Department> depts = deptDao.getAll();
        List<Faculty> facs = facDao.getAll();
        for (Department d : depts) deptCombo.addItem(d);
        for (Faculty f : facs) facCombo.addItem(f);
        for (int i = 1; i <= 8; i++) semCombo.addItem(i);

        if (existing != null) {
            codeField.setText(existing.getCourseCode());
            nameField.setText(existing.getCourseName());
            creditsField.setText(String.valueOf(existing.getCredits()));
            maxField.setText(String.valueOf(existing.getMaxStudents()));
            semCombo.setSelectedItem(existing.getSemester());
            for (int i = 0; i < depts.size(); i++)
                if (depts.get(i).getId() == existing.getDepartmentId()) { deptCombo.setSelectedIndex(i); break; }
            for (int i = 0; i < facs.size(); i++)
                if (facs.get(i).getId() == existing.getFacultyId()) { facCombo.setSelectedIndex(i); break; }
        }

        int row = 0;
        gc.gridy = row++; panel.add(UIUtils.subLabel("Course Code"), gc);
        gc.gridy = row++; panel.add(codeField, gc);
        gc.gridy = row++; panel.add(UIUtils.subLabel("Course Name"), gc);
        gc.gridy = row++; panel.add(nameField, gc);
        gc.gridy = row++; panel.add(UIUtils.subLabel("Credits"), gc);
        gc.gridy = row++; panel.add(creditsField, gc);
        gc.gridy = row++; panel.add(UIUtils.subLabel("Department"), gc);
        gc.gridy = row++; panel.add(deptCombo, gc);
        gc.gridy = row++; panel.add(UIUtils.subLabel("Faculty"), gc);
        gc.gridy = row++; panel.add(facCombo, gc);
        gc.gridy = row++; panel.add(UIUtils.subLabel("Semester"), gc);
        gc.gridy = row++; panel.add(semCombo, gc);
        gc.gridy = row++; panel.add(UIUtils.subLabel("Max Students"), gc);
        gc.gridy = row++; panel.add(maxField, gc);

        JButton saveBtn = UIUtils.primaryButton(existing == null ? "Add Course" : "Save");
        gc.gridy = row++;
        gc.insets = new Insets(16, 0, 0, 0);
        panel.add(saveBtn, gc);

        saveBtn.addActionListener(e -> {
            Course c = existing != null ? existing : new Course();
            c.setCourseCode(codeField.getText().trim());
            c.setCourseName(nameField.getText().trim());
            try { c.setCredits(Integer.parseInt(creditsField.getText().trim())); } catch (Exception ex) { c.setCredits(3); }
            try { c.setMaxStudents(Integer.parseInt(maxField.getText().trim())); } catch (Exception ex) { c.setMaxStudents(60); }
            c.setDepartmentId(((Department) deptCombo.getSelectedItem()).getId());
            c.setFacultyId(((Faculty) facCombo.getSelectedItem()).getId());
            c.setSemester((Integer) semCombo.getSelectedItem());
            boolean ok = existing == null ? dao.add(c) : dao.update(c);
            if (ok) { dialog.dispose(); loadData(); }
            else JOptionPane.showMessageDialog(dialog, "Operation failed.");
        });

        dialog.setContentPane(new JScrollPane(panel));
        dialog.setVisible(true);
    }
}
