/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.app.service

import moe.hhm.parfait.domain.model.certificate.Term
import moe.hhm.parfait.dto.TermDTO
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

interface TermService {
    // 基本CRUD操作
    suspend fun getAll(): List<TermDTO>
    suspend fun getPage(page: Int, size: Int): List<TermDTO>
    suspend fun getByUUID(uuid: UUID): Term?
    suspend fun add(term: TermDTO): EntityID<UUID>
    suspend fun delete(uuid: UUID): Boolean
    suspend fun update(term: TermDTO): Boolean
    suspend fun count(): Long
    
    // 原始方法，保留向后兼容
    suspend fun getByKey(key: String): Term?
    
    // 新的灵活查询方法，替代之前的多个方法
    suspend fun getTerm(field: String, context: String? = null, language: String? = null): Term?
    
    // 批量查询方法
    suspend fun getByFields(fields: List<String>): Map<String, Term>
    suspend fun getByLanguage(language: String): List<Term>
    
    // 预加载方法
    suspend fun preloadTerms(language: String? = null)
}