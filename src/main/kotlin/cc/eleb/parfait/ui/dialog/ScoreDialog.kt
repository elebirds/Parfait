/*
 * Created by JFormDesigner on Fri Mar 10 15:52:56 CST 2023
 */
package cc.eleb.parfait.ui.dialog

import cc.eleb.parfait.entity.Student.Companion.students
import cc.eleb.parfait.ui.panel.ScoreDataPanel
import cc.eleb.parfait.ui.panel.StudentDataPanel
import net.miginfocom.swing.MigLayout
import java.awt.BorderLayout
import java.awt.event.WindowEvent
import java.awt.event.WindowListener
import javax.swing.JDialog
import javax.swing.JPanel

/**
 * @author hhmcn
 */
class ScoreDialog(val sdp: StudentDataPanel, sid: Int) : JDialog() {
    private fun initComponents() {
        val contentPane = contentPane
        contentPane.layout = BorderLayout()
        dialogPane.layout = BorderLayout()
        contentPanel.layout = MigLayout(
            "insets dialog,hidemode 3",  // columns
            "[fill]" + "[fill]",  // rows
            "[]" + "[]" + "[]"
        )
        contentPanel.add(panel1, "cell 0 0")

        dialogPane.add(contentPanel, BorderLayout.CENTER)
        contentPane.add(dialogPane, BorderLayout.CENTER)
        this.pack()
        this.setLocationRelativeTo(owner)
        this.addWindowListener(object : WindowListener {
            override fun windowOpened(e: WindowEvent) {
            }

            override fun windowClosing(e: WindowEvent) {
            }

            override fun windowClosed(e: WindowEvent) {
                sdp.table1.model.fireTableDataChanged()
                sdp.repaint()
            }

            override fun windowIconified(e: WindowEvent) {
            }

            override fun windowDeiconified(e: WindowEvent) {
            }

            override fun windowActivated(e: WindowEvent) {
            }

            override fun windowDeactivated(e: WindowEvent) {
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