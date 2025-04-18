/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.component.dialog

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.swing.Swing
import moe.hhm.parfait.exception.BusinessException
import moe.hhm.parfait.infra.i18n.I18nUtils
import moe.hhm.parfait.ui.viewmodel.StudentDataViewModel
import net.miginfocom.swing.MigLayout
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.awt.*
import java.io.File
import javax.swing.*
import javax.swing.filechooser.FileNameExtensionFilter

/**
 * 学生数据导入对话框
 * 提供文本和Excel两种导入选项
 */
class StudentImportDialog private constructor(owner: Window?) : JDialog(owner, "", ModalityType.APPLICATION_MODAL),
    KoinComponent {
    private val viewModel: StudentDataViewModel by inject()
    private val uiScope = CoroutineScope(Dispatchers.Swing)

    // 导入选项
    private val radioText = JRadioButton().apply { isSelected = true }
    private val radioExcel = JRadioButton().apply { isSelected = false }

    // 文本格式输入
    private val textImportLabel = JLabel()
    private val textFormatField = JTextArea(6, 40).apply {
        lineWrap = true
        wrapStyleWord = true
        border = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        )
    }
    private val textFormatScrollPane = JScrollPane(textFormatField).apply {
        preferredSize = Dimension(500, 120)
        minimumSize = Dimension(200, 100)
    }
    private val textFormatHelpLabel = JLabel().apply {
        font = font.deriveFont(font.size2D - 1f)
        foreground = Color.DARK_GRAY
    }

    // Excel文件选择
    private val excelPathField = JTextField().apply { isEditable = false }
    private val excelBrowseButton = JButton()
    private val excelImportLabel = JLabel()

    // 卡片布局和面板
    private val cardPanel = JPanel(CardLayout())
    private val TEXT_PANEL = "TEXT_PANEL"
    private val EXCEL_PANEL = "EXCEL_PANEL"

    // 按钮
    private val okButton = JButton()
    private val cancelButton = JButton()

    // 选择的文件路径
    private var selectedFilePath: String? = null

    init {
        // 设置对话框属性
        defaultCloseOperation = DISPOSE_ON_CLOSE
        isResizable = true
        preferredSize = Dimension(550, 420)

        // 设置i18n
        I18nUtils.bindTitle(this, "student.import.dialog.title")
        I18nUtils.bindText(radioText, "student.import.option.text")
        I18nUtils.bindText(radioExcel, "student.import.option.excel")
        I18nUtils.bindText(textImportLabel, "student.import.text.label")
        I18nUtils.bindText(excelImportLabel, "student.import.excel.label")
        I18nUtils.bindText(excelBrowseButton, "student.import.excel.browse")
        I18nUtils.bindText(okButton, "button.ok")
        I18nUtils.bindText(cancelButton, "button.cancel")

        // 设置帮助文本HTML格式
        textFormatHelpLabel.text =
            "<html>要求格式: 每行一条记录，字段顺序为：<br>学号,姓名,性别(男/女/未知),状态(在籍/休学/毕业/异常),学院,专业,年级,班级<br>例如: 20250101,张三,男,在籍,计算机学院,计算机科学与技术,2025,计科1班</html>"

        // 按钮组
        val buttonGroup = ButtonGroup()
        buttonGroup.add(radioText)
        buttonGroup.add(radioExcel)

        // 准备文本面板
        val textFormatPanel = JPanel(MigLayout("insets 0, fillx", "[grow]", "[]5[]10[]10[]"))
        textFormatPanel.add(textImportLabel, "grow, wrap")
        textFormatPanel.add(textFormatScrollPane, "grow, h 120!, wrap")
        textFormatPanel.add(textFormatHelpLabel, "grow, wrap")

        // 准备Excel面板
        val excelPanel = JPanel(MigLayout("insets 0, fillx", "[grow][]", "[]10[]10[]"))
        excelPanel.add(excelImportLabel, "span, grow, wrap")
        excelPanel.add(excelPathField, "grow")
        excelPanel.add(excelBrowseButton, "wrap")
        excelPanel.add(JLabel("<html>Excel表格必须包含以下表头：<br>学号 姓名 性别 状态 学院 专业 年级 班级<br>每行代表一个学生记录</html>").apply {
            font = font.deriveFont(font.size2D - 1f)
            foreground = Color.DARK_GRAY
        }, "span, grow")

        // 添加到卡片面板
        cardPanel.add(textFormatPanel, TEXT_PANEL)
        cardPanel.add(excelPanel, EXCEL_PANEL)

        // 设置布局
        contentPane.layout = MigLayout("insets 15, fillx", "[grow]", "[]10[]10[]10[]10[]")

        // 添加组件
        contentPane.add(JLabel(I18nUtils.getText("student.import.dialog.tip")), "wrap")
        contentPane.add(JLabel(I18nUtils.getText("student.import.dialog.message")), "wrap")

        val optionsPanel = JPanel(MigLayout("insets 0", "[]20[]", "[]"))
        optionsPanel.add(radioText)
        optionsPanel.add(radioExcel)
        contentPane.add(optionsPanel, "grow, wrap")

        contentPane.add(cardPanel, "grow, wrap")

        val buttonPanel = JPanel(FlowLayout(FlowLayout.RIGHT, 10, 0))
        buttonPanel.add(okButton)
        buttonPanel.add(cancelButton)
        contentPane.add(buttonPanel, "grow, align right")

        // 设置事件监听器
        radioText.addActionListener {
            (cardPanel.layout as CardLayout).show(cardPanel, TEXT_PANEL)
        }
        radioExcel.addActionListener {
            (cardPanel.layout as CardLayout).show(cardPanel, EXCEL_PANEL)
        }

        excelBrowseButton.addActionListener {
            val fileChooser = JFileChooser()
            fileChooser.fileFilter = FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx")
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                selectedFilePath = fileChooser.selectedFile.absolutePath
                excelPathField.text = selectedFilePath
            }
        }

        okButton.addActionListener { handleOkAction() }
        cancelButton.addActionListener { dispose() }

        // 显示默认面板
        (cardPanel.layout as CardLayout).show(cardPanel, TEXT_PANEL)

        // 包装对话框
        pack()
        setLocationRelativeTo(owner)
    }

    /**
     * 处理确定按钮动作
     */
    private fun handleOkAction() {
        okButton.isEnabled = false
        cancelButton.isEnabled = false

        if (radioText.isSelected) {
            // 处理文本导入
            val text = textFormatField.text
            if (text.isBlank()) throw BusinessException("student.import.text.empty")
            importFromText(text)
        } else {
            // 处理Excel导入
            if (selectedFilePath == null) throw BusinessException("student.import.excel.path.empty")
            importFromExcel(selectedFilePath!!)
        }
        dispose()
    }

    /**
     * 从文本导入学生数据
     */
    private fun importFromText(text: String) {
        val lines = text.split("\n")
            .map { it.trim() }
            .filter { it.isNotBlank() }

        if (lines.isEmpty()) throw BusinessException("student.import.text.empty")

        viewModel.importStudentFromText(lines)
    }

    /**
     * 从Excel导入学生数据
     */
    private fun importFromExcel(filePath: String) {
        val file = File(filePath)
        if (!file.exists() || !file.canRead()) throw BusinessException("student.import.excel.file.error")
        viewModel.importStudentFromExcel(file)
    }

    companion object {
        /**
         * 显示学生导入对话框
         */
        fun show(owner: Window? = null) {
            StudentImportDialog(owner).isVisible = true
        }
    }
} 