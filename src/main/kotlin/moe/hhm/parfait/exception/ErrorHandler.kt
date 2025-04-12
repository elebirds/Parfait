/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.exception

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import moe.hhm.parfait.infra.i18n.I18nUtils
import org.slf4j.LoggerFactory
import javax.swing.JOptionPane
import kotlin.system.exitProcess

object ErrorHandler {
    private val logger = LoggerFactory.getLogger(ErrorHandler::class.java)

    @OptIn(DelicateCoroutinesApi::class)
    fun fatal(e: Exception) {
        logger.error("出现致命错误, 退出Parfait", e)
        JOptionPane.showMessageDialog(
            null,
            I18nUtils.getFormattedText("error.fatal.detail", e.localizedMessage),
            I18nUtils.getText("error.fatal.title"),
            JOptionPane.ERROR_MESSAGE
        )
        GlobalScope.launch {
            delay(10000)
            exitProcess(1)
        }
    }

    fun pause(e: Exception) {
        logger.warn("出现需终结的错误, Parfait冻结, 等待用户手动重载", e)
    }
}