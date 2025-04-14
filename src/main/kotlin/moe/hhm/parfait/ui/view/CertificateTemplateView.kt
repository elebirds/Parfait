/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.view

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import moe.hhm.parfait.dto.CertificateTemplateDTO
import moe.hhm.parfait.infra.i18n.I18nUtils
import moe.hhm.parfait.infra.i18n.I18nUtils.createButton
import moe.hhm.parfait.infra.i18n.I18nUtils.createLabel
import moe.hhm.parfait.ui.base.CoroutineComponent
import moe.hhm.parfait.ui.base.DefaultCoroutineComponent
import moe.hhm.parfait.ui.component.button.CertificateTemplateButton
import moe.hhm.parfait.ui.component.dialog.CertificateRecordsDialog
import moe.hhm.parfait.ui.component.dialog.CertificateTemplateDialog
import moe.hhm.parfait.ui.state.VMState
import moe.hhm.parfait.ui.viewmodel.CertificateTemplateViewModel
import net.miginfocom.swing.MigLayout
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.awt.BorderLayout
import java.awt.Color
import javax.swing.*
import javax.swing.border.EmptyBorder

class CertificateTemplateView(parent: DefaultCoroutineComponent? = null) : JPanel(), KoinComponent,
    CoroutineComponent by DefaultCoroutineComponent(parent) {

    // 通过Koin获取ViewModel
    private val viewModel: CertificateTemplateViewModel by inject()

    // 按钮组件
    private val buttonPanel = TemplateButtonPanel()

    // 模板按钮组和滚动面板
    private val templatesButtonsPanel = JPanel().apply {
        this.layout = MigLayout("wrap 2, fillx, insets 5", "[fill,grow 50][fill,grow 50]", "[]")
    }
    private val templateButtonGroup = ButtonGroup()
    private val scrollPane = JScrollPane().apply {
        this.setViewportView(templatesButtonsPanel)
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

    // 主内容面板 (包含模板按钮)
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

        // 监听证明模板数据变化
        scope.launch {
            viewModel.data.collectLatest { templates ->
                updateTemplateButtons(templates)
            }
        }

        // 监听选中的证明模板变化
        scope.launch {
            viewModel.selectedTemplate.collectLatest { template ->
                // 更新按钮选中状态
                updateSelectedTemplate(template)
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
     * 更新证明模板按钮
     */
    private fun updateTemplateButtons(templates: List<CertificateTemplateDTO>) {
        templatesButtonsPanel.removeAll()
        templateButtonGroup.clearSelection()

        // 对模板进行排序：活跃最高，重要模板其次，其他最后
        val sortedTemplates = templates.sortedWith(compareByDescending<CertificateTemplateDTO> {
            it.isActive
        }.thenByDescending {
            it.isLike
        }.thenBy {
            it.priority
        }.thenBy {
            it.name
        })

        sortedTemplates.forEach { template ->
            val button = CertificateTemplateButton(
                title = template.name,
                category = template.category,
                description = template.description,
                isLike = template.isLike,
                isActive = template.isActive
            )

            button.addActionListener {
                if (button.isSelected) {
                    viewModel.selectTemplate(template)
                }
            }

            templateButtonGroup.add(button)
            templatesButtonsPanel.add(button, "growx")
        }

        templatesButtonsPanel.revalidate()
        templatesButtonsPanel.repaint()
    }

    /**
     * 更新选中的证明模板
     */
    private fun updateSelectedTemplate(template: CertificateTemplateDTO?) {
        // 遍历按钮组中的按钮，找到对应的模板进行选中
        for (i in 0 until templateButtonGroup.buttonCount) {
            val button = templateButtonGroup.elements.toList()[i]
            if (button is CertificateTemplateButton) {
                if (button.text == template?.name) {
                    templateButtonGroup.setSelected(button.model, true)
                    break
                }
            }
        }
    }

    /**
     * 证明模板操作按钮面板
     */
    inner class TemplateButtonPanel : JPanel(), KoinComponent,
        CoroutineComponent by DefaultCoroutineComponent(this@CertificateTemplateView) {

        // 添加模板按钮
        private val buttonAdd = createButton("certificate.action.add").apply {
            addActionListener {
                CertificateTemplateDialog.show(null, SwingUtilities.getWindowAncestor(this@CertificateTemplateView))
            }
        }

        // 删除模板按钮
        private val buttonDelete = createButton("certificate.action.delete").apply {
            addActionListener {
                val template = viewModel.selectedTemplate.value
                if (template == null) {
                    JOptionPane.showMessageDialog(
                        SwingUtilities.getWindowAncestor(this@CertificateTemplateView),
                        I18nUtils.getText("certificate.error.select.template"),
                        I18nUtils.getText("error.business.title"),
                        JOptionPane.WARNING_MESSAGE
                    )
                    return@addActionListener
                }
                // 确认删除
                val result = JOptionPane.showConfirmDialog(
                    SwingUtilities.getWindowAncestor(this@CertificateTemplateView),
                    I18nUtils.getFormattedText("certificate.dialog.delete.confirm", template.name),
                    I18nUtils.getText("term.dialog.delete.title"),
                    JOptionPane.YES_NO_OPTION
                )
                if (result != JOptionPane.YES_OPTION) return@addActionListener
                template.uuid?.let { viewModel.deleteTemplate(it) }
            }
        }

        // 编辑模板按钮
        private val buttonEdit = createButton("certificate.action.edit").apply {
            addActionListener {
                val template = viewModel.selectedTemplate.value
                if (template == null) {
                    JOptionPane.showMessageDialog(
                        SwingUtilities.getWindowAncestor(this@CertificateTemplateView),
                        I18nUtils.getText("certificate.error.select.template"),
                        I18nUtils.getText("error.business.title"),
                        JOptionPane.WARNING_MESSAGE
                    )
                    return@addActionListener
                }
                CertificateTemplateDialog.show(template, SwingUtilities.getWindowAncestor(this@CertificateTemplateView))
            }
        }

        // 切换收藏状态按钮
        private val buttonToggleLike = createButton("certificate.action.toggleLike").apply {
            addActionListener {
                val template = viewModel.selectedTemplate.value
                if (template != null) {
                    viewModel.toggleLikeTemplate(template)
                } else {
                    JOptionPane.showMessageDialog(
                        SwingUtilities.getWindowAncestor(this@CertificateTemplateView),
                        I18nUtils.getText("certificate.error.select.template"),
                        I18nUtils.getText("error.business.title"),
                        JOptionPane.WARNING_MESSAGE
                    )
                }
            }
        }

        // 切换活跃状态按钮
        private val buttonToggleActive = createButton("certificate.action.toggleActive").apply {
            addActionListener {
                val template = viewModel.selectedTemplate.value
                if (template != null) {
                    viewModel.toggleActiveTemplate(template)
                } else {
                    JOptionPane.showMessageDialog(
                        SwingUtilities.getWindowAncestor(this@CertificateTemplateView),
                        I18nUtils.getText("certificate.error.select.template"),
                        I18nUtils.getText("error.business.title"),
                        JOptionPane.WARNING_MESSAGE
                    )
                }
            }
        }

        // 查看导出记录按钮
        private val buttonViewRecords = createButton("certificate.action.viewRecords").apply {
            addActionListener {
                val template = viewModel.selectedTemplate.value
                if (template != null && template.uuid != null) {
                    // 查看该模板的导出记录
                    val owner = SwingUtilities.getWindowAncestor(this@CertificateTemplateView)
                    CertificateRecordsDialog.show(template.uuid!!, template.name, owner)
                } else {
                    JOptionPane.showMessageDialog(
                        SwingUtilities.getWindowAncestor(this@CertificateTemplateView),
                        I18nUtils.getText("certificate.error.select.template"),
                        I18nUtils.getText("error.business.title"),
                        JOptionPane.WARNING_MESSAGE
                    )
                }
            }
        }

        init {
            // 设置布局
            layout = MigLayout(
                "wrap 1, fillx, insets 10, gapy 5",  // 布局约束 - 每行一个元素，水平填充，内边距10，垂直间隙5
                "[fill]",  // 列约束 - 填充可用空间
                "[]"  // 行约束 - 默认行高
            )

            // 添加按钮到面板
            add(buttonAdd, "growx")
            add(buttonEdit, "growx")
            add(buttonDelete, "growx")
            add(buttonToggleLike, "growx")
            add(buttonToggleActive, "growx")
            add(buttonViewRecords, "growx")
        }

        override fun observer() {
            // 监听选中的证明模板，启用/禁用相关按钮
            // 监听选中状态变化，更新按钮启用状态
            scope.launch {
                viewModel.selectedTemplate.collectLatest { standard ->
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
                        buttonToggleActive.isEnabled = false
                        buttonToggleLike.isEnabled = false
                        buttonViewRecords.isEnabled = false
                    }
                }
            }
        }

        private fun updateButtonState(standard: CertificateTemplateDTO?) {
            val hasSelection = standard != null
            buttonDelete.isEnabled = hasSelection
            buttonEdit.isEnabled = hasSelection
            buttonToggleActive.isEnabled = hasSelection
            buttonToggleLike.isEnabled = hasSelection
            buttonViewRecords.isEnabled = hasSelection
        }
    }
}