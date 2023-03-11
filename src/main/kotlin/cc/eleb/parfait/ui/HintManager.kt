/*
 * Copyright 2020 FormDev Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.eleb.parfait.ui

import com.formdev.flatlaf.FlatLaf
import com.formdev.flatlaf.ui.FlatDropShadowBorder
import com.formdev.flatlaf.ui.FlatEmptyBorder
import com.formdev.flatlaf.ui.FlatUIUtils
import com.formdev.flatlaf.util.UIScale
import net.miginfocom.swing.MigLayout
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.MouseAdapter
import java.awt.geom.Area
import java.awt.geom.RoundRectangle2D
import javax.swing.*
import javax.swing.border.Border

/**
 * @author Karl Tauber
 */
object HintManager {
    private val hintPanels: MutableList<HintPanel> = ArrayList()
    fun showHint(hint: Hint) {
        val hintPanel = HintPanel(hint)
        hintPanel.showHint()
        hintPanels.add(hintPanel)
    }

    fun hideAllHints() {
        val hintPanels2 = hintPanels.toTypedArray()
        for (hintPanel in hintPanels2) hintPanel.hideHint()
    }

    //---- class HintPanel ----------------------------------------------------
    class Hint(
        val message: String,
        val owner: Component,
        val position: Int,
        val prefsKey: String,
        val nextHint: Hint?
    )

    //---- class HintPanel ----------------------------------------------------
    private class HintPanel(private val hint: Hint) : JPanel() {
        private lateinit var popup: JPanel
        override fun updateUI() {
            super.updateUI()
            background =
                if (UIManager.getLookAndFeel() is FlatLaf) UIManager.getColor("HintPanel.backgroundColor") else {
                    // using nonUIResource() because otherwise Nimbus does not fill the background
                    FlatUIUtils.nonUIResource(UIManager.getColor("info"))
                }
            updateBalloonBorder()
        }

        private fun updateBalloonBorder() {
            val direction: Int
            direction = when (hint!!.position) {
                SwingConstants.LEFT -> SwingConstants.RIGHT
                SwingConstants.TOP -> SwingConstants.BOTTOM
                SwingConstants.RIGHT -> SwingConstants.LEFT
                SwingConstants.BOTTOM -> SwingConstants.TOP
                else -> throw IllegalArgumentException()
            }
            border = BalloonBorder(direction, FlatUIUtils.getUIColor("PopupMenu.borderColor", Color.gray))
        }

        fun showHint() {
            val rootPane = SwingUtilities.getRootPane(hint!!.owner) ?: return
            val layeredPane = rootPane.layeredPane

            // create a popup panel that has a drop shadow
            popup = object : JPanel(BorderLayout()) {
                override fun updateUI() {
                    super.updateUI()

                    // use invokeLater because at this time the UI delegates
                    // of child components are not yet updated
                    EventQueue.invokeLater {
                        validate()
                        size = preferredSize
                    }
                }
            }
            popup.setOpaque(false)
            popup.add(this)

            // calculate x/y location for hint popup
            val pt = SwingUtilities.convertPoint(hint.owner, 0, 0, layeredPane)
            var x = pt.x
            var y = pt.y
            val size = popup.getPreferredSize()
            val gap = UIScale.scale(6)
            when (hint.position) {
                SwingConstants.LEFT -> x -= size.width + gap
                SwingConstants.TOP -> y -= size.height + gap
                SwingConstants.RIGHT -> x += hint.owner.width + gap
                SwingConstants.BOTTOM -> y += hint.owner.height + gap
            }

            // set hint popup size and show it
            popup.setBounds(x, y, size.width, size.height)
            layeredPane.add(popup, JLayeredPane.POPUP_LAYER)
        }

        fun hideHint() {
            if (popup != null) {
                val parent = popup!!.parent
                if (parent != null) {
                    parent.remove(popup)
                    parent.repaint(popup!!.x, popup!!.y, popup!!.width, popup!!.height)
                }
            }
            hintPanels.remove(this)
        }

        private fun gotIt() {
            // hide hint
            hideHint()
            showHint(hint.nextHint!!)
        }

        private fun initComponents() {
            // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            hintLabel = JLabel()
            gotItButton = JButton()

            //======== this ========
            layout = MigLayout(
                "insets dialog,hidemode 3",  // columns
                "[::200,fill]",  // rows
                "[]para" +
                        "[]"
            )

            //---- hintLabel ----
            hintLabel!!.text = "hint"
            add(hintLabel, "cell 0 0")

            //---- gotItButton ----
            gotItButton!!.text = "Got it!"
            gotItButton!!.isFocusable = false
            gotItButton!!.addActionListener { e: ActionEvent? -> gotIt() }
            add(gotItButton, "cell 0 1,alignx right,growx 0")
            // JFormDesigner - End of component initialization  //GEN-END:initComponents
        }

        // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
        private var hintLabel: JLabel? = null
        private var gotItButton: JButton? = null // JFormDesigner - End of variables declaration  //GEN-END:variables

        init {
            initComponents()
            isOpaque = false
            updateBalloonBorder()
            hintLabel!!.text = "<html>" + hint!!.message + "</html>"

            // grab all mouse events to avoid that components overlapped
            // by the hint panel receive them
            addMouseListener(object : MouseAdapter() {})
        }
    }

    //---- class BalloonBorder ------------------------------------------------
    private class BalloonBorder(private val direction: Int, private val borderColor: Color) :
        FlatEmptyBorder(1 + SHADOW_TOP_SIZE, 1 + SHADOW_SIZE, 1 + SHADOW_SIZE, 1 + SHADOW_SIZE) {
        private val shadowBorder: Border?

        init {
            when (direction) {
                SwingConstants.LEFT -> left += ARROW_SIZE
                SwingConstants.TOP -> top += ARROW_SIZE
                SwingConstants.RIGHT -> right += ARROW_SIZE
                SwingConstants.BOTTOM -> bottom += ARROW_SIZE
            }
            shadowBorder = if (UIManager.getLookAndFeel() is FlatLaf) FlatDropShadowBorder(
                UIManager.getColor("Popup.dropShadowColor"),
                Insets(SHADOW_SIZE2, SHADOW_SIZE2, SHADOW_SIZE2, SHADOW_SIZE2),
                FlatUIUtils.getUIFloat("Popup.dropShadowOpacity", 0.5f)
            ) else null
        }

        override fun paintBorder(c: Component, g: Graphics, x: Int, y: Int, width: Int, height: Int) {
            val g2 = g.create() as Graphics2D
            try {
                FlatUIUtils.setRenderingHints(g2)
                g2.translate(x, y)

                // shadow coordinates
                var sx = 0
                var sy = 0
                var sw = width
                var sh = height
                val arrowSize = UIScale.scale(ARROW_SIZE)
                when (direction) {
                    SwingConstants.LEFT -> {
                        sx += arrowSize
                        sw -= arrowSize
                    }

                    SwingConstants.TOP -> {
                        sy += arrowSize
                        sh -= arrowSize
                    }

                    SwingConstants.RIGHT -> sw -= arrowSize
                    SwingConstants.BOTTOM -> sh -= arrowSize
                }

                // paint shadow
                shadowBorder?.paintBorder(c, g2, sx, sy, sw, sh)

                // create balloon shape
                val bx = UIScale.scale(SHADOW_SIZE)
                val by = UIScale.scale(SHADOW_TOP_SIZE)
                val bw = width - UIScale.scale(SHADOW_SIZE + SHADOW_SIZE)
                val bh = height - UIScale.scale(SHADOW_TOP_SIZE + SHADOW_SIZE)
                g2.translate(bx, by)
                val shape = createBalloonShape(bw, bh)

                // fill balloon background
                g2.color = c.background
                g2.fill(shape)

                // paint balloon border
                g2.color = borderColor
                g2.stroke = BasicStroke(UIScale.scale(1f))
                g2.draw(shape)
            } finally {
                g2.dispose()
            }
        }

        private fun createBalloonShape(width: Int, height: Int): Shape {
            val arc = UIScale.scale(ARC)
            val xy = UIScale.scale(ARROW_XY)
            val awh = UIScale.scale(ARROW_SIZE)
            val rect: Shape
            val arrow: Shape
            when (direction) {
                SwingConstants.LEFT -> {
                    rect = RoundRectangle2D.Float(
                        awh.toFloat(),
                        0f,
                        (width - 1 - awh).toFloat(),
                        (height - 1).toFloat(),
                        arc.toFloat(),
                        arc.toFloat()
                    )
                    arrow = FlatUIUtils.createPath(
                        awh.toDouble(),
                        xy.toDouble(),
                        0.0,
                        (xy + awh).toDouble(),
                        awh.toDouble(),
                        (xy + awh + awh).toDouble()
                    )
                }

                SwingConstants.TOP -> {
                    rect = RoundRectangle2D.Float(
                        0f,
                        awh.toFloat(),
                        (width - 1).toFloat(),
                        (height - 1 - awh).toFloat(),
                        arc.toFloat(),
                        arc.toFloat()
                    )
                    arrow = FlatUIUtils.createPath(
                        xy.toDouble(),
                        awh.toDouble(),
                        (xy + awh).toDouble(),
                        0.0,
                        (xy + awh + awh).toDouble(),
                        awh.toDouble()
                    )
                }

                SwingConstants.RIGHT -> {
                    rect = RoundRectangle2D.Float(
                        0f,
                        0f,
                        (width - 1 - awh).toFloat(),
                        (height - 1).toFloat(),
                        arc.toFloat(),
                        arc.toFloat()
                    )
                    val x = width - 1 - awh
                    arrow = FlatUIUtils.createPath(
                        x.toDouble(),
                        xy.toDouble(),
                        (x + awh).toDouble(),
                        (xy + awh).toDouble(),
                        x.toDouble(),
                        (xy + awh + awh).toDouble()
                    )
                }

                SwingConstants.BOTTOM -> {
                    rect = RoundRectangle2D.Float(
                        0f,
                        0f,
                        (width - 1).toFloat(),
                        (height - 1 - awh).toFloat(),
                        arc.toFloat(),
                        arc.toFloat()
                    )
                    val y = height - 1 - awh
                    arrow = FlatUIUtils.createPath(
                        xy.toDouble(),
                        y.toDouble(),
                        (xy + awh).toDouble(),
                        (y + awh).toDouble(),
                        (xy + awh + awh).toDouble(),
                        y.toDouble()
                    )
                }

                else -> throw RuntimeException()
            }
            val area = Area(rect)
            area.add(Area(arrow))
            return area
        }

        companion object {
            private const val ARC = 8
            private const val ARROW_XY = 16
            private const val ARROW_SIZE = 8
            private const val SHADOW_SIZE = 6
            private const val SHADOW_TOP_SIZE = 3
            private const val SHADOW_SIZE2 = SHADOW_SIZE + 2
        }
    }
}