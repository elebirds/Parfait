package cc.eleb.parfait.ui.model;

import cc.eleb.parfait.entity.Student;

import javax.swing.table.DefaultTableModel;

public class GPATableModel extends DefaultTableModel {
    static final Class<?>[] columnTypes = new Class<?>[]{Integer.class, Double.class};
    static final String[] columnNames = new String[]{"成绩（大于等于此值）", "GPA"};

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnTypes[columnIndex];
    }

    @Override
    public int getColumnCount() {
        return columnTypes.length;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return true;
    }

}
