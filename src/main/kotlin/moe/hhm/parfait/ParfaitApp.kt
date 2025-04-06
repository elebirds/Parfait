/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait

import javafx.application.Application
import javafx.application.Application.launch
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import moe.hhm.parfait.di.appModule
import moe.hhm.parfait.di.domainModule
import moe.hhm.parfait.di.infrastructureModule
import moe.hhm.parfait.di.presentationModule
import moe.hhm.parfait.infra.db.DatabaseFactory
import org.koin.core.context.startKoin
import org.slf4j.LoggerFactory

fun main(args: Array<String>) {
    DatabaseFactory.init()
    startKoin {
        appModule
        domainModule
        infrastructureModule
        presentationModule
    }
    launch(ParfaitApp::class.java, *args)
}

class ParfaitApp : Application() {
    private val logger = LoggerFactory.getLogger(ParfaitApp::class.java)

    override fun start(stage: Stage) {
        try {
            logger.info("启动学生成绩管理系统")
            println("学生成绩管理系统初始化成功！")

            // 显示简单窗口以验证JavaFX运行正常
            stage.title = "学生成绩管理系统"
            stage.width = 800.0
            stage.height = 600.0

            // 创建一个简单的根节点
            val root = StackPane()
            val label = Label("学生成绩管理系统初始化成功！")
            label.style = "-fx-font-size: 20px;"
            root.children.add(label)

            // 设置场景
            val scene = Scene(root)
            stage.scene = scene
            stage.show()

        } catch (e: Exception) {
            logger.error("系统启动失败", e)
            println("启动失败: ${e.message}")
        }
    }
}
