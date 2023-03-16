package cc.eleb.parfait.ui.panel

import cc.eleb.parfait.config.ParConfig
import cc.eleb.parfait.entity.Certificate
import cc.eleb.parfait.entity.Student
import cc.eleb.parfait.i18n.trs
import cc.eleb.parfait.ui.dialog.ScoreDialog
import cc.eleb.parfait.ui.dialog.StudentAddDialog
import cc.eleb.parfait.ui.table.StudentDataTable
import cc.eleb.parfait.utils.GlobalSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.miginfocom.swing.MigLayout
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.File
import javax.swing.*
import javax.swing.filechooser.FileFilter

/**
 * @author hhmcn
 */
class StudentDataPanel : JPanel() {
    fun reloadTranslation(){
        button2.text = "student-panel-button-1".trs()
        button3.text = "student-panel-button-2".trs()
        button4.text = "student-panel-button-3".trs()
        button8.text = "student-panel-button-4".trs()
        button6.text = "student-panel-button-5".trs()
        button7.text = "student-panel-button-6".trs()
        button1.text = "student-panel-button-7".trs()
        this.table1.model.reloadTranslation()
        this.table1.model.fireTableStructureChanged()
    }

    private fun addStudentMouseClicked(e: MouseEvent) {
        if (e.button != MouseEvent.BUTTON1) return
        if (!ParConfig.checkInited()) return
        StudentAddDialog(this).isVisible = true
    }

    private fun deleteStudentMouseClicked(e: MouseEvent) {
        if (e.button != MouseEvent.BUTTON1) return
        if (!ParConfig.checkInited()) return
        if (table1.selectedRows.isEmpty()) {
            JOptionPane.showMessageDialog(
                this, "student-panel-error-5".trs(), "global-error".trs(),
                JOptionPane.ERROR_MESSAGE
            )
            return
        }
        for (selectedRow: Int in table1.selectedRows) {
            val slt = table1.selectedRow
            Student.students.remove(table1.model.getValueAt(table1.convertRowIndexToModel(slt), 0))
        }
        table1.model.fireTableDataChanged()
    }

    private fun impoStudentMouseClicked(e: MouseEvent) {
        if (e.button != MouseEvent.BUTTON1) return
        if (!ParConfig.checkInited()) return
        val fd = JFileChooser().apply {
            this.fileFilter = object : FileFilter() {
                override fun accept(f: File): Boolean {
                    return f.isDirectory || f.name.endsWith(".xlsx")
                }

                override fun getDescription(): String {
                    return "global-excel-file".trs()
                }
            }
            this.isMultiSelectionEnabled = false
            this.fileSelectionMode = JFileChooser.FILES_ONLY
        }
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
                Student.addStudentsFromFile(fd.selectedFile)
                table1.model.fireTableDataChanged()
            } catch (e: Exception) {
                JOptionPane.showMessageDialog(
                    this,
                    "${"student-panel-error-4".trs()}\n${e.stackTraceToString()}",
                    "global-error".trs(),
                    JOptionPane.ERROR_MESSAGE
                )
            }
        }
    }

    private fun editScoreMouseClicked(e: MouseEvent) {
        if (e.button != MouseEvent.BUTTON1) return
        if (!ParConfig.checkInited()) return
        if (table1.selectedRows.isEmpty()) {
            JOptionPane.showMessageDialog(
                this, "student-panel-error-1".trs(), "global-error".trs(),
                JOptionPane.ERROR_MESSAGE
            )
            return
        }
        if (table1.selectedRows.size != 1) {
            JOptionPane.showMessageDialog(
                this, "student-panel-error-3".trs(), "global-error".trs(),
                JOptionPane.ERROR_MESSAGE
            )
            return
        }
        try {
            val sf = ScoreDialog(
                this,
                table1.getValueAt(table1.convertRowIndexToModel(table1.selectedRow), 0).toString().toInt()
            )
            sf.isVisible = true
        } catch (e: Exception) {
            JOptionPane.showMessageDialog(
                this,
                "${"global-error-unknown".trs()}。\n${e.stackTraceToString()}",
                "global-error".trs(),
                JOptionPane.ERROR_MESSAGE
            )
        }
    }

    private fun generateWordMouseClicked(e: MouseEvent) {
        if (e.button != MouseEvent.BUTTON1) return
        if (!ParConfig.checkInited()) return
        if (table1.selectedRows.isEmpty()) {
            JOptionPane.showMessageDialog(
                this, "student-panel-error-2".trs(), "global-error".trs(),
                JOptionPane.ERROR_MESSAGE
            )
            return
        }
        if (table1.selectedRows.size == 1) {
            val fd = JFileChooser().apply {
                this.isMultiSelectionEnabled = false
                this.fileSelectionMode = JFileChooser.FILES_ONLY
            }
            val res: Int = fd.showSaveDialog(this)
            if (res == JFileChooser.APPROVE_OPTION) {
                CoroutineScope(Dispatchers.IO).launch {
                    for (selectedRow: Int in table1.selectedRows) {
                        val student =
                            Student.students[table1.model.getValueAt(table1.convertRowIndexToModel(selectedRow), 0)]!!
                        Certificate.generate(fd.selectedFile, student)
                    }
                }
            }
        } else {
            //多人
            val fd = JFileChooser().apply {
                this.fileFilter = object : FileFilter() {
                    override fun accept(f: File): Boolean {
                        return f.isDirectory
                    }

                    override fun getDescription(): String {
                        return "global-directory".trs()
                    }
                }
                this.isMultiSelectionEnabled = false
                this.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            }
            val res: Int = fd.showSaveDialog(this)
            if (res == JFileChooser.APPROVE_OPTION) {
                CoroutineScope(Dispatchers.IO).launch {
                    for (selectedRow: Int in table1.selectedRows) {
                        val student = Student.students[table1.model.getValueAt(table1.convertRowIndexToModel(selectedRow), 0)]!!
                        Certificate.generate(
                            File(fd.selectedFile.absolutePath + "/${student.id}-${student.name}.docx"),
                            student
                        )
                    }
                }
            }
        }
    }

    private fun impoStudentFromStringMouseClicked(e: MouseEvent) {
        if (e.button != MouseEvent.BUTTON1) return
        if (!ParConfig.checkInited()) return
        // TODO add your code here
    }

    private fun expoToStringMouseClicked(e: MouseEvent) {
        if (e.button != MouseEvent.BUTTON1) return
        if (!ParConfig.checkInited()) return
        if (table1.selectedRows.isEmpty()) {
            JOptionPane.showMessageDialog(
                this, "student-panel-error-1".trs(), "global-error".trs(),
                JOptionPane.ERROR_MESSAGE
            )
            return
        }
        var res = ""
        for (selectedRow: Int in table1.selectedRows) {
            val si: Any = table1.model.getValueAt(table1.convertRowIndexToModel(selectedRow), 0) ?: continue
            val sid: Int = si as Int
            val st = Student.students[sid]!!
            res += GlobalSettings.OUTPUT_STRING
                .replace("%name", st.name)
                .replace("%grade", st.grade.toString())
                .replace("%id", st.id.toString())
                .replace("%gender", st.genderT)
                .replace("%school", st.school)
                .replace("%prof", st.profession)
                .replace("%as", Certificate.nf.format(st.weightedMean))
                .replace("%ss", Certificate.nf.format(st.simpleMean))
                .replace("%gpa", Certificate.nf.format(st.gpa))
            res += "\n"
        }
        if (JOptionPane.showConfirmDialog(
                this, res, "global.clipboard".trs(), JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE
            ) == JOptionPane.OK_OPTION
        ) {
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
        table1.autoCreateRowSorter = true
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

    private val label1 = JLabel()
    private val scrollPane1 = JScrollPane()
    val table1 = StudentDataTable()
    private val panel1 = JPanel().apply {
        this.layout = MigLayout("hidemode 3", "[fill]", "[][][][][][][][][][][]")
    }
    private val button2 = JButton().apply {
        this.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                addStudentMouseClicked(e)
            }
        })
    }
    private val button3 = JButton().apply {
        this.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                editScoreMouseClicked(e)
            }
        })
    }
    private val button4 = JButton().apply {
        this.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                deleteStudentMouseClicked(e)
            }
        })
    }
    private val button8 = JButton().apply {
        this.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                impoStudentFromStringMouseClicked(e)
            }
        })
    }
    private val button6 = JButton().apply {
        this.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                impoStudentMouseClicked(e)
            }
        })
    }
    private val button7 = JButton().apply {
        this.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                expoToStringMouseClicked(e)
            }
        })
    }
    private val button1 = JButton().apply {
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

    companion object {
        lateinit var instance: StudentDataPanel
    }
}