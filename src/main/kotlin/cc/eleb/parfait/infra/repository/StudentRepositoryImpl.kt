package cc.eleb.parfait.infra.repository

import cc.eleb.parfait.domain.model.student.Student
import cc.eleb.parfait.domain.repository.StudentRepository
import cc.eleb.parfait.infra.db.DatabaseUtils
import cc.eleb.parfait.infra.db.student.Students
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.deleteWhere
import java.util.*

class StudentRepositoryImpl : StudentRepository {
    override fun findAll(): List<Student> {
        return DatabaseUtils.dbQuery {
            Student.Companion.all().toList()
        }
    }

    override fun findPage(page: Int, size: Int): List<Student> {
        return DatabaseUtils.dbQuery {
            Student.Companion.all()
                .limit(size).offset((page - 1) * size.toLong())
                .toList()
        }
    }

    override fun findById(studentID: UUID): Student? {
        return DatabaseUtils.dbQuery {
            Student.Companion.find { Students.id eq studentID }.firstOrNull()
        }
    }

    override fun save(student: Student): Boolean {
        return DatabaseUtils.dbQuery {
            student.flush()
        }
    }

    override fun delete(studentID: String): Boolean {
        return DatabaseUtils.dbQuery {
            Students.deleteWhere { Op.build { studentId eq studentID } }
        } > 0
    }
}