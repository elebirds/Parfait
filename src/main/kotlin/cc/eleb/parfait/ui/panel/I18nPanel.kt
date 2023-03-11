/*
 * Created by JFormDesigner on Sat Mar 11 20:39:09 CST 2023
 */
package cc.eleb.parfait.ui.panel

import net.miginfocom.swing.MigLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import javax.swing.*
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder
import javax.swing.border.TitledBorder
import javax.swing.table.DefaultTableModel

/**
 * @author hhmcn
 */
class I18nPanel : JPanel() {
    private fun initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Evaluation license - csgo fps
        label1 = JLabel()
        comboBox1 = JComboBox<Any?>()
        label2 = JLabel()
        scrollPane1 = JScrollPane()
        table1 = JTable()
        panel1 = JPanel()
        button2 = JButton()
        button3 = JButton()

        //======== this ========
        setLayout(
            MigLayout(
                "insets 0,hidemode 3",  // columns
                "[fill]" + "[fill]" + "[fill]" + "[fill]" + "[fill]" + "[fill]" + "[fill]",  // rows
                "[]" + "[]" + "[]" + "[]"
            )
        )

        //---- label1 ----
        label1!!.setText("\u5f53\u524d\u8bed\u8a00\uff1a")
        add(label1, "cell 0 0")
        add(comboBox1, "cell 1 0")

        //---- label2 ----
        label2!!.setText("\u5bf9\u5e94\u7ffb\u8bd1\uff1a")
        add(label2, "cell 0 1")

        //======== scrollPane1 ========
        run({


            //---- table1 ----
            table1!!.setModel(object : DefaultTableModel(
                arrayOf(arrayOf(null, null), arrayOf(null, null)),
                arrayOf("\u4e2d\u6587", "\u5916\u6587")
            ) {
                var columnTypes: Array<Class<*>> = arrayOf(String::class.java, String::class.java)
                public override fun getColumnClass(columnIndex: Int): Class<*> {
                    return columnTypes.get(columnIndex)
                }
            })
            table1!!.setPreferredScrollableViewportSize(Dimension(600, 400))
            scrollPane1!!.setViewportView(table1)
        })
        add(scrollPane1, "cell 1 1,align center center,grow 0 0")

        //======== panel1 ========
        run({
            panel1!!.setLayout(
                MigLayout(
                    "hidemode 3",  // columns
                    "[fill]" + "[fill]" + "[fill]",  // rows
                    "[]" + "[]" + "[]"
                )
            )

            //---- button2 ----
            button2!!.setText("\u4fdd\u5b58")
            panel1!!.add(button2, "cell 1 0")

            //---- button3 ----
            button3!!.setText("\u91cd\u65b0\u52a0\u8f7d")
            panel1!!.add(button3, "cell 1 1")
        })
        add(panel1, "cell 1 1")
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Evaluation license - csgo fps
    private var label1: JLabel? = null
    private var comboBox1: JComboBox<*>? = null
    private var label2: JLabel? = null
    private var scrollPane1: JScrollPane? = null
    private var table1: JTable? = null
    private var panel1: JPanel? = null
    private var button2: JButton? = null
    private var button3: JButton? =
        null // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on

    init {
        initComponents()
    }
}