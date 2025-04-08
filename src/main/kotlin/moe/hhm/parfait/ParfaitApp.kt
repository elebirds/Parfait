/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait

import com.formdev.flatlaf.extras.FlatInspector
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector
import com.formdev.flatlaf.fonts.jetbrains_mono.FlatJetBrainsMonoFont
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont
import com.formdev.flatlaf.themes.FlatMacDarkLaf
import javax.swing.SwingUtilities;
import moe.hhm.parfait.di.appModule
import moe.hhm.parfait.di.domainModule
import moe.hhm.parfait.di.infrastructureModule
import moe.hhm.parfait.di.presentationModule
import moe.hhm.parfait.infra.db.DatabaseFactory
import moe.hhm.parfait.ui.lib.FlatLafUtils
import moe.hhm.parfait.view.MainFrame
import org.koin.core.context.startKoin
import org.slf4j.LoggerFactory
import java.awt.Font
import javax.swing.UIManager

private val logger = LoggerFactory.getLogger(ParfaitApp::class.java)
const val PARFAIT_FULL_NAME = "Parfait"

/**
 * Parfait 应用程序入口
 *
 * @author elebird
 */
fun main(args: Array<String>) {
    try {
        DatabaseFactory.init()
        startKoin {
            appModule
            domainModule
            infrastructureModule
            presentationModule
        }
        logger.info("Koin依赖注入初始化完成")
        ParfaitApp().start()
    }catch (e: Exception) {
        logger.error("应用程序启动失败", e)
        return
    }
}

class ParfaitApp {
    fun start() {
        FlatLafUtils.specialSystemConfigure()
        SwingUtilities.invokeLater {
            FlatLafUtils.preferenceInit()
            FlatLafUtils.fontInit()
            FlatLafUtils.setLookAndFeel()
            FlatLafUtils.installInspector()
            val mainFrame = MainFrame()
            mainFrame.setLocationRelativeTo(null)
            mainFrame.isVisible = true
        }
    }
}
