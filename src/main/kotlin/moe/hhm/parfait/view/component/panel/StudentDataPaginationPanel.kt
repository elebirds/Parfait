/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.view.component.panel

import moe.hhm.parfait.utils.i18n.I18nUtils.createButton
import moe.hhm.parfait.utils.i18n.I18nUtils.createLabel
import moe.hhm.parfait.view.component.table.StudentDataTable
import moe.hhm.parfait.viewmodel.StudentDataUiState
import moe.hhm.parfait.viewmodel.StudentDataViewModel
import net.miginfocom.swing.MigLayout
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.border.EmptyBorder
import kotlin.getValue

class StudentDataPaginationPanel : JPanel(), KoinComponent {
    // 通过Koin获取ViewModel
    private val viewModel: StudentDataViewModel by inject()

    private val buttonPrevPage: JButton = createButton("page.previous").apply {
        addActionListener { viewModel.previousPage() }
    }
    private val buttonNextPage: JButton = createButton("page.next").apply {
        addActionListener { viewModel.nextPage() }
    }
    private val labelCurrentPage: JLabel = JLabel("1")
    private val labelTotalPages: JLabel = JLabel("/ 1")
    private val labelPageSize: JLabel = createLabel("page.eachPage")
    private val comboPageSize: JComboBox<Int> = JComboBox<Int>().apply {
        addItem(10)
        addItem(20)
        addItem(50)
        addItem(100)
        selectedItem = 20
        addActionListener {
            val size = selectedItem as Int
            viewModel.setPageSize(size)
        }
    }

    init {
        this.layout = MigLayout("insets 0", "[][][][][][]", "[]")
        this.add(labelPageSize, "cell 0 0")
        this.add(comboPageSize, "cell 1 0")
        this.add(buttonPrevPage, "cell 2 0")
        this.add(labelCurrentPage, "cell 3 0,alignx center")
        this.add(labelTotalPages, "cell 4 0")
        this.add(buttonNextPage, "cell 5 0")
        this.border = EmptyBorder(5, 0, 5, 0)
    }

    fun updateState(state: StudentDataUiState, table: StudentDataTable) {
        // 根据数据库连接状态和加载状态确定按钮启用状态
        val dbConnected = state.databaseConnected
        val enabled = dbConnected && !state.isLoading
        val hasSelection = table.getSelectedStudent() != null

        // 设置分页按钮状态
        buttonPrevPage.isEnabled = enabled && state.currentPage > 1
        buttonNextPage.isEnabled = enabled && state.currentPage < state.totalPages
        comboPageSize.isEnabled = enabled

        // 更新当前页码和总页码
        labelCurrentPage.text = state.currentPage.toString()
        labelTotalPages.text = "/ ${state.totalPages}"
    }
}