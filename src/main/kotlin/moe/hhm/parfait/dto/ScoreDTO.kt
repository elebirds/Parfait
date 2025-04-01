package moe.hhm.parfait.dto

import moe.hhm.parfait.utils.castTo
import com.alibaba.excel.annotation.ExcelProperty

data class ScoreDTO(
    @ExcelProperty("课程名称") var name: String = "",
    @ExcelProperty("课程类别") var type: String = "",
    @ExcelProperty("考核类型") var exam: String = "",
    @ExcelProperty("学分") var credit: Int = 0,
    @ExcelProperty("成绩") var score: Double = 0.0,
    @ExcelProperty("学位课") var gpaS: String = "是",
) {
    val gpa: Boolean
        get() = gpaS == "是"

    override fun toString(): String {
        return "$name:$type:$exam:$credit:{${score.castTo(2)}}:$gpaS"
    }

    companion object {
        fun fromString(score: String): ScoreDTO {
            val parts = score.split(":")
            return if (parts.size == 5) {
                ScoreDTO(
                    name = parts[0],
                    type = parts[1],
                    exam = parts[2],
                    credit = parts[3].toInt(),
                    score = parts[4].toDouble()
                )
            } else {
                ScoreDTO()
            }
        }
    }
}