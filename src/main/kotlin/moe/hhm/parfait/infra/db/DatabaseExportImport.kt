/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.infra.db

import kotlinx.coroutines.runBlocking
import moe.hhm.parfait.app.service.*
import moe.hhm.parfait.exception.BusinessException
import moe.hhm.parfait.infra.i18n.I18nUtils
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.LoggerFactory
import java.io.File
import javax.swing.JOptionPane

/**
 * 数据库导入导出选项
 */
enum class ImportExportOption(val i18nKey: String) {
    ALL("database.export.option.all"),
    STUDENTS("database.export.option.students"),
    TERMS("database.export.option.terms"),
    GPA_STANDARDS("database.export.option.gpa_standards"),
    CERTIFICATE_TEMPLATES("database.export.option.certificate_templates"),
    CERTIFICATE_RECORDS("database.export.option.certificate_records"),
}

/**
 * 数据库导入导出工具类
 *
 * 新的实现方式：
 * 1. 使用 Exposed 框架进行数据库操作
 * 2. 利用现有的 Service 层获取和保存数据
 * 3. 只支持在线模式导出到单机文件和从单机文件导入
 */
object DatabaseExportImport : KoinComponent {
    private val logger = LoggerFactory.getLogger(this::class.java)

    // 服务依赖注入
    private val studentService: StudentService by inject()
    private val termService: TermService by inject()
    private val gpaStandardService: GpaStandardService by inject()
    private val certificateTemplateService: CertificateTemplateService by inject()
    private val certificateRecordService: CertificateRecordService by inject()
    private val certificateDataService: CertificateDataService by inject()

    /**
     * 判断当前环境是否支持导出操作
     *
     * @return 支持导出的检查结果，包含是否支持及原因
     */
    fun canExport(): Pair<Boolean, String?> {
        // 检查当前连接状态
        val currentState = DatabaseFactory.connectionState.value
        if (currentState !is DatabaseConnectionState.Connected) {
            return Pair(false, I18nUtils.getText("database.export.error.notConnected"))
        }

        // 获取当前连接配置
        val currentConfig = DatabaseFactory.getCurrentConfig()
            ?: return Pair(false, I18nUtils.getText("database.export.error.noConfig"))

        // 只支持在线模式导出
        return when (currentConfig.mode) {
            DatabaseFactoryMode.STANDALONE ->
                Pair(false, I18nUtils.getText("database.export.error.notSupported.standalone"))

            DatabaseFactoryMode.ONLINE ->
                Pair(true, null)
        }
    }

    /**
     * 判断当前环境是否支持导入操作
     *
     * @return 支持导入的检查结果，包含是否支持及原因
     */
    fun canImport(): Pair<Boolean, String?> {
        // 检查当前连接状态
        val currentState = DatabaseFactory.connectionState.value
        if (currentState !is DatabaseConnectionState.Connected) {
            return Pair(false, I18nUtils.getText("database.import.error.notConnected"))
        }

        // 获取当前连接配置
        val currentConfig = DatabaseFactory.getCurrentConfig()
            ?: return Pair(false, I18nUtils.getText("database.import.error.noConfig"))

        // 只支持在线模式导入
        return when (currentConfig.mode) {
            DatabaseFactoryMode.STANDALONE ->
                Pair(false, I18nUtils.getText("database.import.error.notSupported.standalone"))

            DatabaseFactoryMode.ONLINE ->
                Pair(true, null)
        }
    }

    /**
     * 将当前数据库导出到指定文件
     * 仅支持在线模式下导出到SQLite文件
     *
     * @param targetFile 目标文件
     * @return 是否成功
     */
    fun exportDatabase(targetFile: File): Boolean {
        logger.info("开始导出数据库到文件: ${targetFile.absolutePath}")

        try {
            // 再次检查是否支持导出
            val (canExport, reason) = canExport()
            if (!canExport) {
                throw BusinessException(reason ?: "database.export.error.unknown")
            }

            // 获取当前连接配置
            val currentConfig = DatabaseFactory.getCurrentConfig()
                ?: throw BusinessException("database.export.error.noConfig")

            // 确保目标文件所在目录存在
            targetFile.parentFile?.mkdirs()

            // 如果文件已存在，先删除
            if (targetFile.exists()) {
                targetFile.delete()
            }

            // 创建临时SQLite配置
            val tempConfig = DatabaseConnectionConfig.standalone(targetFile.absolutePath)
            exportDataToSQLite(tempConfig)

            logger.info("数据库导出成功")
            return true
        } catch (e: Exception) {
            logger.error("数据库导出失败", e)
            JOptionPane.showMessageDialog(
                null,
                I18nUtils.getFormattedText("database.export.error.msg", e.localizedMessage),
                I18nUtils.getText("database.export.error.title"),
                JOptionPane.ERROR_MESSAGE
            )
            return false
        } finally {
            DatabaseFactory.forceReconnect()
        }
    }

    /**
     * 从指定文件导入数据到当前数据库
     * 仅支持在线模式下从SQLite文件导入
     *
     * @param sourceFile 源文件
     * @param options 导入选项
     * @return 是否成功
     */
    fun importDatabase(sourceFile: File, options: List<ImportExportOption>): Boolean {
        logger.info("开始从文件导入数据库: ${sourceFile.absolutePath}, 选项: $options")

        try {
            // 再次检查是否支持导入
            val (canImport, reason) = canImport()
            if (!canImport) {
                throw BusinessException(reason ?: "database.import.error.unknown")
            }

            // 检查源文件是否存在
            if (!sourceFile.exists() || !sourceFile.isFile) {
                throw BusinessException("database.import.error.fileNotFound", sourceFile.absolutePath)
            }

            // 创建临时SQLite配置
            val tempConfig = DatabaseConnectionConfig.standalone(sourceFile.absolutePath)

            // 创建临时SQLite连接
            val tempDb = DatabaseFactory.temporaryConnect(tempConfig)
            if (tempDb == null) {
                throw BusinessException("database.import.error.tempDbConnection")
            }

            try {
                // 在事务中执行导入操作
                importDataFromSQLite(tempDb, options)
            } finally {
                // 关闭临时连接
                tempDb.connector().close()
            }

            logger.info("数据库导入成功")
            return true
        } catch (e: Exception) {
            logger.error("数据库导入失败", e)
            JOptionPane.showMessageDialog(
                null,
                I18nUtils.getFormattedText("database.import.error.msg", e.localizedMessage),
                I18nUtils.getText("database.import.error.title"),
                JOptionPane.ERROR_MESSAGE
            )

            return false
        } finally {
            DatabaseFactory.forceReconnect()
        }
    }

    /**
     * 将当前数据库数据导出到SQLite临时数据库
     */
    private fun exportDataToSQLite(tempConfig: DatabaseConnectionConfig) {
        // 获取当前所有数据
        val students = runBlocking { studentService.getAllStudents() }
        val terms = runBlocking { termService.getAll() }
        val gpaStandards = runBlocking { gpaStandardService.getAllGpaStandards() }
        val certificateTemplates = runBlocking { certificateTemplateService.getCertificates() }
        val certificateRecords = runBlocking { certificateRecordService.getAll() }
        val certificateDatas = runBlocking { certificateDataService.getAll() }


        // 创建临时SQLite连接
        val tempDb = DatabaseFactory.temporaryConnect(tempConfig)
        if (tempDb == null) {
            throw BusinessException("database.export.error.tempDbConnection")
        }
        try {
            // 先在临时数据库中创建表结构
            transaction(tempDb) {
                // 创建表结构
                DatabaseFactory.createTablesInCurrentTransaction()
            }

            // 导入数据到临时数据库
            transaction(tempDb) {
                runBlocking {
                    // 导入GPA标准
                    for (gpa in gpaStandards) {
                        gpaStandardService.addGpaStandard(gpa)
                    }

                    // 导入术语
                    for (term in terms) {
                        termService.add(term)
                    }

                    // 导入证书模板
                    for (template in certificateTemplates) {
                        certificateTemplateService.add(template)
                    }

                    // 导入学生数据
                    for (student in students) {
                        studentService.addStudent(student)
                    }

                    // 导入证书记录
                    for (record in certificateRecords) {
                        certificateRecordService.add(record)
                    }

                    // 导入证书记录
                    for (data in certificateDatas) {
                        certificateDataService.add(data)
                    }
                }
            }
        } catch (e: Exception) {
            logger.warn("未预期的数据库导出错误: ${e.message}")
        }
    }

    /**
     * 从SQLite临时数据库导入数据到当前数据库
     */
    private fun importDataFromSQLite(tempDb: Database, options: List<ImportExportOption>) {
        val importAll = options.contains(ImportExportOption.ALL)

        // 临时切换数据库连接并导入数据
        val originalDb = transaction { db }
        try {
            // 从临时数据库读取数据
            val students = if (importAll || options.contains(ImportExportOption.STUDENTS)) {
                transaction(tempDb) {
                    runBlocking { studentService.getAllStudents() }
                }
            } else {
                emptyList()
            }

            val terms = if (importAll || options.contains(ImportExportOption.TERMS)) {
                transaction(tempDb) {
                    runBlocking { termService.getAll() }
                }
            } else {
                emptyList()
            }

            val gpaStandards = if (importAll || options.contains(ImportExportOption.GPA_STANDARDS)) {
                transaction(tempDb) {
                    runBlocking { gpaStandardService.getAllGpaStandards() }
                }
            } else {
                emptyList()
            }

            val templates = if (importAll || options.contains(ImportExportOption.CERTIFICATE_TEMPLATES)) {
                transaction(tempDb) {
                    runBlocking { certificateTemplateService.getCertificates() }
                }
            } else {
                emptyList()
            }

            val records = if (importAll || options.contains(ImportExportOption.CERTIFICATE_RECORDS)) {
                transaction(tempDb) {
                    runBlocking { certificateRecordService.getAll() }
                }
            } else {
                emptyList()
            }

            // 切换回原始数据库并导入数据
            transaction(originalDb) {
                runBlocking {
                    // 导入GPA标准
                    for (gpa in gpaStandards) {
                        try {
                            gpaStandardService.addGpaStandard(gpa)
                        } catch (e: Exception) {
                            logger.warn("导入GPA标准失败: ${gpa.name}, ${e.message}")
                        }
                    }

                    // 导入术语
                    for (term in terms) {
                        try {
                            termService.add(term)
                        } catch (e: Exception) {
                            logger.warn("导入术语失败: ${term.field}, ${e.message}")
                        }
                    }

                    // 导入证书模板
                    for (template in templates) {
                        try {
                            certificateTemplateService.add(template)
                        } catch (e: Exception) {
                            logger.warn("导入证书模板失败: ${template.name}, ${e.message}")
                        }
                    }

                    // 导入学生数据
                    for (student in students) {
                        try {
                            studentService.addStudent(student)
                        } catch (e: Exception) {
                            logger.warn("导入学生数据失败: ${student.name}, ${e.message}")
                        }
                    }

                    // 导入证书记录
                    for (record in records) {
                        try {
                            certificateRecordService.add(record)
                        } catch (e: Exception) {
                            logger.warn("导入证书记录失败: ${record.uuid}, ${e.message}")
                        }
                    }
                }
            }
        } finally {
            // 确保恢复原始数据库连接
            transaction(originalDb) {
                // 恢复原始连接
            }
        }
    }
} 