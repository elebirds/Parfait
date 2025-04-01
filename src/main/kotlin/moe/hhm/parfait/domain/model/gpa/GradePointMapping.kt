package moe.hhm.parfait.domain.model.gpa

import moe.hhm.parfait.infra.db.gpa.GradePointMappings
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class GradePointMapping(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<GradePointMapping>(GradePointMappings)

    var standard by GpaStandard referencedOn GradePointMappings.standardId
    var letterGrade by GradePointMappings.letterGrade
    var minScore by GradePointMappings.minScore
    var maxScore by GradePointMappings.maxScore
    var gradePoint by GradePointMappings.gradePoint
}