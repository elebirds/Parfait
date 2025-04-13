/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.app.service.impl

import moe.hhm.parfait.app.service.CertificateDataService
import moe.hhm.parfait.domain.repository.CertificateDataRepository
import moe.hhm.parfait.dto.CertificateDataDTO
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class CertificateDataServiceImpl(private val rep: CertificateDataRepository) : CertificateDataService {
    override suspend fun count(): Long = rep.count()

    override suspend fun getAll(): List<CertificateDataDTO> = rep.findAll().map { it.toDTO() }

    override suspend fun getByUUID(uuid: UUID): CertificateDataDTO? = rep.findByUUID(uuid)?.toDTO()

    override suspend fun add(data: CertificateDataDTO): EntityID<UUID> = rep.add(data)

    override suspend fun delete(uuid: UUID): Boolean = rep.delete(uuid)
    
    override suspend fun updateUsed(uuid: UUID, used: Boolean): Boolean {
        return rep.updateUsed(uuid, used)
    }
}