/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.domain.model.student

import moe.hhm.parfait.dto.ScoreDTO
import moe.hhm.parfait.dto.StudentDTO
import moe.hhm.parfait.infra.db.student.Students
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.time.LocalDateTime
import java.util.*

/**
 * 学生聚合根
 */
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
    var scoreString by Students.scores

    var scores: List<ScoreDTO>
        get() = scoreString.split("|")
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .map { ScoreDTO.fromString(it) }
        set(value) {
            scoreString = value.joinToString("|") { it.toString() }
        }

    /**
     * 转换为DTO
     */
    fun toDTO() = StudentDTO(
        uuid = id.value,
        studentId = studentId,
        name = name,
        gender = StudentDTO.Gender.entries[gender],
        status = StudentDTO.Status.entries[status],
        department = department,
        major = major,
        grade = grade,
        classGroup = classGroup,
        scores = scores
    )

    /**
     * 更新学生信息
     */
    fun updateInfo(
        name: String? = null,
        gender: Int? = null,
        department: String? = null,
        major: String? = null,
        grade: Int? = null,
        classGroup: String? = null,
        status: Int? = null
    ) {
        name?.let { this.name = it }
        gender?.let { this.gender = it }
        department?.let { this.department = it }
        major?.let { this.major = it }
        grade?.let { this.grade = it }
        classGroup?.let { this.classGroup = it }
        status?.let { this.status = it }
        this.updatedAt = LocalDateTime.now()
    }
}