/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.infra.db.certificate

import moe.hhm.parfait.infra.db.student.Students
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDate
import java.time.LocalDateTime

object CertificateRecords : UUIDTable("certificate_records") {
    val studentId = reference("student_id", Students) // 学生ID
    val templateId = reference("template_id", CertificateTemplates) // 模板ID
    val issuedDate = date("issued_date").clientDefault { LocalDate.now() } // 签发日期
    val issuedBy = varchar("issued_by", 100) // 签发人
    val content = varchar("content", 255) // 证书内容
    val purpose = varchar("purpose", 255).nullable() // 用途
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.now() }
}