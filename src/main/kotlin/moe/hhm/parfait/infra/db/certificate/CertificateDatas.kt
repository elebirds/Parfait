/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.infra.db.certificate

import org.jetbrains.exposed.dao.id.UUIDTable


object CertificateDatas : UUIDTable("certificate_datas") {
    val template = reference("template_id", CertificateTemplates).uniqueIndex()
    val data = binary("data", 64)
}