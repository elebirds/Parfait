/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.domain.model.gpa

import moe.hhm.parfait.dto.GpaMappingDTO
import moe.hhm.parfait.dto.GpaStandardDTO
import moe.hhm.parfait.dto.StudentDTO
import moe.hhm.parfait.infra.db.gpa.GpaStandards
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class GpaStandard(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<GpaStandard>(GpaStandards)

    var name by GpaStandards.name
    var description by GpaStandards.description
    var category by GpaStandards.category
    var purpose by GpaStandards.purpose
    var mapping by GpaStandards.mapping
    var isDefault by GpaStandards.isDefault
    var isLike by GpaStandards.isLike

    var createdAt by GpaStandards.createdAt
    var updatedAt by GpaStandards.updatedAt

    fun toDTO() = GpaStandardDTO(
        name = name,
        description = description,
        category = category,
        mapping = GpaMappingDTO(mapping),
        purpose = purpose,
        isDefault = isDefault,
        isLike = isLike,
        createdAt = createdAt
    )
}