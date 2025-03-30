package cc.eleb.parfait.infra.db

import cc.eleb.parfait.infra.db.certificate.CertificateDatas
import cc.eleb.parfait.infra.db.certificate.CertificateRecords
import cc.eleb.parfait.infra.db.certificate.CertificateTemplates
import cc.eleb.parfait.infra.db.gpa.GpaStandards
import cc.eleb.parfait.infra.db.gpa.GradePointMappings
import cc.eleb.parfait.infra.db.student.Students
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
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
            Database.connect(
                driver = "org.sqlite.JDBC",
                url = "jdbc:sqlite:parfait.db",
                user = "",
                password = ""
            )
        }
    }

    private fun createTables() {
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
        initializeDefaultData()
    }

    private fun initializeDefaultData() {
        transaction {
            GpaStandards.init()
            CertificateTemplates.init();
        }
    }
}