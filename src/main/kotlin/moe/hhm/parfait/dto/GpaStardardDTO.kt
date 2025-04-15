/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.dto

import moe.hhm.parfait.infra.db.gpa.GpaStandards
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import java.time.LocalDateTime
import java.util.*

enum class GpaType {
    NORMAL,
    LIKE,
    DEFAULT
}

data class GpaStandardDTO(
    val uuid: UUID? = null,
    val name: String,
    val description: String,
    val category: String,
    val purpose: String,
    val isDefault: Boolean,
    val isLike: Boolean,
    val mapping: GpaMappingDTO,
    val createdAt: LocalDateTime? = null
) {
    fun <T : Any> into(it: UpdateBuilder<T>) {
        it[GpaStandards.name] = name
        it[GpaStandards.category] = category
        it[GpaStandards.description] = description
        it[GpaStandards.purpose] = purpose
        it[GpaStandards.mapping] = mapping.toString()
        it[GpaStandards.isLike] = isLike
    }

    val type: GpaType
        get() = when {
            isDefault -> GpaType.DEFAULT
            isLike -> GpaType.LIKE
            else -> GpaType.NORMAL
        }
}