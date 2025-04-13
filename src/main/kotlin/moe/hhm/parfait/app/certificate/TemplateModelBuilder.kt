/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.app.certificate

import moe.hhm.parfait.app.term.StudentContextProvider
import moe.hhm.parfait.app.term.TermExpression
import moe.hhm.parfait.app.term.TermProcessor
import moe.hhm.parfait.dto.GpaStandardDTO
import moe.hhm.parfait.dto.StudentDTO
import moe.hhm.parfait.dto.simpleMean
import moe.hhm.parfait.dto.weightedMean
import moe.hhm.parfait.infra.i18n.I18nManager
import moe.hhm.parfait.infra.i18n.I18nUtils
import moe.hhm.parfait.utils.HanziUtils
import moe.hhm.parfait.utils.round2Decimal
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.HashMap
import java.util.Locale

/**
 * 模板数据模型构建器
 * 
 * 负责构建POI-TL模板引擎使用的数据模型
 */
class TemplateModelBuilder : KoinComponent {
    private val termProcessor: TermProcessor by inject()
    
    /**
     * 构建完整的数据模型
     */
    suspend fun buildModel(
        student: StudentDTO,
        gpaStandard: GpaStandardDTO?,
        remainingTags: Set<String>,
        termExpressions: List<TermExpression>
    ): HashMap<String, Any> {
        val res = HashMap<String, Any>()

        (termProcessor.contextProvider as? StudentContextProvider)?.setStudent(student)

        // 添加基本信息
        res.putAll(basicInfo().filter { it.key in remainingTags })
        res.putAll(getStudentInfo(student).filter { it.key in remainingTags })
        if (gpaStandard != null) res.putAll(getGpaInfo(student, gpaStandard).filter { it.key in remainingTags })
        termExpressions.forEach {
            res[it.oriKey] = termProcessor.findTermReplacement(it, I18nManager.currentLanguage.value.code)
        }
        return res
    }

    private fun basicInfo() = hashMapOf(
        "year" to LocalDate.now().year,
        "month" to LocalDate.now().monthValue,
        "day" to LocalDate.now().dayOfMonth,
        "month_en" to LocalDate.now().month.getDisplayName(TextStyle.FULL, Locale.ENGLISH),
        "source" to "Parfait",
        "version" to "1.0.0"
    )
    
    /**
     * 添加学生信息
     */
    private fun getStudentInfo(student: StudentDTO) = hashMapOf(
        "id" to student.studentId,
        "name" to student.name,
        "name_en" to HanziUtils.hanzi2English(student.name),
        "department" to student.department,
        "major" to student.major,
        "classGroup" to student.classGroup,
        "grade" to student.grade.toString(),
        "gender" to student.gender.toString(),
        "status" to student.status.toString(),
        "score_weighted" to student.scores.weightedMean(),
        "score_simple" to student.scores.simpleMean()
    )
    
    /**
     * 添加GPA信息
     */
    private fun getGpaInfo(
        student: StudentDTO,
        gpaStandard: GpaStandardDTO
    ) = hashMapOf(
        "gpa" to gpaStandard.mapping.getGpa(student.scores).round2Decimal(),
        "gpa_standard" to gpaStandard.name
    )
} 