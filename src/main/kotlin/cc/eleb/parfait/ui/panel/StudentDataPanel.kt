/*
 * Created by JFormDesigner on Fri Mar 10 10:27:17 CST 2023
 */
package cc.eleb.parfait.ui.panel

import cc.eleb.parfait.config.ParConfig
import cc.eleb.parfait.entity.Student
import cc.eleb.parfait.ui.frame.ScoreFrame
import kotlinx.coroutines.*
import net.miginfocom.swing.MigLayout
import java.awt.Dimension
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
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
class StudentDataPanel : JPanel() {
    fun reload(){
        (table1.model as DefaultTableModel).let {dtm->
            dtm.dataVector.let { vt ->
                vt.clear()
                Student.students.forEach { (t, u) ->
                    vt.add(Vector<Any?>().also {
                        it.add(t)
                        it.add(u.name)
                        it.add(u.genderT)
                        it.add(u.statusT)
                        it.add(u.grade)
                        it.add(u.school)
                        it.add(u.profession)
                        it.add(u.clazz)
                        it.add(u.weightedMean)
                    })
                }
            }
            dtm.fireTableDataChanged()
        }
    }

    private fun addStudentMouseClicked(e: MouseEvent) {
        if (e.button != MouseEvent.BUTTON1) return
        if(!ParConfig.checkInited())return
        (table1.model as DefaultTableModel).addRow(arrayOf<Any?>(null, null, "未知"))
        JOptionPane.showInputDialog(this, "测试", "", JOptionPane.INFORMATION_MESSAGE)
        JOptionPane.showMessageDialog(null, "呃呃", "就", JOptionPane.INFORMATION_MESSAGE)
    }

    private fun deleteStudentMouseClicked(e: MouseEvent) {
        if (e.button != MouseEvent.BUTTON1) return
        if(!ParConfig.checkInited())return
        for (selectedRow: Int in table1.selectedRows) {
            (table1.model as DefaultTableModel).removeRow(table1.selectedRow)
        }
    }

    private fun impoStudentMouseClicked(e: MouseEvent) {
        if (e.button != MouseEvent.BUTTON1) return
        if(!ParConfig.checkInited())return
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
                    Student.addStudentsFromFile(fd.selectedFile)
                //}
            } catch (e: Exception) {
                JOptionPane.showMessageDialog(
                    this,
                    "请选择正确的学生名单。\n${e.stackTraceToString()}",
                    "导入失败",
                    JOptionPane.ERROR_MESSAGE
                )
            }
        }
    }

    private fun editScoreMouseClicked(e: MouseEvent) {
        if (e.button != MouseEvent.BUTTON1) return
        if(!ParConfig.checkInited())return
        val sf: ScoreFrame = ScoreFrame()
        sf.isVisible = true
    }

    private fun generateWordMouseClicked(e: MouseEvent) {
        if (e.button != MouseEvent.BUTTON1) return
        if(!ParConfig.checkInited())return
        //TODO:generateWord
    }

    private fun impoStudentFromStringMouseClicked(e: MouseEvent) {
        if (e.button != MouseEvent.BUTTON1) return
        if(!ParConfig.checkInited())return
        // TODO add your code here
    }

    private fun expoToStringMouseClicked(e: MouseEvent) {
        if (e.button != MouseEvent.BUTTON1) return
        if(!ParConfig.checkInited())return
        if (table1.selectedRows.isEmpty()) {
            JOptionPane.showMessageDialog(
                this, "您未选中任何学生，无法导出数据为文本。", "错误",
                JOptionPane.ERROR_MESSAGE
            )
            return
        }
        val a: StringBuilder = StringBuilder()
        for (selectedRow: Int in table1.selectedRows) {
            val si: Any =
                (table1.model as DefaultTableModel).dataVector.elementAt(table1.selectedRow).elementAt(0) ?: continue
            val sid: Int = si as Int
            a.append(si).append("sisis").append("\n")
        }
        if (JOptionPane.showConfirmDialog(
                this, a.toString(), "是否复制到剪贴板", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE
            ) == JOptionPane.OK_OPTION
        ) {
            Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(a.toString()), null)
        }
        // TODO add your code here
    }

    private fun initComponents() {
        label1 = JLabel()
        scrollPane1 = JScrollPane()
        table1 = JTable()
        panel1 = JPanel()
        button2 = JButton()
        button3 = JButton()
        button4 = JButton()
        button8 = JButton()
        button6 = JButton()
        button7 = JButton()
        button1 = JButton()

        layout = MigLayout(
            "hidemode 3",  // columns
            "[fill]" + "[fill]",  // rows
            "[]" + "[]" + "[]"
        )
        add(label1, "cell 0 0")

        table1.model = object : DefaultTableModel(
            arrayOf(),
            arrayOf("学号", "姓名", "性别", "学籍", "年级", "学院", "专业", "班级", "加权平均分")
        ) {
            var columnTypes: Array<Class<*>> = arrayOf(
                Int::class.java, String::class.java, String::class.java, String::class.java,
                Int::class.java, String::class.java, String::class.java, String::class.java, Double::class.java
            )
            var columnEditable: BooleanArray =
                booleanArrayOf(false, true, true, true, true, true, true, true, false)

            override fun getColumnClass(columnIndex: Int): Class<*> {
                return columnTypes[columnIndex]
            }

            override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean {
                return columnEditable[columnIndex]
            }
        }
        val cm: TableColumnModel = table1.columnModel
        cm.getColumn(2).cellEditor = DefaultCellEditor(JComboBox(DefaultComboBoxModel(arrayOf("未知", "男", "女"))))
        cm.getColumn(3).cellEditor = DefaultCellEditor(JComboBox(DefaultComboBoxModel(arrayOf("在籍", "毕业"))))
        table1.preferredScrollableViewportSize = Dimension(600, 400)
        table1.showHorizontalLines = false
        table1.showVerticalLines = false
        scrollPane1.setViewportView(table1)
        add(scrollPane1, "cell 0 0,dock center")

        panel1.layout = MigLayout(
            "hidemode 3",  // columns
            "[fill]",  // rows
            "[]" + "[]" + "[]" + "[]" + "[]" + "[]" + "[]" + "[]" + "[]" + "[]" + "[]"
        )

        button2.text = "添加学生"
        button2.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                addStudentMouseClicked(e)
            }
        })
        panel1.add(button2, "cell 0 0")

        button3.text = "编辑成绩"
        button3.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                editScoreMouseClicked(e)
            }
        })
        panel1.add(button3, "cell 0 1")

        button4.text = "删除学生"
        button4.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                deleteStudentMouseClicked(e)
            }
        })
        panel1.add(button4, "cell 0 2")

        button8.text = "从文本导入"
        button8.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                impoStudentFromStringMouseClicked(e)
            }
        })
        panel1.add(button8, "cell 0 3")

        button6.text = "从表格导入"
        button6.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                impoStudentMouseClicked(e)
            }
        })
        panel1.add(button6, "cell 0 4")

        button7.text = "导出为文本"
        button7.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                expoToStringMouseClicked(e)
            }
        })
        panel1.add(button7, "cell 0 5")

        button1.text = "生成证明"
        button1.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                generateWordMouseClicked(e)
            }
        })
        panel1.add(button1, "cell 0 6")
        add(panel1, "cell 1 0")
    }

    private lateinit var label1: JLabel
    private lateinit var scrollPane1: JScrollPane
    private lateinit var table1: JTable
    private lateinit var panel1: JPanel
    private lateinit var button2: JButton
    private lateinit var button3: JButton
    private lateinit var button4: JButton
    private lateinit var button8: JButton
    private lateinit var button6: JButton
    private lateinit var button7: JButton
    private lateinit var button1: JButton

    init {
        instance = this
        initComponents()
    }

    companion object{
        lateinit var instance: StudentDataPanel
    }
}