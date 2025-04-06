/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.utils

import java.math.BigDecimal
import java.math.RoundingMode

fun Double.castTo(scale: Int) = BigDecimal(this).setScale(scale, RoundingMode.HALF_EVEN).toDouble()