/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.domain.repository

import moe.hhm.parfait.domain.model.gpa.GpaStandard
import moe.hhm.parfait.dto.GpaStandardDTO
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

interface GpaRepository {
    suspend fun findAll(): List<GpaStandard>
    suspend fun findByUUID(uuid: UUID): GpaStandard?
    suspend fun findByName(name: String): GpaStandard?
    suspend fun add(gpaStandard: GpaStandardDTO): EntityID<UUID>
    suspend fun delete(uuid: UUID): Boolean
    suspend fun count(): Long
    suspend fun update(gpaStandard: GpaStandardDTO): Boolean
    suspend fun isExistByName(name: String): Boolean
    suspend fun getDefault(): GpaStandard
    fun getDefaultSync(): GpaStandard
}