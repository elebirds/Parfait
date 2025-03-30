package cc.eleb.parfait.ui.model;

import cc.eleb.parfait.infra.i18n.Language;

import javax.swing.table.DefaultTableModel;

public class TranslateTableModel extends DefaultTableModel {
    static final Class<?>[] columnTypes = new Class<?>[]{String.class, String.class};
    static String[] columnNames = new String[]{Language.trs("translate-table-column1"), Language.trs("translate-table-column1")};

    public void reloadTranslation() {
        columnNames = new String[]{Language.trs("translate-table-column1"), Language.trs("translate-table-column1")};
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
