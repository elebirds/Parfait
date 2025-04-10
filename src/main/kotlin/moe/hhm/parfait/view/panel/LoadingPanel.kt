/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.view.panel

import com.formdev.flatlaf.FlatClientProperties
import com.formdev.flatlaf.extras.FlatSVGIcon
import moe.hhm.parfait.utils.i18n.I18nUtils.createLabel
import net.miginfocom.swing.MigLayout
import javax.swing.JLabel
import javax.swing.JPanel

class LoadingPanel : JPanel() {
    private val loadingIcon = JLabel(FlatSVGIcon("ui/nwicons/loading.svg", 0.5f)).apply {
        putClientProperty(FlatClientProperties.STYLE, "foreground:\$Component.accentColor;")
    }
    private val loadingText = createLabel("loading.message").apply {
        putClientProperty(FlatClientProperties.STYLE, "font:bold +2;")
    }

    init {
        layout = MigLayout("wrap,al center center", "[center]")
        add(loadingIcon, "gapy 20 10")
        add(loadingText)
    }
} 