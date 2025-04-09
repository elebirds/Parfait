/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.dto

import moe.hhm.parfait.infra.db.student.Students
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import java.util.*

data class StudentDTO(
    val uuid: UUID? = null,
    val studentId: String,
    val name: String,
    val gender: Gender,
    val status: Status,
    val department: String,
    val major: String,
    val grade: Int,
    val classGroup: String,
    var scores: List<ScoreDTO>
) {
    enum class Gender { UNKNOWN, MALE, FEMALE }
    enum class Status { ENROLLED, SUSPENDED, GRADUATED, ABNORMAL }

    fun <T : Any> into(it: UpdateBuilder<T>) {
        it[Students.studentId] = studentId
        it[Students.name] = name
        it[Students.gender] = gender.ordinal
        it[Students.department] = department
        it[Students.major] = major
        it[Students.grade] = grade
        it[Students.classGroup] = classGroup
        it[Students.status] = status.ordinal
        it[Students.scores] = scores.toScoreString()
    }
}