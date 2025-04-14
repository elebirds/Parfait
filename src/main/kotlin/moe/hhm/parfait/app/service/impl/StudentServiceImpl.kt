/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.app.service.impl

import moe.hhm.parfait.app.service.StudentService
import moe.hhm.parfait.domain.model.student.Student
import moe.hhm.parfait.domain.repository.StudentRepository
import moe.hhm.parfait.dto.ScoreDTO
import moe.hhm.parfait.dto.StudentDTO
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class StudentServiceImpl(private val rep: StudentRepository) : StudentService {
    override suspend fun getAllStudents(): List<StudentDTO> {
        return rep.findAll().map { it.toDTO() }
    }

    override suspend fun getStudentsPage(page: Int, size: Int): List<StudentDTO> {
        return rep.findPage(page, size).map { it.toDTO() }
    }

    override suspend fun getStudentByStudentId(studentID: String): Student? {
        return rep.findByStudentId(studentID)
    }

    override suspend fun getStudentByUUID(uuid: UUID): Student? {
        return rep.findByUUID(uuid)
    }

    override suspend fun addStudent(student: StudentDTO) = rep.addStudent(student)
    override suspend fun addAllStudents(students: List<StudentDTO>): List<EntityID<UUID>> {
        return rep.addAllStudents(students)
    }

    override suspend fun deleteStudent(uuid: UUID): Boolean = rep.delete(uuid)

    override suspend fun addScore(student: StudentDTO, scoreDTO: ScoreDTO) {
        student.scores += scoreDTO
        rep.updateScore(student)
    }

    override suspend fun updateInfo(student: StudentDTO) = rep.updateInfo(student)

    override suspend fun updateScore(student: StudentDTO) = rep.updateScore(student)

    override suspend fun count(): Long = rep.count()
}