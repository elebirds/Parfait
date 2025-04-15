/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.utils

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.pattern.color.HighlightingCompositeConverter
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.pattern.color.ANSIConstants.*

class ParfaitHighlightingCompositeConverter : HighlightingCompositeConverter() {
    override fun getForegroundColorCode(event: ILoggingEvent): String {
        return when (event.level) {
            Level.ERROR -> BOLD + RED_FG
            Level.WARN -> BOLD + YELLOW_FG
            Level.INFO -> BOLD + GREEN_FG
            Level.DEBUG -> BOLD + BLUE_FG
            Level.TRACE -> BOLD + CYAN_FG
            else -> BOLD + WHITE_FG
        }
    }
}