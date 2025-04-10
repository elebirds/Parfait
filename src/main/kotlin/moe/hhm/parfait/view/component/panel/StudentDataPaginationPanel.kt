/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.view.component.panel

import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import moe.hhm.parfait.utils.i18n.I18nUtils.createButton
import moe.hhm.parfait.utils.i18n.I18nUtils.createLabel
import moe.hhm.parfait.view.base.CoroutineComponent
import moe.hhm.parfait.view.base.DefaultCoroutineComponent
import moe.hhm.parfait.view.state.StudentDataLoadState
import moe.hhm.parfait.view.state.StudentDataPaginationState
import moe.hhm.parfait.viewmodel.StudentDataViewModel
import net.miginfocom.swing.MigLayout
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.border.EmptyBorder

class StudentDataPaginationPanel : JPanel(), KoinComponent, CoroutineComponent by DefaultCoroutineComponent() {
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

    override fun observer() {
        scope.launch {
            combine(viewModel.loadState, viewModel.paginationState) { loadState, paginationState ->
                Pair(loadState, paginationState)
            }.collect { (loadState, paginationState) ->
                // 更新按钮状态
                updateState(loadState, paginationState)
            }
        }
    }


    fun updateState(loadState: StudentDataLoadState, paginationState: StudentDataPaginationState) {
        val enabled = loadState == StudentDataLoadState.DONE

        // 设置分页按钮状态
        buttonPrevPage.isEnabled = enabled && paginationState.currentPage > 1
        buttonNextPage.isEnabled = enabled && paginationState.currentPage < paginationState.totalPages
        comboPageSize.isEnabled = enabled

        // 更新当前页码和总页码
        labelCurrentPage.text = paginationState.currentPage.toString()
        labelTotalPages.text = "/ ${paginationState.totalPages}"
    }
}