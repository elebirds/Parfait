/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.component.render

import moe.hhm.parfait.dto.CertificateTemplateDTO
import java.awt.Component
import javax.swing.DefaultListCellRenderer
import javax.swing.JList

/**
 * 证书模板下拉框的渲染器
 */
class CertificateTemplateListCellRenderer : DefaultListCellRenderer() {
    override fun getListCellRendererComponent(
        list: JList<*>?,
        value: Any?,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): Component {
        val component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)

        if (value is CertificateTemplateDTO) {
            text = "${value.name} (${value.category})"
            toolTipText = value.description
        }

        return component
    }
} 