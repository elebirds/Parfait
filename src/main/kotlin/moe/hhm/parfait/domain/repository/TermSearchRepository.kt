/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.domain.repository

import moe.hhm.parfait.domain.model.certificate.Term
import moe.hhm.parfait.ui.component.dialog.TermSearchFilterCriteria

/**
 * 术语搜索仓储接口
 */
interface TermSearchRepository {
    /**
     * 根据条件搜索术语
     * @param criteria 搜索条件
     * @return 符合条件的术语列表
     */
    suspend fun searchTerms(criteria: TermSearchFilterCriteria): List<Term>

    /**
     * 根据条件分页搜索术语
     * @param criteria 搜索条件
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 符合条件的术语列表
     */
    suspend fun searchTermsPage(criteria: TermSearchFilterCriteria, page: Int, size: Int): List<Term>

    /**
     * 统计符合搜索条件的术语数量
     * @param criteria 搜索条件
     * @return 符合条件的术语数量
     */
    suspend fun countSearchResults(criteria: TermSearchFilterCriteria): Long
} 