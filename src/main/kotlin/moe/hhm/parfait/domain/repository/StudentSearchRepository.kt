/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.domain.repository

import moe.hhm.parfait.domain.model.student.Student
import moe.hhm.parfait.ui.component.dialog.SearchFilterCriteria

/**
 * 学生搜索仓储接口
 */
interface StudentSearchRepository {
    /**
     * 根据条件搜索学生
     * @param criteria 搜索条件
     * @return 符合条件的学生列表
     */
    suspend fun searchStudents(criteria: SearchFilterCriteria): List<Student>
    
    /**
     * 根据条件分页搜索学生
     * @param criteria 搜索条件
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 符合条件的学生列表
     */
    suspend fun searchStudentsPage(criteria: SearchFilterCriteria, page: Int, size: Int): List<Student>
    
    /**
     * 统计符合搜索条件的学生数量
     * @param criteria 搜索条件
     * @return 符合条件的学生数量
     */
    suspend fun countSearchResults(criteria: SearchFilterCriteria): Long
} 