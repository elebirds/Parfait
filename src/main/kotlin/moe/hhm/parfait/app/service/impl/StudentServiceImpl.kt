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

class StudentServiceImpl(private val rep: StudentRepository) : StudentService {
    override suspend fun getAllStudents(): List<StudentDTO> {
        return rep.findAll().map { it.toDTO() }
    }

    override suspend fun getStudentsPage(page: Int, size: Int): List<StudentDTO> {
        return rep.findPage(page, size).map { it.toDTO() }
    }

    override suspend fun getStudentById(studentID: String): Student? {
        return rep.findById(studentID)
    }

    override suspend fun addStudent(student: StudentDTO) = rep.addStudent(student)

    override suspend fun deleteStudent(studentID: String): Boolean = rep.delete(studentID)

    override suspend fun addScore(student: StudentDTO, scoreDTO: ScoreDTO) {
        student.scores += scoreDTO
        rep.updateScore(student)
    }

    override suspend fun updateInfo(student: StudentDTO) = rep.updateInfo(student)
}