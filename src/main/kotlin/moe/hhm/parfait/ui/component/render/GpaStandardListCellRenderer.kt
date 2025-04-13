/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.component.render

import moe.hhm.parfait.dto.GpaStandardDTO
import java.awt.Component
import javax.swing.DefaultListCellRenderer
import javax.swing.JList

/**
 * GPA标准下拉框的渲染器
 */
class GpaStandardListCellRenderer : DefaultListCellRenderer() {
    override fun getListCellRendererComponent(
        list: JList<*>?,
        value: Any?,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): Component {
        val component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
        
        if (value is GpaStandardDTO) {
            val defaultMark = if (value.isDefault) " (默认)" else ""
            text = "${value.name}${defaultMark}"
            toolTipText = value.description
        }
        
        return component
    }
} 