package cc.eleb.parfait.theme

import cc.eleb.parfait.i18n.trs
import cc.eleb.parfait.ui.ParfaitFrame
import com.formdev.flatlaf.*
import com.formdev.flatlaf.icons.FlatAbstractIcon
import com.formdev.flatlaf.themes.FlatMacDarkLaf
import com.formdev.flatlaf.themes.FlatMacLightLaf
import com.formdev.flatlaf.util.ColorFunctions
import com.formdev.flatlaf.util.LoggingFacade
import java.awt.Color
import java.awt.Component
import java.awt.Graphics2D
import java.beans.PropertyChangeEvent
import javax.swing.*

object ColorUtils {
    private val accentColorKeys: Array<String> = arrayOf(
        "Demo.accent.default", "Demo.accent.blue", "Demo.accent.purple",
        "Demo.accent.red", "Demo.accent.orange", "Demo.accent.yellow", "Demo.accent.green"
    )
    private val accentColorNames: Array<String> =
        arrayOf("Default", "Blue", "Purple", "Red", "Orange", "Yellow", "Green")
    private val accentColorButtons: ArrayList<JToggleButton> = arrayListOf()
    private var accentColorLabel = JLabel()
    private var accentColor: Color? = null

    fun reloadTranslation() {
        accentColorLabel.text = "frame-accent".trs()
    }

    fun init() {
        this.reloadTranslation()
        ParfaitFrame.instance.toolBar.let {
            it.add(Box.createHorizontalGlue())
            it.add(accentColorLabel)
        }
        val group = ButtonGroup()
        for (i in accentColorKeys.indices) {
            val jtb = JToggleButton(AccentColorIcon(accentColorKeys[i]))
            jtb.toolTipText = accentColorNames[i]
            jtb.addActionListener { accentColorChanged() }
            accentColorButtons.add(jtb)
            ParfaitFrame.instance.toolBar.add(jtb)
            group.add(jtb)
        }
        accentColorButtons[0].isSelected = true
        FlatLaf.setSystemColorGetter { name: String -> if (name == "accent") accentColor else null }
        UIManager.addPropertyChangeListener { e: PropertyChangeEvent -> if ("lookAndFeel" == e.propertyName) updateAccentColorButtons() }
        updateAccentColorButtons()
    }

    private fun accentColorChanged() {
        var accentColorKey: String? = null
        for (i in accentColorButtons.indices) {
            if (accentColorButtons[i].isSelected) {
                accentColorKey = accentColorKeys[i]
                break
            }
        }
        accentColor =
            if (accentColorKey != null && accentColorKey !== accentColorKeys[0]) UIManager.getColor(accentColorKey) else null
        val lafClass: Class<out LookAndFeel> = UIManager.getLookAndFeel().javaClass
        try {
            FlatLaf.setup(lafClass.newInstance())
            FlatLaf.updateUI()
        } catch (ex: InstantiationException) {
            LoggingFacade.INSTANCE.logSevere(null, ex)
        } catch (ex: IllegalAccessException) {
            LoggingFacade.INSTANCE.logSevere(null, ex)
        }
    }

    private fun updateAccentColorButtons() {
        val lafClass: Class<out LookAndFeel> = UIManager.getLookAndFeel().javaClass
        val isAccentColorSupported: Boolean =
            (lafClass == FlatLightLaf::class.java) || (lafClass == FlatDarkLaf::class.java) || (lafClass == FlatIntelliJLaf::class.java) || (lafClass == FlatDarculaLaf::class.java) || (lafClass == FlatMacLightLaf::class.java) || (lafClass == FlatMacDarkLaf::class.java)
        accentColorLabel.isVisible = isAccentColorSupported
        for (i in accentColorButtons.indices) accentColorButtons[i].isVisible = isAccentColorSupported
    }

    private class AccentColorIcon(private val colorKey: String) : FlatAbstractIcon(16, 16, null) {
        override fun paintIcon(c: Component, g: Graphics2D) {
            var color: Color? = UIManager.getColor(colorKey)
            if (color == null) color = Color.lightGray else if (!c.isEnabled) {
                color = if (FlatLaf.isLafDark()) ColorFunctions.shade(color, 0.5f) else ColorFunctions.tint(color, 0.6f)
            }
            g.color = color
            g.fillRoundRect(1, 1, width - 2, height - 2, 5, 5)
        }
    }
}