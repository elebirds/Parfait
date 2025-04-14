/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.domain.model.certificate

import moe.hhm.parfait.dto.CertificateRecordDTO
import moe.hhm.parfait.infra.db.certificate.CertificateRecords
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class CertificateRecord(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<CertificateRecord>(CertificateRecords)

    var template by CertificateRecords.templateId
    var issuedDate by CertificateRecords.issuedDate
    var issuedBy by CertificateRecords.issuedBy
    var content by CertificateRecords.content
    var purpose by CertificateRecords.purpose

    fun toDTO() = CertificateRecordDTO(
        uuid = this.id.value,
        templateId = this.template,
        issuedDate = this.issuedDate,
        issuedBy = this.issuedBy,
        content = this.content,
        purpose = this.purpose
    )
}