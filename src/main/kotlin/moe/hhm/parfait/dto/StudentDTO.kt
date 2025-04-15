/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.dto

import moe.hhm.parfait.infra.db.student.Students
import moe.hhm.parfait.infra.i18n.I18nUtils
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
    enum class Gender(val i18nKey: String) {
        UNKNOWN("student.gender.unknown"),
        MALE("student.gender.male"),
        FEMALE("student.gender.female");

        override fun toString(): String {
            return I18nUtils.getText(i18nKey)
        }
    }

    enum class Status(val i18nKey: String) {
        ENROLLED("student.status.enrolled"),
        SUSPENDED("student.status.suspended"),
        GRADUATED("student.status.graduated"),
        ABNORMAL("student.status.abnormal");

        override fun toString(): String {
            return I18nUtils.getText(i18nKey)
        }
    }

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