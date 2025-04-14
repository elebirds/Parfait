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
import moe.hhm.parfait.ui.component.dialog.TermSearchFilterDialog
import moe.hhm.parfait.ui.state.FilterState
import moe.hhm.parfait.ui.viewmodel.CertificateTemplateViewModel
import moe.hhm.parfait.ui.viewmodel.GpaStandardViewModel
import moe.hhm.parfait.ui.viewmodel.StudentDataViewModel
import moe.hhm.parfait.ui.viewmodel.TermViewModel
import org.koin.core.component.KoinComponent
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JToolBar
import javax.swing.SwingUtilities

class MainToolBar(
    private val parent: JPanel,
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
                when (currentIndex) {
                    0 -> SearchFilterDialog.showSearch(window)  // 学生搜索
                    3 -> TermSearchFilterDialog.showSearch(window)  // 术语搜索
                }
            }
        }
    }

    private val filterButton = JButton(FlatSVGIcon("ui/nwicons/filter.svg")).apply {
        bindToolTipText(this, "toolbar.filter")
        addActionListener {
            SwingUtilities.getWindowAncestor(this)?.let { window ->
                when (currentIndex) {
                    0 -> AdvancedFilterDialog.show(window)  // 学生高级筛选
                }
            }
        }
    }

    private val clearFilterButton = JButton(FlatSVGIcon("ui/nwicons/filter-cancel-16.svg")).apply {
        bindToolTipText(this, "toolbar.clearFilter")
        isEnabled = false // 初始状态下禁用
        addActionListener {
            when (currentIndex) {
                0 -> studentViewModel.clearFilter()
                3 -> termViewModel.clearFilter()
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
            parent.repaint()
        }
    }

    init {
        add(searchButton)
        add(filterButton)
        add(clearFilterButton)
        addSeparator()
        add(refreshButton)
        add(redrawButton)
        addSeparator()

        // 初始状态下禁用搜索和筛选按钮
        searchButton.isEnabled = false
        filterButton.isEnabled = false
        clearFilterButton.isEnabled = false

        observer()
        setCurrentIndex(0) // 默认设置为学生视图
    }

    override fun observer() {
        // 监听术语视图模型的筛选状态变化
        scope.launch {
            termViewModel.filterState.collectLatest { filterState ->
                if (currentIndex == 3) {
                    clearFilterButton.isEnabled = filterState == FilterState.FILTERED
                    searchButton.isEnabled = filterState != FilterState.FILTERED
                }
            }
        }

        // 监听学生视图模型的筛选状态变化
        scope.launch {
            studentViewModel.filterState.collectLatest { filterState ->
                if (currentIndex == 0) {
                    clearFilterButton.isEnabled = filterState == FilterState.FILTERED
                    searchButton.isEnabled = filterState != FilterState.FILTERED
                    filterButton.isEnabled = filterState != FilterState.FILTERED
                }
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

    /**
     * 设置当前视图索引，启用/禁用相应的搜索和筛选按钮
     */
    fun setCurrentIndex(index: Int) {
        currentIndex = index

        // 学生视图和术语视图启用搜索和筛选按钮
        searchButton.isEnabled = index == 0 || index == 3
        filterButton.isEnabled = index == 0

        // 根据当前筛选状态启用/禁用清除筛选按钮
        when (index) {
            0 -> clearFilterButton.isEnabled = studentViewModel.filterState.value == FilterState.FILTERED
            3 -> clearFilterButton.isEnabled = termViewModel.filterState.value == FilterState.FILTERED
            else -> clearFilterButton.isEnabled = false
        }
    }
}