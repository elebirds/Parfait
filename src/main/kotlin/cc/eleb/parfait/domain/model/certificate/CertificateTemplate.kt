package cc.eleb.parfait.domain.model.certificate

import cc.eleb.parfait.infra.db.certificate.CertificateTemplates
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class CertificateTemplate(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<CertificateTemplate>(CertificateTemplates)

    var templateName by CertificateTemplates.templateName
    var templateType by CertificateTemplates.templateType
    var contentPath by CertificateTemplates.contentPath
    var language by CertificateTemplates.language
    var isActive by CertificateTemplates.isActive
    var createdAt by CertificateTemplates.createdAt
    var updatedAt by CertificateTemplates.updatedAt
}