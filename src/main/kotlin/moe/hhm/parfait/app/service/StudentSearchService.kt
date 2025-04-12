/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.app.service

import moe.hhm.parfait.dto.StudentDTO
import moe.hhm.parfait.ui.component.dialog.AdvancedFilterCriteria
import moe.hhm.parfait.ui.component.dialog.SearchFilterCriteria

/**
 * 学生搜索服务接口
 */
interface StudentSearchService {
    /**
     * 根据条件搜索学生
     * @param criteria 搜索条件
     * @return 符合条件的学生DTO列表
     */
    suspend fun searchStudents(criteria: SearchFilterCriteria): List<StudentDTO>
    
    /**
     * 根据条件分页搜索学生
     * @param criteria 搜索条件
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 符合条件的学生DTO列表
     */
    suspend fun searchStudentsPage(criteria: SearchFilterCriteria, page: Int, size: Int): List<StudentDTO>
    
    /**
     * 统计符合搜索条件的学生数量
     * @param criteria 搜索条件
     * @return 符合条件的学生数量
     */
    suspend fun countSearchResults(criteria: SearchFilterCriteria): Long

    /**
     * 根据高级筛选条件搜索学生
     * @param criteria 高级筛选条件
     * @return 符合条件的学生DTO列表
     */
    suspend fun searchAdvancedStudents(criteria: AdvancedFilterCriteria): List<StudentDTO>
    
    /**
     * 根据高级筛选条件分页搜索学生
     * @param criteria 高级筛选条件
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 符合条件的学生DTO列表
     */
    suspend fun searchAdvancedStudentsPage(criteria: AdvancedFilterCriteria, page: Int, size: Int): List<StudentDTO>
    
    /**
     * 统计符合高级筛选条件的学生数量
     * @param criteria 高级筛选条件
     * @return 符合条件的学生数量
     */
    suspend fun countAdvancedSearchResults(criteria: AdvancedFilterCriteria): Long
} 