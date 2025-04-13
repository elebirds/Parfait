/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.domain.model.certificate

import moe.hhm.parfait.domain.model.student.Student
import moe.hhm.parfait.infra.db.certificate.CertificateRecords
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class CertificateRecord(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<CertificateRecord>(CertificateRecords)

    var student by Student referencedOn CertificateRecords.studentId
    var template by CertificateTemplate referencedOn CertificateRecords.templateId
    var certificateNumber by CertificateRecords.certificateNumber
    var issuedDate by CertificateRecords.issuedDate
    var expiryDate by CertificateRecords.expiryDate
    var issuedBy by CertificateRecords.issuedBy
    var content by CertificateRecords.content
    var purpose by CertificateRecords.purpose

    var createdAt by CertificateRecords.createdAt
    var updatedAt by CertificateRecords.updatedAt
}