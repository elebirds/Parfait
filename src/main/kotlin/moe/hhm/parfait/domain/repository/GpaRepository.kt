/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.domain.repository

import moe.hhm.parfait.domain.model.gpa.GpaStandard
import moe.hhm.parfait.domain.model.gpa.GradePointMapping
import java.util.*

interface GpaRepository {
    fun getGpaStandard(): GpaStandard
    fun loadGpaStandard(): GpaStandard
    fun loadGradePointMappings(standardID: UUID): List<GradePointMapping>
    fun getGradePointMappings(): List<GradePointMapping>
}