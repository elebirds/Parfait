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
    suspend fun getAll(): List<TermDTO>
    suspend fun getPage(page: Int, size: Int): List<TermDTO>
    suspend fun getByKey(key: String): Term?
    suspend fun getByUUID(uuid: UUID): Term?
    suspend fun add(student: TermDTO): EntityID<UUID>
    suspend fun delete(uuid: UUID): Boolean
    suspend fun update(student: TermDTO): Boolean
    suspend fun count(): Long
}