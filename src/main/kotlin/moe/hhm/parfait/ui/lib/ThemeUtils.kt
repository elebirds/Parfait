package moe.hhm.parfait.ui.lib

import com.formdev.flatlaf.*
import com.formdev.flatlaf.extras.FlatAnimatedLafChange
import com.formdev.flatlaf.themes.FlatMacDarkLaf
import com.formdev.flatlaf.themes.FlatMacLightLaf
import com.formdev.flatlaf.util.LoggingFacade
import com.formdev.flatlaf.util.SystemInfo
import moe.hhm.parfait.infra.i18n.I18nUtils
import java.awt.EventQueue
import java.awt.event.ActionEvent
import java.nio.file.Files
import javax.swing.*
import javax.swing.plaf.metal.MetalLookAndFeel
import javax.swing.plaf.nimbus.NimbusLookAndFeel

object ThemeUtils {
    const val THEMES_PACKAGE = "/themes/"
    val themes: LinkedHashMap<String, ThemeInfo> = linkedMapOf()
    val themeMenu: JMenu = JMenu().apply {
        I18nUtils.bindText(this, "menu.theme")
    }

    fun init() {
        themes["Light"] = ThemeInfo("Light", null, false, null, null, null, null, null, FlatLightLaf::class.java.name)
        themes["Dark"] = ThemeInfo("Dark", null, true, null, null, null, null, null, FlatDarkLaf::class.java.name)
        themes["IntelliJ"] =
            ThemeInfo("IntelliJ", null, false, null, null, null, null, null, FlatIntelliJLaf::class.java.name)
        themes["Darcula"] =
            ThemeInfo("Darcula", null, true, null, null, null, null, null, FlatDarculaLaf::class.java.name)
        themes["macOS Light"] =
            ThemeInfo("macOS Light", null, false, null, null, null, null, null, FlatMacLightLaf::class.java.name)
        themes["macOS Dark"] =
            ThemeInfo("macOS Dark", null, true, null, null, null, null, null, FlatMacDarkLaf::class.java.name)
        if (SystemInfo.isWindows) themes["Windows"] = ThemeInfo(
            "Windows",
            null,
            true,
            null,
            null,
            null,
            null,
            null,
            "com.sun.java.swing.plaf.windows.WindowsLookAndFeel"
        )
        else if (SystemInfo.isMacOS) themes["Aqua"] =
            ThemeInfo("Aqua", null, true, null, null, null, null, null, "com.apple.laf.AquaLookAndFeel")
        else if (SystemInfo.isLinux) themes["GTK"] =
            ThemeInfo("GTK", null, true, null, null, null, null, null, "com.sun.java.swing.plaf.gtk.GTKLookAndFeel")
        themes["Metal"] =
            ThemeInfo("Metal", null, true, null, null, null, null, null, MetalLookAndFeel::class.java.name)
        themes["Nimbus"] =
            ThemeInfo("Nimbus", null, true, null, null, null, null, null, NimbusLookAndFeel::class.java.name)
        val bg = ButtonGroup()
        for (i in 0 until themes.size) {
            val tf = JRadioButtonMenuItem()
            tf.text = themes.values.toTypedArray()[i].name
            tf.addActionListener { e: ActionEvent -> setTheme(e) }
            bg.add(tf)
            if (i == 4) bg.setSelected(tf.model, true)
            themeMenu.add(tf)
        }
    }

    fun setTheme(e: ActionEvent) {
        EventQueue.invokeLater { themes[e.actionCommand]?.let { setTheme(it) } }
    }

    private fun setTheme(themeInfo: ThemeInfo) {
        if (themeInfo.lafClassName != null) {
            if (themeInfo.lafClassName == UIManager.getLookAndFeel().javaClass.name) return
            FlatAnimatedLafChange.showSnapshot()
            try {
                UIManager.setLookAndFeel(themeInfo.lafClassName)
            } catch (ex: java.lang.Exception) {
                LoggingFacade.INSTANCE.logSevere(null, ex)
                JOptionPane.showMessageDialog(
                    null,
                    I18nUtils.getFormattedText("theme.error.create", themeInfo.lafClassName),
                    I18nUtils.getText("error.generic.title"),
                    JOptionPane.ERROR_MESSAGE
                )
            }
        } else if (themeInfo.themeFile != null) {
            FlatAnimatedLafChange.showSnapshot()
            try {
                if (themeInfo.themeFile.name.endsWith(".properties")) {
                    FlatLaf.setup(FlatPropertiesLaf(themeInfo.name, themeInfo.themeFile))
                } else FlatLaf.setup(IntelliJTheme.createLaf(Files.newInputStream(themeInfo.themeFile.toPath())))
                FlatLafPrefs.state.put(FlatLafPrefs.KEY_LAF_THEME, FlatLafPrefs.FILE_PREFIX + themeInfo.themeFile)
            } catch (ex: Exception) {
                LoggingFacade.INSTANCE.logSevere(null, ex)
                JOptionPane.showMessageDialog(
                    null,
                    I18nUtils.getFormattedText("theme.error.load", themeInfo.themeFile.name),
                    I18nUtils.getText("error.generic.title"),
                    JOptionPane.ERROR_MESSAGE
                )
            }
        } else {
            FlatAnimatedLafChange.showSnapshot()
            IntelliJTheme.setup(javaClass.getResourceAsStream(THEMES_PACKAGE + themeInfo.resourceName))
            FlatLafPrefs.state.put(FlatLafPrefs.KEY_LAF_THEME, FlatLafPrefs.RESOURCE_PREFIX + themeInfo.resourceName)
        }
        FlatLaf.updateUI()
        FlatAnimatedLafChange.hideSnapshotWithAnimation()
        FontUtils.updateFontMenuItems()
    }
}