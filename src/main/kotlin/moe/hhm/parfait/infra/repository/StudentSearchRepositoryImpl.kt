/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.infra.repository

import moe.hhm.parfait.domain.model.student.Student
import moe.hhm.parfait.domain.repository.StudentSearchRepository
import moe.hhm.parfait.infra.db.DatabaseUtils
import moe.hhm.parfait.infra.db.student.Students
import moe.hhm.parfait.ui.component.dialog.SearchFilterCriteria
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or

/**
 * 学生搜索仓储实现类
 */
class StudentSearchRepositoryImpl : StudentSearchRepository {

    override suspend fun searchStudents(criteria: SearchFilterCriteria): List<Student> = DatabaseUtils.dbQuery {
        Student.find { buildSearchCondition(criteria) }.toList()
    }

    override suspend fun searchStudentsPage(
        criteria: SearchFilterCriteria,
        page: Int,
        size: Int
    ): List<Student> = DatabaseUtils.dbQuery {
        Student.find { buildSearchCondition(criteria) }
            .limit(size).offset((page - 1) * size.toLong())
            .toList()
    }

    override suspend fun countSearchResults(criteria: SearchFilterCriteria): Long = DatabaseUtils.dbQuery {
        Student.find { buildSearchCondition(criteria) }.count()
    }

    /**
     * 构建搜索条件
     */
    private fun buildSearchCondition(criteria: SearchFilterCriteria): org.jetbrains.exposed.sql.Op<Boolean> {
        var condition = org.jetbrains.exposed.sql.Op.build { Students.id.isNotNull() }

        // 学号条件
        if (criteria.studentId.isNotEmpty()) {
            condition = condition and (Students.studentId like "%${criteria.studentId}%")
        }

        // 姓名条件
        if (criteria.name.isNotEmpty()) {
            condition = condition and (Students.name like "%${criteria.name}%")
        }

        // 性别条件
        criteria.gender?.let {
            condition = condition and (Students.gender eq it.ordinal)
        }

        // 院系条件
        if (criteria.department.isNotEmpty()) {
            condition = condition and (Students.department like "%${criteria.department}%")
        }

        // 专业条件
        if (criteria.major.isNotEmpty()) {
            condition = condition and (Students.major like "%${criteria.major}%")
        }

        // 年级条件
        criteria.grade?.let {
            condition = condition and (Students.grade eq it)
        }

        // 班级条件
        if (criteria.classGroup.isNotEmpty()) {
            condition = condition and (Students.classGroup like "%${criteria.classGroup}%")
        }

        // 状态条件
        criteria.status?.let {
            condition = condition and (Students.status eq it.ordinal)
        }

        return condition
    }
} 