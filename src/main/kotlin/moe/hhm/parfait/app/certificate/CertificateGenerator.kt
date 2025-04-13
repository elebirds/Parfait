/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.app.certificate

import com.deepoove.poi.XWPFTemplate
import moe.hhm.parfait.app.service.CertificateDataService
import moe.hhm.parfait.dto.StudentDTO
import moe.hhm.parfait.exception.BusinessException
import moe.hhm.parfait.ui.component.dialog.CertificateGenerateDialog
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

/**
 * 证书生成器
 * 
 * 负责根据模板生成证书文档
 */
class CertificateGenerator : KoinComponent {
    private val dataService: CertificateDataService by inject()
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val modelBuilder = TemplateModelBuilder()
    
    /**
     * 生成证书
     * 
     * @param params 证书生成参数
     * @param student 学生信息
     * @param outputFile 输出文件
     */
    suspend fun generateCertificate(
        params: CertificateGenerateDialog.CertificateGenerationParams,
        student: StudentDTO,
        outputFile: File
    ) {
        // 1. 获取模板输入流
        val templateInputStream = getTemplateInputStream(params.template.contentPath)
        
        // 2. 编译模板
        val template = XWPFTemplate.compile(templateInputStream)
        // 3. 收集模板中的变量标签
        val variableNames = template.elementTemplates.map { it.variable() }.toSet()
        // 4. 构建数据模型
        val dataModel = modelBuilder.buildModel(params, student, variableNames)

        // 5. 渲染模板
        template.render(dataModel)

        // 6. 保存到输出文件
        val outputStream = FileOutputStream(outputFile)
        template.writeAndClose(outputStream)
        logger.info("证书生成完成: ${outputFile.absolutePath}")
    }
    
    /**
     * 获取模板输入流
     */
    private suspend fun getTemplateInputStream(contentPath: String): InputStream {
        return when {
            // JAR包内资源
            contentPath.startsWith("jar::") -> {
                val resourcePath = contentPath.substring(5)
                javaClass.classLoader.getResourceAsStream("certificate/$resourcePath")
                    ?: throw BusinessException("无法找到内置资源: $resourcePath")
            }
            
            // 数据库中存储的模板
            contentPath.startsWith("db::") -> {
                val uuid = UUID.fromString(contentPath.substring(4))
                val templateData = dataService.getByUUID(uuid)
                    ?: throw BusinessException("无法找到数据库模板数据: $uuid")
                templateData.getStream()
            }
            
            // 本地文件
            else -> {
                val file = File(contentPath)
                if (!file.exists() || !file.isFile) {
                    throw BusinessException("无法找到本机模板文件: $contentPath，请确认文件是否存在")
                }
                FileInputStream(file)
            }
        }
    }
} 