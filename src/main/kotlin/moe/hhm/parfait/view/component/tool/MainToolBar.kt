/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.view.component.tool

import com.formdev.flatlaf.extras.FlatSVGIcon
import javax.swing.JButton
import javax.swing.JToolBar

class MainToolBar : JToolBar() {
    private val searchButton = JButton().apply {
        icon = FlatSVGIcon("ui/nwicons/search.svg")
        toolTipText = "搜索"
        addActionListener {
            // TODO: 实现搜索功能
        }
    }
    private val filterButton = JButton().apply {
        icon = FlatSVGIcon("ui/nwicons/filter.svg")
        toolTipText = "筛选"
        addActionListener {
            // TODO: 实现过滤功能
        }
    }
    private val cleanFilterButton = JButton().apply {
        icon = FlatSVGIcon("ui/nwicons/filter-cancel-16.svg")
        toolTipText = "清除搜索/筛选条件"
        addActionListener {
            // TODO: 实现清除过滤功能
        }
    }
    private val refreshButton = JButton().apply {
        icon = FlatSVGIcon("ui/nwicons/refresh.svg")
        toolTipText = "重新加载数据"
        addActionListener {
            // TODO: 实现刷新功能
        }
    }
    private val redrawButton = JButton().apply {
        icon = FlatSVGIcon("ui/nwicons/redraw.svg")
        toolTipText = "重绘UI"
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

    }
}