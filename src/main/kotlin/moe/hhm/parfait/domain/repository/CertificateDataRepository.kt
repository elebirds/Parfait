/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.domain.repository

import moe.hhm.parfait.domain.model.certificate.CertificateData
import moe.hhm.parfait.dto.CertificateDataDTO
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

interface CertificateDataRepository {
    suspend fun count(): Long
    suspend fun findAll(): List<CertificateData>
    suspend fun findByUUID(uuid: UUID): CertificateData?
    suspend fun add(data: CertificateDataDTO): EntityID<UUID>
    suspend fun delete(uuid: UUID): Boolean
    suspend fun deleteUnused(): Int
    suspend fun updateUsed(uuid: UUID, used: Boolean): Boolean
}