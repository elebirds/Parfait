/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.infra.db.certificate

import moe.hhm.parfait.domain.model.certificate.CertificateTemplate
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object CertificateTemplates : UUIDTable("certificate_templates") {
    val templateName = varchar("template_name", 100).uniqueIndex() // 模板名称
    val templateType = varchar("template_type", 50) // 模板类型（成绩单、在读证明等）
    val contentPath = text("content_path") // 内容模板
    val language = varchar("language", 20).default("中文") // 语言（中文、英文等）
    val isActive = bool("is_active").default(true) // 是否启用
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.now() }

    fun init() {
        if (CertificateTemplate.count() == 0L) {
            // 创建默认证书模板
            CertificateTemplate.new {
                templateName = "标准成绩证明模板"
                templateType = "CERTIFICATE"
                contentPath = "jar::default_english.docx"
                language = "中英双语"
                isActive = true
            }
        }
    }
}