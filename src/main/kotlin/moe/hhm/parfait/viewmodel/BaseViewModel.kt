/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

abstract class BaseViewModel {
    private val viewModelJob = SupervisorJob()
    val viewModelScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    protected val _error = MutableSharedFlow<Throwable>()
    val error: SharedFlow<Throwable> = _error.asSharedFlow()

    open fun onCleared() {
        viewModelScope.cancel()
    }
}