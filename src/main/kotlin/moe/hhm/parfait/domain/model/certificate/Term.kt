/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.domain.model.certificate

import moe.hhm.parfait.dto.TermDTO
import moe.hhm.parfait.infra.db.certificate.Terms
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class Term(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Term>(Terms)

    var key by Terms.key
    var term by Terms.term

    fun toDTO(): TermDTO {
        return TermDTO(
            uuid = this.id.value,
            key = this.key,
            term = this.term
        )
    }
}