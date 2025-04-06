package moe.hhm.parfait.dto

import moe.hhm.parfait.exception.BusinessException
import moe.hhm.parfait.utils.castTo

fun List<ScoreDTO>.toScoreString(): String {
    return this.joinToString("|") { it.toString() }
}

data class ScoreDTO(
    var name: String = "",
    var type: String = "",
    var exam: String = "",
    var credit: Int = 0,
    var score: Double = 0.0,
    var gpaS: String = "是",
) {
    val gpa: Boolean
        get() = gpaS == "是"

    override fun toString(): String {
        return "$name:$type:$exam:$credit:${score.castTo(2)}:$gpaS"
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
                throw BusinessException("无法解析成绩字符串: $score")
            }
        }
    }
}