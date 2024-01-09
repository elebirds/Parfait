package cc.eleb.parfait.utils

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Project FoundHi-Apollo
 *
 * @author hhm-GrowZheng
 * @createDate 2020/3/3 21:13
 */

@Suppress("UNCHECKED_CAST")
fun <T> Any?.cast(): T = this as T

fun Double.castTo(scale: Int) = BigDecimal(this).setScale(scale, RoundingMode.HALF_EVEN).toDouble()