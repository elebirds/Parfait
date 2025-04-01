package cc.eleb.parfait.ui.model;

import cc.eleb.parfait.entity.Student;
import cc.eleb.parfait.i18n.Language;

import javax.swing.table.AbstractTableModel;

public class ScoreTableModel extends AbstractTableModel {
    static final Class<?>[] columnTypes = new Class<?>[]{String.class, String.class, String.class, Boolean.class, Double.class, Integer.class};
    static final String[] columnNames = new String[]{Language.trs("score-table-column1"),
            Language.trs("score-table-column2"), Language.trs("score-table-column3"),
            Language.trs("score-table-column4"), Language.trs("score-table-column5"), Language.trs("score-table-column6")};
    private final Student student;

    public ScoreTableModel(Student student) {
        this.student = student;
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

    @Override
    public Object getValueAt(int row, int column) {
        switch (column) {
            case 0:
                return student.getScores().get(row).getName();
            case 1:
                return student.getScores().get(row).getCType();
            case 2:
                return student.getScores().get(row).getAType();
            case 3:
                return student.getScores().get(row).getGpa();
            case 4:
                return student.getScores().get(row).getCredit();
            case 5:
                return student.getScores().get(row).getScore();
        }
        return null;
    }

    @Override
    public int getRowCount() {
        return student.getScores().size();
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        super.setValueAt(aValue, row, column);
        switch (column) {
            case 0:
                student.getScores().get(row).setName(aValue.toString());
                break;
            case 1:
                student.getScores().get(row).setCType(aValue.toString());
                break;
            case 2:
                student.getScores().get(row).setAType(aValue.toString());
                break;
            case 3:
                student.getScores().get(row).setGpa((boolean) aValue);
                break;
            case 4:
                student.getScores().get(row).setCredit((double) aValue);
                break;
            case 5:
                student.getScores().get(row).setScore((int) aValue);
                break;
        }
    }

    @Override
    public void fireTableDataChanged() {
        super.fireTableDataChanged();
    }
}
