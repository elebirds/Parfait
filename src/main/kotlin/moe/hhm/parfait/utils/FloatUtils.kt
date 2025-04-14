/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.utils

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat

fun Double.castTo(scale: Int) = BigDecimal(this).setScale(scale, RoundingMode.HALF_EVEN).toDouble()

val nf: NumberFormat = NumberFormat.getInstance().apply {
    this.minimumFractionDigits = 2
    this.maximumFractionDigits = 2
    this.isGroupingUsed = false
}

fun Double.round2Decimal(): String {
    return nf.format(this)
}