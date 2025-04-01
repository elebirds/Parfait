package moe.hhm.parfait.domain.repository

import moe.hhm.parfait.domain.model.gpa.GpaStandard
import moe.hhm.parfait.domain.model.gpa.GradePointMapping
import java.util.*

interface GpaRepository {
    fun getDefaultGpaStandard(): GpaStandard
    fun getGradePointMappings(standardID: UUID): List<GradePointMapping>
}