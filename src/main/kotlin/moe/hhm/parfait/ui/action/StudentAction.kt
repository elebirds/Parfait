package moe.hhm.parfait.ui.action

import com.alibaba.excel.EasyExcel
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import moe.hhm.parfait.dto.StudentDTO
import moe.hhm.parfait.infra.i18n.I18nUtils
import moe.hhm.parfait.utils.excel.SimpleWriteStudent
import org.koin.core.component.KoinComponent
import java.awt.Window
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter


object StudentAction : KoinComponent {
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
}