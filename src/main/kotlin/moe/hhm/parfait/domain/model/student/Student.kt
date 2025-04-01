package moe.hhm.parfait.domain.model.student

import moe.hhm.parfait.dto.ScoreDTO
import moe.hhm.parfait.dto.StudentDTO
import moe.hhm.parfait.infra.db.student.Students
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class Student(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Student>(Students)

    var studentId by Students.studentId
    var name by Students.name
    var gender by Students.gender
    var department by Students.department
    var major by Students.major
    var grade by Students.grade
    var classGroup by Students.classGroup
    var status by Students.status
    var createdAt by Students.createdAt
    var updatedAt by Students.updatedAt
    var scores by Students.scores

    var scoreList: List<ScoreDTO>
        get() = scores.split("|")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map { ScoreDTO.fromString(it) }
        set(value) {
            val scoreString = value.joinToString("|") { it.toString() }
            scores = scoreString
        }
    val genderS: String
        get() {
            return when (gender) {
                0 -> "未知"
                1 -> "男"
                else -> "女"
            }
        }
    val statusS: String
        get() {
            return when (status) {
                0 -> "在籍"
                1 -> "休学"
                2 -> "毕业"
                else -> "异常"
            }
        }
    fun toDTO(): StudentDTO {
        return StudentDTO(
            studentId = this.studentId,
            name = this.name,
            gender = StudentDTO.Gender.entries[this.gender],
            status = StudentDTO.Status.entries[this.status],
            department = this.department,
            major = this.major,
            grade = this.grade,
            classGroup = this.classGroup,
            scores = this.scoreList,
        )
    }
}