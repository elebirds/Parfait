package cc.eleb.parfait.ui.dialog

import cc.eleb.parfait.i18n.trs
import cc.eleb.parfait.ui.ParfaitFrame
import cc.eleb.parfait.utils.GlobalSettings
import cc.eleb.parfait.utils.ParfaitPrefs
import net.miginfocom.swing.MigLayout
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*

/**
 * @author elebird
 */
class GlobalSettingDialog : JDialog() {
    private fun initComponents() {
        title = "global-setting-title".trs()
        preferredSize = Dimension(700, 400)
        minimumSize = Dimension(700, 400)
        contentPane.layout = BorderLayout()
        dialogPane.layout = BorderLayout()
        contentPanel.layout = MigLayout("insets dialog,hidemode 3", "[fill][fill]", "[][][][][][][][]")
        contentPanel.add(languageLabel, "cell 0 0")
        contentPanel.add(languageComboBox, "cell 1 0")
        contentPanel.add(outputStringLabel, "cell 0 1")
        contentPanel.add(outputStringIntroduce, "cell 1 1")
        contentPanel.add(outputStringIntroduce2, "cell 1 2")
        contentPanel.add(outputStringIntroduce3, "cell 1 3")
        contentPanel.add(outputStringField, "cell 1 4")
        dialogPane.add(contentPanel, BorderLayout.CENTER)
        buttonBar.layout = MigLayout("insets dialog,alignx right", "[button,fill][button,fill]", null)
        buttonBar.add(okButton, "cell 0 0")
        buttonBar.add(cancelButton, "cell 1 0")
        dialogPane.add(buttonBar, BorderLayout.SOUTH)
        contentPane.add(dialogPane, BorderLayout.CENTER)
        pack()
        setLocationRelativeTo(owner)
        this.isModal = true
    }

    private val dialogPane = JPanel()
    private val contentPanel = JPanel()
    private val languageLabel = JLabel().apply {
        this.text = "global-setting-language".trs()
    }
    private val languageComboBox = JComboBox<String>().apply {
        this.model = DefaultComboBoxModel(arrayOf("中文", "English", "Français", "にほんご"))
        this.model.selectedItem = arrayOf("中文", "English", "Français", "にほんご")[GlobalSettings.LANGUAGE]
    }
    private val outputStringIntroduce = JLabel().apply {
        this.text = "global-setting-outputIntroduction1".trs()
    }
    private val outputStringIntroduce2 = JLabel().apply {
        this.text = "global-setting-outputIntroduction2".trs()
    }
    private val outputStringIntroduce3 = JLabel().apply {
        this.text = "global-setting-outputIntroduction3".trs()
    }
    private val outputStringLabel = JLabel().apply {
        this.text = "global-setting-outputString".trs()
    }
    private val outputStringField = JTextField().apply {
        this.preferredSize = Dimension(400, 30)
        this.text = GlobalSettings.OUTPUT_STRING
    }


    private val buttonBar = JPanel()
    private val okButton = JButton().apply {
        this.text = "global-yes".trs()
        this.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (e.button != MouseEvent.BUTTON1) return
                try {
                    if (GlobalSettings.LANGUAGE != languageComboBox.selectedIndex) {
                        GlobalSettings.LANGUAGE = languageComboBox.selectedIndex
                        ParfaitFrame.instance.reloadTranslation(true)
                    } else {
                        GlobalSettings.LANGUAGE = languageComboBox.selectedIndex
                    }
                    GlobalSettings.OUTPUT_STRING = outputStringField.text
                    ParfaitPrefs.state.putInt("gloSetting.language", languageComboBox.selectedIndex)
                    ParfaitPrefs.state.put("gloSetting.outputString", outputStringField.text)
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
    }

    private val cancelButton = JButton().apply {
        this.text = "global-cancel".trs()
        this.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (e.button != MouseEvent.BUTTON1) return
                dispose()
            }
        })
    }

    init {
        initComponents()
    }
}