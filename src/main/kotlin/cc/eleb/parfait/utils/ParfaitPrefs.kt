/*
 * Copyright 2019 FormDev Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.eleb.parfait.utils

import com.formdev.flatlaf.FlatLaf
import com.formdev.flatlaf.FlatPropertiesLaf
import com.formdev.flatlaf.IntelliJTheme
import com.formdev.flatlaf.themes.FlatMacLightLaf
import com.formdev.flatlaf.util.StringUtils
import java.beans.PropertyChangeEvent
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.prefs.Preferences
import javax.swing.UIManager

/**
 * @author Karl Tauber
 */
object ParfaitPrefs {
    const val KEY_LAF = "laf"
    const val KEY_LAF_THEME = "lafTheme"
    const val RESOURCE_PREFIX = "res:"
    const val FILE_PREFIX = "file:"
    private const val THEME_UI_KEY = "__Parfait.demo.theme"
    lateinit var state: Preferences

    fun init(rootPath: String) {
        state = Preferences.userRoot().node(rootPath)
    }

    fun setupLaf() {
        try {
            val lafClassName: String = state.get(KEY_LAF, FlatMacLightLaf::class.java.name)
            if (IntelliJTheme.ThemeLaf::class.java.name == lafClassName) {
                val theme: String = state.get(KEY_LAF_THEME, "")
                if (theme.startsWith(RESOURCE_PREFIX)) //IntelliJTheme.setup(
                //IJThemesPanel::class.java.getResourceAsStream(
                //    IJThemesPanel.THEMES_PACKAGE + theme.substring(
                //        RESOURCE_PREFIX.length
                //    )
                //))
                else if (theme.startsWith(FILE_PREFIX)) FlatLaf.setup(
                    IntelliJTheme.createLaf(
                        Files.newInputStream(Paths.get(theme.substring(FILE_PREFIX.length)))
                    )
                ) else FlatMacLightLaf.setup()
                if (theme.isNotEmpty()) UIManager.getLookAndFeelDefaults()[THEME_UI_KEY] = theme
            } else if (FlatPropertiesLaf::class.java.name == lafClassName) {
                val theme: String = state.get(KEY_LAF_THEME, "")
                if (theme.startsWith(FILE_PREFIX)) {
                    val themeFile = File(theme.substring(FILE_PREFIX.length))
                    val themeName = StringUtils.removeTrailing(themeFile.name, ".properties")
                    FlatLaf.setup(FlatPropertiesLaf(themeName, themeFile))
                } else FlatMacLightLaf.setup()
                if (theme.isNotEmpty()) UIManager.getLookAndFeelDefaults()[THEME_UI_KEY] = theme
            } else UIManager.setLookAndFeel(lafClassName)
        } catch (ex: Throwable) {
            FlatMacLightLaf.setup()
        }
        UIManager.addPropertyChangeListener { e: PropertyChangeEvent ->
            if ("lookAndFeel" == e.propertyName) state.put(
                KEY_LAF, UIManager.getLookAndFeel().javaClass.name
            )
        }
    }
}