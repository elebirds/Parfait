package cc.eleb.parfait.ui.dialog

import cc.eleb.parfait.config.ParConfig
import cc.eleb.parfait.filter.StringRowFilter
import cc.eleb.parfait.i18n.trs
import cc.eleb.parfait.ui.panel.StudentDataPanel
import net.miginfocom.swing.MigLayout
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*

/**
 * @author hhmcn
 */
class SearchDialog : JDialog() {
    private fun initComponents() {
        this.title = "global-search".trs()
        contentPane.layout = BorderLayout()
        contentPanel.add(comboBox1, "cell 0 0")
        contentPanel.add(textField, "cell 1 0")
        contentPanel.add(label, "cell 0 1")
        contentPanel.add(fullEqualCB, "cell 1 1")
        dialogPane.add(contentPanel, BorderLayout.CENTER)
        buttonBar.add(okButton, "cell 0 0")
        buttonBar.add(cancelButton, "cell 1 0")
        dialogPane.add(buttonBar, BorderLayout.SOUTH)
        contentPane.add(dialogPane, BorderLayout.CENTER)
        this.pack()
        this.setLocationRelativeTo(this.owner)
        this.isModal = true
    }

    private var dialogPane = JPanel()
    private var contentPanel = JPanel().apply {
        this.layout = MigLayout("insets dialog,hidemode 3", "[fill][fill][fill]", "[][][]")
    }
    private var label = JLabel().apply {
        this.text = "search-full-equal".trs()
    }
    private var fullEqualCB = JCheckBox()
    private val fullEqual: Boolean
        get() {
            return fullEqualCB.isSelected
        }

    private var comboBox1 = JComboBox<String>().apply {
        this.minimumSize = Dimension(40, 35)
        this.model = DefaultComboBoxModel(
            arrayOf(
                "student-table-column2".trs(),//姓名
                "student-table-column1".trs(),//学号
                "student-table-column6".trs(),//学院
                "student-table-column7".trs(),//专业
                "student-table-column8".trs()
            )
        )//班级
    }
    private var textField = JTextField().apply {
        this.minimumSize = Dimension(300, 40)
    }
    private var buttonBar = JPanel().apply {
        this.layout = MigLayout("insets dialog,alignx right", "[button,fill][button,fill]", null)
    }
    private var okButton = JButton().apply {
        this.text = "global-yes".trs()
        this.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (e.button != MouseEvent.BUTTON1) return
                if (!ParConfig.checkInited()) return
                if (textField.text.isEmpty()) {
                    JOptionPane.showMessageDialog(
                        null,
                        "isfs-error-1".trs(),
                        "global-error".trs(),
                        JOptionPane.ERROR_MESSAGE
                    )
                    return
                }
                if (comboBox1.selectedItem == "student-table-column1".trs()) {
                    try {
                        textField.text.toInt()
                    } catch (e: Exception) {
                        JOptionPane.showMessageDialog(
                            null,
                            "student-add-error-label1".trs(),
                            "global-error".trs(),
                            JOptionPane.ERROR_MESSAGE
                        )
                        return
                    }
                }
                StudentDataPanel.instance.sorter.rowFilter = StringRowFilter(
                    textField.text,
                    StudentDataPanel.instance.table1.convertColumnIndexToModel(
                        when (comboBox1.selectedItem) {
                            "student-table-column1".trs() -> {//学号
                                0
                            }

                            "student-table-column2".trs() -> {//姓名
                                1
                            }

                            "student-table-column6".trs() -> {//学院
                                5
                            }

                            "student-table-column7".trs() -> {//专业
                                6
                            }

                            "student-table-column8".trs() -> {//班级
                                7
                            }

                            else -> 0
                        }
                    ),
                    fullEqual
                )
                this@SearchDialog.dispose()
            }
        })
    }
    private var cancelButton = JButton().apply {
        this.text = "global-cancel".trs()
        this.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                this@SearchDialog.dispose()
            }
        })
    }

    init {
        initComponents()
    }
}