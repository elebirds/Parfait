/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.domain.model.certificate

import moe.hhm.parfait.dto.CertificateTemplateDTO
import moe.hhm.parfait.infra.db.certificate.CertificateTemplates
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class CertificateTemplate(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<CertificateTemplate>(CertificateTemplates)

    var name by CertificateTemplates.name
    var type by CertificateTemplates.type
    var category by CertificateTemplates.category
    var description by CertificateTemplates.description
    var contentPath by CertificateTemplates.contentPath

    var isLike by CertificateTemplates.isLike
    var isActive by CertificateTemplates.isActive
    var priority by CertificateTemplates.priority

    var createdAt by CertificateTemplates.createdAt
    var updatedAt by CertificateTemplates.updatedAt

    fun toDTO() = CertificateTemplateDTO(
        uuid = this.id.value,
        name = this.name,
        type = this.type,
        category = this.category,
        description = this.description,
        contentPath = this.contentPath,

        isLike = this.isLike,
        isActive = this.isActive,
        priority = this.priority
    )
}