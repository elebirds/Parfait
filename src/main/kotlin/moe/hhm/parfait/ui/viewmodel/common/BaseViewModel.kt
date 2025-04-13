/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.viewmodel.common

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import moe.hhm.parfait.exception.BusinessException
import moe.hhm.parfait.exception.ErrorHandler
import moe.hhm.parfait.infra.i18n.I18nUtils
import moe.hhm.parfait.ui.base.CoroutineComponent
import moe.hhm.parfait.ui.base.DefaultCoroutineComponent
import moe.hhm.parfait.ui.state.VMState
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.swing.JOptionPane

open class VMErrorHandler(
    val validStates: List<VMState>,
    val autoReload: Boolean,
    val exceptionPause: Boolean,
    val exceptionFatal: Boolean
)

sealed class VMErrorHandlerChooser {
    object Modify : VMErrorHandler(
        validStates = listOf(VMState.DONE),
        autoReload = true,
        exceptionPause = false,
        exceptionFatal = false
    )

    object LoadData : VMErrorHandler(
        validStates = listOf(VMState.PRELOADING),
        autoReload = false,
        exceptionPause = true,
        exceptionFatal = false
    )

    object Process : VMErrorHandler(
        validStates = listOf(VMState.DONE),
        autoReload = false,
        exceptionPause = false,
        exceptionFatal = false
    )
}

abstract class BaseViewModel : CoroutineComponent by DefaultCoroutineComponent() {
    protected val logger: Logger = LoggerFactory.getLogger(javaClass)

    protected val _error = MutableSharedFlow<Throwable>()
    val error: SharedFlow<Throwable> = _error.asSharedFlow()

    // 加载状态
    protected val _vmState = MutableStateFlow(VMState.DISCONNECTED)
    val vmState: StateFlow<VMState> = _vmState.asStateFlow()

    init {
        observeError()
    }

    fun observeError() {
        scope.launch {
            _error.collectLatest { error ->
                when (error) {
                    is BusinessException -> {
                        JOptionPane.showMessageDialog(
                            null,
                            I18nUtils.getFormattedText("error.business.detail", error.message ?: ""),
                            I18nUtils.getText("error.business.title"),
                            JOptionPane.WARNING_MESSAGE
                        )
                    }
                    else -> {
                        JOptionPane.showMessageDialog(
                            null,
                            I18nUtils.getFormattedText("error.generic.detail", error.message ?: ""),
                            I18nUtils.getText("error.generic.title"),
                            JOptionPane.ERROR_MESSAGE
                        )
                    }
                }
            }
        }
    }

    open fun reload() {
        // No-op
    }

    /**
     * 携带错误处理的VM挂起函数
     *
     * @param handler 错误处理器
     * @param block 执行的代码块,只有在返回值为true且handler.autoReload为true时才会自动重载
     */
    fun suspendProcessWithErrorHandling(handler: VMErrorHandler, block: suspend () -> Boolean) {
        if (vmState.value !in handler.validStates) {
            logger.warn("当前加载状态不允许执行操作：${vmState.value}，允许的状态：${handler.validStates}")
            return
        }
        scope.launch {
            var res = false
            try {
                res = block()
            } catch (e: BusinessException) {
                _error.emit(e)
            } catch (e: Exception) {
                if (handler.exceptionFatal) {
                    _vmState.value = VMState.FATAL
                    ErrorHandler.fatal(e)
                    return@launch
                } else if (handler.exceptionPause) {
                    _vmState.value = VMState.ERROR
                    ErrorHandler.pause(e)
                    return@launch
                } else {
                    logger.warn("未预期的非业务错误：", e)
                    _error.emit(e)
                }
            }
            if (res && handler.autoReload) reload()
        }
    }
}