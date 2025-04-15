/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */
package moe.hhm.parfait.ui.lib

import com.formdev.flatlaf.FlatLaf
import com.formdev.flatlaf.FlatPropertiesLaf
import com.formdev.flatlaf.IntelliJTheme
import com.formdev.flatlaf.IntelliJTheme.ThemeLaf
import com.formdev.flatlaf.themes.FlatMacLightLaf
import com.formdev.flatlaf.util.LoggingFacade
import com.formdev.flatlaf.util.StringUtils
import com.formdev.flatlaf.util.SystemInfo
import org.slf4j.LoggerFactory
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.io.File
import java.io.FileInputStream
import java.util.prefs.Preferences
import javax.swing.*
import kotlin.system.exitProcess

/**
 * @author Karl Tauber
 */
object FlatLafPrefs {
    private val logger = LoggerFactory.getLogger(FlatLafPrefs::class.java)
    const val KEY_LAF: String = "laf"
    const val KEY_LAF_THEME: String = "lafTheme"
    const val KEY_SYSTEM_SCALE_FACTOR: String = "systemScaleFactor"

    const val RESOURCE_PREFIX: String = "res:"
    const val FILE_PREFIX: String = "file:"

    const val THEME_UI_KEY: String = "parfait.theme"

    lateinit var state: Preferences

    fun init(rootPath: String) {
        state = Preferences.userRoot().node(rootPath)
    }

    private fun fallbackToDefaultLookAndFeel(cause: String) {
        FlatMacLightLaf.setup()
        logger.debug("在${cause}中回退为默认主题：FlatMacLightLaf")
    }

    fun setupLaf() {
        try {
            val lafClassName = state.get(KEY_LAF, FlatMacLightLaf::class.java.getName())
            val theme = state.get(KEY_LAF_THEME, "")
            logger.debug("获取主题偏好设置：$lafClassName, $theme")
            when (lafClassName) {
                ThemeLaf::class.java.getName() -> {
                    val theme = state.get(KEY_LAF_THEME, "")
                    if (theme.startsWith(FILE_PREFIX)) {
                        FlatLaf.setup(
                            IntelliJTheme.createLaf(
                                FileInputStream(theme.substring(FILE_PREFIX.length))
                            )
                        )
                        logger.debug("设置主题为IntelliJTheme：$theme")
                    } else fallbackToDefaultLookAndFeel("IntelliJTheme")
                    if (!theme.isEmpty()) UIManager.getLookAndFeelDefaults().put(THEME_UI_KEY, theme)
                }

                FlatPropertiesLaf::class.java.getName() -> {
                    if (theme.startsWith(FILE_PREFIX)) {
                        val themeFile = File(theme.substring(FILE_PREFIX.length))
                        val themeName = StringUtils.removeTrailing(themeFile.getName(), ".properties")
                        FlatLaf.setup(FlatPropertiesLaf(themeName, themeFile))
                        logger.debug("设置主题为FlatPropertiesLaf：$theme")
                    } else fallbackToDefaultLookAndFeel("FlatPropertiesLaf")
                    if (!theme.isEmpty()) UIManager.getLookAndFeelDefaults().put(THEME_UI_KEY, theme)
                }

                else -> {
                    UIManager.setLookAndFeel(lafClassName)
                    logger.debug("设置主题为：$lafClassName")
                }
            }
        } catch (ex: Throwable) {
            LoggingFacade.INSTANCE.logSevere(null, ex)

            logger.error("设置主题失败：${ex.message}")
            fallbackToDefaultLookAndFeel("错误")
        }

        // remember active look and feel
        UIManager.addPropertyChangeListener(PropertyChangeListener { e: PropertyChangeEvent ->
            if ("lookAndFeel" == e.propertyName) state.put(
                KEY_LAF, UIManager.getLookAndFeel()::class.java.getName()
            )
        })
    }

    fun initSystemScale() {
        if (System.getProperty("sun.java2d.uiScale") == null) {
            val scaleFactor = state.get(KEY_SYSTEM_SCALE_FACTOR, null)
            if (scaleFactor != null) System.setProperty("sun.java2d.uiScale", scaleFactor)
        }
    }

    /**
     * register Alt+Shift+F1, F2, ... F12 keys to change system scale factor
     */
    fun registerSystemScaleFactors(frame: JFrame) {
        registerSystemScaleFactor(frame, "alt shift F1", null)
        registerSystemScaleFactor(frame, "alt shift F2", "1")

        if (SystemInfo.isWindows) {
            registerSystemScaleFactor(frame, "alt shift F3", "1.25")
            registerSystemScaleFactor(frame, "alt shift F4", "1.5")
            registerSystemScaleFactor(frame, "alt shift F5", "1.75")
            registerSystemScaleFactor(frame, "alt shift F6", "2")
            registerSystemScaleFactor(frame, "alt shift F7", "2.25")
            registerSystemScaleFactor(frame, "alt shift F8", "2.5")
            registerSystemScaleFactor(frame, "alt shift F9", "2.75")
            registerSystemScaleFactor(frame, "alt shift F10", "3")
            registerSystemScaleFactor(frame, "alt shift F11", "3.5")
            registerSystemScaleFactor(frame, "alt shift F12", "4")
        } else {
            // Java on macOS and Linux supports only integer scale factors
            registerSystemScaleFactor(frame, "alt shift F3", "2")
            registerSystemScaleFactor(frame, "alt shift F4", "3")
            registerSystemScaleFactor(frame, "alt shift F5", "4")
        }
    }

    private fun registerSystemScaleFactor(frame: JFrame, keyStrokeStr: String?, scaleFactor: String?) {
        val keyStroke = KeyStroke.getKeyStroke(keyStrokeStr)
        requireNotNull(keyStroke) { "Invalid key stroke '$keyStrokeStr'" }

        (frame.contentPane as JComponent).registerKeyboardAction(
            ActionListener { e: ActionEvent? -> applySystemScaleFactor(frame, scaleFactor) },
            keyStroke,
            JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        )
    }

    private fun applySystemScaleFactor(frame: JFrame, scaleFactor: String?) {
        if (JOptionPane.showConfirmDialog(
                frame,
                ("Change system scale factor to "
                        + (scaleFactor ?: "default")
                        + " and exit?"),
                frame.getTitle(), JOptionPane.YES_NO_OPTION
            ) != JOptionPane.YES_OPTION
        ) return

        if (scaleFactor != null) state.put(KEY_SYSTEM_SCALE_FACTOR, scaleFactor)
        else state.remove(KEY_SYSTEM_SCALE_FACTOR)

        exitProcess(0)
    }
}