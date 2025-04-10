/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.app.service

import moe.hhm.parfait.domain.model.student.Student
import moe.hhm.parfait.dto.ScoreDTO
import moe.hhm.parfait.dto.StudentDTO
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

interface StudentService {
    suspend fun getAllStudents(): List<StudentDTO>
    suspend fun getStudentsPage(page: Int, size: Int): List<StudentDTO>
    suspend fun getStudentById(studentID: String): Student?
    suspend fun addStudent(student: StudentDTO): EntityID<UUID>
    suspend fun deleteStudent(studentID: String): Boolean
    suspend fun updateInfo(student: StudentDTO): Boolean
    suspend fun addScore(student: StudentDTO, scoreDTO: ScoreDTO)
    suspend fun count(): Long
}