/*
 * Created by JFormDesigner on Sat Mar 11 20:53:53 CST 2023
 */
package cc.eleb.parfait.ui.panel

import cc.eleb.parfait.config.GPAConfig
import cc.eleb.parfait.config.ParConfig
import cc.eleb.parfait.i18n.trs
import cc.eleb.parfait.ui.model.GPATableModel
import net.miginfocom.swing.MigLayout
import java.awt.Dimension
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.*
import javax.swing.*
import javax.swing.table.DefaultTableModel
import javax.swing.table.TableColumnModel

/**
 * @author hhmcn
 */
class GPAPanel : JPanel() {
    fun reloadTranslation(){
        button2.text = "global-save".trs()
        button3.text = "global-reload".trs()
        (table1.model as GPATableModel).reloadTranslation()
        (table1.model as GPATableModel).fireTableStructureChanged()
    }

    fun reload() {
        (table1.model as DefaultTableModel).dataVector.also {
            it.clear()
            GPAConfig.ranks.forEach { (t, u) ->
                it.add(Vector<Any>().apply {
                    this.add(t)
                    this.add(u)
                })
            }
        }
        (table1.model as DefaultTableModel).fireTableDataChanged()
    }

    private fun initComponents() {
        this.reloadTranslation()
        layout = MigLayout("insets 0,hidemode 3,gap 0 0", "[grow,fill][305,grow,fill][grow,fill]", "[grow,fill]")
        add(label1, "cell 0 0")
        val cm: TableColumnModel = table1.columnModel
        cm.getColumn(0).minWidth = 200
        table1.preferredScrollableViewportSize = Dimension(700, 400)
        scrollPane1.setViewportView(table1)
        add(scrollPane1, "cell 1 0")
        panel1.layout = MigLayout("hidemode 3", "[fill][fill][fill]", "[][][]")
        panel1.add(button2, "cell 1 0")
        panel1.add(button3, "cell 1 1")
        add(panel1, "cell 2 0")
    }

    private var label1 = JLabel()
    private var scrollPane1 = JScrollPane()
    private var table1 = JTable().apply {
        this.model = GPATableModel()
    }
    private var panel1 = JPanel()
    private var button2 = JButton().apply {
        this.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (!ParConfig.checkInited()) return
                GPAConfig.ranks.clear()
                (table1.model as DefaultTableModel).dataVector.forEach {
                    GPAConfig.ranks[it[0].toString().toInt()] = it[1].toString().toDouble()
                }
            }
        })
    }
    private var button3 = JButton().apply {
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
        lateinit var instance: GPAPanel
    }
}