package cc.eleb.parfait.ui.panel

import cc.eleb.parfait.entity.Score
import cc.eleb.parfait.entity.Student
import cc.eleb.parfait.ui.model.ScoreTableModel
import cc.eleb.parfait.ui.table.ScoreDataTable
import net.miginfocom.swing.MigLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.File
import javax.swing.*
import javax.swing.filechooser.FileFilter

/**
 * @author hhmcn
 */
class ScoreDataPanel(private val student:Student) : JPanel() {
    private var edited = false

    private fun addScoreMouseClicked(e: MouseEvent) {
        if (e.button != MouseEvent.BUTTON1) return
        student.scores.add(Score().apply {
            this.name = "新建成绩"
            this.aType = "考试"
            this.cType = "专业基础课程"
            this.score = 0
            this.credit = 1.0
        })
        (table1.model as ScoreTableModel).fireTableDataChanged()
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
            student.scores.removeAt(slt)
            (table1.model as ScoreTableModel).fireTableDataChanged()
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
                student.addScoresFromFile(fd.selectedFile)
                (table1.model as ScoreTableModel).fireTableDataChanged()
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
        this.layout = MigLayout(
            "hidemode 3",  // columns
            "[fill]" + "[fill]" + "[fill]",  // rows
            "[]" + "[]" + "[]"
        )
        scrollPane1.setViewportView(table1)
        this.add(scrollPane1, "cell 1 0")
        panel1.layout = MigLayout(
            "hidemode 3",  // columns
            "[fill]",  // rows
            "[]" + "[]" + "[]" + "[]"
        )
        panel1.add(button1, "cell 0 0")
        panel1.add(button2, "cell 0 1")
        panel1.add(button3, "cell 0 2")
        this.add(panel1, "cell 1 0")
    }

    private val scrollPane1 = JScrollPane()
    private val table1 = ScoreDataTable(student)
    private val panel1 = JPanel()
    private val button1 = JButton().also {
        it.text = "添加成绩"
        it.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                addScoreMouseClicked(e)
            }
        })
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

    init {
        initComponents()
    }
}