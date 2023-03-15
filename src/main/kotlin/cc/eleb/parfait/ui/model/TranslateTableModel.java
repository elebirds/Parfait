package cc.eleb.parfait.ui.model;

import javax.swing.table.DefaultTableModel;

public class TranslateTableModel extends DefaultTableModel {
    static final Class<?>[] columnTypes = new Class<?>[]{String.class, String.class};
    static final String[] columnNames = new String[]{"中文", "外文"};

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
