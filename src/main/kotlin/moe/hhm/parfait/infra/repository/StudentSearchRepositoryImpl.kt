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
import moe.hhm.parfait.ui.component.dialog.AdvancedFilterCriteria
import moe.hhm.parfait.ui.component.dialog.MatchType
import moe.hhm.parfait.ui.component.dialog.SearchFilterCriteria
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greater
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
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

    override suspend fun searchAdvancedStudents(criteria: AdvancedFilterCriteria): List<Student> =
        DatabaseUtils.dbQuery {
            Student.find { buildAdvancedSearchCondition(criteria) }.toList()
        }

    override suspend fun searchAdvancedStudentsPage(
        criteria: AdvancedFilterCriteria,
        page: Int,
        size: Int
    ): List<Student> = DatabaseUtils.dbQuery {
        Student.find { buildAdvancedSearchCondition(criteria) }
            .limit(size).offset((page - 1) * size.toLong())
            .toList()
    }

    override suspend fun countAdvancedSearchResults(criteria: AdvancedFilterCriteria): Long = DatabaseUtils.dbQuery {
        Student.find { buildAdvancedSearchCondition(criteria) }.count()
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

    /**
     * 构建高级搜索条件
     */
    private fun buildAdvancedSearchCondition(criteria: AdvancedFilterCriteria): org.jetbrains.exposed.sql.Op<Boolean> {
        var condition = org.jetbrains.exposed.sql.Op.build { Students.id.isNotNull() }

        // 学号条件
        if (criteria.studentId.isNotEmpty()) {
            condition = condition and when (criteria.studentIdMatchType) {
                MatchType.EXACT -> Students.studentId eq criteria.studentId
                MatchType.FUZZY -> Students.studentId like "%${criteria.studentId}%"
                MatchType.GREATER_THAN -> Students.studentId greater criteria.studentId
                else -> Students.studentId like "%${criteria.studentId}%"
            }
        }

        // 姓名条件
        if (criteria.name.isNotEmpty()) {
            condition = condition and when (criteria.nameMatchType) {
                MatchType.EXACT -> Students.name eq criteria.name
                MatchType.FUZZY -> Students.name like "%${criteria.name}%"
                else -> Students.name like "%${criteria.name}%"  // GREATER_THAN不适用于姓名，使用模糊搜索
            }
        }

        // 性别条件（多选）
        if (criteria.genders.isNotEmpty()) {
            val genderOrdinals = criteria.genders.map { it.ordinal }
            condition = condition and (Students.gender inList genderOrdinals)
        }

        // 院系条件（多选）
        if (criteria.departments.isNotEmpty()) {
            val departmentConditions = criteria.departments.map { department ->
                Students.department eq department
            }
            condition = condition and departmentConditions.reduce { acc, op -> acc or op }
        }

        // 专业条件（多选）
        if (criteria.majors.isNotEmpty()) {
            val majorConditions = criteria.majors.map { major ->
                Students.major eq major
            }
            condition = condition and majorConditions.reduce { acc, op -> acc or op }
        }

        // 年级条件 - 添加小于匹配支持
        criteria.grade?.let {
            condition = condition and when (criteria.gradeMatchType) {
                MatchType.EXACT -> Students.grade eq it
                MatchType.GREATER_THAN -> Students.grade greater it
                MatchType.LESS_THAN -> Students.grade less it
                else -> Students.grade eq it
            }
        }

        // 班级条件（多选）
        if (criteria.classGroups.isNotEmpty()) {
            val classGroupConditions = criteria.classGroups.map { classGroup ->
                Students.classGroup eq classGroup
            }
            condition = condition and classGroupConditions.reduce { acc, op -> acc or op }
        }

        // 状态条件（多选）
        if (criteria.statuses.isNotEmpty()) {
            val statusOrdinals = criteria.statuses.map { it.ordinal }
            condition = condition and (Students.status inList statusOrdinals)
        }

        return condition
    }
} 