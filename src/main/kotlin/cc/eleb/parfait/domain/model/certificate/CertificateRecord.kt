package cc.eleb.parfait.domain.model.certificate

import cc.eleb.parfait.domain.model.student.Student
import cc.eleb.parfait.infra.db.certificate.CertificateRecords
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