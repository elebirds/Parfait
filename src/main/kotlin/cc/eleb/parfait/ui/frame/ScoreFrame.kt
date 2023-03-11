/*
 * Created by JFormDesigner on Fri Mar 10 15:52:56 CST 2023
 */
package cc.eleb.parfait.ui.frame

import cc.eleb.parfait.ui.panel.ScoreDataPanel
import net.miginfocom.swing.MigLayout
import java.awt.BorderLayout
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JPanel

/**
 * @author hhmcn
 */
class ScoreFrame : JFrame() {
    private fun initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Evaluation license - csgo fps

        //======== this ========
        val contentPane = contentPane
        contentPane.layout = BorderLayout()

        //======== dialogPane ========
        dialogPane.layout = BorderLayout()

        //======== contentPanel ========
        contentPanel.layout = MigLayout(
            "insets dialog,hidemode 3",  // columns
            "[fill]" + "[fill]",  // rows
            "[]" + "[]" + "[]"
        )
        contentPanel.add(panel1, "cell 0 0")

        dialogPane.add(contentPanel, BorderLayout.CENTER)

        //======== buttonBar ========
        buttonBar.layout = MigLayout(
            "insets dialog,alignx right",  // columns
            "[button,fill]" + "[button,fill]",  // rows
            null
        )

        //---- okButton ----
        okButton.text = "OK"
        buttonBar.add(okButton, "cell 0 0")

        //---- cancelButton ----
        cancelButton.text = "Cancel"
        buttonBar.add(cancelButton, "cell 1 0")

        dialogPane.add(buttonBar, BorderLayout.SOUTH)

        contentPane.add(dialogPane, BorderLayout.CENTER)
        pack()
        setLocationRelativeTo(owner)
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Evaluation license - csgo fps
    private var dialogPane = JPanel()
    private var contentPanel = JPanel()
    private var panel1 = ScoreDataPanel()
    private var buttonBar = JPanel()
    private var okButton = JButton()
    private var cancelButton = JButton()

    init {
        initComponents()
    }
}