package cc.eleb.parfait.app.service

import cc.eleb.parfait.dto.ScoreDTO

interface GradeCalculationService {
    fun gpa(scores: List<ScoreDTO>): Double
    fun gpa(score: Double): Double
    fun weightedMean(scores: List<ScoreDTO>): Double
    fun simpleMean(scores: List<ScoreDTO>): Double
}