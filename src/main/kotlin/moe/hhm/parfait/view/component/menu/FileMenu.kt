/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.view.component.menu

import moe.hhm.parfait.infra.db.DatabaseConnectionState
import moe.hhm.parfait.infra.db.DatabaseFactory
import moe.hhm.parfait.view.action.DatabaseAction
import java.awt.Toolkit
import java.awt.event.KeyEvent
import javax.swing.JMenu
import javax.swing.JMenuItem
import javax.swing.JOptionPane
import javax.swing.KeyStroke
import kotlin.system.exitProcess

class FileMenu : JMenu("文件") {
    private val open = JMenuItem("建立单机连接").apply {
        accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().menuShortcutKeyMask)
        setMnemonic('O')
        addActionListener {
            if (DatabaseFactory.connectionState.value !is DatabaseConnectionState.Disconnected) {
                JOptionPane.showMessageDialog(null, "请确保当前在未连接状态", "错误", JOptionPane.ERROR_MESSAGE)
                return@addActionListener
            }
            DatabaseAction.openStandaloneChooser()
        }
    }
    private val close = JMenuItem("关闭连接").apply {
        accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().menuShortcutKeyMask)
        setMnemonic('C')
        addActionListener {
            DatabaseFactory.disconnect()
        }
    }
    private val detail = JMenuItem("连接详情").apply {
        accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_D, Toolkit.getDefaultToolkit().menuShortcutKeyMask)
        setMnemonic('D')
        addActionListener {
            // TODO: UI展示连接详情
            JOptionPane.showMessageDialog(
                null,
                "当前连接状态: ${DatabaseFactory.connectionState.value}",
                "连接详情",
                JOptionPane.INFORMATION_MESSAGE
            )
        }
    }
    private val setting = JMenuItem("设置").apply {
        accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_T, Toolkit.getDefaultToolkit().menuShortcutKeyMask)
        setMnemonic('T')
        addActionListener { }
    }
    private val exit = JMenuItem("退出").apply {
        accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().menuShortcutKeyMask)
        setMnemonic('Q')
        addActionListener {
            exitProcess(0)
        }
    }

    init {
        setMnemonic('F')
        add(open)
        add(close)
        add(detail)
        addSeparator()
        add(setting)
        addSeparator()
        add(exit)
    }
}