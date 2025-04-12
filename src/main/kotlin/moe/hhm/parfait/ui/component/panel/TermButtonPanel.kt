/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.component.panel

import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import moe.hhm.parfait.dto.CertificateTermDTO
import moe.hhm.parfait.infra.i18n.I18nUtils.createButton
import moe.hhm.parfait.ui.base.CoroutineComponent
import moe.hhm.parfait.ui.base.DefaultCoroutineComponent
import moe.hhm.parfait.ui.state.TermLoadState
import moe.hhm.parfait.ui.viewmodel.TermViewModel
import net.miginfocom.swing.MigLayout
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import javax.swing.JButton
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.SwingUtilities

/**
 * 术语按钮面板
 */
class TermButtonPanel(parent: CoroutineComponent? = null) : JPanel(), KoinComponent,
    CoroutineComponent by DefaultCoroutineComponent(parent) {
    
    // 通过Koin获取ViewModel
    private val viewModel: TermViewModel by inject()

    private val buttonAdd: JButton = createButton("term.action.add").apply {
        text = "添加术语" // 临时文本，应通过国际化配置
        addActionListener {
            // 打开添加术语对话框
            val owner = SwingUtilities.getWindowAncestor(this@TermButtonPanel)
            val key = JOptionPane.showInputDialog(owner, "请输入术语键", "添加术语", JOptionPane.PLAIN_MESSAGE)
            if (key != null && key.isNotBlank()) {
                val term = JOptionPane.showInputDialog(owner, "请输入术语值", "添加术语", JOptionPane.PLAIN_MESSAGE)
                if (term != null && term.isNotBlank()) {
                    viewModel.addTerm(CertificateTermDTO(key = key, term = term))
                }
            }
        }
    }

    private val buttonDelete: JButton = createButton("term.action.delete").apply {
        text = "删除术语" // 临时文本，应通过国际化配置
        addActionListener {
            val selectedTerms = viewModel.selectedTerms.value
            if (selectedTerms.isNotEmpty()) {
                // 显示确认对话框
                val owner = SwingUtilities.getWindowAncestor(this@TermButtonPanel)
                val message = "确定要删除所选中的 ${selectedTerms.size} 个术语吗？"
                
                val result = JOptionPane.showConfirmDialog(
                    owner,
                    message,
                    "删除确认",
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

    private val buttonExport: JButton = createButton("term.action.export").apply {
        text = "导出术语" // 临时文本，应通过国际化配置
        addActionListener {
            // TODO: 实现导出功能
            JOptionPane.showMessageDialog(
                SwingUtilities.getWindowAncestor(this@TermButtonPanel),
                "导出功能尚未实现",
                "提示",
                JOptionPane.INFORMATION_MESSAGE
            )
        }
    }

    private val buttonImport: JButton = createButton("term.action.import").apply {
        text = "导入术语" // 临时文本，应通过国际化配置
        addActionListener {
            // TODO: 实现导入功能
            JOptionPane.showMessageDialog(
                SwingUtilities.getWindowAncestor(this@TermButtonPanel),
                "导入功能尚未实现",
                "提示",
                JOptionPane.INFORMATION_MESSAGE
            )
        }
    }

    init {
        this.layout = MigLayout("hidemode 3", "[fill]", "[][][][][][]")
        this.add(buttonAdd, "cell 0 0")
        this.add(buttonDelete, "cell 0 1")
        this.add(buttonExport, "cell 0 2")
        this.add(buttonImport, "cell 0 3")
    }

    override fun observer() {
        // 订阅ViewModel的加载状态和选中术语
        scope.launch {
            combine(viewModel.loadState, viewModel.selectedTerms) { loadState, terms ->
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
    private fun updateState(state: TermLoadState, hasSelection: Boolean) {
        // 根据数据库连接状态和加载状态确定按钮启用状态
        val enabled = state == TermLoadState.DONE

        // 设置按钮启用状态
        buttonAdd.isEnabled = enabled
        buttonDelete.isEnabled = enabled && hasSelection
        buttonExport.isEnabled = enabled
        buttonImport.isEnabled = enabled
    }
} 