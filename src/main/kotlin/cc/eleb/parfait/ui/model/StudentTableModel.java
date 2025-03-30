package cc.eleb.parfait.ui.model;

import cc.eleb.parfait.entity.Student;
import cc.eleb.parfait.infra.i18n.Language;

import javax.swing.table.AbstractTableModel;

public class StudentTableModel extends AbstractTableModel {
    static final boolean[] editable = new boolean[]{false, true, true, true, true, true, true, true, false, false, false};
    static final Class<?>[] columnTypes = new Class<?>[]{Integer.class, String.class, String.class, String.class, Integer.class, String.class, String.class, String.class, Double.class, Double.class, Double.class};
    static String[] columnNames = new String[]{Language.trs("student-table-column1"), Language.trs("student-table-column2"),
            Language.trs("student-table-column3"), Language.trs("student-table-column4"),
            Language.trs("student-table-column5"), Language.trs("student-table-column6"),
            Language.trs("student-table-column7"), Language.trs("student-table-column8"),
            Language.trs("student-table-column9"), Language.trs("student-table-column10"),
            Language.trs("student-table-column11")};

    public void reloadTranslation() {
        columnNames = new String[]{Language.trs("student-table-column1"), Language.trs("student-table-column2"),
                Language.trs("student-table-column3"), Language.trs("student-table-column4"),
                Language.trs("student-table-column5"), Language.trs("student-table-column6"),
                Language.trs("student-table-column7"), Language.trs("student-table-column8"),
                Language.trs("student-table-column9"), Language.trs("student-table-column10"),
                Language.trs("student-table-column11")};
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
        return editable[column];
    }

    @Override
    public Object getValueAt(int row, int column) {
        Student student = (Student) Student.getStudents().values().toArray()[row];
        return switch (column) {
            case 0 -> student.getId();
            case 1 -> student.getName();
            case 2 -> student.getGenderS();
            case 3 -> student.getStatusS();
            case 4 -> student.getGrade();
            case 5 -> student.getSchool();
            case 6 -> student.getProfession();
            case 7 -> student.getClazz();
            case 8 -> student.getWeightedMean();
            case 9 -> student.getSimpleMean();
            case 10 -> student.getGpa();
            default -> null;
        };
    }

    @Override
    public int getRowCount() {
        return Student.getStudents().size();
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        Student student = (Student) Student.getStudents().values().toArray()[row];
        switch (column) {
            case 1:
                student.setName(aValue.toString());
                break;
            case 2:
                student.setGender(aValue.toString().equals(Language.trs("global-unknown")) ? 0 : aValue.toString().equals(Language.trs("global-sex-m")) ? 1 : 2);
                break;
            case 3:
                student.setStatus(aValue.toString().equals(Language.trs("global-status-in")) ? 0 : 1);
                break;
            case 4:
                student.setGrade((int) aValue);
                break;
            case 5:
                student.setSchool(aValue.toString());
                break;
            case 6:
                String s = aValue.toString().replace('(', '（').replace(')', '）');
                student.setProfession(s);
                super.setValueAt(s, row, column);
                return;
            case 7:
                student.setClazz(aValue.toString());
                break;
        }
        super.setValueAt(aValue, row, column);
    }

    @Override
    public void fireTableDataChanged() {
        super.fireTableDataChanged();
    }
}
