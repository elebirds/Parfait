package moe.hhm.parfait.app.service

import moe.hhm.parfait.domain.model.student.Student
import moe.hhm.parfait.dto.ScoreDTO
import moe.hhm.parfait.dto.StudentDTO
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

interface StudentService {
    suspend fun getAllStudents(): List<Student>
    suspend fun getStudentsPage(page: Int, size: Int): List<Student>
    suspend fun getStudentById(studentID: String): Student?
    suspend fun addStudent(student: StudentDTO) : EntityID<UUID>
    suspend fun save(student: Student)
    suspend fun addScore(student: Student, scoreDTO: ScoreDTO)
}