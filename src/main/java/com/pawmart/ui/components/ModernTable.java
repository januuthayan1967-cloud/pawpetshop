package com.pawmart.ui.components;

import com.pawmart.ui.theme.PawMartTheme;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

/**
 * Modern styled JTable with zebra striping, custom header, and comfortable row padding.
 */
public class ModernTable extends JTable {

    private final Color alternateColor = new Color(248, 250, 252);

    public ModernTable(TableModel model) {
        super(model);
        initStyle();
    }

    private void initStyle() {
        setRowHeight(38);
        setFont(PawMartTheme.FONT_BODY);
        setForeground(PawMartTheme.TEXT_DARK);
        setSelectionBackground(PawMartTheme.TEAL_LIGHT);
        setSelectionForeground(PawMartTheme.TEAL_PRIMARY);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        setFillsViewportHeight(true);

        // Header Styling
        JTableHeader header = getTableHeader();
        header.setFont(PawMartTheme.FONT_BODY_BOLD);
        header.setBackground(PawMartTheme.TEAL_PRIMARY);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 38));
        header.setReorderingAllowed(false);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(PawMartTheme.TEAL_PRIMARY);
                c.setForeground(Color.WHITE);
                setFont(PawMartTheme.FONT_BODY_BOLD);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return c;
            }
        });
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component c = super.prepareRenderer(renderer, row, column);
        if (!isRowSelected(row)) {
            c.setBackground(row % 2 == 0 ? Color.WHITE : alternateColor);
            c.setForeground(PawMartTheme.TEXT_DARK);
        } else {
            c.setBackground(PawMartTheme.TEAL_LIGHT);
            c.setForeground(PawMartTheme.TEAL_PRIMARY);
        }
        return c;
    }

    public static JScrollPane createScrollPane(JTable table) {
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(PawMartTheme.CARD_BORDER, 1));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }
}
