package cc.eleb.parfait.domain.repository

import cc.eleb.parfait.domain.model.student.Student
import java.util.*

interface StudentRepository {
    fun findAll(): List<Student>
    fun findPage(page: Int, size: Int): List<Student>
    fun findById(studentID: UUID): Student?
    fun save(student: Student): Boolean
    fun delete(studentID: String): Boolean
}