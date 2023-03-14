/*
 * Created by JFormDesigner on Fri Mar 10 10:27:17 CST 2023
 */
package cc.eleb.parfait.ui.panel

import cc.eleb.parfait.config.ParConfig
import cc.eleb.parfait.entity.Certificate
import cc.eleb.parfait.entity.Student
import cc.eleb.parfait.ui.dialog.ScoreDialog
import cc.eleb.parfait.ui.table.StudentDataTable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.miginfocom.swing.MigLayout
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.File
import java.util.*
import javax.swing.*
import javax.swing.filechooser.FileFilter
import javax.swing.table.DefaultTableModel

/**
 * @author hhmcn
 */
class StudentDataPanel : JPanel() {
    private fun addStudentMouseClicked(e: MouseEvent) {
        if (e.button != MouseEvent.BUTTON1) return
        if(!ParConfig.checkInited())return
        Student
        JOptionPane.showInputDialog(this, "测试", "", JOptionPane.INFORMATION_MESSAGE)
        JOptionPane.showMessageDialog(null, "呃呃", "就", JOptionPane.INFORMATION_MESSAGE)
    }

    private fun deleteStudentMouseClicked(e: MouseEvent) {
        if (e.button != MouseEvent.BUTTON1) return
        if(!ParConfig.checkInited())return
        if (table1.selectedRows.isEmpty()) {
            JOptionPane.showMessageDialog(
                this, "您未选中任何学生，无法进行删除操作。", "错误",
                JOptionPane.ERROR_MESSAGE
            )
            return
        }
        for (selectedRow: Int in table1.selectedRows) {
            val slt = table1.selectedRow
            Student.students.remove(table1.model.getValueAt(slt,0))
        }
        table1.model.fireTableDataChanged()
    }

    private fun impoStudentMouseClicked(e: MouseEvent) {
        if (e.button != MouseEvent.BUTTON1) return
        if(!ParConfig.checkInited())return
        val fd = JFileChooser().apply {
            this.fileFilter = object : FileFilter() {
                override fun accept(f: File): Boolean {
                    return f.isDirectory || f.name.endsWith(".xlsx")
                }

                override fun getDescription(): String {
                    return "Excel文件(.xlsx)"
                }
            }
            this.isMultiSelectionEnabled = false
            this.fileSelectionMode = JFileChooser.FILES_ONLY
        }
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
                Student.addStudentsFromFile(fd.selectedFile)
                table1.model.fireTableDataChanged()
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
        if (table1.selectedRows.isEmpty()) {
            JOptionPane.showMessageDialog(
                this, "您未选中任何学生，无法导出数据为文本。", "错误",
                JOptionPane.ERROR_MESSAGE
            )
            return
        }
        if (table1.selectedRows.size!=1) {
            JOptionPane.showMessageDialog(
                this, "您只能选择一个学生进行成绩编辑。", "错误",
                JOptionPane.ERROR_MESSAGE
            )
            return
        }
        try {
            this.isEnabled = false
            val sf = ScoreDialog(this,table1.getValueAt(table1.selectedRow,0).toString().toInt())
            sf.isVisible = true
        }catch (e:Exception){
            JOptionPane.showMessageDialog(
                this,
                "发生错误。\n${e.stackTraceToString()}",
                "打开失败",
                JOptionPane.ERROR_MESSAGE
            )
        }
    }

    private fun generateWordMouseClicked(e: MouseEvent) {
        if (e.button != MouseEvent.BUTTON1) return
        if(!ParConfig.checkInited())return
        if (table1.selectedRows.isEmpty()) {
            JOptionPane.showMessageDialog(
                this, "您未选中任何学生，无法生成证明。", "错误",
                JOptionPane.ERROR_MESSAGE
            )
            return
        }
        if (table1.selectedRows.size==1) {
            val fd = JFileChooser().apply {
                this.isMultiSelectionEnabled = false
                this.fileSelectionMode = JFileChooser.FILES_ONLY
            }
            val res: Int = fd.showSaveDialog(this)
            if (res == JFileChooser.APPROVE_OPTION) {
                CoroutineScope(Dispatchers.IO).launch {
                    for (selectedRow: Int in table1.selectedRows) {
                        val student = Student.students[table1.model.getValueAt(selectedRow,0)]!!
                        Certificate.generate(fd.selectedFile,student)
                    }
                }
            }
        }else {
            //多人
            val fd = JFileChooser().apply {
                this.fileFilter = object : FileFilter() {
                    override fun accept(f: File): Boolean {
                        return f.isDirectory
                    }

                    override fun getDescription(): String {
                        return "文件夹"
                    }
                }
                this.isMultiSelectionEnabled = false
                this.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            }
            val res: Int = fd.showSaveDialog(this)
            if (res == JFileChooser.APPROVE_OPTION) {
                CoroutineScope(Dispatchers.IO).launch {
                    for (selectedRow: Int in table1.selectedRows) {
                        val student = Student.students[table1.model.getValueAt(selectedRow,0)]!!
                        Certificate.generate(File(fd.selectedFile.absolutePath+"/${student.id}-${student.name}-证明.docx"),student)
                    }
                }
            }
        }
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
        var res = ""
        for (selectedRow: Int in table1.selectedRows) {
            val si: Any = table1.model.getValueAt(selectedRow,0) ?: continue
            val sid: Int = si as Int
            val st = Student.students[sid]!!
            res += "姓名：${st.name}，性别:${st.genderT}，学号:${sid}，系${st.school}${st.grade}级${st.profession}专业的学生。" +
                    "截至目前，该生所修读的所有课程的加权平均分为${st.weightedMean}\n"
        }
        if (JOptionPane.showConfirmDialog(
                this, res, "是否复制到剪贴板", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE
            ) == JOptionPane.OK_OPTION) {
            Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(res), null)
        }
    }

    private fun initComponents() {
        this.layout = MigLayout(
            "hidemode 3",  // columns
            "[fill]" + "[fill]",  // rows
            "[]" + "[]" + "[]"
        )
        this.add(label1, "cell 0 0")
        scrollPane1.setViewportView(table1)
        this.add(scrollPane1, "cell 0 0,dock center")
        panel1.add(button2, "cell 0 0")
        panel1.add(button3, "cell 0 1")
        panel1.add(button4, "cell 0 2")
        panel1.add(button8, "cell 0 3")
        panel1.add(button6, "cell 0 4")
        panel1.add(button7, "cell 0 5")
        panel1.add(button1, "cell 0 6")
        this.add(panel1, "cell 1 0")
    }

    private val label1 =  JLabel()
    private val scrollPane1 = JScrollPane()
    val table1 =  StudentDataTable()
    private val panel1 =  JPanel().apply {
        this.layout = MigLayout(
            "hidemode 3",  // columns
            "[fill]",  // rows
            "[]" + "[]" + "[]" + "[]" + "[]" + "[]" + "[]" + "[]" + "[]" + "[]" + "[]"
        )
    }
    private val button2 = JButton().apply {
        this.text = "添加学生"
        this.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                addStudentMouseClicked(e)
            }
        })
    }
    private val button3 = JButton().apply {
        this.text = "编辑成绩"
        this.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                editScoreMouseClicked(e)
            }
        })
    }
    private val button4 = JButton().apply {
        this.text = "删除学生"
        this.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                deleteStudentMouseClicked(e)
            }
        })
    }
    private val button8 = JButton().apply {
        this.text = "从文本导入"
        this.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                impoStudentFromStringMouseClicked(e)
            }
        })
    }
    private val button6 = JButton().apply {
        this.text = "从表格导入"
        this.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                impoStudentMouseClicked(e)
            }
        })
    }
    private val button7 = JButton().apply {
        this.text = "导出为文本"
        this.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                expoToStringMouseClicked(e)
            }
        })
    }
    private val button1 = JButton().apply {
        this.text = "生成证明"
        this.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                generateWordMouseClicked(e)
            }
        })
    }

    init {
        instance = this
        initComponents()
    }

    companion object{
        lateinit var instance: StudentDataPanel
    }
}