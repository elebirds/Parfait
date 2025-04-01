package cc.eleb.parfait.ui.dialog

import cc.eleb.parfait.entity.Student
import cc.eleb.parfait.i18n.trs
import cc.eleb.parfait.ui.panel.StudentDataPanel
import net.miginfocom.swing.MigLayout
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*

class StudentAddDialog(val sdp: StudentDataPanel) : JDialog() {
    private fun initComponents() {
        title = "student-add-title".trs()
        preferredSize = Dimension(500, 400)
        minimumSize = Dimension(450, 300)
        val contentPane = contentPane
        contentPane.layout = BorderLayout()
        dialogPane.layout = BorderLayout()
        contentPanel.layout = MigLayout("insets dialog,hidemode 3", "[fill][fill]", "[][][][][][][][]")
        label1.text = "student-add-label1".trs()
        contentPanel.add(label1, "cell 0 0")
        contentPanel.add(textField1, "cell 1 0")
        label2.text = "student-add-label2".trs()
        contentPanel.add(label2, "cell 0 1")
        contentPanel.add(textField2, "cell 1 1")
        label3.text = "student-add-label3".trs()
        contentPanel.add(label3, "cell 0 2")
        comboBox1.model =
            DefaultComboBoxModel(arrayOf("global-unknown".trs(), "global-sex-m".trs(), "global-sex-f".trs()))
        contentPanel.add(comboBox1, "cell 1 2")
        label4.text = "student-add-label4".trs()
        contentPanel.add(label4, "cell 0 3")
        comboBox2.model = DefaultComboBoxModel(arrayOf("global-status-in".trs(), "global-status-out".trs()))
        contentPanel.add(comboBox2, "cell 1 3")
        label5.text = "student-add-label5".trs()
        contentPanel.add(label5, "cell 0 4")
        contentPanel.add(textField6, "cell 1 4")
        label6.text = "student-add-label6".trs()
        contentPanel.add(label6, "cell 0 5")
        contentPanel.add(textField3, "cell 1 5")
        label7.text = "student-add-label7".trs()
        contentPanel.add(label7, "cell 0 6")
        contentPanel.add(textField4, "cell 1 6")
        label8.text = "student-add-label8".trs()
        contentPanel.add(label8, "cell 0 7")
        contentPanel.add(textField5, "cell 1 7")
        dialogPane.add(contentPanel, BorderLayout.CENTER)
        buttonBar.layout = MigLayout("insets dialog,alignx right", "[button,fill][button,fill]", null)
        okButton.text = "global-yes".trs()

        okButton.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (e.button != MouseEvent.BUTTON1) return
                try {
                    textField1.text.toInt()
                } catch (e: Exception) {
                    JOptionPane.showMessageDialog(
                        null,
                        "student-add-error-label1".trs(),
                        "global-error".trs(),
                        JOptionPane.ERROR_MESSAGE
                    )
                    return
                }
                try {
                    textField6.text.toInt()
                } catch (e: Exception) {
                    JOptionPane.showMessageDialog(
                        null,
                        "student-add-error-label5".trs(),
                        "global-error".trs(),
                        JOptionPane.ERROR_MESSAGE
                    )
                    return
                }
                if (textField2.text == null) {
                    JOptionPane.showMessageDialog(
                        null,
                        "student-add-error-label2".trs(),
                        "global-error".trs(),
                        JOptionPane.ERROR_MESSAGE
                    )
                    return
                }
                if (textField3.text == null) {
                    JOptionPane.showMessageDialog(
                        null,
                        "student-add-error-label3".trs(),
                        "global-error".trs(),
                        JOptionPane.ERROR_MESSAGE
                    )
                    return
                }
                if (textField4.text == null) {
                    JOptionPane.showMessageDialog(
                        null,
                        "student-add-error-label6".trs(),
                        "global-error".trs(),
                        JOptionPane.ERROR_MESSAGE
                    )
                    return
                }
                if (textField5.text == null) {
                    JOptionPane.showMessageDialog(
                        null,
                        "student-add-error-label7".trs(),
                        "global-error".trs(),
                        JOptionPane.ERROR_MESSAGE
                    )
                    return
                }
                if (Student.students.containsKey(textField1.text.toInt())) {
                    JOptionPane.showMessageDialog(
                        null,
                        "student-add-error-label4".trs(),
                        "global-error".trs(),
                        JOptionPane.ERROR_MESSAGE
                    )
                    return
                }
                try {
                    Student.students[textField1.text.toInt()] = Student(
                        id = textField1.text.toInt(),
                        name = textField2.text!!,
                        school = textField3.text!!,
                        profession = textField4.text!!,
                        clazz = textField5.text!!,
                        gender = comboBox1.selectedIndex,
                        status = comboBox2.selectedIndex,
                        grade = textField6.text.toInt(),
                        scores = arrayListOf()
                    )
                    sdp.table1.model.fireTableDataChanged()
                    dispose()
                } catch (e: Exception) {
                    JOptionPane.showMessageDialog(
                        null,
                        "${"global-error-unknown".trs()}\n${e.stackTraceToString()}",
                        "global-error".trs(),
                        JOptionPane.ERROR_MESSAGE
                    )
                }
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
    private val buttonBar = JPanel()
    private val okButton = JButton()
    private val cancelButton = JButton()

    init {
        initComponents()
    }
}