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
import moe.hhm.parfait.ui.component.dialog.SearchFilterDialog
import moe.hhm.parfait.ui.state.FilterState
import moe.hhm.parfait.ui.viewmodel.StudentDataViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import javax.swing.JButton
import javax.swing.JToolBar
import javax.swing.SwingUtilities

class MainToolBar : JToolBar(), KoinComponent, CoroutineComponent by DefaultCoroutineComponent() {
    
    private val viewModel: StudentDataViewModel by inject()
    
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
                SearchFilterDialog.showFilter(window)
            }
        }
    }
    
    private val cleanFilterButton = JButton(FlatSVGIcon("ui/nwicons/filter-cancel-16.svg")).apply {
        bindToolTipText(this, "toolbar.clearFilter")
        isEnabled = false // 初始状态下禁用
        addActionListener {
            viewModel.clearFilter()
        }
    }
    
    private val refreshButton = JButton(FlatSVGIcon("ui/nwicons/refresh.svg")).apply {
        bindToolTipText(this, "toolbar.refresh")
        addActionListener {
            viewModel.loadData()
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
        observeFilterState()
    }
    
    /**
     * 监听筛选状态变化
     */
    private fun observeFilterState() {
        scope.launch {
            viewModel.filterState.collectLatest { state ->
                cleanFilterButton.isEnabled = state == FilterState.FILTERED
            }
        }
    }
}