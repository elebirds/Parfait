/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.infra.repository

import moe.hhm.parfait.domain.model.gpa.GpaStandard
import moe.hhm.parfait.domain.model.gpa.GradePointMapping
import moe.hhm.parfait.domain.repository.GpaRepository
import moe.hhm.parfait.exception.BusinessException
import moe.hhm.parfait.infra.db.DatabaseUtils
import moe.hhm.parfait.infra.db.gpa.GpaStandards
import moe.hhm.parfait.infra.db.gpa.GradePointMappings
import java.util.*
import kotlin.collections.firstOrNull
import kotlin.collections.sortedByDescending
import kotlin.collections.toList

class GpaRepositoryImpl : GpaRepository {
    companion object {
        private var GPA_STANDARD : GpaStandard? = null
        private var GRADE_POINT_MAPPING : List<GradePointMapping>? = null
    }

    override fun getGpaStandard(): GpaStandard {
        return GPA_STANDARD ?: loadGpaStandard()
    }

    override fun getGradePointMappings(): List<GradePointMapping> {
        return GRADE_POINT_MAPPING ?: loadGradePointMappings(getGpaStandard().id.value)
    }

    override fun loadGpaStandard(): GpaStandard {
        return DatabaseUtils.dbQuery {
            val res = GpaStandard.Companion.find { GpaStandards.isDefault eq true }.firstOrNull()
            GPA_STANDARD = res
            res
        } ?: throw BusinessException("缺失默认绩点标准")
    }

    override fun loadGradePointMappings(standardID: UUID): List<GradePointMapping> = DatabaseUtils.dbQuery {
        val res = GradePointMapping.Companion.find { GradePointMappings.standardId eq standardID }.toList()
            .sortedByDescending {
                it.gradePoint
            }
        GRADE_POINT_MAPPING = res
        res
    }
}