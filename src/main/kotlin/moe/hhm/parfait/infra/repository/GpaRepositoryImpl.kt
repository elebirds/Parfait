package moe.hhm.parfait.infra.repository

import moe.hhm.parfait.domain.model.gpa.GpaStandard
import moe.hhm.parfait.domain.model.gpa.GradePointMapping
import moe.hhm.parfait.domain.repository.GpaRepository
import moe.hhm.parfait.exception.BusinessException
import moe.hhm.parfait.infra.db.DatabaseUtils
import moe.hhm.parfait.infra.db.gpa.GpaStandards
import moe.hhm.parfait.infra.db.gpa.GradePointMappings
import java.util.*

class GpaRepositoryImpl : GpaRepository {
    override fun getDefaultGpaStandard(): GpaStandard {
        return DatabaseUtils.dbQuery {
            GpaStandard.Companion.find { GpaStandards.isDefault eq true }.firstOrNull()
        } ?: throw BusinessException("缺失默认绩点标准")
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