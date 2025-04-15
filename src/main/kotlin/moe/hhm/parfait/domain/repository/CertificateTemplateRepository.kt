/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.domain.repository

import moe.hhm.parfait.domain.model.certificate.CertificateTemplate
import moe.hhm.parfait.dto.CertificateTemplateDTO
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

interface CertificateTemplateRepository {
    suspend fun isExistByNameExceptMe(name: String, uuid: UUID): Boolean
    suspend fun findAll(): List<CertificateTemplate>
    suspend fun findActiveAll(): List<CertificateTemplate>
    suspend fun findByUUID(uuid: UUID): CertificateTemplate?
    suspend fun findByName(name: String): CertificateTemplate?
    suspend fun add(data: CertificateTemplateDTO): EntityID<UUID>
    suspend fun delete(uuid: UUID): Boolean
    suspend fun count(): Long
    suspend fun update(data: CertificateTemplateDTO): Boolean
    suspend fun isExistByName(name: String): Boolean
}