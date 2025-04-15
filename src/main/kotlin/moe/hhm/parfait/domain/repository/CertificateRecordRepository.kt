/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.domain.repository

import moe.hhm.parfait.domain.model.certificate.CertificateRecord
import moe.hhm.parfait.dto.CertificateRecordDTO
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

interface CertificateRecordRepository {
    suspend fun count(): Long
    suspend fun findAll(): List<CertificateRecord>
    suspend fun findByUUID(uuid: UUID): CertificateRecord?
    suspend fun findByTemplateId(templateId: UUID): List<CertificateRecord>
    suspend fun add(record: CertificateRecordDTO): EntityID<UUID>
    suspend fun delete(uuid: UUID): Boolean
} 