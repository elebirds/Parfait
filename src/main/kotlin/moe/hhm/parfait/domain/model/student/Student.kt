/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.domain.model.student

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
}