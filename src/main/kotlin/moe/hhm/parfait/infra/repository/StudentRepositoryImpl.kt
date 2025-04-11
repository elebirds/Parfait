/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.infra.repository

import moe.hhm.parfait.domain.model.student.Student
import moe.hhm.parfait.domain.repository.StudentRepository
import moe.hhm.parfait.dto.StudentDTO
import moe.hhm.parfait.dto.toScoreString
import moe.hhm.parfait.exception.BusinessException
import moe.hhm.parfait.infra.db.DatabaseUtils
import moe.hhm.parfait.infra.db.student.Students
import moe.hhm.parfait.infra.db.student.Students.studentId
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime
import java.util.*

/**
 * StudentRepository的实现类
 */
class StudentRepositoryImpl : StudentRepository {
    override suspend fun findAll(): List<Student> = DatabaseUtils.dbQuery {
        Student.all().toList()
    }

    override suspend fun findPage(
        page: Int,
        size: Int
    ): List<Student> = DatabaseUtils.dbQuery {
        Student.all()
            .limit(size).offset((page - 1) * size.toLong())
            .toList()
    }

    override suspend fun findByUUID(uuid: UUID): Student? = DatabaseUtils.dbQuery {
        Student.findById(EntityID(uuid, Students))
    }

    override suspend fun findByStudentId(studentID: String): Student? = DatabaseUtils.dbQuery {
        Student.find { studentId eq studentID }.firstOrNull()
    }

    override suspend fun isExistByUUID(uuid: UUID): Boolean = DatabaseUtils.dbQuery {
        Student.find { Students.id eq uuid }.count() > 0
    }

    override suspend fun isExistByStudentId(studentID: String): Boolean = DatabaseUtils.dbQuery {
        Student.find { studentId eq studentID }.count() > 0
    }

    override suspend fun addStudent(student: StudentDTO): EntityID<UUID> {
        if (isExistByStudentId(student.studentId)) throw BusinessException("学生已存在") // TODO: 国际化
        return DatabaseUtils.dbQuery {
            Students.insertAndGetId {
                student.into(it)
            }
        }
    }

    override suspend fun updateInfo(student: StudentDTO): Boolean {
        if (!isExistByUUID(student.uuid!!)) throw BusinessException("学生不存在") // TODO: 国际化
        return DatabaseUtils.dbQuery { // 使用uuid更新
            Students.update({ Students.id eq student.uuid }) {
                student.into(it)
                it[updatedAt] = LocalDateTime.now()
            }
        } > 0
    }

    override suspend fun delete(uuid: UUID): Boolean {
        return DatabaseUtils.dbQuery {
            Students.deleteWhere { Op.build { id eq uuid } }
        } > 0
    }

    override suspend fun updateScore(student: StudentDTO): Boolean {
        if (!isExistByUUID(student.uuid!!)) throw BusinessException("学生不存在") // TODO: 国际化
        return DatabaseUtils.dbQuery {
            Students.update({ Students.id eq student.uuid }) {
                it[scores] = student.scores.toScoreString()
                it[updatedAt] = LocalDateTime.now()
            }
        } > 0
    }

    override suspend fun count(): Long = DatabaseUtils.dbQuery {
        Student.all().count()
    }
}