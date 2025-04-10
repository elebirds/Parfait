/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.viewmodel

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import moe.hhm.parfait.view.base.CoroutineComponent
import moe.hhm.parfait.view.base.DefaultCoroutineComponent

abstract class BaseViewModel : CoroutineComponent by DefaultCoroutineComponent() {
    protected val _error = MutableSharedFlow<Throwable>()
    val error: SharedFlow<Throwable> = _error.asSharedFlow()
}