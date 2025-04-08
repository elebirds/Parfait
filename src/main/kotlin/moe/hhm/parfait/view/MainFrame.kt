/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.view

import net.miginfocom.swing.MigLayout
import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.JMenu


class MainFrame : JFrame() {
    private val fileMenu = JMenu()

    init {
        setDefaultCloseOperation(EXIT_ON_CLOSE)
        size = Dimension(1366, 768)
        setLocationRelativeTo(null)
        layout = MigLayout("al center center")
        add(LoginForm())
    }
}