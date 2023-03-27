package cc.eleb.parfait.ui.panel

import cc.eleb.parfait.entity.*
import cc.eleb.parfait.i18n.trs
import cc.eleb.parfait.ui.model.ScoreTableModel
import cc.eleb.parfait.ui.table.ScoreDataTable
import com.alibaba.excel.EasyExcel
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.miginfocom.swing.MigLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.File
import javax.swing.*
import javax.swing.filechooser.FileFilter

class ScoreDataPanel(private val student: Student) : JPanel() {
    private fun addScoreMouseClicked(e: MouseEvent) {
        if (e.button != MouseEvent.BUTTON1) return
        student.scores.add(Score().apply {
            this.name = "score-panel-new".trs()
            this.aType = "考试"
            this.cType = "专业基础课程"
            this.score = 0
            this.credit = 1.0
        })
        (table1.model as ScoreTableModel).fireTableDataChanged()
    }

    private fun removeScoreMouseClicked(e: MouseEvent) {
        if (e.button != MouseEvent.BUTTON1) return
        if (table1.selectedRows.isEmpty()) {
            JOptionPane.showMessageDialog(
                this, "score-panel-error-1".trs(), "global-error".trs(),
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

    private fun expoScoreMouseClicked(e:MouseEvent){
        if (e.button != MouseEvent.BUTTON1) return
        val fd = JFileChooser().apply {
            this.isMultiSelectionEnabled = false
            this.fileSelectionMode = JFileChooser.FILES_ONLY
            this.fileFilter = object : FileFilter() {
                override fun accept(f: File): Boolean {
                    return f.isDirectory || f.name.endsWith(".xlsx")
                }

                override fun getDescription(): String {
                    return "global-excel-file".trs()
                }
            }
        }
        val res: Int = fd.showSaveDialog(this)
        if (res == JFileChooser.APPROVE_OPTION) {
            val sf = if(fd.selectedFile.absolutePath.endsWith(".xlsx"))fd.selectedFile else File(fd.selectedFile.absolutePath+".xlsx")
            CoroutineScope(Dispatchers.IO).launch {
                val al = arrayListOf<SimpleWriteScore>().apply {
                    student.scores.forEach { u ->
                        this.add(SimpleWriteScore().also {
                            it.name = u.name
                            it.cType = u.cType
                            it.aType = u.aType
                            it.credit = u.credit
                            it.score = u.score
                            it.gpa = if(u.gpa)"是" else "否"
                        })
                    }
                }
                EasyExcel.write(sf, SimpleWriteScore::class.java).registerWriteHandler(
                    LongestMatchColumnWidthStyleStrategy()
                ).sheet("模板").doWrite(al)
            }
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
                return "global-excel-file".trs()
            }
        }
        fd.isMultiSelectionEnabled = false
        fd.fileSelectionMode = JFileChooser.FILES_ONLY
        val res: Int = fd.showOpenDialog(this)
        if (res == JFileChooser.APPROVE_OPTION) {
            if (!fd.selectedFile.toString().endsWith(".xlsx")) {
                JOptionPane.showMessageDialog(
                    this,
                    "score-panel-error-2".trs(),
                    "global-error".trs(),
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
                    "${"score-panel-error-3".trs()}\n${e.stackTraceToString()}",
                    "global-error".trs(),
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
        panel1.add(button4, "cell 0 3")
        this.add(panel1, "cell 1 0")
    }

    private val scrollPane1 = JScrollPane()
    private val table1 = ScoreDataTable(student)
    private val panel1 = JPanel()
    private val button1 = JButton().also {
        it.text = "score-panel-button-1".trs()
        it.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                addScoreMouseClicked(e)
            }
        })
    }

    private val button2 = JButton().also {
        it.text = "score-panel-button-2".trs()
        it.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                removeScoreMouseClicked(e)
            }
        })
    }
    private val button3 = JButton().also {
        it.text = "score-panel-button-3".trs()
        it.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                impoScoreMouseClicked(e)
            }
        })
    }
    private val button4 = JButton().also {
        it.text = "score-panel-button-4".trs()
        it.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                expoScoreMouseClicked(e)
            }
        })
    }

    init {
        initComponents()
    }
}