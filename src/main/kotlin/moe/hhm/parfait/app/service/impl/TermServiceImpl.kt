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
import java.util.*

class TermServiceImpl(private val rep: TermRepository) : TermService {
    override suspend fun getAll(): List<TermDTO> {
        return rep.findAll().map { it.toDTO() }
    }

    override suspend fun getPage(
        page: Int,
        size: Int
    ): List<TermDTO> {
        return rep.findPage(page, size).map { it.toDTO() }
    }

    override suspend fun getByKey(key: String): Term? {
        return rep.findByKey(key)
    }

    override suspend fun getByUUID(uuid: UUID): Term? {
        return rep.findByUUID(uuid)
    }

    override suspend fun add(student: TermDTO): EntityID<UUID> {
        return rep.add(student)
    }

    override suspend fun delete(uuid: UUID): Boolean {
        return rep.delete(uuid)
    }

    override suspend fun update(student: TermDTO): Boolean {
        return rep.update(student)
    }

    override suspend fun count(): Long {
        return rep.count()
    }
}