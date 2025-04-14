/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.component.dialog

import moe.hhm.parfait.infra.i18n.I18nUtils
import moe.hhm.parfait.ui.viewmodel.StudentDataViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.io.File
import net.miginfocom.swing.MigLayout

/**
 * 学生数据导出对话框
 * 提供文本和Excel两种导出选项
 */
class StudentExportDialog private constructor(owner: Window?) : JDialog(owner, "", ModalityType.APPLICATION_MODAL), KoinComponent {
    private val viewModel: StudentDataViewModel by inject()
    
    // 导出选项
    private val radioText = JRadioButton().apply { isSelected = true }
    private val radioExcel = JRadioButton().apply { isSelected = false }
    
    // 文本格式输入
    private val textFormatField = JTextArea(3, 40).apply { 
        text = "{id},{name},{gender},{department},{major},{grade},{class}"
        lineWrap = true
        wrapStyleWord = true
        border = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        )
    }
    private val textFormatScrollPane = JScrollPane(textFormatField).apply {
        preferredSize = Dimension(500, 80)
        minimumSize = Dimension(200, 60)
    }
    private val textFormatHelpLabel = JLabel().apply {
        font = font.deriveFont(font.size2D - 1f)
        foreground = Color.DARK_GRAY
    }
    
    // Excel文件选择
    private val excelPathField = JTextField().apply { isEditable = false }
    private val excelBrowseButton = JButton()
    
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
        preferredSize = Dimension(550, 350)
        
        // 设置i18n
        I18nUtils.bindTitle(this, "student.export.dialog.title")
        I18nUtils.bindText(radioText, "student.export.option.text")
        I18nUtils.bindText(radioExcel, "student.export.option.excel")
        I18nUtils.bindText(excelBrowseButton, "student.export.excel.browse")
        I18nUtils.bindText(okButton, "dialog.button.ok")
        I18nUtils.bindText(cancelButton, "dialog.button.cancel")
        
        // 设置帮助文本HTML格式
        textFormatHelpLabel.text = "<html>支持变量: {id} - 学号, {name} - 姓名, {gender} - 性别<br>{department} - 学院, {major} - 专业, {grade} - 年级, {class} - 班级<br>{datetime} - 当前时间, {score_weighted} - 加权平均分, {score_simple} - 简单平均分, {gpa} - 绩点</html>"
        
        // 按钮组
        val buttonGroup = ButtonGroup()
        buttonGroup.add(radioText)
        buttonGroup.add(radioExcel)
        
        // 准备文本面板
        val textFormatPanel = JPanel(MigLayout("insets 0, fillx", "[grow]", "[]10[]10[]"))
        textFormatPanel.add(textFormatScrollPane, "grow, h 80!, wrap")
        textFormatPanel.add(textFormatHelpLabel, "grow, wrap")
        
        // 添加预设格式按钮
        val presetPanel = JPanel(FlowLayout(FlowLayout.LEFT, 5, 0))
        
        val csvButton = JButton("CSV").apply {
            addActionListener { 
                textFormatField.text = "{id},{name},{gender},{department},{major},{grade},{class},{datetime},{score_weighted},{score_simple},{gpa}"
            }
            toolTipText = "CSV逗号分隔"
        }
        
        val customButton = JButton("简洁").apply {
            addActionListener { 
                textFormatField.text = "学号:{id} 姓名:{name} 院系:{department} 专业:{major} 年级:{grade} 班级:{class} 导出时间:{datetime} 加权平均分:{score_weighted} 简单平均分:{score_simple} 绩点:{gpa}"
            }
            toolTipText = "简洁格式"
        }
        
        presetPanel.add(JLabel("预设: "))
        presetPanel.add(csvButton)
        presetPanel.add(customButton)
        
        textFormatPanel.add(presetPanel, "grow")
        
        // 准备Excel面板
        val excelPanel = JPanel(MigLayout("insets 0, fillx", "[grow][]", "[]"))
        excelPanel.add(excelPathField, "grow")
        excelPanel.add(excelBrowseButton)
        
        // 添加到卡片面板
        cardPanel.add(textFormatPanel, TEXT_PANEL)
        cardPanel.add(excelPanel, EXCEL_PANEL)
        
        // 设置布局
        contentPane.layout = MigLayout("insets 15, fillx", "[grow]", "[]10[]10[]10[]")
        
        // 添加组件
        contentPane.add(JLabel(I18nUtils.getText("student.export.dialog.message")), "wrap")
        
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
            fileChooser.fileFilter = javax.swing.filechooser.FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx")
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                var path = fileChooser.selectedFile.absolutePath
                if (!path.endsWith(".xlsx")) {
                    path += ".xlsx"
                }
                selectedFilePath = path
                excelPathField.text = path
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
        if (radioText.isSelected) {
            // 处理文本导出
            val format = textFormatField.text
            if (format.isBlank()) {
                JOptionPane.showMessageDialog(
                    this,
                    I18nUtils.getText("student.export.text.format.empty"),
                    I18nUtils.getText("dialog.error"),
                    JOptionPane.ERROR_MESSAGE
                )
                return
            }
            exportToText(format)
        } else {
            // 处理Excel导出
            if (selectedFilePath == null) {
                JOptionPane.showMessageDialog(
                    this,
                    I18nUtils.getText("student.export.excel.path.empty"),
                    I18nUtils.getText("dialog.error"),
                    JOptionPane.ERROR_MESSAGE
                )
                return
            }
            viewModel.exportStudentToExcel()
        }
        dispose()
    }
    
    /**
     * 导出为文本
     */
    private fun exportToText(format: String) {
        viewModel.exportStudentToText(format)
    }
    
    companion object {
        /**
         * 显示学生导出对话框
         */
        fun show(owner: Window? = null) {
            StudentExportDialog(owner).isVisible = true
        }
    }
} 