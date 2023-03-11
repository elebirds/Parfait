/*
 * Created by JFormDesigner on Fri Mar 10 10:27:17 CST 2023
 */
package cc.eleb.parfait.ui.panel

import cc.eleb.parfait.ui.frame.ScoreFrame
import net.miginfocom.swing.MigLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.io.File
import javax.swing.*
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder
import javax.swing.border.TitledBorder
import javax.swing.filechooser.FileFilter
import javax.swing.table.DefaultTableModel
import javax.swing.table.TableColumnModel

/**
 * @author hhmcn
 */
class StudentDataPanel constructor() : JPanel() {
    private fun addStudentMouseClicked(e: MouseEvent) {
        if (e.getButton() != MouseEvent.BUTTON1) return
        (table1!!.getModel() as DefaultTableModel).addRow(arrayOf<Any?>(null, null, "未知"))
        JOptionPane.showInputDialog(this, "测试", "", JOptionPane.INFORMATION_MESSAGE)
        JOptionPane.showMessageDialog(null, "呃呃", "就", JOptionPane.INFORMATION_MESSAGE)
    }

    private fun deleteStudentMouseClicked(e: MouseEvent) {
        if (e.getButton() != MouseEvent.BUTTON1) return
        for (selectedRow: Int in table1!!.getSelectedRows()) {
            (table1!!.getModel() as DefaultTableModel).removeRow(table1!!.getSelectedRow())
        }
    }

    private fun impoStudentMouseClicked(e: MouseEvent) {
        if (e.getButton() != MouseEvent.BUTTON1) return
        val fd: JFileChooser = JFileChooser()
        fd.setFileFilter(object : FileFilter() {
            public override fun accept(f: File): Boolean {
                return f.isDirectory() || f.toString().endsWith(".xlsx")
            }

            public override fun getDescription(): String {
                return "Excel文件(.xlsx)"
            }
        })
        fd.setMultiSelectionEnabled(false)
        fd.setFileSelectionMode(JFileChooser.FILES_ONLY)
        val res: Int = fd.showOpenDialog(this)
        if (res == JFileChooser.APPROVE_OPTION) {
            if (!fd.getSelectedFile().toString().endsWith(".xlsx")) return
            JOptionPane.showMessageDialog(null, "??", "???", JOptionPane.ERROR_MESSAGE)
        }
    }

    private fun editScoreMouseClicked(e: MouseEvent) {
        if (e.getButton() != MouseEvent.BUTTON1) return
        val sf: ScoreFrame = ScoreFrame()
        sf.setVisible(true)
    }

    private fun generateWordMouseClicked(e: MouseEvent) {
        if (e.getButton() != MouseEvent.BUTTON1) return
        //TODO:generateWord
    }

    private fun impoStudentFromStringMouseClicked(e: MouseEvent) {
        if (e.getButton() != MouseEvent.BUTTON1) return
        // TODO add your code here
    }

    private fun expoToStringMouseClicked(e: MouseEvent) {
        if (e.getButton() != MouseEvent.BUTTON1) return
        if (table1!!.getSelectedRows().size == 0) {
            JOptionPane.showMessageDialog(
                this, "您未选中任何学生，无法导出数据为文本。", "错误",
                JOptionPane.ERROR_MESSAGE
            )
            return
        }
        val a: StringBuilder = StringBuilder()
        for (selectedRow: Int in table1!!.getSelectedRows()) {
            val si: Any? =
                (table1!!.getModel() as DefaultTableModel).getDataVector().elementAt(table1!!.getSelectedRow())
                    .elementAt(0)
            if (si == null) continue
            val sid: Int = si as Int
            a.append(si).append("sisis").append("\n")
        }
        if (JOptionPane.showConfirmDialog(
                this, a.toString(), "是否复制到剪贴板", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE
            ) == JOptionPane.OK_OPTION
        ) {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(StringSelection(a.toString()), null)
        }
        // TODO add your code here
    }

    private fun initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Evaluation license - csgo fps
        label1 = JLabel()
        scrollPane1 = JScrollPane()
        table1 = JTable()
        panel1 = JPanel()
        button2 = JButton()
        button3 = JButton()
        button4 = JButton()
        button8 = JButton()
        button6 = JButton()
        button7 = JButton()
        button1 = JButton()

        setLayout(
            MigLayout(
                "hidemode 3",  // columns
                "[fill]" + "[fill]",  // rows
                "[]" + "[]" + "[]"
            )
        )
        add(label1, "cell 0 0")

        //======== scrollPane1 ========
        run({


            //---- table1 ----
            table1!!.setModel(
                object : DefaultTableModel(
                    arrayOf(arrayOf<Any?>(111111, null, null, null, null, null, null, null, null)), arrayOf(
                        "\u5b66\u53f7", "\u59d3\u540d", "\u6027\u522b", "\u5b66\u7c4d\u72b6\u6001",
                        "\u5e74\u7ea7", "\u5b66\u9662", "\u4e13\u4e1a", "\u73ed\u7ea7",
                        "\u52a0\u6743\u5e73\u5747\u5206"
                    )
                ) {
                    var columnTypes: Array<Class<*>> = arrayOf(
                        Int::class.java, String::class.java, String::class.java, String::class.java,
                        Int::class.java, String::class.java, String::class.java, String::class.java, Double::class.java
                    )
                    var columnEditable: BooleanArray =
                        booleanArrayOf(false, true, true, true, true, true, true, true, false)

                    public override fun getColumnClass(columnIndex: Int): Class<*> {
                        return columnTypes.get(columnIndex)
                    }

                    public override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean {
                        return columnEditable.get(columnIndex)
                    }
                })
            run({
                val cm: TableColumnModel = table1!!.getColumnModel()
                cm.getColumn(2).setCellEditor(
                    DefaultCellEditor(
                        JComboBox(
                            DefaultComboBoxModel<Any?>(arrayOf<String?>("\u672a\u77e5", "\u7537", "\u5973"))
                        )
                    )
                )
                cm.getColumn(3).setCellEditor(
                    DefaultCellEditor(
                        JComboBox(DefaultComboBoxModel<Any?>(arrayOf<String?>("\u5728\u7c4d", "\u6bd5\u4e1a")))
                    )
                )
            })
            table1!!.setPreferredScrollableViewportSize(Dimension(600, 400))
            table1!!.setShowHorizontalLines(false)
            table1!!.setShowVerticalLines(false)
            scrollPane1!!.setViewportView(table1)
        })
        add(scrollPane1, "cell 0 0,dock center")

        //======== panel1 ========
        run({
            panel1!!.setLayout(
                MigLayout(
                    "hidemode 3",  // columns
                    "[fill]",  // rows
                    "[]" + "[]" + "[]" + "[]" + "[]" + "[]" + "[]" + "[]" + "[]" + "[]" + "[]"
                )
            )

            //---- button2 ----
            button2!!.setText("\u6dfb\u52a0\u5b66\u751f")
            button2!!.addMouseListener(object : MouseAdapter() {
                public override fun mouseClicked(e: MouseEvent) {
                    addStudentMouseClicked(e)
                }
            })
            panel1!!.add(button2, "cell 0 0")

            //---- button3 ----
            button3!!.setText("\u7f16\u8f91\u6210\u7ee9")
            button3!!.addMouseListener(object : MouseAdapter() {
                public override fun mouseClicked(e: MouseEvent) {
                    editScoreMouseClicked(e)
                }
            })
            panel1!!.add(button3, "cell 0 1")

            //---- button4 ----
            button4!!.setText("\u5220\u9664\u5b66\u751f")
            button4!!.addMouseListener(object : MouseAdapter() {
                public override fun mouseClicked(e: MouseEvent) {
                    deleteStudentMouseClicked(e)
                }
            })
            panel1!!.add(button4, "cell 0 2")

            //---- button8 ----
            button8!!.setText("\u4ece\u6587\u672c\u5bfc\u5165")
            button8!!.addMouseListener(object : MouseAdapter() {
                public override fun mouseClicked(e: MouseEvent) {
                    impoStudentFromStringMouseClicked(e)
                }
            })
            panel1!!.add(button8, "cell 0 3")

            //---- button6 ----
            button6!!.setText("\u4ece\u8868\u683c\u5bfc\u5165")
            button6!!.addMouseListener(object : MouseAdapter() {
                public override fun mouseClicked(e: MouseEvent) {
                    impoStudentMouseClicked(e)
                }
            })
            panel1!!.add(button6, "cell 0 4")

            //---- button7 ----
            button7!!.setText("\u5bfc\u51fa\u4e3a\u6587\u672c")
            button7!!.addMouseListener(object : MouseAdapter() {
                public override fun mouseClicked(e: MouseEvent) {
                    expoToStringMouseClicked(e)
                }
            })
            panel1!!.add(button7, "cell 0 5")

            //---- button1 ----
            button1!!.setText("\u751f\u6210\u8bc1\u660e")
            button1!!.addMouseListener(object : MouseAdapter() {
                public override fun mouseClicked(e: MouseEvent) {
                    generateWordMouseClicked(e)
                }
            })
            panel1!!.add(button1, "cell 0 6")
        })
        add(panel1, "cell 1 0")
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Evaluation license - csgo fps
    private var label1: JLabel? = null
    private var scrollPane1: JScrollPane? = null
    private var table1: JTable? = null
    private var panel1: JPanel? = null
    private var button2: JButton? = null
    private var button3: JButton? = null
    private var button4: JButton? = null
    private var button8: JButton? = null
    private var button6: JButton? = null
    private var button7: JButton? = null
    private var button1: JButton? =
        null // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on

    init {
        initComponents()
    }
}