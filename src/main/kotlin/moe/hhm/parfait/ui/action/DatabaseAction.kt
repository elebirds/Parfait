/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.action

import moe.hhm.parfait.infra.db.DatabaseConnectionConfig
import moe.hhm.parfait.infra.db.DatabaseFactory
import moe.hhm.parfait.infra.db.STANDALONE_DB_SUFFIX
import moe.hhm.parfait.infra.i18n.I18nUtils
import java.io.File
import javax.swing.JFileChooser
import javax.swing.JOptionPane
import javax.swing.filechooser.FileFilter

object DatabaseAction {
    fun openStandaloneChooser() {
        val fc = JFileChooser().apply {
            fileFilter = object : FileFilter() {
                override fun accept(f: File): Boolean {
                    return f.isDirectory || f.name.endsWith(".pardb")
                }

                override fun getDescription(): String = "Parfait Standalone Data File (*.pardb)"
            }
            isMultiSelectionEnabled = false
            fileSelectionMode = JFileChooser.FILES_ONLY
        }
        if (fc.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) return

        // 确保文件名以 .pardb 结尾
        var path = fc.selectedFile.absolutePath
        if (!fc.selectedFile.absolutePath.endsWith(STANDALONE_DB_SUFFIX)) path += STANDALONE_DB_SUFFIX
        DatabaseFactory.connect(DatabaseConnectionConfig.standalone(path))
    }

    fun validateOnlineParams(
        address: String,
        user: String,
        password: String,
        databaseName: String
    ) {
        // 检测地址是否合法
        val a = address.split(":")
        if (a.size != 2) {
            throw IllegalArgumentException(I18nUtils.getFormattedText("database.invalidAddress", a))
        } 
        // IP 地址是否合法，需要支持域名,localhost,127.0.0.1等
        val ip = a[0]
        if (!ip.matches(Regex("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) && !ip.matches(Regex("[a-zA-Z0-9.-]+"))) {
            throw IllegalArgumentException(I18nUtils.getFormattedText("database.illegalIp", ip))
        }
        // 端口是否合法
        val port = a[1].toInt()
        if (port < 0 || port > 65535) {
            throw IllegalArgumentException(I18nUtils.getFormattedText("database.invalidPort", port))
        }
        // 数据库名是否合法
        if (databaseName.isEmpty()) {
            throw IllegalArgumentException(I18nUtils.getFormattedText("database.invalidDatabaseName", databaseName))
        }
        
        // 用户名是否合法
        if (user.isEmpty()) {
            throw IllegalArgumentException(I18nUtils.getText("database.invalidUserName"))
        }
        // 密码允许为空
    }

    fun connectOnline(
        address: String,
        user: String,
        password: String,
        databaseName: String
    ) {
        try {
            validateOnlineParams(address, user, password, databaseName)
            val a = address.split(":")
            val config = DatabaseConnectionConfig.online(
                host = a[0],
                port = a[1].toInt(),
                user = user,
                password = password,
                databaseName = databaseName
            )
            DatabaseFactory.connect(config)
        }catch (e: Exception){
            // 显示连接错误
            JOptionPane.showMessageDialog( // TODO: 需要国际化
                null,
                I18nUtils.getText("database.failconnet") ,
                I18nUtils.getText("database.connectError"),
                JOptionPane.ERROR_MESSAGE
            )
        }
    }
}