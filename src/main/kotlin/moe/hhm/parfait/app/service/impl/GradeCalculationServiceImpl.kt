/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.app.service.impl

import moe.hhm.parfait.app.service.GradeCalculationService
import moe.hhm.parfait.domain.repository.GpaRepository
import moe.hhm.parfait.dto.ScoreDTO
import moe.hhm.parfait.exception.BusinessException
import moe.hhm.parfait.utils.castTo

class GradeCalculationServiceImpl(private val rep: GpaRepository) : GradeCalculationService {
    override fun gpa(score: Double): Double {
        val mappings = rep.getGradePointMappings()
        val mapping = mappings.find { score >= it.minScore }
            ?: throw BusinessException("未找到对应的绩点映射")
        return mapping.gradePoint
    }

    override fun gpa(scores: List<ScoreDTO>): Double {
        val totalCredit = scores.sumOf { it.credit }
        if (totalCredit == 0) return 0.0
        return scores.sumOf {
            it.credit * gpa(it.score)
        }.castTo(2) / totalCredit
    }

    override fun weightedMean(scores: List<ScoreDTO>): Double {
        val totalCredit = scores.sumOf { it.credit }
        if (totalCredit == 0) return 0.0
        return scores.sumOf {
            it.credit * it.score
        }.castTo(2) / totalCredit
    }

    override fun simpleMean(scores: List<ScoreDTO>): Double {
        if (scores.isEmpty()) return 0.0
        return scores.sumOf { it.score }.castTo(2) / scores.size
    }
}