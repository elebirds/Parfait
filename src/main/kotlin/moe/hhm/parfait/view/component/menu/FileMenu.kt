/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.view.component.menu

import moe.hhm.parfait.infra.db.DatabaseConnectionState
import moe.hhm.parfait.infra.db.DatabaseFactory
import moe.hhm.parfait.utils.i18n.I18nUtils
import moe.hhm.parfait.utils.i18n.I18nUtils.bindText
import moe.hhm.parfait.view.action.DatabaseAction
import java.awt.Toolkit
import java.awt.event.KeyEvent
import javax.swing.JMenu
import javax.swing.JMenuItem
import javax.swing.JOptionPane
import javax.swing.KeyStroke
import kotlin.system.exitProcess

class FileMenu : JMenu() {
    private val open = JMenuItem().apply {
        bindText(this, "menu.open")
        accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().menuShortcutKeyMask)
        setMnemonic('O')
        addActionListener {
            if (DatabaseFactory.connectionState.value !is DatabaseConnectionState.Disconnected) {
                JOptionPane.showMessageDialog(
                    null,
                    I18nUtils.getText("error.needDisconnect"),
                    I18nUtils.getText("error.connection"),
                    JOptionPane.ERROR_MESSAGE
                )
                return@addActionListener
            }
            DatabaseAction.openStandaloneChooser()
        }
    }
    private val close = JMenuItem().apply {
        bindText(this, "menu.close")
        accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().menuShortcutKeyMask)
        setMnemonic('C')
        addActionListener {
            DatabaseFactory.disconnect()
        }
    }
    private val detail = JMenuItem().apply {
        bindText(this, "menu.detail")
        accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_D, Toolkit.getDefaultToolkit().menuShortcutKeyMask)
        setMnemonic('D')
        addActionListener {
            // TODO: UI展示连接详情
            JOptionPane.showMessageDialog(
                null,
                "当前连接状态: ${DatabaseFactory.connectionState.value}",
                I18nUtils.getText("menu.detail"),
                JOptionPane.INFORMATION_MESSAGE
            )
        }
    }
    private val setting = JMenuItem().apply {
        bindText(this, "menu.setting")
        accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_T, Toolkit.getDefaultToolkit().menuShortcutKeyMask)
        setMnemonic('T')
        addActionListener { }
    }
    private val exit = JMenuItem().apply {
        bindText(this, "menu.exit")
        accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().menuShortcutKeyMask)
        setMnemonic('Q')
        addActionListener {
            exitProcess(0)
        }
    }

    init {
        setMnemonic('F')
        bindText(this, "menu.file")

        add(open)
        add(close)
        add(detail)
        addSeparator()
        add(setting)
        addSeparator()
        add(exit)
    }
}