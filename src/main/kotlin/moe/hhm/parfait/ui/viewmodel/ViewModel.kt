package moe.hhm.parfait.ui.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

/**
 * 基础ViewModel类
 */
abstract class ViewModel {
    private val viewModelJob = SupervisorJob()
    val viewModelScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    /**
     * 清理资源
     */
    fun clear() {
        viewModelScope.cancel()
    }
} 