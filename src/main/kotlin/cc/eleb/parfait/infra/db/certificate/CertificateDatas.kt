package cc.eleb.parfait.infra.db.certificate

import org.jetbrains.exposed.dao.id.UUIDTable


object CertificateDatas : UUIDTable("certificate_datas") {
    val uuid = reference("template_id", CertificateTemplates).uniqueIndex()
    val data = binary("data", 64)
}