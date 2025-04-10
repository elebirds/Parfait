/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.component.tool

import com.formdev.flatlaf.extras.FlatSVGIcon
import moe.hhm.parfait.infra.i18n.I18nUtils.bindToolTipText
import javax.swing.JButton
import javax.swing.JToolBar

class MainToolBar : JToolBar() {
    private val searchButton = JButton(FlatSVGIcon("ui/nwicons/search.svg")).apply {
        bindToolTipText(this, "toolbar.search")
        addActionListener {
            // TODO: 实现搜索功能
        }
    }
    private val filterButton = JButton(FlatSVGIcon("ui/nwicons/filter.svg")).apply {
        bindToolTipText(this, "toolbar.filter")
        addActionListener {
            // TODO: 实现过滤功能
        }
    }
    private val cleanFilterButton = JButton(FlatSVGIcon("ui/nwicons/filter-cancel-16.svg")).apply {
        bindToolTipText(this, "toolbar.clearFilter")
        addActionListener {
            // TODO: 实现清除过滤功能
        }
    }
    private val refreshButton = JButton(FlatSVGIcon("ui/nwicons/refresh.svg")).apply {
        bindToolTipText(this, "toolbar.refresh")
        addActionListener {
            // TODO: 实现刷新功能
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
    }
}