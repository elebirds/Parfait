package cc.eleb.parfait.ui.dialog

import cc.eleb.parfait.entity.Student.Companion.students
import cc.eleb.parfait.ui.panel.ScoreDataPanel
import cc.eleb.parfait.ui.panel.StudentDataPanel
import net.miginfocom.swing.MigLayout
import java.awt.BorderLayout
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JDialog
import javax.swing.JPanel

class ScoreDialog(val sdp: StudentDataPanel, sid: Int) : JDialog() {
    private fun initComponents() {
        val contentPane = contentPane
        contentPane.layout = BorderLayout()
        dialogPane.layout = BorderLayout()
        contentPanel.layout = MigLayout("insets dialog,hidemode 3", "[fill][fill]", "[][][]")
        contentPanel.add(panel1, "cell 0 0")
        dialogPane.add(contentPanel, BorderLayout.CENTER)
        contentPane.add(dialogPane, BorderLayout.CENTER)
        this.pack()
        this.setLocationRelativeTo(owner)
        this.addWindowListener(object : WindowAdapter() {
            override fun windowClosed(e: WindowEvent) {
                sdp.table1.model.fireTableDataChanged()
                sdp.repaint()
            }
        })
        this.isModal = true
    }

    private val student = students[sid]!!
    private var dialogPane = JPanel()
    private var contentPanel = JPanel()
    private var panel1 = ScoreDataPanel(student)

    init {
        initComponents()
    }
}