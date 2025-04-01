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

class ImpoStudentFromStringDialog : JDialog() {
    private fun initComponents() {
        title = "isfs-title".trs()
        this.preferredSize = Dimension(800, 600)
        this.minimumSize = Dimension(800, 600)
        contentPane.layout = BorderLayout()
        dialogPane.layout = BorderLayout()
        contentPanel.layout = MigLayout("insets dialog,hidemode 3", "[fill]", "[][][][][]")
        label1.text = "isfs-label1".trs()
        label2.text = "isfs-label2".trs()
        label3.text = "isfs-label3".trs()
        label4.text = "isfs-label4".trs()
        contentPanel.add(label1, "cell 0 0")
        contentPanel.add(label2, "cell 0 1")
        contentPanel.add(label3, "cell 0 2")
        contentPanel.add(label4, "cell 0 3")
        textArea1.preferredSize = Dimension(750, 570)
        scrollPane1.setViewportView(textArea1)
        contentPanel.add(scrollPane1, "cell 0 4")
        dialogPane.add(contentPanel, BorderLayout.CENTER)
        buttonBar.layout = MigLayout("insets dialog,alignx right", "[button,fill][button,fill]", "[]")
        okButton.text = "global-yes".trs()
        okButton.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                val ipd = textArea1.text
                if (ipd == null || ipd == "") {
                    JOptionPane.showMessageDialog(
                        null,
                        "isfs-error-1".trs(),
                        "global-error".trs(),
                        JOptionPane.ERROR_MESSAGE
                    )
                    return
                }
                val a = ipd.split("\n")
                var i = 0
                a.forEach {
                    i++
                    val b = it.split(".")
                    if (b.size != 6 && b.size != 8) {
                        JOptionPane.showMessageDialog(
                            null,
                            "isfs-error-2".trs().replace("%line", i.toString()) + "isfs-error-3".trs(),
                            "global-error".trs(),
                            JOptionPane.ERROR_MESSAGE
                        )
                        return
                    }
                    try {
                        b[0].toInt()
                    } catch (e: Exception) {
                        JOptionPane.showMessageDialog(
                            null,
                            "isfs-error-2".trs().replace("%line", i.toString()) + "student-add-error-label1".trs(),
                            "global-error".trs(),
                            JOptionPane.ERROR_MESSAGE
                        )
                        return
                    }
                    if (Student.students.containsKey(b[0].toInt())) {
                        JOptionPane.showMessageDialog(
                            null,
                            "isfs-error-2".trs().replace("%line", i.toString()) + "student-add-error-label4".trs(),
                            "global-error".trs(),
                            JOptionPane.ERROR_MESSAGE
                        )
                        return
                    }
                    if (!arrayListOf("男", "女", "未知").contains(b[2])) {
                        JOptionPane.showMessageDialog(
                            null,
                            "isfs-error-2".trs().replace("%line", i.toString()) + "isfs-error-4".trs(),
                            "global-error".trs(),
                            JOptionPane.ERROR_MESSAGE
                        )
                        return
                    }
                    if (!arrayListOf("在籍", "毕业").contains(b[3])) {
                        JOptionPane.showMessageDialog(
                            null,
                            "isfs-error-2".trs().replace("%line", i.toString()) + "isfs-error-5".trs(),
                            "global-error".trs(),
                            JOptionPane.ERROR_MESSAGE
                        )
                        return
                    }
                    if (b.size == 8) {
                        try {
                            b[6].toInt()
                        } catch (e: Exception) {
                            JOptionPane.showMessageDialog(
                                null,
                                "isfs-error-2".trs().replace("%line", i.toString()) + "student-add-error-label5".trs(),
                                "global-error".trs(),
                                JOptionPane.ERROR_MESSAGE
                            )
                            return
                        }
                    } else {
                        val clazz = b[5]
                        try {
                            val grade = clazz.substring(0, 4).toInt()
                            val profession = clazz.replace(Regex("本科\\d班\$"), "").replace(Regex("^\\d{4}级"), "")
                                .replace(Regex("^\\d{4}"), "")
                            if (profession == "") throw Exception()
                        } catch (e: Exception) {
                            JOptionPane.showMessageDialog(
                                null,
                                "isfs-error-2".trs().replace("%line", i.toString()) + "isfs-error-6".trs(),
                                "global-error".trs(),
                                JOptionPane.ERROR_MESSAGE
                            )
                            return
                        }
                    }
                }
                try {
                    a.forEach {
                        val b = it.split(".")
                        val st = Student(
                            id = b[0].toInt(),
                            name = b[1],
                            gender = if (b[2].contains('女')) {
                                2
                            } else if (b[2].contains('男')) {
                                1
                            } else {
                                0
                            },
                            status = if (b[3] == "在籍") 0 else 1,
                            clazz = b[5],
                            scores = arrayListOf(),
                            grade = if (b.size == 6) b[5].substring(0, 4).toInt() else b[6].toInt(),
                            school = b[4],
                            profession = if (b.size == 6) b[5].replace(Regex("本科\\d班\$"), "")
                                .replace(Regex("^\\d{4}级"), "")
                                .replace(Regex("^\\d{4}"), "") else b[7]
                        )
                        Student.students[b[0].toInt()] = st
                    }
                } catch (e: Exception) {
                    JOptionPane.showMessageDialog(
                        null,
                        "isfs-error-2".trs().replace("%line", i.toString()) + e.stackTraceToString(),
                        "global-error".trs(),
                        JOptionPane.ERROR_MESSAGE
                    )
                }
                JOptionPane.showMessageDialog(
                    null,
                    "isfs-success".trs().replace("%i", i.toString()),
                    "global-success".trs(),
                    JOptionPane.INFORMATION_MESSAGE
                )
                StudentDataPanel.instance.table1.model.fireTableDataChanged()
                dispose()
            }
        })
        buttonBar.add(okButton, "cell 0 0")
        cancelButton.text = "global-cancel".trs()
        cancelButton.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
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
    private val label2 = JLabel()
    private val label3 = JLabel()
    private val label4 = JLabel()
    private val scrollPane1 = JScrollPane()
    private val textArea1 = JTextArea().apply {
        this.autoscrolls = true
    }
    private val buttonBar = JPanel()
    private val okButton = JButton()
    private val cancelButton = JButton()

    init {
        initComponents()
    }
}