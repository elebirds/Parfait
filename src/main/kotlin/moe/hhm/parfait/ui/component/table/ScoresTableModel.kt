/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.component.table

import moe.hhm.parfait.dto.ScoreDTO
import moe.hhm.parfait.infra.i18n.I18nUtils
import javax.swing.table.AbstractTableModel

class ScoresTableModel : AbstractTableModel() {
    private val columnNames = arrayOf(
        I18nUtils.getText("score.course"),
        I18nUtils.getText("score.type"),
        I18nUtils.getText("score.exam"),
        I18nUtils.getText("score.credit"),
        I18nUtils.getText("score.score"),
        I18nUtils.getText("score.gpa")
    )

    private var data = mutableListOf<ScoreDTO>()

    fun setData(scores: List<ScoreDTO>) {
        data = scores.toMutableList()
        fireTableDataChanged()
    }

    fun getData(): List<ScoreDTO> = data.toList()

    fun addScore(score: ScoreDTO) {
        data.add(score)
        fireTableRowsInserted(data.size - 1, data.size - 1)
    }

    fun updateScore(rowIndex: Int, score: ScoreDTO) {
        if (rowIndex >= 0 && rowIndex < data.size) {
            data[rowIndex] = score
            fireTableRowsUpdated(rowIndex, rowIndex)
        }
    }

    fun removeScore(rowIndex: Int) {
        if (rowIndex >= 0 && rowIndex < data.size) {
            data.removeAt(rowIndex)
            fireTableRowsDeleted(rowIndex, rowIndex)
        }
    }

    override fun getRowCount(): Int = data.size

    override fun getColumnCount(): Int = columnNames.size

    override fun getColumnName(column: Int): String = columnNames[column]

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        val score = data[rowIndex]
        return when (columnIndex) {
            0 -> score.name
            1 -> I18nUtils.getText(score.type.i18nKey)
            2 -> score.exam
            3 -> score.credit
            4 -> score.score
            5 -> if (score.gpa) I18nUtils.getText("score.dialog.yes") else I18nUtils.getText("score.dialog.no")
            else -> ""
        }
    }
}