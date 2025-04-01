package moe.hhm.parfait.dto

data class StudentDTO(
    val studentId: String,
    val name: String,
    val gender: Gender,
    val status: Status,
    val department: String,
    val major: String,
    val grade: Int,
    val classGroup: String,
    val scores: List<ScoreDTO>
) {
    enum class Gender { UNKNOWN, MALE, FEMALE }
    enum class Status { ENROLLED, SUSPENDED, GRADUATED, ABNORMAL }
}
