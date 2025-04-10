/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.view.view

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import moe.hhm.parfait.utils.i18n.I18nUtils.createButton
import moe.hhm.parfait.utils.i18n.I18nUtils.createLabel
import moe.hhm.parfait.view.component.panel.StudentDataButtonPanel
import moe.hhm.parfait.view.component.panel.StudentDataPaginationPanel
import moe.hhm.parfait.view.component.table.StudentDataTable
import moe.hhm.parfait.viewmodel.StudentDataViewModel
import net.miginfocom.swing.MigLayout
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.border.EmptyBorder
import java.awt.Color
import java.awt.BorderLayout

class StudentDataView : JPanel(), KoinComponent {
    // 通过Koin获取ViewModel
    private val viewModel: StudentDataViewModel by inject()

    // 视图层协程作用域
    private val viewScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
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
        
        // 设置事件监听器
        setupEventListeners()
        
        // 订阅ViewModel状态更新
        observeViewModel()
    }
    
    /**
     * 设置事件监听器
     */
    private fun setupEventListeners() {
        // 表格行选择监听器
        table.setRowSelectionListener { selectedStudent ->
            viewModel.setSelectedStudent(selectedStudent)

            buttonPanel.updateState(viewModel.uiState.value, table)
            paginationPanel.updateState(viewModel.uiState.value, table)
        }
    }
    
    /**
     * 订阅ViewModel状态更新
     */
    private fun observeViewModel() {
        viewScope.launch {
            viewModel.uiState.collectLatest { state ->
                // 更新数据库连接状态
                updateDatabaseConnectionStatus(state.databaseConnected)
                
                // 更新表格数据
                table.updateData(state.students)
                
                // 恢复选中状态
                if (state.selectedStudent != null) {
                    table.selectStudent(state.selectedStudent)
                }
                
                // 更新状态
                buttonPanel.updateState(state, table)
                paginationPanel.updateState(state, table)
            }
        }
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
     * 获取表格组件
     */
    fun getTable(): StudentDataTable {
        return table
    }
}