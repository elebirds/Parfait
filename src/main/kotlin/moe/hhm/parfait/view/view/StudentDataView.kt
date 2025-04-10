/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.view.view

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import moe.hhm.parfait.view.base.CoroutineComponent
import moe.hhm.parfait.view.base.DefaultCoroutineComponent
import moe.hhm.parfait.view.component.panel.StudentDataButtonPanel
import moe.hhm.parfait.view.component.panel.StudentDataPaginationPanel
import moe.hhm.parfait.view.component.table.StudentDataTable
import moe.hhm.parfait.viewmodel.StudentDataViewModel
import net.miginfocom.swing.MigLayout
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.border.EmptyBorder
import java.awt.Color
import java.awt.BorderLayout

class StudentDataView(parent: DefaultCoroutineComponent? = null) : JPanel(), KoinComponent, CoroutineComponent by DefaultCoroutineComponent(parent) {
    // 通过Koin获取ViewModel
    private val viewModel: StudentDataViewModel by inject()

    // 按钮组件
    private val buttonPanel = StudentDataButtonPanel()
    // 分页面板
    private val paginationPanel = StudentDataPaginationPanel()
    
    // 表格和滚动面板
    private val table: StudentDataTable = StudentDataTable()
    private val scrollPane = JScrollPane().apply {
        this.setViewportView(table)
    }
    
    // 数据库连接状态提示
    private val databaseStatusLabel = JLabel("请先连接数据库").apply {
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
    
    // 主内容面板 (包含表格和分页)
    private val contentPanel: JPanel = JPanel().apply {
        this.layout = MigLayout("insets 0, hidemode 3", "[grow,fill]", "[grow,fill][]")
        this.add(scrollPane, "cell 0 0,grow")
        this.add(paginationPanel, "cell 0 1,alignx center")
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
        this.add(contentWrapperPanel, "cell 0 0,grow")  // 内容面板(表格+分页)在左侧并自动增长
        this.add(buttonPanel, "cell 1 0,aligny top")  // 按钮面板在右侧顶部对齐

        this.observer()
    }

    override fun observer() {
        // 监听数据库连接状态变化
        scope.launch {
            viewModel.loadState.collectLatest {
                updateDatabaseConnectionStatus(it.isConnected())
            }
        }

        buttonPanel.observer() // 按钮模块监听
        paginationPanel.observer() // 分页模块监听
        table.observer() // 表格模块监听
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
}