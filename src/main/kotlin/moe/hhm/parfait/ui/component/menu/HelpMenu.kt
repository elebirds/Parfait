/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.component.menu

import com.formdev.flatlaf.FlatClientProperties
import moe.hhm.parfait.infra.i18n.I18nUtils
import moe.hhm.parfait.infra.i18n.I18nUtils.bindText
import moe.hhm.parfait.utils.VersionUtils
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

class HelpMenu : JMenu() {
    private val about = JMenuItem().apply {
        bindText(this, "menu.about")
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
                    I18nUtils.getText("about.description"),
                    VersionUtils.getFullInfo(),
                    " ",
                    "Copyright 2023-$currentYear @elebirds, @MidRatKing, @poloyang.",
                    "All rights reserved.",
                    linkLabel,
                    I18nUtils.getFormattedText("about.runtime", javaVersion, osName),
                ),
                I18nUtils.getText("about.title"),
                JOptionPane.PLAIN_MESSAGE
            )
        }
    }

    init {
        setMnemonic('C')

        bindText(this, "menu.help")

        add(about)
    }
}