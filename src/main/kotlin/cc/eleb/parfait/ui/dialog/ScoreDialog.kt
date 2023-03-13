/*
 * Created by JFormDesigner on Fri Mar 10 15:52:56 CST 2023
 */
package cc.eleb.parfait.ui.dialog

import cc.eleb.parfait.entity.Student.Companion.students
import cc.eleb.parfait.ui.panel.ScoreDataPanel
import net.miginfocom.swing.MigLayout
import java.awt.BorderLayout
import javax.swing.JDialog
import javax.swing.JPanel

/**
 * @author hhmcn
 */
class ScoreDialog(sid:Int) : JDialog() {
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
        pack()
        setLocationRelativeTo(owner)
    }
    private var dialogPane = JPanel()
    private var contentPanel = JPanel()
    private var panel1 = ScoreDataPanel(students[sid]!!)

    init {
        initComponents()
    }
}