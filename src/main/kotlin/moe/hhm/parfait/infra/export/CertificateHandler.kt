/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.infra.export

import com.deepoove.poi.XWPFTemplate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moe.hhm.parfait.app.service.CertificateDataService
import moe.hhm.parfait.app.service.CertificateTemplateService
import moe.hhm.parfait.app.service.TermService
import moe.hhm.parfait.dto.GpaStandardDTO
import moe.hhm.parfait.dto.StudentDTO
import moe.hhm.parfait.dto.simpleMean
import moe.hhm.parfait.dto.weightedMean
import moe.hhm.parfait.exception.BusinessException
import moe.hhm.parfait.infra.i18n.I18nManager
import java.io.File
import java.util.UUID

class CertificateHandler(
    private val dataService: CertificateDataService,
    private val templateService: CertificateTemplateService,
    private val termService: TermService
) {
    suspend fun getTemplate(uuid: UUID, contentPath: String): XWPFTemplate {
        val (prefix, suffix) = contentPath.split("::")
        val stream = when (prefix) {
            "jar" -> {
                this::class.java.classLoader.getResourceAsStream("certificate/$suffix") ?: throw BusinessException("Jar内置模板${suffix}不存在")
            }
            "db" -> {
                dataService.getByUUID(uuid)?.getStream() ?: throw BusinessException("对应数据库模板不存在")
            }
            "file" -> {
                val file = File(suffix)
                if (!file.exists()) throw BusinessException("文件模板${suffix}不存在")
                file.inputStream()
            }
            else -> throw BusinessException("未知的证书模板类型: $prefix")
        }
        return XWPFTemplate.compile(stream) ?: throw BusinessException("证书模板${contentPath}编译失败")
    }

    suspend fun getTerm() {
        I18nManager.Language.entries.forEach {

        }
    }

    suspend fun exportToFile(
        template: XWPFTemplate,
        studentDTO: StudentDTO,
        gpaStandardDTO: GpaStandardDTO?,
        outputPath: String
    ): File {
        //template.elementTemplates.forEach {
        //    it.
        //}
        val file = File(outputPath)
        if (!file.parentFile.exists()) file.parentFile.mkdirs()
        val data = hashMapOf<String, Any>(
            "studentId" to studentDTO.studentId,
            "name" to studentDTO.name,
            "weightMean" to studentDTO.scores.weightedMean(),
            "simpleMean" to studentDTO.scores.simpleMean(),

        )
        template.render(data).writeToFile(outputPath)
        return file
    }
}