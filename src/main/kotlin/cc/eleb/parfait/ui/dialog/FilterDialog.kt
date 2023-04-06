package cc.eleb.parfait.ui.dialog

import cc.eleb.parfait.filter.AlwaysRowFilter
import cc.eleb.parfait.filter.AndFilter
import cc.eleb.parfait.filter.StringRowFilter
import cc.eleb.parfait.i18n.trs
import cc.eleb.parfait.ui.model.StudentTableModel
import cc.eleb.parfait.ui.panel.StudentDataPanel
import net.miginfocom.swing.MigLayout
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*

/**
 * @author elebird
 */
class FilterDialog : JDialog() {
    private fun initComponents() {
        title = "global-filter".trs()
        preferredSize = Dimension(500, 500)
        minimumSize = Dimension(500, 500)
        val contentPane = contentPane
        contentPane.layout = BorderLayout()
        dialogPane.layout = BorderLayout()
        contentPanel.layout = MigLayout("insets dialog,hidemode 3", "[fill][fill][fill]", "[][][][][][][][][][][][]")
        label1.text = "student-add-label1".trs()
        contentPanel.add(label1, "cell 0 1")
        contentPanel.add(fullEqual1, "cell 1 1")
        contentPanel.add(textField1, "cell 2 1")
        label2.text = "student-add-label2".trs()
        contentPanel.add(label2, "cell 0 2")
        contentPanel.add(fullEqual2, "cell 1 2")
        contentPanel.add(textField2, "cell 2 2")
        label3.text = "student-add-label3".trs()
        contentPanel.add(label3, "cell 0 3")
        comboBox1.model = DefaultComboBoxModel(arrayOf("global-any".trs(), "global-unknown".trs(), "global-sex-m".trs(), "global-sex-f".trs()))
        contentPanel.add(comboBox1, "cell 1 3")
        label4.text = "student-add-label4".trs()
        contentPanel.add(label4, "cell 0 4")
        comboBox2.model = DefaultComboBoxModel(arrayOf("global-any".trs(), "global-status-in".trs(), "global-status-out".trs()))
        contentPanel.add(comboBox2, "cell 1 4")
        label5.text = "student-add-label5".trs()
        contentPanel.add(label5, "cell 0 5")
        contentPanel.add(fullEqual3, "cell 1 5")
        contentPanel.add(textField3, "cell 2 5")
        label6.text = "student-add-label6".trs()
        contentPanel.add(label6, "cell 0 6")
        contentPanel.add(fullEqual4, "cell 1 6")
        contentPanel.add(textField4, "cell 2 6")
        label7.text = "student-add-label7".trs()
        contentPanel.add(label7, "cell 0 7")
        contentPanel.add(fullEqual5, "cell 1 7")
        contentPanel.add(textField5, "cell 2 7")
        label8.text = "student-add-label8".trs()
        contentPanel.add(label8, "cell 0 8")
        contentPanel.add(fullEqual6, "cell 1 8")
        contentPanel.add(textField6, "cell 2 8")
        label9.text = "filter-label1".trs()
        contentPanel.add(label9, "cell 0 9")
        contentPanel.add(fullEqual7, "cell 1 9")
        contentPanel.add(textField7, "cell 2 9")
        label10.text = "filter-label2".trs()
        contentPanel.add(label10, "cell 0 10")
        contentPanel.add(fullEqual8, "cell 1 10")
        contentPanel.add(textField8, "cell 2 10")
        label11.text = "filter-label3".trs()
        contentPanel.add(label11, "cell 0 11")
        contentPanel.add(fullEqual9, "cell 1 11")
        contentPanel.add(textField9, "cell 2 11")
        dialogPane.add(JLabel("空置为不设置此条件",JLabel.CENTER), BorderLayout.NORTH)
        dialogPane.add(contentPanel, BorderLayout.CENTER)
        buttonBar.layout = MigLayout("insets dialog,alignx right", "[button,fill][button,fill]", null)
        okButton.text = "global-yes".trs()
        okButton.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (e.button != MouseEvent.BUTTON1) return
                val filter: ArrayList<RowFilter<in StudentTableModel,Int>> = arrayListOf()
                if(textField1.text.isNotEmpty()){
                    try {
                        textField1.text.toInt()
                    }catch (e:Exception){
                        JOptionPane.showMessageDialog(
                            null,
                            "student-add-error-label1".trs(),
                            "global-error".trs(),
                            JOptionPane.ERROR_MESSAGE
                        )
                        return
                    }
                    filter.add(StringRowFilter(
                        textField1.text,
                        StudentDataPanel.instance.table1.convertColumnIndexToModel(0),
                        fullEqual1.selectedIndex==1
                    ))
                }
                if(textField2.toString().isNotEmpty()){
                    filter.add(StringRowFilter(
                        textField2.text,
                        StudentDataPanel.instance.table1.convertColumnIndexToModel(1),
                        fullEqual2.selectedIndex==1
                    ))
                }
                if(comboBox1.selectedIndex!=0){
                    filter.add(StringRowFilter(
                        comboBox1.selectedItem!!.toString(),
                        StudentDataPanel.instance.table1.convertColumnIndexToModel(2),
                        true
                    ))
                }
                if(comboBox2.selectedIndex!=0){
                    filter.add(StringRowFilter(
                        comboBox2.selectedItem!!.toString(),
                        StudentDataPanel.instance.table1.convertColumnIndexToModel(3),
                        true
                    ))
                }
                if(textField3.toString().isNotEmpty()){
                    try {
                        textField3.text.toInt()
                    }catch (e:Exception){
                        JOptionPane.showMessageDialog(
                            null,
                            "student-add-error-label5".trs(),
                            "global-error".trs(),
                            JOptionPane.ERROR_MESSAGE
                        )
                        return
                    }
                    filter.add(RowFilter.numberFilter(
                        when(fullEqual3.selectedIndex){
                            0->RowFilter.ComparisonType.AFTER
                            1->RowFilter.ComparisonType.BEFORE
                            2->RowFilter.ComparisonType.NOT_EQUAL
                            else->RowFilter.ComparisonType.EQUAL
                        },
                        textField3.text.toInt(),
                        StudentDataPanel.instance.table1.convertColumnIndexToModel(4)
                    ))
                }
                if(textField4.toString().isNotEmpty()){
                    filter.add(StringRowFilter(
                        textField4.text,
                        StudentDataPanel.instance.table1.convertColumnIndexToModel(5),
                        fullEqual4.selectedIndex==1
                    ))
                }
                if(textField5.toString().isNotEmpty()){
                    filter.add(StringRowFilter(
                        textField5.text,
                        StudentDataPanel.instance.table1.convertColumnIndexToModel(6),
                        fullEqual5.selectedIndex==1
                    ))
                }
                if(textField6.toString().isNotEmpty()){
                    filter.add(StringRowFilter(
                        textField6.text,
                        StudentDataPanel.instance.table1.convertColumnIndexToModel(7),
                        fullEqual6.selectedIndex==1
                    ))
                }
                val s = AndFilter(filter)

                StudentDataPanel.instance.sorter.rowFilter = s
            }
        })
        buttonBar.add(okButton, "cell 0 0")
        cancelButton.text = "global-cancel".trs()
        cancelButton.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (e.button != MouseEvent.BUTTON1) return
                dispose()
            }
        })
        buttonBar.add(cancelButton, "cell 1 0")
        dialogPane.add(buttonBar, BorderLayout.SOUTH)
        contentPane.add(dialogPane, BorderLayout.CENTER)
        pack()
        setLocationRelativeTo(owner)
        this.isModal = true
    }

    private val dialogPane = JPanel()
    private val contentPanel = JPanel()
    private val label1 = JLabel()
    private val textField1 = JTextField().apply {//学号
        this.preferredSize = Dimension(400, 30)
    }
    private val label2 = JLabel()
    private val textField2 = JTextField()
    private val label3 = JLabel()
    private val comboBox1 = JComboBox<String>()
    private val label4 = JLabel()
    private val comboBox2 = JComboBox<String>()
    private val label5 = JLabel()
    private val textField3 = JTextField()
    private val label6 = JLabel()
    private val textField4 = JTextField()
    private val label7 = JLabel()
    private val textField5 = JTextField()
    private val label8 = JLabel()
    private val textField6 = JTextField()
    private val label9 = JLabel()
    private val textField7 = JTextField()
    private val label10 = JLabel()
    private val textField8 = JTextField()
    private val label11 = JLabel()
    private val textField9 = JTextField()
    private val buttonBar = JPanel()
    private val okButton = JButton()
    private val cancelButton = JButton()
    private var fullEqual1 = JComboBox<String>().apply{
        this.model = DefaultComboBoxModel(arrayOf("部分匹配","完全匹配"))
    }
    private var fullEqual2 = JComboBox<String>().apply{
        this.model = DefaultComboBoxModel(arrayOf("部分匹配","完全匹配"))
    }
    private var fullEqual3 = JComboBox<String>().apply{
        this.model = DefaultComboBoxModel(arrayOf("大于","小于","不等于","等于"))
    }
    private var fullEqual4 = JComboBox<String>().apply{
        this.model = DefaultComboBoxModel(arrayOf("部分匹配","完全匹配"))
    }
    private var fullEqual5 = JComboBox<String>().apply{
        this.model = DefaultComboBoxModel(arrayOf("部分匹配","完全匹配"))
    }
    private var fullEqual6 = JComboBox<String>().apply{
        this.model = DefaultComboBoxModel(arrayOf("部分匹配","完全匹配"))
    }
    private var fullEqual7 = JComboBox<String>().apply{
        this.model = DefaultComboBoxModel(arrayOf("大于","小于","大于等于","小于等于","等于"))
    }
    private var fullEqual8 = JComboBox<String>().apply{
        this.model = DefaultComboBoxModel(arrayOf("大于","小于","大于等于","小于等于","等于"))
    }
    private var fullEqual9 = JComboBox<String>().apply{
        this.model = DefaultComboBoxModel(arrayOf("大于","小于","大于等于","小于等于","等于"))
    }

    init {
        initComponents()
    }
}