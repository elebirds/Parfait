/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.component.panel

import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import moe.hhm.parfait.dto.TermDTO
import moe.hhm.parfait.infra.i18n.I18nUtils
import moe.hhm.parfait.infra.i18n.I18nUtils.createButton
import moe.hhm.parfait.ui.base.CoroutineComponent
import moe.hhm.parfait.ui.base.DefaultCoroutineComponent
import moe.hhm.parfait.ui.state.VMState
import moe.hhm.parfait.ui.viewmodel.TermViewModel
import net.miginfocom.swing.MigLayout
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import javax.swing.*

/**
 * 术语按钮面板
 */
class TermButtonPanel(parent: CoroutineComponent? = null) : JPanel(), KoinComponent,
    CoroutineComponent by DefaultCoroutineComponent(parent) {

    // 通过Koin获取ViewModel
    private val viewModel: TermViewModel by inject()

    private val buttonAdd: JButton = createButton("term.action.add").apply {
        addActionListener {
            // 打开添加术语对话框
            showAddTermDialog()
        }
    }

    private val buttonDelete: JButton = createButton("term.action.delete").apply {
        addActionListener {
            val selectedTerms = viewModel.selectedTerms.value
            if (selectedTerms.isNotEmpty()) {
                // 显示确认对话框
                val owner = SwingUtilities.getWindowAncestor(this@TermButtonPanel)
                val message = I18nUtils.getFormattedText("term.dialog.delete.confirm", selectedTerms.size)

                val result = JOptionPane.showConfirmDialog(
                    owner,
                    message,
                    I18nUtils.getText("term.dialog.delete.title"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                )

                // 如果用户确认删除
                if (result == JOptionPane.YES_OPTION) {
                    // 批量删除选中的术语
                    viewModel.deleteTerms(selectedTerms.mapNotNull { it.uuid })
                }
            }
        }
    }

    init {
        this.layout = MigLayout("hidemode 3", "[fill]", "[][][][]")
        this.add(buttonAdd, "cell 0 0")
        this.add(buttonDelete, "cell 0 1")
    }

    /**
     * 显示添加术语对话框
     */
    private fun showAddTermDialog() {
        val owner = SwingUtilities.getWindowAncestor(this)
        
        // 创建面板
        val panel = JPanel(MigLayout("wrap 2", "[][grow,fill]"))
        
        // 添加字段输入
        panel.add(JLabel(I18nUtils.getText("term.dialog.add.field")))
        val fieldField = JTextField(20)
        panel.add(fieldField)
        
        // 添加上下文输入
        panel.add(JLabel(I18nUtils.getText("term.dialog.add.context")))
        val contextField = JTextField(20)
        panel.add(contextField)
        
        // 添加语言输入
        panel.add(JLabel(I18nUtils.getText("term.dialog.add.language")))
        val languageField = JTextField(5)
        panel.add(languageField)
        
        // 添加术语值输入
        panel.add(JLabel(I18nUtils.getText("term.dialog.add.term")))
        val termField = JTextField(20)
        panel.add(termField)

        // 显示对话框
        val result = JOptionPane.showConfirmDialog(
            owner,
            panel,
            I18nUtils.getText("term.dialog.add.title"),
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        )

        // 如果用户点击确定
        if (result == JOptionPane.OK_OPTION) {
            val field = fieldField.text.trim()
            val context = contextField.text.trim().ifBlank { null }
            val language = languageField.text.trim().ifBlank { null }
            val term = termField.text.trim()

            // 验证必填字段
            if (field.isBlank() || term.isBlank()) {
                JOptionPane.showMessageDialog(
                    owner,
                    I18nUtils.getText("term.dialog.add.error.requiredFields"),
                    I18nUtils.getText("term.dialog.error.title"),
                    JOptionPane.ERROR_MESSAGE
                )
                return
            }

            // 创建术语对象并添加
            val termDTO = TermDTO(
                field = field,
                context = context,
                language = language,
                term = term
            )
            viewModel.addTerm(termDTO)
        }
    }

    override fun observer() {
        // 订阅ViewModel的加载状态和选中术语
        scope.launch {
            combine(viewModel.vmState, viewModel.selectedTerms) { loadState, terms ->
                loadState to terms.isNotEmpty()
            }.collect { (loadState, hasSelection) ->
                // 更新按钮状态
                updateState(loadState, hasSelection)
            }
        }
    }

    /**
     * 更新按钮状态
     */
    private fun updateState(state: VMState, hasSelection: Boolean) {
        // 根据数据库连接状态和加载状态确定按钮启用状态
        val enabled = state == VMState.DONE

        // 设置按钮启用状态
        buttonAdd.isEnabled = enabled
        buttonDelete.isEnabled = enabled && hasSelection
    }
} 