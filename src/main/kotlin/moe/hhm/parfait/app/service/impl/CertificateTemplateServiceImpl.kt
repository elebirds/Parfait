/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.app.service.impl

import moe.hhm.parfait.app.service.CertificateTemplateService
import moe.hhm.parfait.domain.model.certificate.CertificateTemplate
import moe.hhm.parfait.domain.repository.CertificateTemplateRepository
import moe.hhm.parfait.dto.CertificateTemplateDTO
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class CertificateTemplateServiceImpl(private val rep:CertificateTemplateRepository) : CertificateTemplateService{
    override suspend fun getCertificates(): List<CertificateTemplateDTO> {
        return rep.findAll().map { it.toDTO() }
    }
    override suspend fun count(): Long {
        return rep.count()
    }
    override suspend fun getCertificateTemplateByName(name: String): CertificateTemplate? {
        return rep.findByName(name)
    }
    override suspend fun addGpaStandard(certificateTemplate: CertificateTemplateDTO): EntityID<UUID>? {
        return rep.add(certificateTemplate)
    }
    override suspend fun deleteGpaStandard(uuid: UUID): Boolean {
        return rep.delete(uuid)
    }
    override suspend fun updateGpaStandard(certificateTemplate: CertificateTemplateDTO): Boolean {
        return rep.update(certificateTemplate)
    }
}