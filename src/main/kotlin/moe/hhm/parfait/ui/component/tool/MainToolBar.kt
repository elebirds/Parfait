/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.component.tool

import com.formdev.flatlaf.extras.FlatSVGIcon
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import moe.hhm.parfait.infra.i18n.I18nUtils.bindToolTipText
import moe.hhm.parfait.ui.base.CoroutineComponent
import moe.hhm.parfait.ui.base.DefaultCoroutineComponent
import moe.hhm.parfait.ui.component.dialog.AdvancedFilterDialog
import moe.hhm.parfait.ui.component.dialog.SearchFilterDialog
import moe.hhm.parfait.ui.state.FilterState
import moe.hhm.parfait.ui.viewmodel.StudentDataViewModel
import moe.hhm.parfait.ui.viewmodel.TermViewModel
import org.koin.core.component.KoinComponent
import javax.swing.JButton
import javax.swing.JToolBar
import javax.swing.SwingUtilities
import moe.hhm.parfait.ui.viewmodel.CertificateTemplateViewModel
import moe.hhm.parfait.ui.viewmodel.GpaStandardViewModel

class MainToolBar(
    private val studentViewModel: StudentDataViewModel,
    private val gpaViewModel: GpaStandardViewModel,
    private val certificateViewModel: CertificateTemplateViewModel,
    private val termViewModel: TermViewModel
) : JToolBar(), KoinComponent, CoroutineComponent by DefaultCoroutineComponent() {
    private var currentIndex: Int = 0

    private val searchButton = JButton(FlatSVGIcon("ui/nwicons/search.svg")).apply {
        bindToolTipText(this, "toolbar.search")
        addActionListener {
            SwingUtilities.getWindowAncestor(this)?.let { window ->
                SearchFilterDialog.showSearch(window)
            }
        }
    }

    private val filterButton = JButton(FlatSVGIcon("ui/nwicons/filter.svg")).apply {
        bindToolTipText(this, "toolbar.filter")
        addActionListener {
            SwingUtilities.getWindowAncestor(this)?.let { window ->
                AdvancedFilterDialog.show(window)
            }
        }
    }

    private val cleanFilterButton = JButton(FlatSVGIcon("ui/nwicons/filter-cancel-16.svg")).apply {
        bindToolTipText(this, "toolbar.clearFilter")
        isEnabled = false // 初始状态下禁用
        addActionListener {
            when(currentIndex) {
                0 -> studentViewModel.clearFilter()
            }
        }
    }

    private val refreshButton = JButton(FlatSVGIcon("ui/nwicons/refresh.svg")).apply {
        bindToolTipText(this, "toolbar.refresh")
        addActionListener {
            refreshData()
        }
    }

    private val redrawButton = JButton(FlatSVGIcon("ui/nwicons/redraw.svg")).apply {
        bindToolTipText(this, "toolbar.redraw")
        addActionListener {
            // TODO: 实现重绘功能
        }
    }

    init {
        add(searchButton)
        add(filterButton)
        add(cleanFilterButton)
        addSeparator()
        add(refreshButton)
        add(redrawButton)
        addSeparator()

        // 监听筛选状态变化
        scope.launch {
            studentViewModel.filterState.collectLatest { state ->
                if (currentIndex == 0) {
                    cleanFilterButton.isEnabled = state == FilterState.FILTERED
                }
            }
        }
    }

    /**
     * 设置当前选中的标签页索引
     */
    fun setCurrentIndex(index: Int) {
        currentIndex = index
        updateButtonStates()
    }

    /**
     * 更新按钮状态
     */
    private fun updateButtonStates() {
        when (currentIndex) {
            0 -> { // 学生数据
                searchButton.isEnabled = true
                filterButton.isEnabled = true
                refreshButton.isEnabled = true
                redrawButton.isEnabled = true
                studentViewModel.let { vm ->
                    cleanFilterButton.isEnabled = vm.filterState.value == FilterState.FILTERED
                }
            }
            1, 2 -> { // GPA标准, 证明模板
                searchButton.isEnabled = false
                filterButton.isEnabled = false
                cleanFilterButton.isEnabled = false
                refreshButton.isEnabled = true
                redrawButton.isEnabled = true
            }
            3 -> { // 术语
                searchButton.isEnabled = true
                filterButton.isEnabled = true
                cleanFilterButton.isEnabled = false
                refreshButton.isEnabled = true
                redrawButton.isEnabled = true
            }
        }
    }

    /**
     * 刷新数据
     */
    private fun refreshData() {
        when (currentIndex) {
            0 -> studentViewModel.forceReload()
            1 -> gpaViewModel.forceReload()
            2 -> certificateViewModel.forceReload()
            3 -> termViewModel.forceReload()
        }
    }
}