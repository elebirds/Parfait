package moe.hhm.parfait.ui.action

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import moe.hhm.parfait.app.service.StudentService
import moe.hhm.parfait.dto.StudentDTO
import moe.hhm.parfait.infra.i18n.I18nUtils
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.awt.Window
import java.io.FileOutputStream
import javax.swing.JFileChooser
import javax.swing.JOptionPane
import javax.swing.filechooser.FileNameExtensionFilter

object StudentAction : KoinComponent {
    private val studentService: StudentService by inject()

    /**
     * 导出学生信息到Excel

    * @param selectedStudents 选中的学生列表
    * @param filteredStudents 筛选后的学生列表
    * @param owner 父窗口
    */
    suspend fun exportScoresToExcel(students: List<StudentDTO> = emptyList(), owner: Window? = null) {
        val fileChooser = JFileChooser()
        fileChooser.fileFilter = FileNameExtensionFilter("Excel Files", "xlsx")
        
        if (fileChooser.showSaveDialog(owner) == JFileChooser.APPROVE_OPTION) {
            var filePath = fileChooser.selectedFile.absolutePath
            if (!filePath.endsWith(".xlsx")) {
                filePath += ".xlsx"
            }

            try {
                val workbook = XSSFWorkbook()
                val sheet = workbook.createSheet("学生信息表")

                // 创建标题样式
                val headerStyle = workbook.createCellStyle().apply {
                    fillForegroundColor = IndexedColors.GREY_25_PERCENT.index
                    fillPattern = FillPatternType.SOLID_FOREGROUND
                    alignment = HorizontalAlignment.CENTER
                }

                // 创建标题行
                val headerRow = sheet.createRow(0)
                val headers = listOf(
                    "学号", "姓名", "性别", "学院", "专业", "年级", "班级", "学籍状态"
                )

                // 记录每列的最大宽度
                val columnWidths = MutableList(headers.size) { 0 }

                // 设置表头并更新列宽
                headers.forEachIndexed { index, header ->
                    headerRow.createCell(index).apply {
                        setCellValue(header)
                        cellStyle = headerStyle
                        // 更新列宽（标题的每个字符占一个单位宽度）
                        columnWidths[index] = maxOf(columnWidths[index], header.length)
                    }
                }

                // 填充数据
                var rowNum = 1
                students.forEach { student ->
                    val row = sheet.createRow(rowNum++)
                    var colNum = 0
                    
                    // 学生基本信息
                    // 学号（数字，每个数字占0.5个单位宽度）
                    row.createCell(colNum).setCellValue(student.studentId)
                    columnWidths[colNum] = maxOf(columnWidths[colNum], (student.studentId.length * 0.5).toInt())
                    colNum++

                    // 姓名（文字，每个字符占一个单位宽度）
                    row.createCell(colNum).setCellValue(student.name)
                    columnWidths[colNum] = maxOf(columnWidths[colNum], student.name.length)
                    colNum++

                    // 性别
                    val gender = when (student.gender) {
                        StudentDTO.Gender.MALE -> I18nUtils.getText("student.gender.male")
                        StudentDTO.Gender.FEMALE -> I18nUtils.getText("student.gender.female")
                        StudentDTO.Gender.UNKNOWN -> I18nUtils.getText("student.gender.unknown")
                    }
                    row.createCell(colNum).setCellValue(gender)
                    columnWidths[colNum] = maxOf(columnWidths[colNum], gender.length)
                    colNum++

                    // 学院
                    row.createCell(colNum).setCellValue(student.department)
                    columnWidths[colNum] = maxOf(columnWidths[colNum], student.department.length)
                    colNum++

                    // 专业
                    row.createCell(colNum).setCellValue(student.major)
                    columnWidths[colNum] = maxOf(columnWidths[colNum], student.major.length)
                    colNum++

                    // 年级（数字，每个数字占0.5个单位宽度）
                    row.createCell(colNum).setCellValue(student.grade.toString())
                    columnWidths[colNum] = maxOf(columnWidths[colNum], (student.grade.toString().length * 0.5).toInt())
                    colNum++

                    // 班级
                    row.createCell(colNum).setCellValue(student.classGroup)
                    columnWidths[colNum] = maxOf(columnWidths[colNum], student.classGroup.length)
                    colNum++

                    // 学籍状态
                    val status = when (student.status) {
                        StudentDTO.Status.ENROLLED -> I18nUtils.getText("student.status.enrolled")
                        StudentDTO.Status.SUSPENDED -> I18nUtils.getText("student.status.suspended")
                        StudentDTO.Status.GRADUATED -> I18nUtils.getText("student.status.graduated")
                        StudentDTO.Status.ABNORMAL -> I18nUtils.getText("student.status.abnormal")
                    }
                    row.createCell(colNum).setCellValue(status)
                    columnWidths[colNum] = maxOf(columnWidths[colNum], status.length)
                }
                
                // 设置列宽（每个单位宽度对应256个单位）
                columnWidths.forEachIndexed { index, width ->
                    sheet.setColumnWidth(index, width * 1000)
                }
                
                // 保存文件
                withContext(Dispatchers.IO) {
                    FileOutputStream(filePath).use {
                        workbook.write(it)
                    }
                }
                workbook.close()

                JOptionPane.showMessageDialog(
                    owner,
                    I18nUtils.getText("student.export.success"),
                    I18nUtils.getText("success"),
                    JOptionPane.INFORMATION_MESSAGE
                )
            } catch (e: Exception) {
                JOptionPane.showMessageDialog(
                    owner,
                    e.message,
                    I18nUtils.getText("error.generic"),
                    JOptionPane.ERROR_MESSAGE
                )
            }
        }
    }
}