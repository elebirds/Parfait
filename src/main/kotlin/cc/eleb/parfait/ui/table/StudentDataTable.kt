package cc.eleb.parfait.ui.table

import cc.eleb.parfait.i18n.trs
import cc.eleb.parfait.ui.model.StudentTableModel
import java.awt.Dimension
import javax.swing.DefaultCellEditor
import javax.swing.DefaultComboBoxModel
import javax.swing.JComboBox
import javax.swing.JTable
import javax.swing.table.TableColumn
import javax.swing.table.TableModel

class StudentDataTable : JTable(StudentTableModel()) {
    init {
        preferredViewportSize = Dimension(600, 400)
    }

    override fun createDefaultColumnsFromModel() {
        val m: TableModel = this.model
        val cm = getColumnModel()
        while (cm.columnCount > 0) {
            cm.removeColumn(cm.getColumn(0))
        }
        for (i in 0 until m.columnCount) {
            val newColumn = TableColumn(i)
            if (i == 2) {
                newColumn.cellEditor =
                    DefaultCellEditor(
                        JComboBox(
                            DefaultComboBoxModel(
                                arrayOf("global-unknown".trs(), "global-sex-m".trs(), "global-sex-f".trs())
                            )
                        )
                    )
            } else if (i == 3) {
                newColumn.cellEditor =
                    DefaultCellEditor(
                        JComboBox(
                            DefaultComboBoxModel(
                                arrayOf(
                                    "global-status-in".trs(),
                                    "global-status-out".trs()
                                )
                            )
                        )
                    )
            }
            this.addColumn(newColumn)
        }
    }

    override fun getModel(): StudentTableModel {
        return super.getModel() as StudentTableModel
    }
}