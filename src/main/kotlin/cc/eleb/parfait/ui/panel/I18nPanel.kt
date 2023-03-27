package cc.eleb.parfait.ui.panel

import cc.eleb.parfait.config.ParConfig
import cc.eleb.parfait.i18n.GenLanguage
import cc.eleb.parfait.i18n.trs
import cc.eleb.parfait.ui.model.TranslateTableModel
import net.miginfocom.swing.MigLayout
import java.awt.Dimension
import java.awt.event.ItemEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.*
import javax.swing.*

class I18nPanel : JPanel() {
    fun reload() {
        comboBox1.model = DefaultComboBoxModel(GenLanguage.langs.keys.toTypedArray())
        this.reloadModel()
    }

    fun reloadTranslation(){
        label1.text = "i18n-panel-label1".trs()
        label2.text = "i18n-panel-label2".trs()
        button2.text = "global-save".trs()
        button3.text = "global-reload".trs()
        (table1.model as TranslateTableModel).reloadTranslation()
        (table1.model as TranslateTableModel).fireTableStructureChanged()
    }

    private fun reloadModel() {
        (table1.model as TranslateTableModel).apply {
            this.dataVector.clear()
            GenLanguage.langs[GenLanguage.nowGenLanguage]?.data?.forEach { (t, u) ->
                this.dataVector.add(Vector<String>().also {
                    it.add(t)
                    it.add(u)
                })
            }
        }
        (table1.model as TranslateTableModel).fireTableDataChanged()
    }

    private fun initComponents() {
        this.reloadTranslation()
        this.layout = MigLayout("insets 0,hidemode 3", "[fill][fill][fill][fill][fill][fill][fill]", "[][][][]")
        this.add(label1, "cell 0 0")
        comboBox1.model = DefaultComboBoxModel(GenLanguage.langs.keys.toTypedArray())
        comboBox1.addItemListener {
            if (it.stateChange != ItemEvent.SELECTED) return@addItemListener
            GenLanguage.nowGenLanguage = comboBox1.selectedItem!!.toString()
            this.reloadModel()
        }
        this.add(comboBox1, "cell 1 0")
        this.add(label2, "cell 0 1")
        scrollPane1.setViewportView(table1)
        this.add(scrollPane1, "cell 1 1,align center center,grow 0 0")
        panel1.layout = MigLayout("hidemode 3", "[fill][fill][fill]", "[][][]")
        panel1.add(button2, "cell 1 0")
        panel1.add(button3, "cell 1 1")
        this.add(panel1, "cell 1 1")
    }

    private val label1 = JLabel()
    private val comboBox1 = JComboBox<String>()
    private val label2 = JLabel()
    private val scrollPane1 = JScrollPane()
    private val table1 = JTable().apply {
        this.model = TranslateTableModel()
        this.preferredScrollableViewportSize = Dimension(600, 400)
    }
    private val panel1 = JPanel()
    private val button2 = JButton().apply {
        this.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (!ParConfig.checkInited()) return
                GenLanguage.langs[comboBox1.selectedItem!!.toString()]!!.data.apply {
                    this.clear()
                    (table1.model as TranslateTableModel).dataVector.forEach {
                        this[it[0].toString()] = it[1].toString()
                    }
                }
            }
        })
    }
    private val button3 = JButton().apply {
        this.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (!ParConfig.checkInited()) return
                reload()
            }
        })
    }

    init {
        instance = this
        initComponents()
    }

    companion object {
        lateinit var instance: I18nPanel
    }
}