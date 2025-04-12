/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.app.service.impl

import moe.hhm.parfait.app.service.StudentSearchService
//import moe.hhm.parfait.domain.model.student.toDTO
import moe.hhm.parfait.domain.repository.StudentSearchRepository
import moe.hhm.parfait.dto.StudentDTO
import moe.hhm.parfait.ui.component.dialog.AdvancedFilterCriteria
import moe.hhm.parfait.ui.component.dialog.SearchFilterCriteria
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * 学生搜索服务实现类
 */
class StudentSearchServiceImpl : StudentSearchService, KoinComponent {
    private val repository: StudentSearchRepository by inject()

    override suspend fun searchStudents(criteria: SearchFilterCriteria): List<StudentDTO> {
        return repository.searchStudents(criteria).map { it.toDTO() }
    }

    override suspend fun searchStudentsPage(criteria: SearchFilterCriteria, page: Int, size: Int): List<StudentDTO> {
        return repository.searchStudentsPage(criteria, page, size).map { it.toDTO() }
    }

    override suspend fun countSearchResults(criteria: SearchFilterCriteria): Long {
        return repository.countSearchResults(criteria)
    }
    
    override suspend fun searchAdvancedStudents(criteria: AdvancedFilterCriteria): List<StudentDTO> {
        return repository.searchAdvancedStudents(criteria).map { it.toDTO() }
    }
    
    override suspend fun searchAdvancedStudentsPage(criteria: AdvancedFilterCriteria, page: Int, size: Int): List<StudentDTO> {
        return repository.searchAdvancedStudentsPage(criteria, page, size).map { it.toDTO() }
    }
    
    override suspend fun countAdvancedSearchResults(criteria: AdvancedFilterCriteria): Long {
        return repository.countAdvancedSearchResults(criteria)
    }
} 