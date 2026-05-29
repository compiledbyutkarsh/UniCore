package com.unicore.ui;

import com.unicore.dao.DepartmentDAO;
import com.unicore.models.Department;
import com.unicore.utils.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DepartmentPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private DepartmentDAO dao = new DepartmentDAO();

    public DepartmentPanel() {
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
        header.add(UIUtils.heading("Departments"), BorderLayout.WEST);

        JButton addBtn = UIUtils.primaryButton("+ Add Department");
        addBtn.addActionListener(e -> showForm(null));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actions.setBackground(UIUtils.BG_DARK);
        actions.add(addBtn);
        header.add(actions, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        String[] cols = {"ID", "Code", "Name", "Head"};
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
        for (Department d : dao.getAll())
            model.addRow(new Object[]{d.getId(), d.getCode(), d.getName(), d.getHeadName()});
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a department first."); return; }
        int id = (int) model.getValueAt(row, 0);
        Department d = new Department();
        d.setId(id);
        d.setCode((String) model.getValueAt(row, 1));
        d.setName((String) model.getValueAt(row, 2));
        d.setHeadName((String) model.getValueAt(row, 3));
        showForm(d);
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a department first."); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this department?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.delete((int) model.getValueAt(row, 0))) loadData();
        }
    }

    private void showForm(Department existing) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            existing == null ? "Add Department" : "Edit Department", true);
        dialog.setSize(400, 340);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIUtils.BG_DARK);
        panel.setBorder(new EmptyBorder(24, 28, 24, 28));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(6, 0, 6, 0);
        gc.gridwidth = 2;
        gc.weightx = 1.0;

        JTextField nameField = UIUtils.styledField("");
        JTextField codeField = UIUtils.styledField("");
        JTextField headField = UIUtils.styledField("");

        if (existing != null) {
            nameField.setText(existing.getName());
            codeField.setText(existing.getCode());
            headField.setText(existing.getHeadName());
        }

        int row = 0;
        gc.gridy = row++; panel.add(UIUtils.subLabel("Department Name"), gc);
        gc.gridy = row++; panel.add(nameField, gc);
        gc.gridy = row++; panel.add(UIUtils.subLabel("Code"), gc);
        gc.gridy = row++; panel.add(codeField, gc);
        gc.gridy = row++; panel.add(UIUtils.subLabel("Head Name"), gc);
        gc.gridy = row++; panel.add(headField, gc);

        JButton saveBtn = UIUtils.primaryButton(existing == null ? "Add" : "Save");
        gc.gridy = row++;
        gc.insets = new Insets(16, 0, 0, 0);
        panel.add(saveBtn, gc);

        saveBtn.addActionListener(e -> {
            Department d = existing != null ? existing : new Department();
            d.setName(nameField.getText().trim());
            d.setCode(codeField.getText().trim());
            d.setHeadName(headField.getText().trim());
            boolean ok = existing == null ? dao.add(d) : dao.update(d);
            if (ok) { dialog.dispose(); loadData(); }
            else JOptionPane.showMessageDialog(dialog, "Operation failed.");
        });

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }
}
