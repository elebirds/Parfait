/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.app.service

import moe.hhm.parfait.dto.CertificateRecordDTO
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

interface CertificateRecordService {
    suspend fun count(): Long
    suspend fun getAll(): List<CertificateRecordDTO>
    suspend fun getByUUID(uuid: UUID): CertificateRecordDTO?
    suspend fun getByTemplateId(templateId: UUID): List<CertificateRecordDTO>
    suspend fun add(record: CertificateRecordDTO): EntityID<UUID>
    suspend fun delete(uuid: UUID): Boolean
} 