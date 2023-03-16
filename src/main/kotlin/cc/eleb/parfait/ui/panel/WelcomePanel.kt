package cc.eleb.parfait.ui.panel

import cc.eleb.parfait.i18n.trs
import net.miginfocom.swing.MigLayout
import javax.swing.JLabel
import javax.swing.JPanel

class WelcomePanel : JPanel() {
    fun reloadTranslation(){
        label1.text = "welcome-panel-1".trs()
        label2.text = "welcome-panel-2".trs()
    }

    private fun initComponents() {
        this.reloadTranslation()
        layout = MigLayout("hidemode 3", "[fill][fill]", "[][][]")
        add(label1, "cell 0 0")
        add(label2, "cell 0 1")
    }

    private var label1 = JLabel().apply {
        this.putClientProperty("FlatLaf.styleClass", "h0")
    }
    private var label2 = JLabel().apply {
        this.putClientProperty("FlatLaf.styleClass", "h1")
    }

    init {
        initComponents()
    }
}