package moe.hhm.parfait.infra.repository

import moe.hhm.parfait.domain.model.student.Student
import moe.hhm.parfait.domain.repository.StudentRepository
import moe.hhm.parfait.dto.StudentDTO
import moe.hhm.parfait.infra.db.DatabaseUtils
import moe.hhm.parfait.infra.db.student.Students
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import java.util.*

class StudentRepositoryImpl : StudentRepository {
    override suspend fun findAll(): List<Student> {
        return DatabaseUtils.dbQuery {
            Student.all().toList()
        }
    }

    override suspend fun findPage(page: Int, size: Int): List<Student> {
        return DatabaseUtils.dbQuery {
            Student.all()
                .limit(size).offset((page - 1) * size.toLong())
                .toList()
        }
    }

    override suspend fun findByUUID(id: UUID): Student? {
        return DatabaseUtils.dbQuery {
            Student.find { Students.id eq id }.firstOrNull()
        }
    }

    override suspend fun findById(studentID: String): Student? {
        return DatabaseUtils.dbQuery {
            Student.find { Students.studentId eq studentID }.firstOrNull()
        }
    }

    override suspend fun add(student: StudentDTO) = DatabaseUtils.dbQuery {
        Students.insertAndGetId {
            it[studentId] = student.studentId
            it[name] = student.name
            it[gender] = student.gender.ordinal
            it[status] = student.status.ordinal
            it[department] = student.department
            it[major] = student.major
            it[grade] = student.grade
            it[classGroup] = student.classGroup
            it[scores] = "[]"
        }
    }

    override suspend fun save(student: Student): Boolean {
        return DatabaseUtils.dbQuery {
            student.flush()
        }
    }

    override suspend fun delete(studentID: String): Boolean {
        return DatabaseUtils.dbQuery {
            Students.deleteWhere { Op.build { studentId eq studentID } }
        } > 0
    }
}