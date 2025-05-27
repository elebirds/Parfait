package moe.hhm.parfait.ui.action

import com.alibaba.excel.EasyExcel
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import moe.hhm.parfait.app.certificate.CertificateGenerator
import moe.hhm.parfait.app.term.TemplateModelBuilder
import moe.hhm.parfait.app.term.TermParser
import moe.hhm.parfait.app.term.TermParser.Companion.TERM_PREFIX
import moe.hhm.parfait.dto.GpaStandardDTO
import moe.hhm.parfait.dto.StudentDTO
import moe.hhm.parfait.dto.simpleMean
import moe.hhm.parfait.dto.weightedMean
import moe.hhm.parfait.infra.i18n.I18nUtils
import moe.hhm.parfait.ui.component.dialog.CertificateGenerateDialog
import moe.hhm.parfait.utils.excel.SimpleWriteStudent
import moe.hhm.parfait.utils.round2Decimal
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.awt.Window
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter


object StudentAction : KoinComponent {
    private val certificateGenerator: CertificateGenerator by inject()
    private val modelBuilder: TemplateModelBuilder by inject()

    private val studentsExcelHeader = arrayListOf(
        "student.property.id", "student.property.name", "student.property.gender",
        "student.property.status", "student.property.department", "student.property.major",
        "student.property.grade", "student.property.classGroup", "score.weighted", "score.simple", "score.gpa"
    )
    /*
     * 软件已内置部分常用标签供选择，包括：
     * - 基础信息：{year}（年份）、{month}（月份）、{day}（天）、{month_en}（英文月份）、{datetime}（当前时间）
     * - 学生标识：{id}（学号）、{name}（学生姓名）、{name_en}（学生英文名）
     * - 院校信息：{department}（学院）、{major}（专业）、{classGroup}（班级）、{class}（班级）、{grade}（年级）
     * - 其他属性：{gender}（性别）、{status}（学籍状态）
     * - 成绩数据：{score_weighted}（加权平均分）、{score_simple}（简单平均分）、{gpa}（绩点成绩）、{gpa_standard}（绩点标准）
     */
    fun getExcelHeader(key: String): String {
        when (key) {
            "year" -> return I18nUtils.getText("student.export.year")
            "month" -> return I18nUtils.getText("student.export.month")
            "day" -> return I18nUtils.getText("student.export.day")
            "month_en" -> return I18nUtils.getText("student.export.month_en")
            "datetime" -> return I18nUtils.getText("student.export.datetime")
            "id" -> return I18nUtils.getText("student.property.id")
            "name" -> return I18nUtils.getText("student.property.name")
            "name_en" -> return I18nUtils.getText("student.property.name_en")
            "department" -> return I18nUtils.getText("student.property.department")
            "major" -> return I18nUtils.getText("student.property.major")
            "classGroup" -> return I18nUtils.getText("student.property.classGroup")
            "class" -> return I18nUtils.getText("student.property.classGroup")
            "grade" -> return I18nUtils.getText("student.property.grade")
            "gender" -> return I18nUtils.getText("student.property.gender")
            "status" -> return I18nUtils.getText("student.property.status")
            "score_weighted" -> return I18nUtils.getText("student.export.score_weighted")
            "score_simple" -> return I18nUtils.getText("student.export.score_simple")
            "gpa" -> return I18nUtils.getText("student.export.gpa")
            "gpa_standard" -> return I18nUtils.getText("student.export.gpa_standard")
            else -> return key
        }
    }

    private val extractRegex = "\\{(.*?)\\}".toRegex()

    /**
     * 导出学生信息到Excel
     * @param students 学生列表
     * */
    suspend fun exportToExcel(
        students: List<StudentDTO> = emptyList(),
        gpaStandard: GpaStandardDTO,
        selectedFilePath: String
    ) {
        withContext(Dispatchers.IO) {
            EasyExcel.write(selectedFilePath, SimpleWriteStudent::class.java)
                .head(studentsExcelHeader.map { arrayListOf(I18nUtils.getText(it)) })
                .registerWriteHandler(LongestMatchColumnWidthStyleStrategy())
                .sheet("学生")
                .doWrite(students.map {
                    SimpleWriteStudent(
                        studentId = it.studentId,
                        name = it.name,
                        gender = it.gender.toString(),
                        status = it.status.toString(),
                        department = it.department,
                        major = it.major,
                        grade = it.grade,
                        classGroup = it.classGroup,
                        scoreWeighted = it.scores.weightedMean().round2Decimal(),
                        scoreSimple = it.scores.simpleMean().round2Decimal(),
                        gpa = gpaStandard.mapping.getGpa(it.scores).round2Decimal()
                    )
                })
        }
    }

    suspend fun exportToCustomExcel(
        students: List<StudentDTO> = emptyList(),
        gpaStandard: GpaStandardDTO,
        format: String,
        selectedFilePath: String
    ) {
        val extractedTags = extractRegex.findAll(format).map { it.groupValues[1] }.toList()
        val (remainingTags, termExpressions) = modelBuilder.separateTerms(extractedTags)

        withContext(Dispatchers.IO) {
            val dataList = arrayListOf<Map<String, Any>>()
            students.forEach { student ->
                val models = modelBuilder.buildModel(
                    student = student,
                    gpaStandard = gpaStandard,
                    remainingTags = remainingTags,
                    termExpressions = termExpressions
                )
                dataList.add(models)
            }
            // 确保有数据
            if (dataList.isEmpty()) return@withContext

            // 从第一个Map提取表头
            val headers = dataList.first().keys.toList()

            // 将Map列表转换为List<List<String>>格式
            val rows = dataList.map { map ->
                headers.map { key -> map[key]?.toString() ?: "" }
            }
            // 创建表头
            val headRows = headers.map { listOf(getExcelHeader(it)) }

            // 写入Excel
            EasyExcel.write(selectedFilePath)
                .head(headRows)
                .registerWriteHandler(LongestMatchColumnWidthStyleStrategy())
                .sheet("Parfait")
                .doWrite(rows)
        }
    }

    /**
     * 导出学生信息到文本文件
     * @param students 学生列表
     * @param format 文本格式字符串，支持变量与术语一致
     * @param owner 父窗口
     */
    suspend fun exportToText(
        students: List<StudentDTO> = emptyList(),
        gpaStandard: GpaStandardDTO,
        format: String,
        isCSV: Boolean,
        owner: Window? = null
    ) {
        val fileChooser = JFileChooser()
        if(!isCSV) {
            fileChooser.fileFilter = FileNameExtensionFilter("Text Files", "txt")
        }else {
            fileChooser.fileFilter = FileNameExtensionFilter("CSV表格", "csv")
        }

        if (fileChooser.showSaveDialog(owner) != JFileChooser.APPROVE_OPTION) return
        var filePath = fileChooser.selectedFile.absolutePath
        if(!isCSV) {
            if (!filePath.endsWith(".txt")) {
                filePath += ".txt"
            }
        }else {
            if (!filePath.endsWith(".csv")) {
                filePath += ".csv"
            }
        }

        val extractedTags = extractRegex.findAll(format).map { it.groupValues[1] }.toList()
        val (remainingTags, termExpressions) = modelBuilder.separateTerms(extractedTags)

        withContext(Dispatchers.IO) {
            val content = StringBuilder()

            if(isCSV) {
                content.append('\ufeff')
            }

            students.forEach { student ->
                val models = modelBuilder.buildModel(
                    student = student,
                    gpaStandard = gpaStandard,
                    remainingTags = remainingTags,
                    termExpressions = termExpressions
                )
                var line = format
                for (model in models) {
                    line = line.replace("{${model.key}}", model.value.toString())
                }
                content.append(line).append("\n")
            }
            // 删除最后一个换行符
            if (content.isNotEmpty()) {
                content.deleteCharAt(content.length - 1)
            }
            File(filePath).writeText(content.toString())
        }
    }

    /**
     * 生成学生证书
     * @param params 证书生成参数
     * @return 生成的证书文件列表
     */
    suspend fun generateCertificates(params: CertificateGenerateDialog.CertificateGenerationParams) =
        withContext(Dispatchers.IO) {
            certificateGenerator.generateCertificate(params)
        }
}