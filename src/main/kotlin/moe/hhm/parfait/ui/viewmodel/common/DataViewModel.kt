/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.viewmodel.common

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import moe.hhm.parfait.ui.state.VMState

abstract class DataViewModel<T>(defaultState: T) : BaseViewModel() {
    // 数据状态
    protected val _data = MutableStateFlow(defaultState)
    val data: StateFlow<T> = _data.asStateFlow()

    abstract fun loadData()

    override fun reload() {
        _vmState.value = VMState.PRELOADING
        loadData()
    }
}