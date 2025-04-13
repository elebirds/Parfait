/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.app.certificate

import com.deepoove.poi.data.TextRenderData
import moe.hhm.parfait.app.term.TermProcessor
import moe.hhm.parfait.dto.GpaStandardDTO
import moe.hhm.parfait.dto.StudentDTO
import moe.hhm.parfait.dto.simpleMean
import moe.hhm.parfait.dto.weightedMean
import moe.hhm.parfait.ui.component.dialog.CertificateGenerateDialog
import moe.hhm.parfait.utils.HanziUtils
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.HashMap

/**
 * 模板数据模型构建器
 * 
 * 负责构建POI-TL模板引擎使用的数据模型
 */
class TemplateModelBuilder : KoinComponent {
    private val termProcessor : TermProcessor by inject()
    
    /**
     * 构建完整的数据模型
     */
    fun buildModel(
        params: CertificateGenerateDialog.CertificateGenerationParams,
        student: StudentDTO,
        variableNames: Set<String>
    ): Map<String, Any> {
        val model = HashMap<String, Any>()
        
        // 添加各类数据到模型中
        addStudentInfo(model, student)
        addCertificateInfo(model, params)
        
        if (params.gpaStandard != null) {
            addGpaInfo(model, student, params.gpaStandard)
        }
        
        addTerms(model, variableNames)
        
        return model
    }

    private fun basicInfo() = hashMapOf(
        "year" to LocalDate.now().year,
        "month" to LocalDate.now().monthValue,
        "day" to LocalDate.now().dayOfMonth,
        "source" to "Parfait",
        "version" to "1.0.0"
    )
    
    /**
     * 添加学生信息
     */
    private fun addStudentInfo(model: HashMap<String, Any>, student: StudentDTO) {
        model["studentId"] = student.studentId
        model["name"] = student.name
        model["name_en"] = HanziUtils.hanzi2English(student.name)
        model["department"] = student.department
        model["major"] = student.major
        model["classGroup"] = student.classGroup
        model["grade"] = student.grade.toString()
        model["gender"] = student.gender.toString()
        model["status"] = student.status.toString()
        model["score_weight"] = student.scores.weightedMean()
        model["score_simple"] = student.scores.simpleMean()
    }
    
    /**
     * 添加证书信息
     */
    private fun addCertificateInfo(
        model: HashMap<String, Any>,
        params: CertificateGenerateDialog.CertificateGenerationParams
    ) {
        model["issuer"] = TextRenderData(params.issuer)
        model["purpose"] = TextRenderData(params.purpose)
        model["date"] = TextRenderData(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
        
        // 处理过期时间
        val expiryText = params.expiry?.format(DateTimeFormatter.ISO_LOCAL_DATE) ?: "永久有效"
        model["expiry"] = TextRenderData(expiryText)
        
        // 证书模板信息
        model["templateName"] = TextRenderData(params.template.name)
        model["templateCategory"] = TextRenderData(params.template.category)
    }
    
    /**
     * 添加GPA信息
     */
    private fun addGpaInfo(
        model: HashMap<String, Any>,
        student: StudentDTO,
        gpaStandard: GpaStandardDTO
    ) {
        model["gpa"] = TextRenderData(String.format("%.2f", gpaStandard.mapping.getGpa(student.scores)))
        model["gpaStandard"] = TextRenderData(gpaStandard.name)
    }
    
    /**
     * 添加术语
     */
    private fun addTerms(model: HashMap<String, Any>, variableNames: Set<String>) {
        // 查找所有以"term_"开头的变量
        val termVariables = variableNames.filter { it.startsWith("term_") }
        
        // 添加术语翻译
        for (termVariable in termVariables) {
            val termKey = termVariable.substring(5) // 移除"term_"前缀
            model[termVariable] = TextRenderData("a")
        }
    }
} 