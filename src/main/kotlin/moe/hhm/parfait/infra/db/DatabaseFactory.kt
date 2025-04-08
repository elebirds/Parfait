/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.infra.db

import moe.hhm.parfait.infra.db.certificate.CertificateDatas
import moe.hhm.parfait.infra.db.certificate.CertificateRecords
import moe.hhm.parfait.infra.db.certificate.CertificateTemplates
import moe.hhm.parfait.infra.db.gpa.GpaStandards
import moe.hhm.parfait.infra.db.gpa.GradePointMappings
import moe.hhm.parfait.infra.db.student.Students
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

object DatabaseFactory {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private var connection: Database? = null
    fun init() {
        // 连接数据库
        connect(isServerMode = false)
        // 创建表
        transaction {
            createTables()
        }
        // 初始化数据
        transaction {
            initializeDefaultData()
        }
    }

    fun connect(isServerMode: Boolean) {
        if (isServerMode) {
            // TODO: MySQL实现
            throw NotImplementedError("C/S模式尚未实现")
        } else {
            connection = Database.connect(
                driver = "org.sqlite.JDBC",
                url = "jdbc:sqlite:parfait.db",
                user = "",
                password = ""
            )
        }
    }

    fun close() {
        connection = null
        logger.info("关闭数据库连接")
    }

    private fun createTables() {
        logger.info("创建数据库表")
        transaction {
            SchemaUtils.create(
                Students,
                GpaStandards,
                GradePointMappings,
                CertificateTemplates,
                CertificateRecords,
                CertificateDatas
            )
        }
    }

    private fun initializeDefaultData() {
        logger.info("初始化默认数据")
        transaction {
            GpaStandards.init()
            CertificateTemplates.init();
        }
    }
}