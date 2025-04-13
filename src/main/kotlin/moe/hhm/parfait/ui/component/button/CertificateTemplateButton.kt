/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.component.button

import com.formdev.flatlaf.FlatClientProperties
import com.formdev.flatlaf.extras.FlatSVGIcon
import moe.hhm.parfait.infra.i18n.I18nUtils.createLabel
import net.miginfocom.swing.MigLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JRadioButton
import javax.swing.JToggleButton

class CertificateTemplateButton(
    private val title: String,
    private val category: String,
    private val type: String,
    private val description: String,
    private val isLike: Boolean,
    private val isActive: Boolean
) : JToggleButton() {

    init {
        init()
    }

    private fun init() {
        setLayout(MigLayout("fillx,wrap 2", "[grow 0][fill]", "[top]"))
        putClientProperty(
            FlatClientProperties.STYLE, "" +
                    "background:null;" +
                    "selectedBackground:null;" +
                    "pressedBackground:null;" +
                    "borderWidth:1;"
        )

        val radioButton = JRadioButton()

        radioButton.setModel(getModel())
        add(radioButton)

        // 创建文本部分
        val panel = JPanel(MigLayout("insets 0,wrap,fillx", "[left]", "[][]"))
        val lbName: JLabel = JLabel(title)
        lbName.putClientProperty(
            FlatClientProperties.STYLE, "" +
                    "font:bold;"
        )
        panel.add(lbName, "split 2, gapleft 0")
        val lbCategory = JLabel(category)
        lbCategory.putClientProperty(
            FlatClientProperties.STYLE, "" +
                    "background:\$TextField.background;" +
                    "border:3,5,3,5,\$Component.borderColor,1,15;"
        )
        lbCategory.setIcon(FlatSVGIcon("ui/nwicons/category.svg"))
        panel.add(lbCategory, "gapleft 5, width pref, wrap")

        panel.add(JLabel(description), "wrap")

        // 特殊标记（优先显示收藏，未收藏则显示活跃状态）
        if (isLike) {
            val lbLike = createLabel("certificate.status.like")
            lbLike.setIcon(FlatSVGIcon("ui/nwicons/love_pink.svg"))
            lbLike.putClientProperty(
                FlatClientProperties.STYLE, "foreground:#d4237a"
            )
            panel.add(lbLike)
        } else if (isActive) {
            val lbActive = createLabel("certificate.status.active")
            lbActive.setIcon(FlatSVGIcon("ui/nwicons/check_circle.svg"))
            lbActive.putClientProperty(
                FlatClientProperties.STYLE, "foreground:#22b65a"
            )
            panel.add(lbActive)
        } else {
            val lbInactive = createLabel("certificate.status.inactive")
            lbInactive.setIcon(FlatSVGIcon("ui/nwicons/error_circle.svg"))
            lbInactive.putClientProperty(
                FlatClientProperties.STYLE, "foreground:#d45c5c"
            )
            panel.add(lbInactive)
        }

        add(panel)
    }
} 