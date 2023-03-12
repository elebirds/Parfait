/*
 * Created by JFormDesigner on Sat Mar 11 20:53:53 CST 2023
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
class GPAPanel : JPanel() {
    private fun initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Evaluation license - csgo fps
        label1 = JLabel()
        scrollPane1 = JScrollPane()
        table1 = JTable()
        panel1 = JPanel()
        button2 = JButton()
        button3 = JButton()

        //======== this ========
        layout = MigLayout(
            "insets 0,hidemode 3,gap 0 0",  // columns
            "[grow,fill]" + "[305,grow,fill]" + "[grow,fill]",  // rows
            "[grow,fill]"
        )
        add(label1, "cell 0 0")

        //======== scrollPane1 ========
        //---- table1 ----
        table1.model = object : DefaultTableModel(
            arrayOf(arrayOf(null, null), arrayOf(null, null)),
            arrayOf("\u6210\u7ee9\uff08\u5927\u4e8e\u7b49\u4e8e\u6b64\u503c\uff09", "GPA")
        ) {
            var columnTypes: Array<Class<*>> = arrayOf(Int::class.java, Double::class.java)
            override fun getColumnClass(columnIndex: Int): Class<*> {
                return columnTypes[columnIndex]
            }
        }
        val cm: TableColumnModel = table1.columnModel
        cm.getColumn(0).minWidth = 200
        table1.preferredScrollableViewportSize = Dimension(700, 400)
        scrollPane1.setViewportView(table1)
        add(scrollPane1, "cell 1 0")

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
        add(panel1, "cell 2 0")
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Evaluation license - csgo fps
    private lateinit var label1: JLabel
    private lateinit var scrollPane1: JScrollPane
    private lateinit var table1: JTable
    private lateinit var panel1: JPanel
    private lateinit var button2: JButton
    private lateinit var button3: JButton

    init {
        initComponents()
    }
}