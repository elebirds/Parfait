/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.infra.repository

import moe.hhm.parfait.domain.model.certificate.Term
import moe.hhm.parfait.domain.repository.TermSearchRepository
import moe.hhm.parfait.infra.db.DatabaseUtils
import moe.hhm.parfait.infra.db.term.Terms
import moe.hhm.parfait.ui.component.dialog.TermSearchFilterCriteria
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.and

/**
 * 术语搜索仓储实现类
 */
class TermSearchRepositoryImpl : TermSearchRepository {

    override suspend fun searchTerms(criteria: TermSearchFilterCriteria): List<Term> = DatabaseUtils.dbQuery {
        Term.find { buildSearchCondition(criteria) }
            .orderBy(Terms.field to SortOrder.ASC)
            .orderBy(Terms.context to SortOrder.ASC_NULLS_FIRST)
            .orderBy(Terms.language to SortOrder.ASC_NULLS_FIRST)
            .toList()
    }

    override suspend fun searchTermsPage(
        criteria: TermSearchFilterCriteria,
        page: Int,
        size: Int
    ): List<Term> = DatabaseUtils.dbQuery {
        Term.find { buildSearchCondition(criteria) }
            .orderBy(Terms.field to SortOrder.ASC)
            .orderBy(Terms.context to SortOrder.ASC_NULLS_FIRST)
            .orderBy(Terms.language to SortOrder.ASC_NULLS_FIRST)
            .limit(size).offset(((page - 1) * size).toLong())
            .toList()
    }

    override suspend fun countSearchResults(criteria: TermSearchFilterCriteria): Long = DatabaseUtils.dbQuery {
        Term.find { buildSearchCondition(criteria) }.count()
    }

    /**
     * 构建搜索条件
     */
    private fun buildSearchCondition(criteria: TermSearchFilterCriteria): org.jetbrains.exposed.sql.Op<Boolean> {
        var condition = org.jetbrains.exposed.sql.Op.build { Terms.id.isNotNull() }

        // 字段条件
        if (criteria.field.isNotEmpty()) {
            condition = condition and (Terms.field like "%${criteria.field}%")
        }

        // 上下文条件
        if (criteria.context.isNotEmpty()) {
            condition = condition and (Terms.context like "%${criteria.context}%")
        }

        // 语言条件
        if (criteria.language.isNotEmpty()) {
            condition = condition and (Terms.language like "%${criteria.language}%")
        }

        // 术语值条件
        if (criteria.term.isNotEmpty()) {
            condition = condition and (Terms.term like "%${criteria.term}%")
        }

        return condition
    }
} 