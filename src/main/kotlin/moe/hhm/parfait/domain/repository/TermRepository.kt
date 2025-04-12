/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.domain.repository

import moe.hhm.parfait.domain.model.certificate.Term
import moe.hhm.parfait.dto.TermDTO
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

interface TermRepository {
    suspend fun findAll(): List<Term>
    suspend fun findPage(page: Int, size: Int): List<Term>
    suspend fun findByUUID(id: UUID): Term?
    suspend fun findByKey(key: String): Term?
    suspend fun isExistByUUID(uuid: UUID): Boolean
    suspend fun isExistByKey(key: String): Boolean
    suspend fun isExistByKeyExceptMe(key: String, uuid: UUID): Boolean
    suspend fun add(data: TermDTO): EntityID<UUID>
    suspend fun update(data: TermDTO): Boolean
    suspend fun delete(uuid: UUID): Boolean
    suspend fun count(): Long
}