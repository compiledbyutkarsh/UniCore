package com.unicore.ui;

import com.unicore.dao.DepartmentDAO;
import com.unicore.dao.FacultyDAO;
import com.unicore.models.Department;
import com.unicore.models.Faculty;
import com.unicore.utils.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;
import java.util.List;

public class FacultyPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;
    private FacultyDAO dao = new FacultyDAO();
    private DepartmentDAO deptDao = new DepartmentDAO();

    public FacultyPanel() {
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
        header.add(UIUtils.heading("Faculty"), BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setBackground(UIUtils.BG_DARK);

        searchField = UIUtils.styledField("Search...");
        searchField.setPreferredSize(new Dimension(220, 36));
        searchField.addActionListener(e -> search());

        JButton searchBtn = UIUtils.ghostButton("Search");
        searchBtn.addActionListener(e -> search());

        JButton addBtn = UIUtils.primaryButton("+ Add Faculty");
        addBtn.addActionListener(e -> showForm(null));

        actions.add(searchField);
        actions.add(searchBtn);
        actions.add(addBtn);
        header.add(actions, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        String[] cols = {"ID", "Faculty ID", "Name", "Email", "Department", "Designation", "Salary", "Status"};
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

        JButton deleteBtn = UIUtils.dangerButton("🗑 Delete");
        deleteBtn.addActionListener(e -> deleteSelected());

        bottom.add(editBtn);
        bottom.add(deleteBtn);
        tableCard.add(bottom, BorderLayout.SOUTH);

        add(tableCard, BorderLayout.CENTER);
    }

    private void loadData() { fillTable(dao.getAll()); }

    private void search() {
        String q = searchField.getText().trim();
        fillTable(q.isEmpty() ? dao.getAll() : dao.search(q));
    }

    private void fillTable(List<Faculty> list) {
        model.setRowCount(0);
        for (Faculty f : list) {
            model.addRow(new Object[]{
                f.getId(), f.getFacultyId(), f.getFullName(),
                f.getEmail(), f.getDepartmentName(),
                f.getDesignation(), f.getSalary(), f.getStatus()
            });
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a faculty member first."); return; }
        showForm(dao.getById((int) model.getValueAt(row, 0)));
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a faculty member first."); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this faculty member?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.delete((int) model.getValueAt(row, 0))) loadData();
        }
    }

    private void showForm(Faculty existing) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            existing == null ? "Add Faculty" : "Edit Faculty", true);
        dialog.setSize(480, 580);
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
        JTextField salaryField = UIUtils.styledField("");
        JComboBox<Object> deptCombo = UIUtils.styledCombo();
        JComboBox<Object> desgCombo = UIUtils.styledCombo();
        JComboBox<Object> statusCombo = UIUtils.styledCombo();

        List<Department> depts = deptDao.getAll();
        for (Department d : depts) deptCombo.addItem(d);
        for (String d : new String[]{"Professor", "Associate Professor", "Assistant Professor", "Lecturer"})
            desgCombo.addItem(d);
        for (String s : new String[]{"active", "inactive"}) statusCombo.addItem(s);

        if (existing != null) {
            idField.setText(existing.getFacultyId());
            idField.setEditable(false);
            fnField.setText(existing.getFirstName());
            lnField.setText(existing.getLastName());
            emailField.setText(existing.getEmail());
            phoneField.setText(existing.getPhone());
            salaryField.setText(String.valueOf(existing.getSalary()));
            desgCombo.setSelectedItem(existing.getDesignation());
            statusCombo.setSelectedItem(existing.getStatus());
            for (int i = 0; i < depts.size(); i++) {
                if (depts.get(i).getId() == existing.getDepartmentId()) {
                    deptCombo.setSelectedIndex(i); break;
                }
            }
        }

        String[] labels = {"Faculty ID", "First Name", "Last Name", "Email", "Phone", "Salary"};
        JTextField[] inputs = {idField, fnField, lnField, emailField, phoneField, salaryField};

        int row = 0;
        for (int i = 0; i < inputs.length; i++) {
            gc.gridy = row++; panel.add(UIUtils.subLabel(labels[i]), gc);
            gc.gridy = row++; panel.add(inputs[i], gc);
        }

        gc.gridy = row++; panel.add(UIUtils.subLabel("Department"), gc);
        gc.gridy = row++; panel.add(deptCombo, gc);
        gc.gridy = row++; panel.add(UIUtils.subLabel("Designation"), gc);
        gc.gridy = row++; panel.add(desgCombo, gc);
        gc.gridy = row++; panel.add(UIUtils.subLabel("Status"), gc);
        gc.gridy = row++; panel.add(statusCombo, gc);

        JButton saveBtn = UIUtils.primaryButton(existing == null ? "Add Faculty" : "Save Changes");
        gc.gridy = row++;
        gc.insets = new Insets(16, 0, 0, 0);
        panel.add(saveBtn, gc);

        saveBtn.addActionListener(e -> {
            Faculty f = existing != null ? existing : new Faculty();
            f.setFacultyId(idField.getText().trim());
            f.setFirstName(fnField.getText().trim());
            f.setLastName(lnField.getText().trim());
            f.setEmail(emailField.getText().trim());
            f.setPhone(phoneField.getText().trim());
            f.setDepartmentId(((Department) deptCombo.getSelectedItem()).getId());
            f.setDesignation((String) desgCombo.getSelectedItem());
            f.setStatus((String) statusCombo.getSelectedItem());
            try { f.setSalary(Double.parseDouble(salaryField.getText().trim())); }
            catch (NumberFormatException ex) { f.setSalary(0); }

            if (existing == null) {
                f.setJoinDate(new Date());
                if (dao.add(f)) { dialog.dispose(); loadData(); }
                else JOptionPane.showMessageDialog(dialog, "Failed to add faculty.");
            } else {
                if (dao.update(f)) { dialog.dispose(); loadData(); }
                else JOptionPane.showMessageDialog(dialog, "Failed to update faculty.");
            }
        });

        dialog.setContentPane(new JScrollPane(panel));
        dialog.setVisible(true);
    }
}
