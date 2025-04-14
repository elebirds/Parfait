/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.component.dialog

import kotlinx.coroutines.launch
import moe.hhm.parfait.infra.i18n.I18nUtils
import moe.hhm.parfait.infra.i18n.I18nUtils.createButton
import moe.hhm.parfait.infra.i18n.I18nUtils.createLabel
import moe.hhm.parfait.ui.base.CoroutineComponent
import moe.hhm.parfait.ui.base.DefaultCoroutineComponent
import moe.hhm.parfait.ui.viewmodel.TermViewModel
import net.miginfocom.swing.MigLayout
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.awt.Window
import javax.swing.JDialog
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JTextField

/**
 * 术语搜索筛选对话框
 * @param owner 父窗口
 */
class TermSearchFilterDialog(
    owner: Window? = null
) : JDialog(owner), KoinComponent, CoroutineComponent by DefaultCoroutineComponent() {
    private val viewModel: TermViewModel by inject()

    // 表单组件
    private val textField = JTextField().apply { columns = 10 }
    private val textContext = JTextField().apply { columns = 10 }
    private val textLanguage = JTextField().apply { columns = 10 }
    private val textTerm = JTextField().apply { columns = 10 }

    // 按钮
    private val btnSubmit = createButton("button.ok")
    private val btnCancel = createButton("button.cancel")

    init {
        // 设置对话框属性
        title = I18nUtils.getText("term.search.title")
        isModal = true

        // 创建布局
        contentPane = createContent()
        defaultCloseOperation = DISPOSE_ON_CLOSE
        rootPane.defaultButton = btnSubmit

        // 添加事件监听
        btnSubmit.addActionListener { submitForm() }
        btnCancel.addActionListener { dispose() }
    }

    /**
     * 创建内容面板
     */
    private fun createContent(): JPanel {
        val panel = JPanel().apply {
            layout = MigLayout(
                "fillx, wrap 2",
                "[][grow, fill]",
                "[]10[]10[]10[]15[]"
            )

            // 字段名
            add(createLabel("term.search.field"), "align label")
            add(textField, "growx")

            // 上下文
            add(createLabel("term.search.context"), "align label")
            add(textContext, "growx")

            // 语言
            add(createLabel("term.search.language"), "align label")
            add(textLanguage, "growx")

            // 术语值
            add(createLabel("term.search.term"), "align label")
            add(textTerm, "growx")

            // 按钮面板
            val buttonPanel = JPanel().apply {
                add(btnSubmit)
                add(btnCancel)
            }
            add(buttonPanel, "span, align center")
        }

        return panel
    }

    /**
     * 提交表单
     */
    private fun submitForm() {
        // 获取表单数据
        val criteria = TermSearchFilterCriteria(
            field = textField.text.trim(),
            context = textContext.text.trim(),
            language = textLanguage.text.trim(),
            term = textTerm.text.trim()
        )

        // 提交到ViewModel
        scope.launch {
            try {
                viewModel.search(criteria)
                dispose()
            } catch (e: Exception) {
                JOptionPane.showMessageDialog(
                    this@TermSearchFilterDialog,
                    e.message,
                    I18nUtils.getText("error.generic.title"),
                    JOptionPane.ERROR_MESSAGE
                )
            }
        }
    }

    /**
     * 显示对话框
     */
    fun showDialog() {
        // 初始化界面
        pack()
        setLocationRelativeTo(owner)
        isVisible = true
    }

    companion object {
        // 静态方法便于在其他地方调用
        fun showSearch(owner: Window? = null) {
            val dialog = TermSearchFilterDialog(owner)
            dialog.showDialog()
        }
    }
} 