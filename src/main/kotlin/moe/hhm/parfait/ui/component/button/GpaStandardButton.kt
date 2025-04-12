/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.component.button

import com.formdev.flatlaf.FlatClientProperties
import com.formdev.flatlaf.extras.FlatSVGIcon
import moe.hhm.parfait.dto.GpaType
import moe.hhm.parfait.infra.i18n.I18nUtils.createLabel
import net.miginfocom.swing.MigLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JRadioButton
import javax.swing.JToggleButton

class GpaStandardButton(
    private val title: String,
    private val category: String,
    private val description: String,
    private val purpose: String,
    private val type: GpaType,
    private val createTime: String
) : JToggleButton() {

    init {
        init()
    }

    private fun init() {
        setLayout(MigLayout("fillx,wrap 3", "[grow 0][fill][grow 0]", "[top]"))
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

        // create text
        val panel = JPanel(MigLayout("insets 0,nogrid"))
        val lbName: JLabel = JLabel(title)
        lbName.putClientProperty(
            FlatClientProperties.STYLE, "" +
                    "font:bold;"
        )
        panel.add(lbName)
        val lbRegion = JLabel(category)
        lbRegion.putClientProperty(
            FlatClientProperties.STYLE, "" +
                    "background:\$TextField.background;" +
                    "border:3,5,3,5,\$Component.borderColor,1,15;"
        )
        lbRegion.setIcon(FlatSVGIcon("ui/nwicons/category.svg"))
        panel.add(lbRegion, "wrap")

        panel.add(JLabel(description), "wrap")

        // create size
        val lbSizeDescription = JLabel(purpose)
        lbSizeDescription.putClientProperty(
            FlatClientProperties.STYLE, "" +
                    "foreground:\$Label.disabledForeground;"
        )
        panel.add(lbSizeDescription)

        add(panel)

        // panel price
        val panelPrice = JPanel(MigLayout("insets 0,wrap"))
        when (type) {
            GpaType.NORMAL -> {
                panelPrice.add(createLabel("gpa.type.normal"))
            }

            GpaType.LIKE -> {
                val lbLimit = createLabel("gpa.type.like")
                lbLimit.setIcon(FlatSVGIcon("ui/nwicons/love_pink.svg"))
                lbLimit.putClientProperty(
                    FlatClientProperties.STYLE, "foreground:#d4237a"
                )
                panelPrice.add(lbLimit)
            }

            GpaType.DEFAULT -> {
                val lbLimit = createLabel("gpa.type.default")
                lbLimit.setIcon(FlatSVGIcon("ui/nwicons/infinity.svg"))
                lbLimit.putClientProperty(
                    FlatClientProperties.STYLE, "foreground:#22b65a"
                )
                panelPrice.add(lbLimit)
            }
        }
        panelPrice.add(JLabel(createTime), "al trailing")

        add(panelPrice)
    }
}
