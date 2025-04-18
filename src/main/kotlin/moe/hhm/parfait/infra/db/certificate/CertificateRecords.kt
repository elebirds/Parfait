/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.infra.db.certificate

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object CertificateRecords : UUIDTable("certificate_records") {
    val templateId = uuid("template_id") // 模板ID
    val issuedDate = datetime("issued_date").clientDefault { LocalDateTime.now() } // 签发日期
    val issuedBy = varchar("issued_by", 100) // 签发人
    val content = varchar("content", 255) // 证书内容
    val purpose = varchar("purpose", 255).nullable() // 用途
}