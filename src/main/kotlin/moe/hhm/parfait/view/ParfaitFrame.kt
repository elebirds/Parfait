/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.view

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.swing.Swing
import moe.hhm.parfait.infra.db.DatabaseConnectionState
import moe.hhm.parfait.infra.db.DatabaseFactory
import moe.hhm.parfait.view.panel.LoadingPanel
import moe.hhm.parfait.view.panel.MainPanel
import moe.hhm.parfait.view.panel.WelcomePanel
import moe.hhm.parfait.view.component.menu.MainMenuBar
import net.miginfocom.swing.MigLayout
import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.JPanel

class ParfaitFrame : JFrame() {
    private val menuBar = MainMenuBar()
    private var contentPane = JPanel(MigLayout("al center center"))
    private val welcomePanel = WelcomePanel()
    private val loadingPanel = LoadingPanel()
    private val mainPanel = MainPanel()
    private val coroutineScope = CoroutineScope(Dispatchers.Swing + SupervisorJob())

    init {
        setDefaultCloseOperation(EXIT_ON_CLOSE)
        size = Dimension(1366, 768)
        setLocationRelativeTo(null)
        jMenuBar = menuBar

        add(contentPane)

        // 设置内容面板
        contentPane.add(welcomePanel)

        // 监听连接状态变化
        DatabaseFactory.connectionState
            .onEach { state ->
                contentPane.removeAll()
                contentPane.add(
                    when (state) {
                        is DatabaseConnectionState.Connected -> mainPanel
                        is DatabaseConnectionState.Disconnected -> welcomePanel
                        is DatabaseConnectionState.Connecting -> loadingPanel
                    }
                )
                contentPane.revalidate()
                contentPane.repaint()
            }
            .launchIn(coroutineScope)
    }
}