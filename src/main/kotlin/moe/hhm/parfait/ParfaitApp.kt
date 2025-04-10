/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait

import moe.hhm.parfait.di.appModule
import moe.hhm.parfait.di.domainModule
import moe.hhm.parfait.di.infrastructureModule
import moe.hhm.parfait.di.presentationModule
import moe.hhm.parfait.ui.lib.FlatLafUtils
import moe.hhm.parfait.utils.i18n.I18nUtils
import moe.hhm.parfait.view.ParfaitFrame
import org.koin.core.context.startKoin
import org.slf4j.LoggerFactory
import javax.swing.SwingUtilities

private val logger = LoggerFactory.getLogger(ParfaitApp::class.java)
const val PARFAIT_FULL_NAME = "Parfait"

/**
 * Parfait 应用程序入口
 *
 * @author elebird
 */
fun main(args: Array<String>) {
    try {
        // 初始化依赖注入
        startKoin {
            appModule
            domainModule
            infrastructureModule
            presentationModule
        }
        logger.info("Koin依赖注入初始化完成")
        
        // 初始化国际化
        I18nUtils.init()
        logger.info("国际化支持初始化完成")
        
        // 启动应用
        ParfaitApp().start()
    } catch (e: Exception) {
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
            
            // 创建并显示主窗口
            val mainFrame = ParfaitFrame()
            mainFrame.isVisible = true
        }
    }
}
