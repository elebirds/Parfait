/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.dto

import moe.hhm.parfait.utils.castTo
import kotlin.div
import kotlin.math.roundToInt

typealias SingleGpaMapping = Triple<String, IntRange, Double>

class GpaMappingDTO(val data: List<SingleGpaMapping>) {
    constructor(str: String) : this(str.split(",").map {
        val (name, range, gpa) = it.split(":")
        SingleGpaMapping(name, range.split("-").let { IntRange(it[0].toInt(), it[1].toInt()) }, gpa.toDouble())
    })

    fun getGpa(score: Double) = data.find {
        score.roundToInt() in it.second
    }?.third ?: 0.0

    fun getGpa(scores: List<ScoreDTO>) : Double {
        val totalCredit = scores.sumOf { it.credit }
        if (totalCredit == 0) return 0.0
        return scores.sumOf {
            it.credit * getGpa(it.score)
        }.castTo(2) / totalCredit
    }

    // 检查Gpa设置是否合法，需要完全覆盖0-100分区间，且中间没有间隔
    fun isValid() = data.map { it.second }.flatten().sorted().let {
        it.first() == 0 && it.last() == 100 && it.zipWithNext().all { (a, b) -> a + 1 == b }
    }

    override fun toString(): String {
        return data.joinToString(",") { "${it.first}:${it.second.start}-${it.second.endInclusive}:${it.third}" }
    }
}