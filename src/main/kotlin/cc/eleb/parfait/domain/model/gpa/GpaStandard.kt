package cc.eleb.parfait.domain.model.gpa

import cc.eleb.parfait.infra.db.gpa.GpaStandards
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class GpaStandard(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<GpaStandard>(GpaStandards)

    var standardName by GpaStandards.standardName
    var description by GpaStandards.description
    var isDefault by GpaStandards.isDefault
}