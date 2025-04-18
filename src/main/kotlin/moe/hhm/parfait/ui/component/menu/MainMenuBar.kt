/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.component.menu

import javax.swing.JMenuBar

class MainMenuBar : JMenuBar() {
    private val fileMenu = FileMenu()
    private val languageMenu = LanguageMenu()
    private val helpMenu = HelpMenu()

    init {
        add(fileMenu)
        add(languageMenu)
        // add(ThemeUtils.themeMenu)
        // add(FontUtils.fontMenu)
        add(helpMenu)
    }
}