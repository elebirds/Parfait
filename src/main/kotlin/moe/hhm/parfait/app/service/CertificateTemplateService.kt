/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.app.service

import moe.hhm.parfait.domain.model.certificate.CertificateTemplate
import moe.hhm.parfait.dto.CertificateTemplateDTO
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

interface CertificateTemplateService {
    suspend fun getCertificates(): List<CertificateTemplateDTO>
    suspend fun getActiveCertificates(): List<CertificateTemplateDTO>
    suspend fun count(): Long
    suspend fun getCertificateTemplateByName(name : String): CertificateTemplate?
    suspend fun add(certificateTemplate: CertificateTemplateDTO): EntityID<UUID>?
    suspend fun delete(uuid:UUID): Boolean
    suspend fun update(certificateTemplate: CertificateTemplateDTO): Boolean
}