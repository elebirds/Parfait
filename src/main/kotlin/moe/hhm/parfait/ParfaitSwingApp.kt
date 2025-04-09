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
import moe.hhm.parfait.infra.db.DatabaseConnectionConfig
import moe.hhm.parfait.infra.db.DatabaseFactory
import moe.hhm.parfait.ui.view.StudentListView
import org.koin.core.context.startKoin
import org.slf4j.LoggerFactory
import javax.swing.SwingUtilities
import javax.swing.UIManager

/**
 * Parfait 应用程序 Swing 版本的入口点
 */
fun main() {
    val logger = LoggerFactory.getLogger("ParfaitSwingApp")

    try {
        // 初始化数据库
        DatabaseFactory.connect(DatabaseConnectionConfig.standalone("parfait.db"))

        // 初始化Koin依赖注入
        startKoin {
            modules(listOf(appModule, domainModule, infrastructureModule, presentationModule))
        }

        // 设置跨平台外观
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

        // 在EDT中启动UI
        SwingUtilities.invokeLater {
            try {
                val mainFrame = StudentListView()
                mainFrame.isVisible = true
                logger.info("学生成绩管理系统 (Swing) 启动成功")
            } catch (e: Exception) {
                logger.error("UI启动失败", e)
            }
        }
    } catch (e: Exception) {
        logger.error("系统初始化失败", e)
    }
} 