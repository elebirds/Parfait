/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.domain.model.certificate

import moe.hhm.parfait.dto.TermDTO
import moe.hhm.parfait.infra.db.term.Terms
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class Term(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Term>(Terms)

    var field by Terms.field
    var context by Terms.context
    var language by Terms.language
    var term by Terms.term
    var isSystem by Terms.isSystem
    var createdAt by Terms.createdAt
    var updatedAt by Terms.updatedAt

    fun toDTO(): TermDTO {
        return TermDTO(
            uuid = this.id.value,
            field = this.field,
            context = this.context,
            language = this.language,
            term = this.term
        )
    }
}