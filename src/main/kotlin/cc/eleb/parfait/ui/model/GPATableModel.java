package cc.eleb.parfait.ui.model;

import cc.eleb.parfait.infra.i18n.Language;

import javax.swing.table.DefaultTableModel;

public class GPATableModel extends DefaultTableModel {
    static final Class<?>[] columnTypes = new Class<?>[]{Integer.class, Double.class};
    static String[] columnNames = new String[]{Language.trs("gpa-table-column1"), Language.trs("gpa-table-column2")};

    public void reloadTranslation() {
        columnNames = new String[]{Language.trs("gpa-table-column1"), Language.trs("gpa-table-column2")};
    }

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
