/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.component.button

import com.formdev.flatlaf.FlatClientProperties
import com.formdev.flatlaf.extras.FlatSVGIcon
import moe.hhm.parfait.domain.model.gpa.GpaStandard
import net.miginfocom.swing.MigLayout
import java.text.NumberFormat
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JRadioButton
import javax.swing.JToggleButton

class GpaStandardButton(private val data: GpaStandard): JToggleButton() {
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
        val lbName: JLabel = JLabel(data.name)
        lbName.putClientProperty(
            FlatClientProperties.STYLE, "" +
                    "font:bold;"
        )
        panel.add(lbName)
        val lbRegion: JLabel = JLabel("地区")
        lbRegion.putClientProperty(
            FlatClientProperties.STYLE, "" +
                    "background:\$TextField.background;" +
                    "border:3,5,3,5,\$Component.borderColor,1,15;"
        )
        lbRegion.setIcon(FlatSVGIcon("database/icon/globe.svg", 0.9f))
        panel.add(lbRegion, "wrap")

        panel.add(JLabel(data.description), "wrap")

        // create size
        if (false) {
            // unlimited size
            val lbLimit = JLabel("Unlimited")
            lbLimit.setIcon(FlatSVGIcon("database/icon/infinity.svg", 0.9f))
            lbLimit.putClientProperty(
                FlatClientProperties.STYLE, "" +
                        "foreground:#22b65a"
            )
            panel.add(lbLimit)
        } else {
            panel.add(JLabel(300.toString() + " GB"), "wrap")
        }
        val lbSizeDescription: JLabel = JLabel(data.description)
        lbSizeDescription.putClientProperty(
            FlatClientProperties.STYLE, "" +
                    "foreground:\$Label.disabledForeground;"
        )
        panel.add(lbSizeDescription)

        add(panel)

        // panel price
        val panelPrice = JPanel(MigLayout("insets 0,wrap"))
        val nf = NumberFormat.getCurrencyInstance()
        val lbPrice = JLabel("from " + nf.format(11.31321903122))
        lbPrice.putClientProperty(
            FlatClientProperties.STYLE, "" +
                    "foreground:\$Component.accentColor;" +
                    "font:bold +2;"
        )
        panelPrice.add(lbPrice)
        panelPrice.add(JLabel("/ month"), "al trailing")

        add(panelPrice)
    }
}