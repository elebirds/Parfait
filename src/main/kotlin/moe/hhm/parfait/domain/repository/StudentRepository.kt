/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.domain.repository

import moe.hhm.parfait.domain.model.student.Student
import moe.hhm.parfait.dto.StudentDTO
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

/**
 * 学生仓储接口
 */
interface StudentRepository {
    /**
     * 查找所有学生
     */
    suspend fun findAll(): List<Student>

    /**
     * 分页查找学生
     */
    suspend fun findPage(page: Int, size: Int): List<Student>

    /**
     * 根据UUID查找学生
     */
    suspend fun findByUUID(id: UUID): Student?

    /**
     * 根据学号查找学生
     */
    suspend fun findByStudentId(studentID: String): Student?

    /**
     * 查询指定UUID的学生是否存在
     */
    suspend fun isExistByUUID(uuid: UUID): Boolean

    /**
     * 查询指定学号的学生是否存在
     */
    suspend fun isExistByStudentId(studentID: String): Boolean

    /**
     * 添加学生
     */
    suspend fun addStudent(student: StudentDTO): EntityID<UUID>

    /**
     * 更新学生成绩
     */
    suspend fun updateScore(student: StudentDTO): Boolean

    /**
     * 更新学生信息
     */
    suspend fun updateInfo(student: StudentDTO): Boolean

    /**
     * 删除学生
     */
    suspend fun delete(uuid: UUID): Boolean

    /**
     * 统计学生数量
     */
    suspend fun count(): Long
}