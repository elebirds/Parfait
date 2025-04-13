/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.app.service.impl

import moe.hhm.parfait.app.service.CertificateRecordService
import moe.hhm.parfait.domain.repository.CertificateRecordRepository
import moe.hhm.parfait.dto.CertificateRecordDTO
import org.jetbrains.exposed.dao.id.EntityID
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.UUID

class CertificateRecordServiceImpl : CertificateRecordService, KoinComponent {
    private val repository: CertificateRecordRepository by inject()

    override suspend fun count(): Long {
        return repository.count()
    }

    override suspend fun getAll(): List<CertificateRecordDTO> {
        return repository.findAll().map { it.toDTO() }
    }

    override suspend fun getByUUID(uuid: UUID): CertificateRecordDTO? {
        return repository.findByUUID(uuid)?.toDTO()
    }

    override suspend fun getByTemplateId(templateId: UUID): List<CertificateRecordDTO> {
        return repository.findByTemplateId(templateId).map { it.toDTO() }
    }

    override suspend fun add(record: CertificateRecordDTO): EntityID<UUID> {
        return repository.add(record)
    }

    override suspend fun delete(uuid: UUID): Boolean {
        return repository.delete(uuid)
    }
} 