package cc.eleb.parfait.ui.table

import cc.eleb.parfait.ui.model.StudentTableModel
import java.awt.Dimension
import javax.swing.DefaultCellEditor
import javax.swing.DefaultComboBoxModel
import javax.swing.JComboBox
import javax.swing.JTable
import javax.swing.table.DefaultTableModel

class StudentDataTable : JTable(StudentTableModel()) {
    init {
        preferredViewportSize = Dimension(600, 400)
        this.getColumnModel().getColumn(2).cellEditor = DefaultCellEditor(JComboBox(DefaultComboBoxModel(arrayOf("未知", "男", "女"))))
        this.getColumnModel().getColumn(3).cellEditor = DefaultCellEditor(JComboBox(DefaultComboBoxModel(arrayOf("在籍", "毕业"))))
    }

    override fun getModel(): StudentTableModel {
        return super.getModel() as StudentTableModel
    }
}