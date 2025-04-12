/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.app.service.impl

import moe.hhm.parfait.app.service.GpaStandardService
import moe.hhm.parfait.domain.model.gpa.GpaStandard
import moe.hhm.parfait.domain.repository.GpaRepository
import moe.hhm.parfait.dto.GpaStandardDTO
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class GpaStandardServiceImpl(private val rep: GpaRepository) : GpaStandardService  {
    override suspend fun getAllGpaStandards(): List<GpaStandardDTO> {
        return rep.findAll().map { it.toDTO() }
    }

    override suspend fun count(): Long {
        return rep.count()
    }

    override suspend fun getGpaStandardByName(name: String): GpaStandard? {
        return rep.findByName(name)
    }

    override suspend fun addGpaStandard(gpaStandard: GpaStandardDTO): EntityID<UUID> {
        return rep.add(gpaStandard)
    }

    override suspend fun deleteGpaStandard(uuid: UUID): Boolean {
        return rep.delete(uuid)
    }

    override suspend fun updateGpaStandard(gpaStandard: GpaStandardDTO): Boolean {
        return rep.update(gpaStandard)
    }
}