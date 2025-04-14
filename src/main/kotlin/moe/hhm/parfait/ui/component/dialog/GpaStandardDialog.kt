/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.component.dialog

import com.formdev.flatlaf.FlatClientProperties
import kotlinx.coroutines.launch
import moe.hhm.parfait.dto.GpaMappingDTO
import moe.hhm.parfait.dto.GpaMappingValidState
import moe.hhm.parfait.dto.GpaStandardDTO
import moe.hhm.parfait.dto.SingleGpaMapping
import moe.hhm.parfait.exception.BusinessException
import moe.hhm.parfait.infra.i18n.I18nUtils
import moe.hhm.parfait.infra.i18n.I18nUtils.bindText
import moe.hhm.parfait.infra.i18n.I18nUtils.createButton
import moe.hhm.parfait.infra.i18n.I18nUtils.createLabel
import moe.hhm.parfait.ui.base.CoroutineComponent
import moe.hhm.parfait.ui.base.DefaultCoroutineComponent
import moe.hhm.parfait.ui.component.table.GpaMappingTable
import moe.hhm.parfait.ui.viewmodel.GpaStandardViewModel
import net.miginfocom.swing.MigLayout
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.awt.Dimension
import java.awt.Window
import java.time.LocalDateTime
import javax.swing.*

/**
 * GPA标准添加与修改对话框
 */
class GpaStandardDialog(
    private val existingStandard: GpaStandardDTO? = null,
    owner: Window? = null,
    parent: CoroutineComponent? = null
) : JDialog(owner),
    KoinComponent, CoroutineComponent by DefaultCoroutineComponent(parent) {

    // 通过Koin获取ViewModel
    private val viewModel: GpaStandardViewModel by inject()
    private val isAlreadyExists = existingStandard != null
    private val existsUUID = existingStandard?.uuid

    // 表单组件 - 基础信息
    private val textName = JTextField()
    private val textCategory = JTextField()
    private val textAreaDescription = JTextArea(3, 20).apply {
        lineWrap = true
        wrapStyleWord = true
    }
    private val textPurpose = JTextField()

    // 绩点映射表格
    private val mappingTable = GpaMappingTable()
    private val scrollPane = JScrollPane(mappingTable)

    // 映射表操作按钮
    private val addRowButton = createButton("gpa.action.mapping.add")
    private val removeRowButton = createButton("gpa.action.mapping.delete").apply {
        isEnabled = false
    }
    private val moveUpButton = createButton("gpa.action.mapping.up").apply {
        isEnabled = false
    }
    private val moveDownButton = createButton("gpa.action.mapping.down").apply {
        isEnabled = false
    }

    // 对话框按钮
    private val buttonSubmit = object : JButton() {
        override fun isDefaultButton(): Boolean = true
    }.apply {
        bindText(this, "button.ok")
        addActionListener { submitForm() }
    }
    private val buttonCancel = JButton().apply {
        bindText(this, "button.cancel")
        addActionListener { dispose() }
    }

    init {
        initDialog()
        initComponents()
        setupListeners()
        setupButtonActions()

        // 如果存在GPA标准，则设置信息
        if (existingStandard != null) {
            textName.text = existingStandard.name
            textCategory.text = existingStandard.category
            textAreaDescription.text = existingStandard.description
            textPurpose.text = existingStandard.purpose

            // 设置映射表数据
            mappingTable.setMappingData(existingStandard.mapping.data)
        } else {
            // 新建时添加默认的映射数据
            val defaultMappings = listOf(
                SingleGpaMapping("A", 90..100, 4.0),
                SingleGpaMapping("B+", 85..89, 3.5),
                SingleGpaMapping("B", 80..84, 3.0),
                SingleGpaMapping("C+", 75..79, 2.5),
                SingleGpaMapping("C", 70..74, 2.0),
                SingleGpaMapping("D+", 65..69, 1.5),
                SingleGpaMapping("D", 60..64, 1.0),
                SingleGpaMapping("F", 0..59, 0.0)
            )
            mappingTable.setMappingData(defaultMappings)
        }
    }

    private fun setupButtonActions() {
        addRowButton.addActionListener { mappingTable.addRow() }
        removeRowButton.addActionListener { mappingTable.removeSelectedRow() }
        moveUpButton.addActionListener { mappingTable.moveSelectedRowUp() }
        moveDownButton.addActionListener { mappingTable.moveSelectedRowDown() }
    }

    private fun initDialog() {
        // 设置对话框基本属性
        title = I18nUtils.getText(if (isAlreadyExists) "gpa.dialog.modify.title" else "gpa.dialog.add.title")
        isModal = true
        defaultCloseOperation = DISPOSE_ON_CLOSE

        // 禁止调整窗口大小
        isResizable = false

        // 固定大小
        preferredSize = Dimension(800, 800)
        minimumSize = Dimension(800, 800)
        maximumSize = Dimension(800, 800)

        // 使用单一布局面板
        contentPane = JPanel(MigLayout("wrap 2, fillx, insets n 35 n 35", "[fill, 220][fill]"))
    }

    private fun initComponents() {
        // 设置输入框提示文本
        textName.putClientProperty(
            FlatClientProperties.PLACEHOLDER_TEXT,
            I18nUtils.getText("gpa.dialog.placeholder.name")
        )
        textCategory.putClientProperty(
            FlatClientProperties.PLACEHOLDER_TEXT,
            I18nUtils.getText("gpa.dialog.placeholder.category")
        )
        textAreaDescription.putClientProperty(
            FlatClientProperties.PLACEHOLDER_TEXT,
            I18nUtils.getText("gpa.dialog.placeholder.description")
        )
        textPurpose.putClientProperty(
            FlatClientProperties.PLACEHOLDER_TEXT,
            I18nUtils.getText("gpa.dialog.placeholder.purpose")
        )

        add(JLabel(I18nUtils.getText(if (isAlreadyExists) "gpa.dialog.modify.title" else "gpa.dialog.add.title")).apply {
            putClientProperty(FlatClientProperties.STYLE, "font:+6")
        }, "span 2")

        // 基础信息组标题
        val basicInfoLabel = createLabel("gpa.dialog.info.basic")
        basicInfoLabel.putClientProperty(FlatClientProperties.STYLE, "font:bold +2;")
        add(basicInfoLabel, "gapy 10 10, span 2")

        // 名称和分类（同一行）
        add(createLabel("gpa.property.name"))
        add(createLabel("gpa.property.category"))
        add(textName)
        add(textCategory)

        // 描述（单独一行）
        add(createLabel("gpa.property.description"), "span 2")
        add(JScrollPane(textAreaDescription), "span 2, gapy n 5, height 80:80:80")

        // 用途（单独一行）
        add(createLabel("gpa.property.purpose"), "span 2")
        add(textPurpose, "span 2, gapy n 5")

        // 绩点映射组标题
        val mappingLabel = createLabel("gpa.dialog.info.mapping")
        mappingLabel.putClientProperty(FlatClientProperties.STYLE, "font:bold +2;")
        add(mappingLabel, "gapy 15 10, span 2")

        // 绩点映射表格
        add(scrollPane, "span 2, grow, height 200:200:200")

        // 表格按钮面板
        val buttonPanel = JPanel(MigLayout("insets 0", "[grow][grow][grow][grow]", "[]"))
        buttonPanel.add(addRowButton, "grow")
        buttonPanel.add(removeRowButton, "grow")
        buttonPanel.add(moveUpButton, "grow")
        buttonPanel.add(moveDownButton, "grow")
        add(buttonPanel, "span 2, gapy 5 10")

        // 添加提示信息
        val tipLabel = createLabel("gpa.dialog.tip")
        tipLabel.putClientProperty(
            FlatClientProperties.STYLE, "" +
                    "border:8,8,8,8;" +
                    "arc:10;" +
                    "background:fade(#1a7aad,10%);"
        )
        add(tipLabel, "gapy 15 10, span 2")

        // 对话框按钮
        add(buttonCancel, "gapy 10, grow 0")
        add(buttonSubmit, "grow 0, al trailing")
    }

    private fun setupListeners() {
        // 表格选择监听器
        mappingTable.selectionModel.addListSelectionListener {
            updateButtonState()
        }
    }

    private fun updateButtonState() {
        removeRowButton.isEnabled = mappingTable.hasSelection()
        moveUpButton.isEnabled = mappingTable.canMoveUp()
        moveDownButton.isEnabled = mappingTable.canMoveDown()
    }

    private fun submitForm() {
        // 获取表单数据
        val name = textName.text
        val category = textCategory.text
        val description = textAreaDescription.text
        val purpose = textPurpose.text
        val mappingData = mappingTable.getMappingData()

        // 验证表单数据
        if (name.isEmpty() || category.isEmpty() || description.isEmpty() || purpose.isEmpty())
            throw BusinessException("gpa.dialog.validation.required")

        if (mappingData.isEmpty()) throw BusinessException("gpa.dialog.validation.mapping.empty")
        // 验证映射表数据有效性
        val mapping = GpaMappingDTO(mappingData)
        val vCode = mapping.validCode()
        when (vCode) {
            GpaMappingValidState.VALID -> {
                // 映射表数据有效
            }

            GpaMappingValidState.OVERLAP -> throw BusinessException("gpa.dialog.validation.mapping.overlap")
            GpaMappingValidState.UNCOVERED -> throw BusinessException("gpa.dialog.validation.mapping.uncovered")
        }

        // 创建GPA标准DTO
        val gpaStandard = GpaStandardDTO(
            uuid = existsUUID,
            name = name,
            description = description,
            category = category,
            purpose = purpose,
            isDefault = existingStandard?.isDefault == true,
            isLike = existingStandard?.isLike == true,
            mapping = mapping,
            createdAt = existingStandard?.createdAt ?: LocalDateTime.now()
        )

        // 提交到ViewModel
        scope.launch {
            try {
                if (isAlreadyExists) {
                    viewModel.updateStandard(gpaStandard)
                } else {
                    viewModel.addStandard(gpaStandard)
                }
                dispose()
            } catch (e: BusinessException) {
                JOptionPane.showMessageDialog(
                    this@GpaStandardDialog,
                    e.message,
                    I18nUtils.getText("error.business.title"),
                    JOptionPane.ERROR_MESSAGE
                )
            } catch (e: Exception) {
                JOptionPane.showMessageDialog(
                    this@GpaStandardDialog,
                    e.message,
                    I18nUtils.getText("error.generic.title"),
                    JOptionPane.ERROR_MESSAGE
                )
            }
        }
    }

    companion object {
        /**
         * 显示添加或修改GPA标准对话框
         * @param existingStandard 现有的GPA标准（传null表示添加新标准）
         * @param owner 父窗口
         */
        fun show(existingStandard: GpaStandardDTO? = null, owner: Window? = null) {
            val dialog = GpaStandardDialog(existingStandard, owner)
            dialog.pack()
            dialog.setLocationRelativeTo(owner)
            dialog.isVisible = true
        }
    }
} 