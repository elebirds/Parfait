/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.domain.repository

import moe.hhm.parfait.domain.model.student.Student
import moe.hhm.parfait.dto.StudentDTO
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

interface StudentRepository {
    suspend fun findAll(): List<Student>
    suspend fun findPage(page: Int, size: Int): List<Student>
    suspend fun findByUUID(id: UUID): Student?
    suspend fun findById(studentID: String): Student?
    suspend fun addStudent(student: StudentDTO): EntityID<UUID>
    suspend fun updateScore(student: StudentDTO): Boolean
    suspend fun save(student: StudentDTO): Boolean
    suspend fun delete(studentID: String): Boolean
}