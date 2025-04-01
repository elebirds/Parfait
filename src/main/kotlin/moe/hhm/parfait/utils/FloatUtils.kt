package moe.hhm.parfait.utils

import java.math.BigDecimal
import java.math.RoundingMode

fun Double.castTo(scale: Int) = BigDecimal(this).setScale(scale, RoundingMode.HALF_EVEN).toDouble()