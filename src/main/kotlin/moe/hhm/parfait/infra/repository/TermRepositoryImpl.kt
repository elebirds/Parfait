/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.infra.repository

import moe.hhm.parfait.domain.model.certificate.Term
import moe.hhm.parfait.domain.repository.TermRepository
import moe.hhm.parfait.dto.TermDTO
import moe.hhm.parfait.exception.BusinessException
import moe.hhm.parfait.infra.db.DatabaseUtils
import moe.hhm.parfait.infra.db.term.Terms
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import java.time.LocalDateTime
import java.util.*

class TermRepositoryImpl : TermRepository {
    override suspend fun findAll(): List<Term> = DatabaseUtils.dbQuery {
        Term.all().toList()
    }

    override suspend fun findPage(
        page: Int,
        size: Int
    ): List<Term> = DatabaseUtils.dbQuery {
        Term.all()
            .limit(size).offset((page - 1) * size.toLong())
            .toList()
    }

    override suspend fun findByUUID(id: UUID): Term? = DatabaseUtils.dbQuery {
        Term.findById(EntityID(id, Terms))
    }

    override suspend fun findByKey(key: String): Term? = DatabaseUtils.dbQuery {
        Term.find { Terms.field eq key }.firstOrNull()
    }

    override suspend fun findTerm(field: String, context: String?, language: String?): Term? = DatabaseUtils.dbQuery {
        val query = buildTermQuery(field, context, language)
        Term.find(query).firstOrNull()
    }
    
    override suspend fun findByFields(fields: List<String>): Map<String, Term> = DatabaseUtils.dbQuery {
        if (fields.isEmpty()) return@dbQuery emptyMap<String, Term>()
        
        val terms = Term.find { Terms.field inList fields }.toList()
        terms.associateBy { it.field }
    }
    
    override suspend fun findByLanguage(language: String): List<Term> = DatabaseUtils.dbQuery {
        Term.find { Terms.language eq language }.toList()
    }

    override suspend fun isExistByUUID(uuid: UUID): Boolean = DatabaseUtils.dbQuery {
        Term.find { Terms.id eq uuid }.count() > 0
    }

    override suspend fun isExistByKey(key: String): Boolean = DatabaseUtils.dbQuery {
        Term.find { Terms.field eq key }.count() > 0
    }

    override suspend fun isExistByKeyExceptMe(key: String, uuid: UUID): Boolean = DatabaseUtils.dbQuery {
        Term.find { (Terms.field eq key) and (Terms.id neq uuid) }.count() > 0
    }

    override suspend fun add(data: TermDTO): EntityID<UUID> {
        val exists = DatabaseUtils.dbQuery {
            val query = buildTermQuery(data.field, data.context, data.language)
            Term.find(query).count() > 0
        }
        
        if (exists) {
            val termKey = buildTermKey(data.field, data.context, data.language)
            throw BusinessException("term.error.exists", termKey)
        }

        val now = LocalDateTime.now()
        return DatabaseUtils.dbQuery {
            Terms.insertAndGetId {
                it[field] = data.field
                it[context] = data.context
                it[language] = data.language
                it[term] = data.term
                it[isSystem] = false
                it[createdAt] = now
                it[updatedAt] = now
            }
        }
    }

    override suspend fun update(data: TermDTO): Boolean {
        if (data.uuid == null || !isExistByUUID(data.uuid)) throw BusinessException("term.error.uuid.notExists")

        val exists = DatabaseUtils.dbQuery {
            val query = buildTermQuery(data.field, data.context, data.language, data.uuid)
            Term.find(query).count() > 0
        }
        
        if (exists) {
            val termKey = buildTermKey(data.field, data.context, data.language)
            throw BusinessException("term.error.exists", termKey)
        }

        val now = LocalDateTime.now()
        return DatabaseUtils.dbQuery {
            Terms.update({ Terms.id eq data.uuid }) {
                it[field] = data.field
                it[context] = data.context
                it[language] = data.language
                it[term] = data.term
                it[updatedAt] = now
            }
        } > 0
    }

    override suspend fun delete(uuid: UUID): Boolean {
        return DatabaseUtils.dbQuery {
            Terms.deleteWhere { Op.build { id eq uuid } }
        } > 0
    }

    override suspend fun count(): Long = DatabaseUtils.dbQuery {
        Term.all().count()
    }
    
    private fun buildTermQuery(field: String, context: String?, language: String?, excludeUuid: UUID? = null): Op<Boolean> {
        return Op.build { 
            (Terms.field eq field) and 
            (if(context != null) Terms.context eq context else Terms.context.isNull()) and 
            (if(language != null) Terms.language eq language else Terms.language.isNull()) and
            (if(excludeUuid != null) Terms.id neq excludeUuid else Op.TRUE)
        }
    }
    
    private fun buildTermKey(field: String, context: String?, language: String?): String {
        return when {
            context != null && language != null -> "${field}_${context}/${language}"
            context != null -> "${field}_${context}"
            language != null -> "${field}/${language}"
            else -> field
        }
    }
}