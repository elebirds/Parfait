package moe.hhm.parfait.ui.action

import com.alibaba.excel.EasyExcel
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import moe.hhm.parfait.dto.CertificateTemplateDTO
import moe.hhm.parfait.dto.StudentDTO
import moe.hhm.parfait.infra.i18n.I18nUtils
import moe.hhm.parfait.ui.component.dialog.CertificateGenerateDialog
import moe.hhm.parfait.app.certificate.CertificateGenerator
import moe.hhm.parfait.domain.model.gpa.GpaStandard
import moe.hhm.parfait.dto.GpaStandardDTO
import moe.hhm.parfait.dto.simpleMean
import moe.hhm.parfait.dto.weightedMean
import moe.hhm.parfait.utils.excel.SimpleWriteStudent
import moe.hhm.parfait.utils.round2Decimal
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.LoggerFactory
import java.awt.Window
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter


object StudentAction : KoinComponent {
    private val certificateGenerator: CertificateGenerator by inject()
    
    private val studentsExcelHeader = arrayListOf(
        "student.property.id", "student.property.name", "student.property.gender",
        "student.property.status", "student.property.department", "student.property.major",
        "student.property.grade", "student.property.classGroup", "score.weighted", "score.simple", "score.gpa"
    )

    /**
     * 导出学生信息到Excel
     * @param students 学生列表
     * */
    suspend fun exportToExcel(students: List<StudentDTO> = emptyList(), gpaStandard: GpaStandardDTO, selectedFilePath: String) {
        withContext(Dispatchers.IO) {
            EasyExcel.write(selectedFilePath, SimpleWriteStudent::class.java)
                .head(studentsExcelHeader.map { arrayListOf(I18nUtils.getText(it)) })
                .registerWriteHandler(LongestMatchColumnWidthStyleStrategy())
                .sheet("学生")
                .doWrite(students.map { SimpleWriteStudent(
                    studentId = it.studentId,
                    name = it.name,
                    gender = it.gender.toString(),
                    status = it.status.toString(),
                    department = it.department,
                    major = it.major,
                    grade = it.grade,
                    classGroup = it.classGroup,
                    scoreWeighted = it.scores.weightedMean().round2Decimal().toString(),
                    scoreSimple = it.scores.simpleMean().round2Decimal().toString(),
                    gpa = gpaStandard.mapping.getGpa(it.scores).round2Decimal().toString()
            ) })
        }
    }
    
    /**
     * 导出学生信息到文本文件
     * @param students 学生列表
     * @param format 文本格式字符串，支持以下变量：
     *              {name} - 学生姓名
     *              {id} - 学生ID
     *              {gender} - 性别
     *              {status} - 学生状态
     *              {department} - 系部
     *              {major} - 专业
     *              {grade} - 年级
     *              {class} - 班级
     *              {datetime} - 当前时间
     *              {score_weighted} - 加权平均分
     *              {score_simple} - 简单平均分
     *              {gpa} - 绩点
     * @param owner 父窗口
     */
    suspend fun exportToText(students: List<StudentDTO> = emptyList(), gpaStandard: GpaStandardDTO, format: String, owner: Window? = null) {
        val fileChooser = JFileChooser()
        fileChooser.fileFilter = FileNameExtensionFilter("Text Files", "txt")
        
        if (fileChooser.showSaveDialog(owner) != JFileChooser.APPROVE_OPTION) return
        var filePath = fileChooser.selectedFile.absolutePath
        if (!filePath.endsWith(".txt")) {
            filePath += ".txt"
        }
        
        withContext(Dispatchers.IO) {
            val content = StringBuilder()
            
            students.forEach { student ->
                var line = format
                    .replace("{name}", student.name)
                    .replace("{id}", student.studentId)
                    .replace("{gender}", student.gender.toString())
                    .replace("{status}", student.status.toString())
                    .replace("{department}", student.department)
                    .replace("{major}", student.major)
                    .replace("{grade}", student.grade.toString())
                    .replace("{class}", student.classGroup)
                    .replace("{datetime}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .replace("{score_weighted}", student.scores.weightedMean().round2Decimal().toString())
                    .replace("{score_simple}", student.scores.simpleMean().round2Decimal().toString())
                    .replace("{gpa}", gpaStandard.mapping.getGpa(student.scores).round2Decimal().toString())
                
                content.append(line).append("\n")
            }
            
            File(filePath).writeText(content.toString())
        }
    }
    
    /**
     * 生成学生证书
     * @param params 证书生成参数
     * @return 生成的证书文件列表
     */
    suspend fun generateCertificates(params: CertificateGenerateDialog.CertificateGenerationParams) = withContext(Dispatchers.IO) {
        certificateGenerator.generateCertificate(params)
    }
}