/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.app.service.impl

import moe.hhm.parfait.app.service.TermService
import moe.hhm.parfait.domain.model.certificate.Term
import moe.hhm.parfait.domain.repository.TermRepository
import moe.hhm.parfait.dto.TermDTO
import org.jetbrains.exposed.dao.id.EntityID
import org.slf4j.LoggerFactory
import java.util.*

class TermServiceImpl(private val rep: TermRepository) : TermService {
    private val logger = LoggerFactory.getLogger(this::class.java)
    
    override suspend fun getAll(): List<TermDTO> {
        return rep.findAll().map { it.toDTO() }
    }

    override suspend fun getPage(
        page: Int,
        size: Int
    ): List<TermDTO> {
        return rep.findPage(page, size).map { it.toDTO() }
    }
    
    override suspend fun getTerm(field: String, context: String?, language: String?): Term? {
        return rep.findTerm(field, context, language)
    }
    
    override suspend fun getByFields(fields: List<String>): Map<String, Term> {
        if (fields.isEmpty()) return emptyMap()
        return rep.findByFields(fields)
    }
    
    override suspend fun getByLanguage(language: String): List<Term> {
        return rep.findByLanguage(language)
    }

    override suspend fun getByUUID(uuid: UUID): Term? {
        return rep.findByUUID(uuid)
    }

    override suspend fun add(term: TermDTO): EntityID<UUID> {
        return rep.add(term)
    }

    override suspend fun delete(uuid: UUID): Boolean {
        return rep.delete(uuid)
    }

    override suspend fun update(term: TermDTO): Boolean {
        return rep.update(term)
    }

    override suspend fun count(): Long {
        return rep.count()
    }
}