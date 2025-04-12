/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.dto

import moe.hhm.parfait.utils.castTo
import kotlin.math.roundToInt

typealias SingleGpaMapping = Triple<String, IntRange, Double>

enum class GpaMappingValidState(val i18nKey: String) {
    /**
     * 映射表合法
     */
    VALID("gpa.dialog.validation.mapping.success"),

    /**
     * 映射表不合法，分数段重叠
     */
    OVERLAP("gpa.dialog.validation.mapping.overlap"),

    /**
     * 映射表不合法，分数段不覆盖0-100
     */
    UNCOVERED("gpa.dialog.validation.mapping.uncovered"),
}

/**
 * GPA映射表
 * @param data 映射数据
 */
class GpaMappingDTO(val data: List<SingleGpaMapping>) {
    constructor(str: String) : this(str.split(",").map {
        val (name, range, gpa) = it.split(":")
        SingleGpaMapping(name, range.split("-").let { IntRange(it[0].toInt(), it[1].toInt()) }, gpa.toDouble())
    })

    fun getGpa(score: Double) = data.find {
        score.roundToInt() in it.second
    }?.third ?: 0.0

    fun getGpa(scores: List<ScoreDTO>): Double {
        val totalCredit = scores.sumOf { it.credit }
        if (totalCredit == 0) return 0.0
        return scores.sumOf {
            it.credit * getGpa(it.score)
        }.castTo(2) / totalCredit
    }

    // 检查Gpa设置是否合法，需要完全覆盖0-100分区间，且中间没有间隔
    fun isValid() = GpaMappingValidState.VALID

    fun validCode() = data.map { it.second }.flatten().sorted().let {
        if (it.first() == 0 && it.last() == 100) {
            if (it.zipWithNext().all { (a, b) -> a + 1 == b }) GpaMappingValidState.VALID
            else GpaMappingValidState.OVERLAP
        } else GpaMappingValidState.UNCOVERED
    }

    override fun toString(): String {
        return data.joinToString(",") { "${it.first}:${it.second.start}-${it.second.endInclusive}:${it.third}" }
    }
}