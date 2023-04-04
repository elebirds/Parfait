package cc.eleb.parfait.ui.table

import cc.eleb.parfait.entity.Student
import cc.eleb.parfait.ui.model.ScoreTableModel
import java.awt.Dimension
import javax.swing.DefaultCellEditor
import javax.swing.DefaultComboBoxModel
import javax.swing.JComboBox
import javax.swing.JTable

class ScoreDataTable(private val student: Student) : JTable() {
    init {
        preferredViewportSize = Dimension(600, 400)
        this.model = ScoreTableModel(student)
        val cm = getColumnModel()
        cm.getColumn(0).minWidth = 150
        cm.getColumn(1).minWidth = 150
        cm.getColumn(1).maxWidth = 200
        cm.getColumn(1).cellEditor = DefaultCellEditor(
            JComboBox(
                DefaultComboBoxModel(
                    arrayOf(
                        "专业基础课程",
                        "专业主干课程",
                        "专业方向课程",
                        "专业拓展课程",
                        "实践类课程",
                        "大类平台课程",
                        "通识教育必修课程"
                    )
                )
            )
        )
        cm.getColumn(2).minWidth = 80
        cm.getColumn(2).maxWidth = 80
        cm.getColumn(2).cellEditor = DefaultCellEditor(
            JComboBox(
                DefaultComboBoxModel(
                    arrayOf(
                        "考试",
                        "考察"
                    )
                )
            )
        )
        cm.getColumn(3).minWidth = 60
        cm.getColumn(3).maxWidth = 60
    }
}