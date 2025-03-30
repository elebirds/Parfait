package cc.eleb.parfait.app.service.impl

import cc.eleb.parfait.app.service.GradeCalculationService
import cc.eleb.parfait.domain.repository.GpaRepository
import cc.eleb.parfait.dto.ScoreDTO
import cc.eleb.parfait.exception.BussinessException
import cc.eleb.parfait.utils.castTo

class GradeCalculationServiceImpl(private val rep: GpaRepository) : GradeCalculationService {
    override fun gpa(score: Double): Double {
        val standard = rep.getDefaultGpaStandard()
        val mappings = rep.getGradePointMappings(standard.id.value)
        val mapping = mappings.find { score >= it.minScore }
            ?: throw BussinessException("未找到对应的绩点映射")
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