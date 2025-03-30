package cc.eleb.parfait.infra.repository

import cc.eleb.parfait.domain.model.gpa.GpaStandard
import cc.eleb.parfait.domain.model.gpa.GradePointMapping
import cc.eleb.parfait.domain.repository.GpaRepository
import cc.eleb.parfait.exception.BussinessException
import cc.eleb.parfait.infra.db.DatabaseUtils
import cc.eleb.parfait.infra.db.gpa.GpaStandards
import cc.eleb.parfait.infra.db.gpa.GradePointMappings
import java.util.*

class GpaRepositoryImpl : GpaRepository {
    override fun getDefaultGpaStandard(): GpaStandard {
        return DatabaseUtils.dbQuery {
            GpaStandard.Companion.find { GpaStandards.isDefault eq true }.firstOrNull()
        } ?: throw BussinessException("缺失默认绩点标准")
    }

    override fun getGradePointMappings(standardID: UUID): List<GradePointMapping> {
        return DatabaseUtils.dbQuery {
            GradePointMapping.Companion.find { GradePointMappings.standardId eq standardID }.toList()
                .sortedByDescending {
                    it.gradePoint
                }
        }
    }
}