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
import moe.hhm.parfait.infra.db.certificate.Terms
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
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
        Term.find { Terms.key eq key }.firstOrNull()
    }

    override suspend fun isExistByUUID(uuid: UUID): Boolean = DatabaseUtils.dbQuery {
        Term.find { Terms.id eq uuid }.count() > 0
    }

    override suspend fun isExistByKey(key: String): Boolean = DatabaseUtils.dbQuery {
        Term.find { Terms.key eq key }.count() > 0
    }

    override suspend fun isExistByKeyExceptMe(key: String, uuid: UUID): Boolean = DatabaseUtils.dbQuery {
        Term.find { (Terms.key eq key) and (Terms.id neq uuid) }.count() > 0
    }

    override suspend fun add(data: TermDTO): EntityID<UUID> {
        if (isExistByKey(data.key)) throw BusinessException("term.error.key.exists", data.key)
        return DatabaseUtils.dbQuery {
            Terms.insertAndGetId {
                it[key] = data.key
                it[term] = data.term
            }
        }
    }

    override suspend fun update(data: TermDTO): Boolean {
        if (data.uuid == null || !isExistByUUID(data.uuid)) throw BusinessException("term.error.uuid.notExists")
        if (isExistByKeyExceptMe(data.key, data.uuid)) throw BusinessException("term.error.key.exists", data.key)
        return DatabaseUtils.dbQuery { // 使用uuid更新
            Terms.update({ Terms.id eq data.uuid }) {
                it[key] = data.key
                it[term] = data.term
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
}