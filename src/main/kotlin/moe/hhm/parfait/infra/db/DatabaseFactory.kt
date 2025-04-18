/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.infra.db

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import moe.hhm.parfait.infra.db.certificate.CertificateDatas
import moe.hhm.parfait.infra.db.certificate.CertificateRecords
import moe.hhm.parfait.infra.db.certificate.CertificateTemplates
import moe.hhm.parfait.infra.db.gpa.GpaStandards
import moe.hhm.parfait.infra.db.student.Students
import moe.hhm.parfait.infra.db.term.Terms
import moe.hhm.parfait.infra.i18n.I18nUtils
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import javax.swing.JOptionPane
import javax.swing.SwingUtilities

enum class DatabaseFactoryMode {
    ONLINE,
    STANDALONE,
}

data class DatabaseConnectionConfig(
    val mode: DatabaseFactoryMode,
    val host: String,
    val port: Int,
    val user: String,
    val password: String,
    val databaseName: String = ""
) {
    companion object {
        fun standalone(filePath: String): DatabaseConnectionConfig {
            return DatabaseConnectionConfig(
                mode = DatabaseFactoryMode.STANDALONE,
                host = filePath,
                port = 0,
                user = "",
                password = ""
            )
        }

        fun online(
            host: String,
            port: Int,
            user: String,
            password: String,
            databaseName: String
        ): DatabaseConnectionConfig {
            return DatabaseConnectionConfig(
                mode = DatabaseFactoryMode.ONLINE,
                host = host,
                port = port,
                user = user,
                password = password,
                databaseName = databaseName
            )
        }
    }

    fun checkValid(): Boolean {
        return when (mode) {
            DatabaseFactoryMode.STANDALONE -> host.isNotEmpty()
            DatabaseFactoryMode.ONLINE -> host.isNotEmpty() && port > 0 && user.isNotEmpty() && databaseName.isNotEmpty()
        }
    }

    override fun toString(): String {
        return if (mode == DatabaseFactoryMode.STANDALONE) {
            I18nUtils.getFormattedText(
                "database.config.standalone.detail",
                host
            )
        } else {
            I18nUtils.getFormattedText(
                "database.config.online.detail",
                "$host:$port",
                databaseName,
                user,
            )
        }
    }
}

sealed class DatabaseConnectionState(val i18nKey: String) {
    class Connected(val config: DatabaseConnectionConfig) : DatabaseConnectionState("database.state.connected")
    class Disconnected : DatabaseConnectionState("database.state.disconnected")
    class Connecting(val config: DatabaseConnectionConfig) : DatabaseConnectionState("database.state.connecting")
}

fun DatabaseConnectionState.toStr(): String {
    return I18nUtils.getText(i18nKey) + when (this) {
        is DatabaseConnectionState.Connected -> "\n" + config.toString()
        is DatabaseConnectionState.Disconnected -> ""
        is DatabaseConnectionState.Connecting -> "\n" + config.toString()
    }
}

const val STANDALONE_DB_SUFFIX = ".pardb"

object DatabaseFactory {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private var connection: Database? = null
    private val _connectionState = MutableStateFlow<DatabaseConnectionState>(DatabaseConnectionState.Disconnected())
    val connectionState: StateFlow<DatabaseConnectionState> = _connectionState.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO)

    private fun init() {
        // 创建表
        transaction {
            createTables()
        }
        // 初始化数据
        transaction {
            initializeDefaultData()
        }
    }

    fun connect(config: DatabaseConnectionConfig) {
        logger.info("尝试连接数据库：$config")
        if (!config.checkValid()) {
            logger.error("数据库连接配置无效")
            throw IllegalArgumentException("数据库连接配置无效")
        }
        if (_connectionState.value is DatabaseConnectionState.Connecting) {
            logger.warn("数据库连接正在进行中，请稍后再试")
            return
        }
        if (connection != null || _connectionState.value is DatabaseConnectionState.Connected) {
            logger.warn("数据库连接已存在，请先断开连接")
            return
        }
        scope.launch {
            try {
                _connectionState.value = DatabaseConnectionState.Connecting(config)
                connection = when (config.mode) {
                    DatabaseFactoryMode.ONLINE -> Database.connect(
                        driver = "com.mysql.cj.jdbc.Driver",
                        url = "jdbc:mysql://${config.host}:${config.port}/${config.databaseName}?useSSL=false&serverTimezone=UTC",
                        user = config.user,
                        password = config.password
                    )

                    DatabaseFactoryMode.STANDALONE -> Database.connect(
                        driver = "org.sqlite.JDBC",
                        url = "jdbc:sqlite:${config.host}",
                        user = config.user,
                        password = config.password
                    )
                }
                init()
                _connectionState.value = DatabaseConnectionState.Connected(config)
                logger.info("数据库连接成功")
            } catch (e: Throwable) {
                logger.error("数据库连接失败", e)
                disconnect()
                SwingUtilities.invokeLater {
                    JOptionPane.showMessageDialog(
                        null,
                        I18nUtils.getFormattedText("error.database.connection.detail", e.message ?: ""),
                        I18nUtils.getText("error.database.connection.title"),
                        JOptionPane.ERROR_MESSAGE
                    )
                }
            }
        }
    }

    fun disconnect() {
        _connectionState.value = DatabaseConnectionState.Disconnected()
        connection = null
        logger.info("关闭数据库连接")
    }

    private fun createTables() {
        logger.info("创建数据库表")
        transaction {
            SchemaUtils.create(
                Students,
                GpaStandards,
                CertificateTemplates,
                CertificateRecords,
                CertificateDatas,
                Terms
            )
        }
    }

    private fun initializeDefaultData() {
        logger.info("初始化默认数据")
        transaction {
            GpaStandards.init()
            CertificateTemplates.init()
            Terms.init()
        }
    }
}