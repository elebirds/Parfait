/*
 * Created by JFormDesigner on Sat Mar 11 19:12:58 CST 2023
 */
package cc.eleb.parfait.ui.panel

import net.miginfocom.swing.MigLayout
import java.awt.Color
import java.awt.Font
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder
import javax.swing.border.TitledBorder

/**
 * @author hhmcn
 */
class WelcomePanel constructor() : JPanel() {
    private fun initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Evaluation license - csgo fps
        label1 = JLabel()
        label2 = JLabel()

        setLayout(
            MigLayout(
                "hidemode 3",  // columns
                "[fill]" + "[fill]",  // rows
                "[]" + "[]" + "[]"
            )
        )

        //---- label1 ----
        label1!!.setText("\u6b22\u8fce\u6765\u5230Parfait Demo.")
        add(label1, "cell 0 0")

        //---- label2 ----
        label2!!.setText("\u8bf7\u65b0\u5efa\u6216\u6253\u5f00\u4e00\u4e2aPar\u6587\u4ef6\u4ee5\u5f00\u59cb\u3002")
        add(label2, "cell 0 1")
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
        label1!!.putClientProperty("FlatLaf.styleClass", "h0")
        label2!!.putClientProperty("FlatLaf.styleClass", "h1")
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Evaluation license - csgo fps
    private var label1: JLabel? = null
    private var label2: JLabel? =
        null // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on

    init {
        initComponents()
    }
}