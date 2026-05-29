package com.unicore.ui;

import com.unicore.dao.DepartmentDAO;
import com.unicore.dao.StudentDAO;
import com.unicore.models.Department;
import com.unicore.models.Student;
import com.unicore.utils.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class StudentPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;
    private StudentDAO dao = new StudentDAO();
    private DepartmentDAO deptDao = new DepartmentDAO();

    public StudentPanel() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.BG_DARK);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        buildUI();
        loadData();
    }

    private void buildUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIUtils.BG_DARK);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel title = UIUtils.heading("Students");
        header.add(title, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setBackground(UIUtils.BG_DARK);

        searchField = UIUtils.styledField("Search...");
        searchField.setPreferredSize(new Dimension(220, 36));
        searchField.addActionListener(e -> search());

        JButton searchBtn = UIUtils.ghostButton("Search");
        searchBtn.addActionListener(e -> search());

        JButton addBtn = UIUtils.primaryButton("+ Add Student");
        addBtn.addActionListener(e -> showForm(null));

        actions.add(searchField);
        actions.add(searchBtn);
        actions.add(addBtn);
        header.add(actions, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID", "Student ID", "Name", "Email", "Department", "Semester", "CGPA", "Status"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        UIUtils.styleTable(table);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(UIUtils.BG_CARD);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIUtils.BORDER, 1),
            new EmptyBorder(0, 0, 0, 0)
        ));
        tableCard.add(UIUtils.styledScroll(table), BorderLayout.CENTER);

        // Bottom buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        bottom.setBackground(UIUtils.BG_CARD);

        JButton editBtn = UIUtils.ghostButton("✏ Edit");
        editBtn.addActionListener(e -> editSelected());

        JButton deleteBtn = UIUtils.dangerButton("🗑 Delete");
        deleteBtn.addActionListener(e -> deleteSelected());

        bottom.add(editBtn);
        bottom.add(deleteBtn);
        tableCard.add(bottom, BorderLayout.SOUTH);

        add(tableCard, BorderLayout.CENTER);
    }

    private void loadData() {
        fillTable(dao.getAll());
    }

    private void search() {
        String q = searchField.getText().trim();
        fillTable(q.isEmpty() ? dao.getAll() : dao.search(q));
    }

    private void fillTable(List<Student> list) {
        model.setRowCount(0);
        for (Student s : list) {
            model.addRow(new Object[]{
                s.getId(), s.getStudentId(), s.getFullName(),
                s.getEmail(), s.getDepartmentName(),
                s.getSemester(), s.getCgpa(), s.getStatus()
            });
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a student first."); return; }
        int id = (int) model.getValueAt(row, 0);
        showForm(dao.getById(id));
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a student first."); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this student?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) model.getValueAt(row, 0);
            if (dao.delete(id)) loadData();
        }
    }

    private void showForm(Student existing) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            existing == null ? "Add Student" : "Edit Student", true);
        dialog.setSize(480, 560);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIUtils.BG_DARK);
        panel.setBorder(new EmptyBorder(24, 28, 24, 28));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(6, 0, 6, 0);
        gc.gridwidth = 2;
        gc.weightx = 1.0;

        JTextField idField    = UIUtils.styledField("");
        JTextField fnField    = UIUtils.styledField("");
        JTextField lnField    = UIUtils.styledField("");
        JTextField emailField = UIUtils.styledField("");
        JTextField phoneField = UIUtils.styledField("");
        JComboBox<Object> deptCombo = UIUtils.styledCombo();
        JComboBox<Object> semCombo  = UIUtils.styledCombo();
        JComboBox<Object> statusCombo = UIUtils.styledCombo();

        List<Department> depts = deptDao.getAll();
        for (Department d : depts) deptCombo.addItem(d);

        for (int i = 1; i <= 8; i++) semCombo.addItem(i);
        for (String s : new String[]{"active", "inactive", "graduated"}) statusCombo.addItem(s);

        if (existing != null) {
            idField.setText(existing.getStudentId());
            idField.setEditable(false);
            fnField.setText(existing.getFirstName());
            lnField.setText(existing.getLastName());
            emailField.setText(existing.getEmail());
            phoneField.setText(existing.getPhone());
            semCombo.setSelectedItem(existing.getSemester());
            statusCombo.setSelectedItem(existing.getStatus());
            for (int i = 0; i < depts.size(); i++) {
                if (depts.get(i).getId() == existing.getDepartmentId()) {
                    deptCombo.setSelectedIndex(i); break;
                }
            }
        }

        String[][] fields = {
            {"Student ID", null}, {"First Name", null}, {"Last Name", null},
            {"Email", null}, {"Phone", null}
        };
        JTextField[] inputs = {idField, fnField, lnField, emailField, phoneField};

        int row = 0;
        for (int i = 0; i < inputs.length; i++) {
            gc.gridy = row++;
            panel.add(UIUtils.subLabel(fields[i][0]), gc);
            gc.gridy = row++;
            panel.add(inputs[i], gc);
        }

        gc.gridy = row++; panel.add(UIUtils.subLabel("Department"), gc);
        gc.gridy = row++; panel.add(deptCombo, gc);
        gc.gridy = row++; panel.add(UIUtils.subLabel("Semester"), gc);
        gc.gridy = row++; panel.add(semCombo, gc);
        gc.gridy = row++; panel.add(UIUtils.subLabel("Status"), gc);
        gc.gridy = row++; panel.add(statusCombo, gc);

        JButton saveBtn = UIUtils.primaryButton(existing == null ? "Add Student" : "Save Changes");
        gc.gridy = row++;
        gc.insets = new Insets(16, 0, 0, 0);
        panel.add(saveBtn, gc);

        saveBtn.addActionListener(e -> {
            Student s = existing != null ? existing : new Student();
            s.setStudentId(idField.getText().trim());
            s.setFirstName(fnField.getText().trim());
            s.setLastName(lnField.getText().trim());
            s.setEmail(emailField.getText().trim());
            s.setPhone(phoneField.getText().trim());
            s.setDepartmentId(((Department) deptCombo.getSelectedItem()).getId());
            s.setSemester((Integer) semCombo.getSelectedItem());
            s.setStatus((String) statusCombo.getSelectedItem());

            if (existing == null) {
                s.setAdmissionDate(new Date());
                if (dao.add(s)) { dialog.dispose(); loadData(); }
                else JOptionPane.showMessageDialog(dialog, "Failed to add student.");
            } else {
                if (dao.update(s)) { dialog.dispose(); loadData(); }
                else JOptionPane.showMessageDialog(dialog, "Failed to update student.");
            }
        });

        dialog.setContentPane(new JScrollPane(panel));
        dialog.getContentPane().setBackground(UIUtils.BG_DARK);
        dialog.setVisible(true);
    }
}
