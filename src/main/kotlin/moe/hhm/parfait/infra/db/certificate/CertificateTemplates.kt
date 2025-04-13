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
    val name = varchar("name", 100).uniqueIndex() // 模板名称
    val type = varchar("type", 50) // 模板类型（成绩单、在读证明等）
    val category = varchar("category", 50) // 语言（中文、英文等）
    val description = text("description") // 描述

    val contentPath = text("content_path") // 内容模板

    val isLike = bool("is_like").default(false) // 是否喜欢
    val isActive = bool("is_active").default(true) // 是否启用
    val priority = integer("priority").default(50) // 优先级，越大越高

    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.now() }

    fun init() {
        if (CertificateTemplate.count() == 0L) {
            // 创建默认证书模板
            CertificateTemplate.new {
                name = "默认模板"
                type = "成绩证明"
                contentPath = "jar::score_default.docx"
                category = "中英双语"
                description = "默认成绩证明模板，显示学生的基本信息与加权平均分"
                isActive = true
            }
        }
    }
}