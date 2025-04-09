/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.view.component.menu

import com.formdev.flatlaf.FlatClientProperties
import java.awt.Cursor
import java.awt.Desktop
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import java.time.Year
import javax.swing.JLabel
import javax.swing.JMenu
import javax.swing.JMenuItem
import javax.swing.JOptionPane

class HelpMenu : JMenu("帮助") {
    private val about = JMenuItem("关于").apply {
        this.setMnemonic('A')
        this.addActionListener {
            val titleLabel = JLabel("Parfait Moe").apply {
                putClientProperty(FlatClientProperties.STYLE_CLASS, "h1")
            }

            val link = "https://hhm.moe/"
            val linkLabel = JLabel("<html><a href=\"#\">$link</a></html>").apply {
                cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                addMouseListener(object : MouseAdapter() {
                    override fun mouseClicked(e: MouseEvent) {
                        try {
                            Desktop.getDesktop().browse(URI(link))
                        } catch (_: IOException) {
                        } catch (_: URISyntaxException) {
                        }
                    }
                })
            }

            val currentYear = Year.now()
            val javaVersion = System.getProperty("java.version")
            val osName = System.getProperty("os.name")

            JOptionPane.showMessageDialog(
                this, arrayOf(
                    titleLabel,
                    "简易的学生数据管理系统",
                    " ",
                    "Copyright 2023-$currentYear Elebird(Grow Zheng).",
                    "All rights reserved.",
                    linkLabel,
                    "Running in Java™ SE Runtime Environment (build $javaVersion) on $osName",
                ),
                "关于 Parfait",
                JOptionPane.PLAIN_MESSAGE
            )
        }
    }

    init {
        setMnemonic('C')
        add(about)
    }
}