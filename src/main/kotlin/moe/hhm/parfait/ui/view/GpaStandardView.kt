/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.view

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import moe.hhm.parfait.dto.GpaStandardDTO
import moe.hhm.parfait.infra.i18n.I18nUtils.createButton
import moe.hhm.parfait.infra.i18n.I18nUtils.createLabel
import moe.hhm.parfait.ui.base.CoroutineComponent
import moe.hhm.parfait.ui.base.DefaultCoroutineComponent
import moe.hhm.parfait.ui.component.button.GpaStandardButton
import moe.hhm.parfait.ui.component.dialog.GpaStandardDialog
import moe.hhm.parfait.ui.state.VMState
import moe.hhm.parfait.ui.viewmodel.GpaStandardViewModel
import net.miginfocom.swing.MigLayout
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.awt.BorderLayout
import java.awt.Color
import java.time.format.DateTimeFormatter
import javax.swing.*
import javax.swing.border.EmptyBorder

class GpaStandardView(parent: DefaultCoroutineComponent? = null) : JPanel(), KoinComponent,
    CoroutineComponent by DefaultCoroutineComponent(parent) {
    // 通过Koin获取ViewModel
    val viewModel: GpaStandardViewModel by inject()

    // 按钮组件
    private val buttonPanel = GpaStandardButtonPanel()

    // 标准按钮组和滚动面板
    private val standardButtonsPanel = JPanel().apply {
        this.layout = MigLayout("wrap 2, fillx, insets 5", "[fill,grow][fill,grow]", "[]")
    }
    private val standardButtonGroup = ButtonGroup()
    private val scrollPane = JScrollPane().apply {
        this.setViewportView(standardButtonsPanel)
    }

    // 数据库连接状态提示
    private val databaseStatusLabel = createLabel("database.connect.error.needConnect").apply {
        foreground = Color.RED
        horizontalAlignment = JLabel.CENTER
    }

    // 状态面板
    private val statusPanel = JPanel(BorderLayout()).apply {
        add(databaseStatusLabel, BorderLayout.CENTER)
        isVisible = false
        background = Color(255, 255, 224) // 浅黄色背景
        border = EmptyBorder(5, 5, 5, 5)
    }

    // 主内容面板 (包含标准按钮)
    private val contentPanel: JPanel = JPanel().apply {
        this.layout = MigLayout("insets 0, hidemode 3", "[grow,fill]", "[grow,fill]")
        this.add(scrollPane, "cell 0 0,grow")
    }

    // 主内容包装面板（包含状态提示和内容）
    private val contentWrapperPanel: JPanel = JPanel().apply {
        this.layout = MigLayout("insets 0, hidemode 3", "[grow,fill]", "[]0[grow,fill]")
        this.add(statusPanel, "cell 0 0,growx")
        this.add(contentPanel, "cell 0 1,grow")
    }

    init {
        // 设置布局
        this.layout = MigLayout(
            "hidemode 3",  // layout constraints
            "[grow,fill][fill]",  // column constraints - 左列自动增长填充，右列固定
            "[grow,fill]"  // row constraints - 行自动增长填充
        )
        this.add(contentWrapperPanel, "cell 0 0,grow")  // 内容面板(按钮组)在左侧并自动增长
        this.add(buttonPanel, "cell 1 0,aligny top")  // 按钮面板在右侧顶部对齐

        this.observer()
    }

    override fun observer() {
        // 监听数据库连接状态变化
        scope.launch {
            viewModel.vmState.collectLatest { state ->
                updateDatabaseConnectionStatus(state.isConnected())
            }
        }

        // 监听GPA标准数据变化
        scope.launch {
            viewModel.data.collectLatest { standards ->
                updateStandardButtons(standards)
            }
        }

        // 监听选中的GPA标准变化
        scope.launch {
            viewModel.selectedStandard.collectLatest { standard ->
                // 更新按钮选中状态
                updateSelectedStandard(standard)
            }
        }

        buttonPanel.observer() // 按钮模块监听
    }

    /**
     * 更新数据库连接状态
     */
    private fun updateDatabaseConnectionStatus(connected: Boolean) {
        if (connected) {
            statusPanel.isVisible = false
            databaseStatusLabel.text = ""
        } else {
            statusPanel.isVisible = true
            databaseStatusLabel.text = "请先连接数据库"
        }
    }

    /**
     * 更新GPA标准按钮
     */
    private fun updateStandardButtons(standards: List<GpaStandardDTO>) {
        standardButtonsPanel.removeAll()
        standardButtonGroup.clearSelection()

        // 对标准进行排序：默认最高，重要标准其次，其他最后
        val sortedStandards = standards.sortedWith(compareByDescending<GpaStandardDTO> {
            it.isDefault
        }.thenByDescending {
            it.isLike
        }.thenBy {
            it.name
        })

        sortedStandards.forEach { standard ->
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val createdAtStr = standard.createdAt?.format(dateFormatter) ?: ""

            val button = GpaStandardButton(
                title = standard.name,
                category = standard.category,
                description = standard.description,
                purpose = standard.purpose,
                type = standard.type,
                createTime = createdAtStr
            )

            button.addActionListener {
                if (button.isSelected) {
                    viewModel.selectStandard(standard)
                }
            }

            standardButtonGroup.add(button)
            standardButtonsPanel.add(button, "growx")
        }

        standardButtonsPanel.revalidate()
        standardButtonsPanel.repaint()
    }

    /**
     * 更新选中的GPA标准
     */
    private fun updateSelectedStandard(standard: GpaStandardDTO?) {
        // 遍历按钮组中的按钮，找到对应的标准进行选中
        for (i in 0 until standardButtonGroup.buttonCount) {
            val button = standardButtonGroup.elements.toList()[i]
            if (button is GpaStandardButton) {
                if (button.text == standard?.name) {
                    standardButtonGroup.setSelected(button.model, true)
                    break
                }
            }
        }
    }

    /**
     * GPA标准操作按钮面板
     */
    inner class GpaStandardButtonPanel : JPanel(), KoinComponent,
        CoroutineComponent by DefaultCoroutineComponent(this@GpaStandardView) {

        // 添加标准按钮
        private val buttonAdd = createButton("gpa.action.add").apply {
            addActionListener {
                // 打开添加GPA标准对话框
                val owner = SwingUtilities.getWindowAncestor(this@GpaStandardView)
                GpaStandardDialog.show(null, owner)
            }
        }

        // 删除标准按钮
        private val buttonDelete = createButton("gpa.action.delete").apply {
            addActionListener {
                val standard = viewModel.selectedStandard.value
                if (standard != null && !standard.isDefault) {
                    // 确认删除
                    val result = JOptionPane.showConfirmDialog(
                        SwingUtilities.getWindowAncestor(this@GpaStandardView),
                        "确定要删除标准 '${standard.name}' 吗？",
                        "确认删除",
                        JOptionPane.YES_NO_OPTION
                    )

                    if (result == JOptionPane.YES_OPTION) {
                        standard.uuid?.let { viewModel.deleteStandard(it) }
                    }
                }
            }
        }

        // 编辑标准按钮
        private val buttonEdit = createButton("gpa.action.edit").apply {
            addActionListener {
                val standard = viewModel.selectedStandard.value
                if (standard != null) {
                    // 打开编辑GPA标准对话框
                    val owner = SwingUtilities.getWindowAncestor(this@GpaStandardView)
                    GpaStandardDialog.show(standard, owner)
                }
            }
        }

        // 设为默认按钮
        private val buttonSetDefault = createButton("gpa.action.setDefault").apply {
            addActionListener {
                val standard = viewModel.selectedStandard.value
                if (standard != null && !standard.isDefault) {
                    // 确认设为默认
                    val result = JOptionPane.showConfirmDialog(
                        SwingUtilities.getWindowAncestor(this@GpaStandardView),
                        "确定要将标准 '${standard.name}' 设为默认吗？",
                        "确认设为默认",
                        JOptionPane.YES_NO_OPTION
                    )

                    if (result == JOptionPane.YES_OPTION) {
                        standard.uuid?.let { viewModel.setDefaultStandard(it) }
                    }
                }
            }
        }

        // 设为重要/取消重要按钮
        private val buttonToggleLike = createButton("gpa.action.toggleLike").apply {
            addActionListener {
                val standard = viewModel.selectedStandard.value
                if (standard != null) {
                    val action = if (standard.isLike) "取消重要" else "设为重要"
                    // 确认操作
                    val result = JOptionPane.showConfirmDialog(
                        SwingUtilities.getWindowAncestor(this@GpaStandardView),
                        "确定要将标准 '${standard.name}' ${action}吗？",
                        "确认操作",
                        JOptionPane.YES_NO_OPTION
                    )

                    if (result == JOptionPane.YES_OPTION) {
                        viewModel.toggleLikeStandard(standard)
                    }
                }
            }
        }

        init {
            this.layout = MigLayout("hidemode 3", "[fill]", "[][][][][]")
            this.add(buttonAdd, "cell 0 0")
            this.add(buttonDelete, "cell 0 1")
            this.add(buttonEdit, "cell 0 2")
            this.add(buttonSetDefault, "cell 0 3")
            this.add(buttonToggleLike, "cell 0 4")
        }

        override fun observer() {
            // 监听选中状态变化，更新按钮启用状态
            scope.launch {
                viewModel.selectedStandard.collectLatest { standard ->
                    // 更新按钮启用状态
                    updateButtonState(standard)
                }
            }

            // 监听加载状态
            scope.launch {
                viewModel.vmState.collectLatest { state ->
                    // 根据加载状态更新按钮启用状态
                    val isLoaded = state == VMState.DONE
                    buttonAdd.isEnabled = isLoaded
                    if (!isLoaded) {
                        buttonDelete.isEnabled = false
                        buttonEdit.isEnabled = false
                        buttonSetDefault.isEnabled = false
                        buttonToggleLike.isEnabled = false
                    }
                }
            }
        }

        private fun updateButtonState(standard: GpaStandardDTO?) {
            val hasSelection = standard != null
            val isDefault = standard?.isDefault == true

            // 删除按钮只有在选中非默认标准时可用
            buttonDelete.isEnabled = hasSelection && !isDefault
            // 编辑按钮在有选中项时可用
            buttonEdit.isEnabled = hasSelection
            // 设为默认按钮只有在选中非默认标准时可用
            buttonSetDefault.isEnabled = hasSelection && !isDefault
            // 设为重要/取消重要按钮在有选中项时可用
            buttonToggleLike.isEnabled = hasSelection
        }
    }
}