/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.dto

import moe.hhm.parfait.infra.db.certificate.CertificateTemplates
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import java.util.UUID

data class CertificateTemplateDTO(
    val uuid: UUID? = null,
    val name: String,
    val type: String,
    val category: String,
    val description: String,
    val contentPath: String,

    val isLike: Boolean,
    val isActive: Boolean,
    val priority: Int
) {
    fun <T : Any> into(it: UpdateBuilder<T>) {
        it[CertificateTemplates.name] = name
        it[CertificateTemplates.type] = type
        it[CertificateTemplates.category] = category
        it[CertificateTemplates.description] = description
        it[CertificateTemplates.contentPath] = contentPath
        it[CertificateTemplates.isLike] = isLike
        it[CertificateTemplates.isActive] = isActive
        it[CertificateTemplates.priority] = priority
    }
}