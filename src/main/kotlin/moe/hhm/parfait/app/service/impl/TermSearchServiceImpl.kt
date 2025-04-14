/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.app.service.impl

import moe.hhm.parfait.app.service.TermSearchService
import moe.hhm.parfait.domain.repository.TermSearchRepository
import moe.hhm.parfait.dto.TermDTO
import moe.hhm.parfait.ui.component.dialog.TermSearchFilterCriteria
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * 术语搜索服务实现类
 */
class TermSearchServiceImpl : TermSearchService, KoinComponent {
    private val repository: TermSearchRepository by inject()

    override suspend fun searchTerms(criteria: TermSearchFilterCriteria): List<TermDTO> {
        return repository.searchTerms(criteria).map { it.toDTO() }
    }

    override suspend fun searchTermsPage(
        criteria: TermSearchFilterCriteria,
        page: Int,
        size: Int
    ): List<TermDTO> {
        return repository.searchTermsPage(criteria, page, size).map { it.toDTO() }
    }

    override suspend fun countSearchResults(criteria: TermSearchFilterCriteria): Long {
        return repository.countSearchResults(criteria)
    }
} 