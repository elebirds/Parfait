/*
 * Created by JFormDesigner on Fri Mar 10 15:30:40 CST 2023
 */
package cc.eleb.parfait.ui.panel

import net.miginfocom.swing.MigLayout
import java.awt.Dimension
import javax.swing.*
import javax.swing.table.DefaultTableModel
import javax.swing.table.TableColumnModel

/**
 * @author hhmcn
 */
class ScoreDataPanel : JPanel() {
    private fun initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Evaluation license - csgo fps
        scrollPane1 = JScrollPane()
        table1 = JTable()
        panel1 = JPanel()
        button1 = JButton()
        button2 = JButton()
        button3 = JButton()
        button4 = JButton()

        layout = MigLayout(
            "hidemode 3",  // columns
            "[fill]" + "[fill]" + "[fill]",  // rows
            "[]" + "[]" + "[]"
        )

        //======== scrollPane1 ========


        //---- table1 ----
        table1.preferredScrollableViewportSize = Dimension(600, 400)
        table1.model = object : DefaultTableModel(
            arrayOf(
                arrayOf<Any?>(null, "\u4e13\u4e1a\u57fa\u7840\u8bfe\u7a0b", null, false, null, null),
                arrayOf(null, null, null, null, null, null)
            ), arrayOf(
                "\u8bfe\u7a0b\u540d\u79f0", "\u8bfe\u7a0b\u7c7b\u578b", "\u8003\u6838\u7c7b\u578b",
                "\u5b66\u4f4d\u8bfe", "\u5b66\u5206", "\u6210\u7ee9"
            )
        ) {
            var columnTypes: Array<Class<*>> = arrayOf(
                String::class.java, String::class.java, String::class.java, Boolean::class.java,
                Double::class.java, Int::class.java
            )

            override fun getColumnClass(columnIndex: Int): Class<*> {
                return columnTypes[columnIndex]
            }
        }
        val cm: TableColumnModel = table1.columnModel
        cm.getColumn(0).minWidth = 200
        cm.getColumn(1).minWidth = 100
        cm.getColumn(1).maxWidth = 100
        cm.getColumn(1).cellEditor = DefaultCellEditor(
            JComboBox(
                DefaultComboBoxModel<Any?>(
                    arrayOf<String?>(
                        "\u4e13\u4e1a\u57fa\u7840\u8bfe\u7a0b", "\u4e13\u4e1a\u4e3b\u5e72\u8bfe\u7a0b",
                        "\u4e13\u4e1a\u65b9\u5411\u8bfe\u7a0b", "\u4e13\u4e1a\u62d3\u5c55\u8bfe\u7a0b",
                        "\u5b9e\u8df5\u7c7b\u8bfe\u7a0b", "\u5927\u7c7b\u5e73\u53f0\u8bfe\u7a0b",
                        "\u901a\u8bc6\u6559\u80b2\u5fc5\u4fee\u8bfe\u7a0b"
                    )
                )
            )
        )
        cm.getColumn(2).minWidth = 80
        cm.getColumn(2).maxWidth = 80
        cm.getColumn(2).cellEditor = DefaultCellEditor(
            JComboBox(DefaultComboBoxModel<Any?>(arrayOf<String?>("\u8003\u8bd5", "\u8003\u5bdf")))
        )
        cm.getColumn(3).minWidth = 60
        cm.getColumn(3).maxWidth = 60
        scrollPane1.setViewportView(table1)
        add(scrollPane1, "cell 1 0")

        //======== panel1 ========
        panel1.layout = MigLayout(
            "hidemode 3",  // columns
            "[fill]",  // rows
            "[]" + "[]" + "[]" + "[]"
        )

        //---- button1 ----
        button1.text = "\u6dfb\u52a0\u6210\u7ee9"
        panel1.add(button1, "cell 0 0")

        //---- button2 ----
        button2.text = "\u5220\u9664\u6210\u7ee9"
        panel1.add(button2, "cell 0 1")

        //---- button3 ----
        button3.text = "\u5bfc\u5165\u6210\u7ee9"
        panel1.add(button3, "cell 0 2")

        //---- button4 ----
        button4.text = "\u4fdd\u5b58"
        panel1.add(button4, "cell 0 3")
        add(panel1, "cell 1 0")
    }


    private lateinit var scrollPane1: JScrollPane
    private lateinit var table1: JTable
    private lateinit var panel1: JPanel
    private lateinit var button1: JButton
    private lateinit var button2: JButton
    private lateinit var button3: JButton
    private lateinit var button4: JButton

    init {
        initComponents()
    }
}