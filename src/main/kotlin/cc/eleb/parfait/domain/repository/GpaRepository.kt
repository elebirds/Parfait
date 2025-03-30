package cc.eleb.parfait.domain.repository

import cc.eleb.parfait.domain.model.gpa.GpaStandard
import cc.eleb.parfait.domain.model.gpa.GradePointMapping
import java.util.*

interface GpaRepository {
    fun getDefaultGpaStandard(): GpaStandard
    fun getGradePointMappings(standardID: UUID): List<GradePointMapping>
}