package moe.hhm.parfait.domain.model.certificate

import moe.hhm.parfait.infra.db.certificate.CertificateDatas
import moe.hhm.parfait.infra.db.certificate.CertificateRecords
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class CertificateData(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<CertificateData>(CertificateDatas)

    var template by CertificateTemplate referencedOn CertificateRecords.templateId
    var data by CertificateDatas.data
}