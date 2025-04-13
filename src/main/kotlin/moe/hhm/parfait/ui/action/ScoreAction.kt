/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.action

import com.alibaba.excel.EasyExcel
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import moe.hhm.parfait.dto.ScoreDTO
import moe.hhm.parfait.infra.i18n.I18nUtils
import moe.hhm.parfait.utils.excel.SimpleWriteScore
import java.awt.Window
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

object ScoreAction {
    private val scoresExcelHeader = arrayListOf(
        "student.property.id", "score.type", "student.property.gender",
        "student.property.status", "student.property.department", "student.property.major",
        "student.property.grade", "student.property.classGroup"
    )

    suspend fun exportToExcel(students: List<ScoreDTO> = emptyList(), owner: Window? = null) {
        val fileChooser = JFileChooser()
        fileChooser.fileFilter = FileNameExtensionFilter("Excel Files", "xlsx")

        if (fileChooser.showSaveDialog(owner) != JFileChooser.APPROVE_OPTION) return
        var filePath = fileChooser.selectedFile.absolutePath
        if (!filePath.endsWith(".xlsx")) {
            filePath += ".xlsx"
        }
        withContext(Dispatchers.IO) {
            EasyExcel.write(filePath, SimpleWriteScore::class.java)
                .head(scoresExcelHeader.map { arrayListOf(I18nUtils.getText(it)) })
                .registerWriteHandler(LongestMatchColumnWidthStyleStrategy())
                .sheet("模板")
                .doWrite(students.map { SimpleWriteScore(
                    name = it.name,
                    type = it.type.toString(),
                    exam = it.exam,
                    credit = it.credit,
                    score = it.score,
                    gpa = when(it.gpa) {
                        true -> I18nUtils.getText("score.gpa.yes")
                        false -> I18nUtils.getText("score.gpa.no")
                    },
                ) })
        }
    }
}