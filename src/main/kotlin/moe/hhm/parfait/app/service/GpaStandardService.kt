/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.app.service

import moe.hhm.parfait.domain.model.gpa.GpaStandard
import moe.hhm.parfait.dto.GpaStandardDTO
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

interface GpaStandardService {
    suspend fun getAllGpaStandards(): List<GpaStandardDTO>
    suspend fun count(): Long
    suspend fun getGpaStandardByName(name: String): GpaStandard?
    suspend fun addGpaStandard(gpaStandard: GpaStandardDTO): EntityID<UUID>
    suspend fun deleteGpaStandard(uuid: UUID): Boolean
    suspend fun updateGpaStandard(gpaStandard: GpaStandardDTO): Boolean
    suspend fun loadDefault(): GpaStandardDTO
    fun getDefault(): GpaStandardDTO
}