/*
 * Created by JFormDesigner on Sat Mar 11 19:12:58 CST 2023
 */
package cc.eleb.parfait.ui.panel

import net.miginfocom.swing.MigLayout
import javax.swing.JLabel
import javax.swing.JPanel

/**
 * @author hhmcn
 */
class WelcomePanel : JPanel() {
    private fun initComponents() {
        label1 = JLabel()
        label2 = JLabel()

        layout = MigLayout(
            "hidemode 3",  // columns
            "[fill]" + "[fill]",  // rows
            "[]" + "[]" + "[]"
        )

        //---- label1 ----
        label1.text = "欢迎来到 Parfait Demo."
        label1.putClientProperty("FlatLaf.styleClass", "h0")
        add(label1, "cell 0 0")

        //---- label2 ----
        label2.text = "请新建或打开.par文件以开始。"
        label2.putClientProperty("FlatLaf.styleClass", "h1")
        add(label2, "cell 0 1")
    }

    private lateinit var label1: JLabel
    private lateinit var label2: JLabel

    init {
        initComponents()
    }
}