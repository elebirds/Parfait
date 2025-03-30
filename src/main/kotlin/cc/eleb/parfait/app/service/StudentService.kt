package cc.eleb.parfait.app.service

import cc.eleb.parfait.domain.model.student.Student
import cc.eleb.parfait.dto.ScoreDTO

interface StudentService {
    fun getAllStudents(): List<Student>
    fun addScore(student: Student, scoreDTO: ScoreDTO)
    fun save(student: Student)
}