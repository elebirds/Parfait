/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.app.certificate

import com.deepoove.poi.XWPFTemplate
import com.deepoove.poi.config.Configure
import com.deepoove.poi.util.RegexUtils
import moe.hhm.parfait.app.service.CertificateDataService
import moe.hhm.parfait.app.service.CertificateRecordService
import moe.hhm.parfait.app.term.TermParser
import moe.hhm.parfait.app.term.TermProcessor
import moe.hhm.parfait.dto.CertificateRecordDTO
import moe.hhm.parfait.dto.CertificateTemplateDTO
import moe.hhm.parfait.dto.StudentDTO
import moe.hhm.parfait.exception.BusinessException
import moe.hhm.parfait.infra.i18n.I18nUtils
import moe.hhm.parfait.ui.component.dialog.CertificateGenerateDialog
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.swing.JOptionPane

/**
 * 证书生成器
 * 
 * 负责根据模板生成证书文档
 */
class CertificateGenerator : KoinComponent {
    private val dataService: CertificateDataService by inject()
    private val recordService: CertificateRecordService by inject()
    private val termParser: TermParser by inject()
    private val modelBuilder: TemplateModelBuilder by inject()
    private val builder = Configure.builder().apply {
        buildGrammerRegex(RegexUtils.createGeneral("{{", "}}"));
    }


    /**
     * 构建证书文件名
     */
    private fun buildCertificateFileName(student: StudentDTO, template: CertificateTemplateDTO): String {
        val timestamp = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
        return "/${student.studentId}_${student.name}_${template.name}_$timestamp.docx"
    }

    /**
     * 生成证书
     * 
     * @param params 证书生成参数
     */
    suspend fun generateCertificate(
        params: CertificateGenerateDialog.CertificateGenerationParams
    ) {
        // 1. 获取模板输入流并读取为字节数组（只读取一次文件）
        val templateInputStream = getTemplateInputStream(params.template.contentPath)
        val templateBytes = templateInputStream.readBytes()
        templateInputStream.close()
        
        // 2. 使用第一个模板分析获取变量
        val analysisTemplate = XWPFTemplate.compile(templateBytes.inputStream(), builder.build())

        // 3. 收集模板中的变量标签 并分离{{和}}生成术语标签
        val variableNames = analysisTemplate.elementTemplates.map { it.variable().replace("{{", "").replace("}}", "") }.toSet()
        val termPairs = variableNames.mapNotNull {
            val res = termParser.parse(it)
            if(res != null) {
                it to res
            } else {
                null
            }
        }
        val remainingTags = variableNames - termPairs.map { it.first }
        val termExpressions = termPairs.map { it.second }
        
        // 关闭分析模板
        analysisTemplate.close()

        var successCount = 0
        val errorList = mutableListOf<String>()
        // 4. 为每个学生单独生成证书
        params.students.forEach { student ->
            try {
                // 为每个学生从字节数组重新创建模板（不需要重新读取文件）
                val studentTemplateStream = templateBytes.inputStream()
                val template = XWPFTemplate.compile(studentTemplateStream, builder.build())
                
                try {
                    // 5. 解析变量标签
                    val models = modelBuilder.buildModel(
                        student = student,
                        gpaStandard = params.gpaStandard,
                        remainingTags = remainingTags,
                        termExpressions = termExpressions
                    )
                    
                    // 6. 渲染并写入文件
                    val outputFilePath = params.outputDirectory.absolutePath + buildCertificateFileName(student, params.template)
                    template.render(models).writeToFile(outputFilePath)
                    
                    // 7. 记录证书生成信息
                    recordCertificateGeneration(student, params)

                    successCount++
                } finally {
                    // 确保关闭资源
                    template.close()
                    studentTemplateStream.close()
                }
            } catch (e: Exception) {
                errorList.add("${student.studentId}-${student.name}：" + (e.message ?: "未知错误"))
            }
        }
        JOptionPane.showMessageDialog(
            null, 
            I18nUtils.getFormattedText("certificate.generate.result.detail", successCount, errorList.size, "\n${errorList.joinToString("\n")}"), 
            I18nUtils.getFormattedText("certificate.generate.result.title"), 
            JOptionPane.INFORMATION_MESSAGE
        )
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

    /**
     * 记录证书生成信息
     */
    private suspend fun recordCertificateGeneration(
        student: StudentDTO,
        params: CertificateGenerateDialog.CertificateGenerationParams
    ) {
        try {
            val record = CertificateRecordDTO(
                templateId = params.template.uuid!!,
                issuedDate = LocalDate.now(),
                issuedBy = params.issuer,
                content = "${student.studentId}-${student.name}-${params.template.name}",
                purpose = params.purpose
            )
            recordService.add(record)
        } catch (e: Exception) {
            // 记录错误但不中断主流程
            println("记录证书生成信息失败: ${e.message}")
        }
    }
} 