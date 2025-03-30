package cc.eleb.parfait.dto

import cc.eleb.parfait.infra.i18n.trs
import java.util.*

data class StudentDTO(
    val id: UUID,
    val studentId: String,
    val name: String,
    val gender: Int,
    val status: Int,
    val department: String,
    val major: String,
    val grade: Int,
    val classGroup: String,
    val scores: List<ScoreDTO>
) {
    val genderT: String
        get() {
            return when (gender) {
                0 -> "未知"
                1 -> "男"
                else -> "女"
            }
        }
    val genderS: String
        get() {
            return when (gender) {
                0 -> "global-unknown".trs()
                1 -> "global-sex-m".trs()
                else -> "global-sex-f".trs()
            }
        }

    val statusT: String
        get() {
            return when (status) {
                0 -> "在籍"
                1 -> "休学"
                2 -> "毕业"
                else -> "异常"
            }
        }

    val statusS: String
        get() {
            return when (status) {
                0 -> "global-status-current".trs()
                1 -> "global-status-absent".trs()
                2 -> "global-status-graduated".trs()
                else -> "global-status-exception".trs()
            }
        }
}
