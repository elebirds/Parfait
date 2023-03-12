/*
 * Created by JFormDesigner on Sat Mar 11 20:39:09 CST 2023
 */
package cc.eleb.parfait.ui.panel

import net.miginfocom.swing.MigLayout
import java.awt.Dimension
import javax.swing.*
import javax.swing.table.DefaultTableModel

/**
 * @author hhmcn
 */
class I18nPanel : JPanel() {
    private fun initComponents() {
        label1 = JLabel()
        comboBox1 = JComboBox<Any?>()
        label2 = JLabel()
        scrollPane1 = JScrollPane()
        table1 = JTable()
        panel1 = JPanel()
        button2 = JButton()
        button3 = JButton()

        //======== this ========
        layout = MigLayout(
            "insets 0,hidemode 3",  // columns
            "[fill]" + "[fill]" + "[fill]" + "[fill]" + "[fill]" + "[fill]" + "[fill]",  // rows
            "[]" + "[]" + "[]" + "[]"
        )

        //---- label1 ----
        label1.text = "\u5f53\u524d\u8bed\u8a00\uff1a"
        add(label1, "cell 0 0")
        add(comboBox1, "cell 1 0")

        //---- label2 ----
        label2.text = "\u5bf9\u5e94\u7ffb\u8bd1\uff1a"
        add(label2, "cell 0 1")

        //======== scrollPane1 ========
        table1.model = object : DefaultTableModel(
            arrayOf(arrayOf(null, null), arrayOf(null, null)),
            arrayOf("\u4e2d\u6587", "\u5916\u6587")
        ) {
            var columnTypes: Array<Class<*>> = arrayOf(String::class.java, String::class.java)
            override fun getColumnClass(columnIndex: Int): Class<*> {
                return columnTypes[columnIndex]
            }
        }
        table1.preferredScrollableViewportSize = Dimension(600, 400)
        scrollPane1.setViewportView(table1)
        add(scrollPane1, "cell 1 1,align center center,grow 0 0")

        //======== panel1 ========
        panel1.layout = MigLayout(
            "hidemode 3",  // columns
            "[fill]" + "[fill]" + "[fill]",  // rows
            "[]" + "[]" + "[]"
        )
        //---- button2 ----
        button2.text = "\u4fdd\u5b58"
        panel1.add(button2, "cell 1 0")
        //---- button3 ----
        button3.text = "\u91cd\u65b0\u52a0\u8f7d"
        panel1.add(button3, "cell 1 1")
        add(panel1, "cell 1 1")
    }

    private lateinit var label1: JLabel
    private lateinit var comboBox1: JComboBox<*>
    private lateinit var label2: JLabel
    private lateinit var scrollPane1: JScrollPane
    private lateinit var table1: JTable
    private lateinit var panel1: JPanel
    private lateinit var button2: JButton
    private lateinit var button3: JButton

    init {
        initComponents()
    }
}