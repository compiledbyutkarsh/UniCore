package com.unicore.utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class UIUtils {

    public static final Color BG_DARK      = new Color(15, 17, 26);
    public static final Color BG_PANEL     = new Color(22, 25, 37);
    public static final Color BG_CARD      = new Color(30, 34, 50);
    public static final Color ACCENT       = new Color(99, 102, 241);
    public static final Color ACCENT_HOVER = new Color(79, 82, 221);
    public static final Color TEXT_PRIMARY = new Color(236, 237, 245);
    public static final Color TEXT_SUB     = new Color(140, 145, 170);
    public static final Color BORDER       = new Color(45, 50, 70);
    public static final Color SUCCESS      = new Color(34, 197, 94);
    public static final Color DANGER       = new Color(239, 68, 68);
    public static final Color WARNING      = new Color(234, 179, 8);

    public static final Font FONT_TITLE   = new Font("SF Pro Display", Font.BOLD, 22);
    public static final Font FONT_HEADING = new Font("SF Pro Display", Font.BOLD, 15);
    public static final Font FONT_BODY    = new Font("SF Pro Text", Font.PLAIN, 13);
    public static final Font FONT_SMALL   = new Font("SF Pro Text", Font.PLAIN, 11);
    public static final Font FONT_MONO    = new Font("JetBrains Mono", Font.PLAIN, 12);

    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_HEADING);
        btn.setBorder(new EmptyBorder(10, 22, 10, 22));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setBorderPainted(false);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(ACCENT_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(ACCENT);
            }
        });
        return btn;
    }

    public static JButton dangerButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(DANGER);
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_HEADING);
        btn.setBorder(new EmptyBorder(10, 22, 10, 22));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        return btn;
    }

    public static JButton ghostButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(BG_CARD);
        btn.setForeground(TEXT_PRIMARY);
        btn.setFont(FONT_BODY);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            new EmptyBorder(8, 16, 8, 16)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setBorderPainted(true);
        return btn;
    }

    public static JTextField styledField(String placeholder) {
        JTextField field = new JTextField();
        field.setBackground(BG_DARK);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(TEXT_PRIMARY);
        field.setFont(FONT_BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }

    public static JComboBox<Object> styledCombo() {
        JComboBox<Object> combo = new JComboBox<>();
        combo.setBackground(BG_DARK);
        combo.setForeground(TEXT_PRIMARY);
        combo.setFont(FONT_BODY);
        combo.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        return combo;
    }

    public static JLabel heading(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_TITLE);
        lbl.setForeground(TEXT_PRIMARY);
        return lbl;
    }

    public static JLabel subLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_BODY);
        lbl.setForeground(TEXT_SUB);
        return lbl;
    }

    public static void styleTable(JTable table) {
        table.setBackground(BG_PANEL);
        table.setForeground(TEXT_PRIMARY);
        table.setFont(FONT_BODY);
        table.setRowHeight(36);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(99, 102, 241, 60));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setBackground(BG_CARD);
        header.setForeground(TEXT_SUB);
        header.setFont(FONT_SMALL);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                setBackground(row % 2 == 0 ? BG_PANEL : BG_CARD);
                if (sel) setBackground(new Color(99, 102, 241, 60));
                setBorder(new EmptyBorder(0, 12, 0, 12));
                return this;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    public static JScrollPane styledScroll(Component c) {
        JScrollPane scroll = new JScrollPane(c);
        scroll.setBackground(BG_PANEL);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        scroll.getViewport().setBackground(BG_PANEL);
        scroll.getVerticalScrollBar().setBackground(BG_DARK);
        return scroll;
    }

    public static JPanel card(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            new EmptyBorder(16, 16, 16, 16)
        ));
        return panel;
    }
}
