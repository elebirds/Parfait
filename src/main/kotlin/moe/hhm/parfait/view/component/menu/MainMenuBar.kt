/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.view.component.menu

import javax.swing.JMenuBar

class MainMenuBar : JMenuBar() {
    private val fileMenu = FileMenu()
    private val helpMenu = HelpMenu()

    init {
        add(fileMenu)
        add(helpMenu)
    }
}