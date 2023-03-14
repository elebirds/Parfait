package cc.eleb.parfait.ui.model;

import cc.eleb.parfait.entity.Student;

import javax.swing.table.AbstractTableModel;

public class StudentTableModel extends AbstractTableModel {
    static final boolean[] editable = new boolean[]{false, true, true, true, true, true, true, true, false};
    static final Class<?>[] columnTypes = new Class<?>[]{Integer.class, String.class, String.class, String.class, Integer.class, String.class, String.class, String.class, Double.class};
    static final String[] columnNames = new String[]{"学号", "姓名", "性别", "学籍", "年级", "学院", "专业", "班级", "加权平均分"};

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
        switch (column){
            case 0:
                return student.getId();
            case 1:
                return student.getName();
            case 2:
                return student.getGenderT();
            case 3:
                return student.getStatusT();
            case 4:
                return student.getGrade();
            case 5:
                return student.getSchool();
            case 6:
                return student.getProfession();
            case 7:
                return student.getClazz();
            case 8:
                return student.getWeightedMean();
        }
        return null;
    }

    @Override
    public int getRowCount() {
        return Student.getStudents().size();
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        Student student = (Student) Student.getStudents().values().toArray()[row];
        super.setValueAt(aValue, row, column);
        switch (column){
            case 1:
                student.setName(aValue.toString());
                break;
            case 2:
                student.setGender(aValue.toString().equals("未知")?0:aValue.toString().equals("男")?1:2);
                break;
            case 3:
                student.setStatus(aValue.toString().equals("在籍")?0:1);
                break;
            case 4:
                student.setGrade((int)aValue);
                break;
            case 5:
                student.setSchool(aValue.toString());
                break;
            case 6:
                student.setProfession(aValue.toString());
                break;
            case 7:
                student.setClazz(aValue.toString());
                break;
        }
    }

    @Override
    public void fireTableDataChanged() {
        super.fireTableDataChanged();
    }
}