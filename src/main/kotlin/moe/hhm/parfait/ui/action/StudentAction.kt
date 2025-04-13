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
import moe.hhm.parfait.utils.excel.SimpleWriteStudent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.LoggerFactory
import java.awt.Window
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter


object StudentAction : KoinComponent {
    private val logger = LoggerFactory.getLogger(StudentAction::class.java)
    private val certificateGenerator: CertificateGenerator by inject()
    
    private val studentsExcelHeader = arrayListOf(
        "score.course", "student.property.name", "score.exam",
        "student.property.status", "student.property.department", "student.property.major",
        "student.property.grade", "student.property.classGroup"
    )

    /**
     * 导出学生信息到Excel
     * @param students 学生列表
     * @param owner 父窗口
     * */
    suspend fun exportToExcel(students: List<StudentDTO> = emptyList(), owner: Window? = null) {
        val fileChooser = JFileChooser()
        fileChooser.fileFilter = FileNameExtensionFilter("Excel Files", "xlsx")
        
        if (fileChooser.showSaveDialog(owner) != JFileChooser.APPROVE_OPTION) return
        var filePath = fileChooser.selectedFile.absolutePath
        if (!filePath.endsWith(".xlsx")) {
            filePath += ".xlsx"
        }
        withContext(Dispatchers.IO) {
            EasyExcel.write(filePath, SimpleWriteStudent::class.java)
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
                    classGroup = it.classGroup
            ) })
        }
    }
    
    /**
     * 生成学生证书
     * @param params 证书生成参数
     * @return 生成的证书文件列表
     */
    suspend fun generateCertificates(params: CertificateGenerateDialog.CertificateGenerationParams): List<File> {
        // 在IO线程中执行证书生成
        return withContext(Dispatchers.IO) {
            logger.info("开始生成证书，学生数量: ${params.students.size}, 模板: ${params.template.name}")
            
            val generatedFiles = mutableListOf<File>()
            
            // 处理每个学生
            params.students.forEach { student ->
                try {
                    // 构建证书文件名
                    val fileName = buildCertificateFileName(student, params.template)
                    val outputFile = File(params.outputDirectory, fileName)
                    
                    // 使用证书生成器生成证书
                    certificateGenerator.generateCertificate(params, student, outputFile)
                    
                    // 添加到生成的文件列表
                    generatedFiles.add(outputFile)
                    
                    logger.info("成功为学生 ${student.name}(${student.studentId}) 生成证书: ${outputFile.name}")
                } catch (e: Exception) {
                    logger.error("为学生 ${student.name}(${student.studentId}) 生成证书时发生错误", e)
                    // 继续处理下一个学生
                }
            }
            
            logger.info("证书生成完成，成功: ${generatedFiles.size}/${params.students.size}")
            generatedFiles
        }
    }
    
    /**
     * 构建证书文件名
     */
    private fun buildCertificateFileName(student: StudentDTO, template: CertificateTemplateDTO): String {
        val timestamp = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
        return "${student.studentId}_${student.name}_${template.name}_$timestamp.docx"
    }
}