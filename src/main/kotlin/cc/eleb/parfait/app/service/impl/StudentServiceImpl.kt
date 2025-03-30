package cc.eleb.parfait.app.service.impl

import cc.eleb.parfait.app.service.StudentService
import cc.eleb.parfait.domain.model.student.Student
import cc.eleb.parfait.domain.repository.StudentRepository
import cc.eleb.parfait.dto.ScoreDTO

class StudentServiceImpl(private val rep: StudentRepository) : StudentService {
    override fun getAllStudents(): List<Student> {
        return rep.findAll()
    }

    override fun addScore(student: Student, scoreDTO: ScoreDTO) {
        student.scoreList += scoreDTO
        rep.save(student)
    }

    override fun save(student: Student) {
        rep.save(student)
    }
}