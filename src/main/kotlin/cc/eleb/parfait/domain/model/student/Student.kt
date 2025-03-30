package cc.eleb.parfait.domain.model.student

import cc.eleb.parfait.dto.ScoreDTO
import cc.eleb.parfait.dto.StudentDTO
import cc.eleb.parfait.infra.db.student.Students
import cc.eleb.parfait.infra.i18n.trs
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

    fun toDTO(): StudentDTO {
        return StudentDTO(
            id = this.id.value,
            studentId = this.studentId,
            name = this.name,
            gender = this.gender,
            status = this.status,
            department = this.department,
            major = this.major,
            grade = this.grade,
            classGroup = this.classGroup,
            scores = this.scoreList,
        )
    }
}