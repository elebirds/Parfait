package moe.hhm.parfait.dto

import moe.hhm.parfait.exception.BusinessException
import moe.hhm.parfait.utils.castTo

fun List<ScoreDTO>.toScoreString(): String {
    return this.joinToString("|") { it.toString() }
}

enum class CourseType(val i18nKey: String) {
    DEFAULT("course.type.default"),
    PROFESSIONAL_BASIC("course.type.professional.basic"),
    PROFESSIONAL_CORE("course.type.professional.core"),
    PROFESSIONAL_DIRECTION("course.type.professional.direction"),
    PROFESSIONAL_EXTENSION("course.type.professional.extension"),
    PRACTICAL("course.type.practical"),
    PLATFORM("course.type.platform"),
    GENERAL_REQUIRED("course.type.general.required"),
    GENERAL_ELECTIVE("course.type.general.elective");
} 

data class ScoreDTO(
    var name: String = "",
    var type: CourseType = CourseType.DEFAULT,
    var exam: String = "",
    var credit: Int = 0,
    var score: Double = 0.0,
    var gpa: Boolean = true,
) {

    override fun toString(): String {
        return "$name:${type.ordinal}:$exam:$credit:${score.castTo(2)}:$gpa"
    }

    companion object {
        fun fromString(score: String): ScoreDTO {
            val parts = score.split(":")
            return if (parts.size == 6) {
                ScoreDTO(
                    name = parts[0],
                    type = CourseType.entries[parts[1].toInt()],
                    exam = parts[2],
                    credit = parts[3].toInt(),
                    score = parts[4].toDouble(),
                    gpa = parts[5].toBoolean()
                )
            } else {
                throw BusinessException("无法解析成绩字符串: $score")
            }
        }
    }
}