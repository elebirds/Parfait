/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.swing.Swing
import moe.hhm.parfait.infra.db.DatabaseConnectionState
import moe.hhm.parfait.infra.db.DatabaseFactory
import moe.hhm.parfait.infra.i18n.I18nUtils.bindTitle
import moe.hhm.parfait.ui.component.menu.MainMenuBar
import moe.hhm.parfait.ui.panel.LoadingPanel
import moe.hhm.parfait.ui.panel.MainPanel
import moe.hhm.parfait.ui.panel.WelcomePanel
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
        size = Dimension(1250, 750)
        setLocationRelativeTo(null)
        jMenuBar = menuBar

        // 设置国际化标题
        bindTitle(this, "app.title")

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