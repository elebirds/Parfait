/*
 * Created by JFormDesigner on Fri Mar 10 15:30:40 CST 2023
 */
package cc.eleb.parfait.ui.panel

import cc.eleb.parfait.entity.Student
import net.miginfocom.swing.MigLayout
import java.awt.Dimension
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.File
import java.util.*
import javax.swing.*
import javax.swing.filechooser.FileFilter
import javax.swing.table.DefaultTableModel
import javax.swing.table.TableColumnModel

/**
 * @author hhmcn
 */
class ScoreDataPanel(private val student:Student) : JPanel() {
    fun reload(){
        (table1.model as DefaultTableModel).let {dtm->
            dtm.dataVector.let { vt ->
                vt.clear()
                student.scores.forEach { (_, u) ->
                    vt.add(Vector<Any?>().also {
                        it.add(u.name)
                        it.add(u.cType)
                        it.add(u.aType)
                        it.add(u.gpa)
                        it.add(u.credit)
                        it.add(u.score)
                    })
                }
            }
            dtm.fireTableDataChanged()
        }
    }

    private fun removeScoreMouseClicked(e:MouseEvent){
        if (e.button != MouseEvent.BUTTON1) return
        if (table1.selectedRows.isEmpty()) {
            JOptionPane.showMessageDialog(
                this, "您未选中任何成绩，无法进行删除操作。", "错误",
                JOptionPane.ERROR_MESSAGE
            )
            return
        }
        for (selectedRow: Int in table1.selectedRows) {
            val slt = table1.selectedRow
            student.scores.remove((table1.model as DefaultTableModel).getValueAt(slt,0))
            (table1.model as DefaultTableModel).removeRow(slt)
        }
    }

    private fun impoScoreMouseClicked(e: MouseEvent) {
        if (e.button != MouseEvent.BUTTON1) return
        val fd = JFileChooser()
        fd.fileFilter = object : FileFilter() {
            override fun accept(f: File): Boolean {
                return f.isDirectory || f.name.endsWith(".xlsx")
            }

            override fun getDescription(): String {
                return "Excel文件(.xlsx)"
            }
        }
        fd.isMultiSelectionEnabled = false
        fd.fileSelectionMode = JFileChooser.FILES_ONLY
        val res: Int = fd.showOpenDialog(this)
        if (res == JFileChooser.APPROVE_OPTION) {
            if (!fd.selectedFile.toString().endsWith(".xlsx")){
                JOptionPane.showMessageDialog(
                    this,
                    "请选择Excel表格文件！",
                    "导入失败",
                    JOptionPane.ERROR_MESSAGE
                )
                return
            }
            try {
                //CoroutineScope(Dispatchers.IO).launch {
                student.addScoresFromFile(fd.selectedFile)
                this.reload()
                //}
            } catch (e: Exception) {
                JOptionPane.showMessageDialog(
                    this,
                    "请选择正确的成绩数据。\n${e.stackTraceToString()}",
                    "导入失败",
                    JOptionPane.ERROR_MESSAGE
                )
            }
        }
    }

    private fun initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Evaluation license - csgo fps
        layout = MigLayout(
            "hidemode 3",  // columns
            "[fill]" + "[fill]" + "[fill]",  // rows
            "[]" + "[]" + "[]"
        )
        table1.preferredScrollableViewportSize = Dimension(600, 400)
        table1.model = object : DefaultTableModel(arrayOf(arrayOf("","","",false,null,null)), arrayOf("课程名称", "课程类型", "考核类型", "学位课", "学分", "成绩")) {
            var columnTypes: Array<Class<*>> = arrayOf(String::class.java, String::class.java, String::class.java, Boolean::class.java, Double::class.java, Int::class.java)
            var columnEditable: BooleanArray = booleanArrayOf(false, true, true, true, true, true)

            override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean {
                return columnEditable[columnIndex]
            }

            override fun getColumnClass(columnIndex: Int): Class<*> {
                return columnTypes[columnIndex]
            }
        }
        //this.reload()
        val cm: TableColumnModel = table1.columnModel
        cm.getColumn(0).minWidth = 200
        cm.getColumn(1).minWidth = 100
        cm.getColumn(1).maxWidth = 100
        cm.getColumn(1).cellEditor = DefaultCellEditor(JComboBox(DefaultComboBoxModel(arrayOf("专业基础课程", "专业主干课程", "专业方向课程", "专业拓展课程", "实践类课程", "大类平台课程", "通识教育必修课程"))))
        cm.getColumn(2).minWidth = 80
        cm.getColumn(2).maxWidth = 80
        cm.getColumn(2).cellEditor = DefaultCellEditor(JComboBox(DefaultComboBoxModel(arrayOf("考试", "考察"))))
        cm.getColumn(3).minWidth = 60
        cm.getColumn(3).maxWidth = 60
        scrollPane1.setViewportView(table1)
        add(scrollPane1, "cell 1 0")
        panel1.layout = MigLayout(
            "hidemode 3",  // columns
            "[fill]",  // rows
            "[]" + "[]" + "[]" + "[]"
        )
        panel1.add(button1, "cell 0 0")
        panel1.add(button2, "cell 0 1")
        panel1.add(button3, "cell 0 2")
        //panel1.add(button4, "cell 0 3")
        add(panel1, "cell 1 0")
    }


    private val scrollPane1 = JScrollPane()
    private val table1 = JTable()
    private val panel1 = JPanel()
    private val button1 = JButton().also {
        it.text = "添加成绩"
    }
    private val button2 = JButton().also {
        it.text = "删除成绩"
        it.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                removeScoreMouseClicked(e)
            }
        })
    }
    private val button3 = JButton().also {
        it.text = "导入成绩"
        it.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                impoScoreMouseClicked(e)
            }
        })
    }
    //private val button4 = JButton().also {
    //    it.text = "保存"
    //}

    init {
        initComponents()
    }
}