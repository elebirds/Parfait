/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.app.service

import moe.hhm.parfait.dto.CertificateDataDTO
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

interface CertificateDataService {
    suspend fun count(): Long
    suspend fun getAll(): List<CertificateDataDTO>
    suspend fun getByUUID(uuid: UUID): CertificateDataDTO?
    suspend fun add(data: CertificateDataDTO): EntityID<UUID>
    suspend fun delete(uuid: UUID): Boolean
    suspend fun updateUsed(uuid: UUID, used: Boolean): Boolean
}