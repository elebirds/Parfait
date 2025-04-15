/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import moe.hhm.parfait.app.service.GpaStandardService
import moe.hhm.parfait.dto.GpaStandardDTO
import moe.hhm.parfait.infra.db.DatabaseConnectionState
import moe.hhm.parfait.infra.db.DatabaseFactory
import moe.hhm.parfait.ui.state.VMState
import moe.hhm.parfait.ui.viewmodel.common.DataViewModel
import moe.hhm.parfait.ui.viewmodel.common.VMErrorHandlerChooser
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

class GpaStandardViewModel : DataViewModel<List<GpaStandardDTO>>(emptyList()), KoinComponent {
    private val service: GpaStandardService by inject()

    // 选中的GPA标准
    private val _selectedStandard = MutableStateFlow<GpaStandardDTO?>(null)
    val selectedStandard: StateFlow<GpaStandardDTO?> = _selectedStandard.asStateFlow()

    init {
        // 监听数据库连接状态
        observeDatabaseConnectionState()
    }

    /**
     * 监听数据库连接状态
     */
    private fun observeDatabaseConnectionState() {
        scope.launch {
            DatabaseFactory.connectionState.collectLatest { state ->
                when (state) {
                    is DatabaseConnectionState.Connected -> {
                        logger.info("数据库已连接，准备加载GPA标准数据")
                        _vmState.value = VMState.PRELOADING
                        loadData()
                    }

                    is DatabaseConnectionState.Disconnected -> {
                        logger.info("数据库已断开连接")
                        _vmState.value = VMState.DISCONNECTED
                        _selectedStandard.value = null
                    }

                    is DatabaseConnectionState.Connecting -> _vmState.value = VMState.CONNECTING
                }
            }
        }
    }

    /**
     * 加载所有GPA标准
     */
    override fun loadData() = suspendProcessWithErrorHandling(VMErrorHandlerChooser.LoadData) {
        // 设置加载状态为加载中
        _vmState.value = VMState.LOADING
        // 从服务加载所有GPA标准
        val standardsList = service.getAllGpaStandards()
        _data.value = standardsList
        // 设置加载状态为已加载
        _vmState.value = VMState.DONE
        true
    }

    /**
     * 选择一个GPA标准
     */
    fun selectStandard(standard: GpaStandardDTO) {
        _selectedStandard.value = standard
    }

    /**
     * 添加GPA标准
     */
    fun addStandard(standard: GpaStandardDTO) = suspendProcessWithErrorHandling(VMErrorHandlerChooser.Modify) {
        // 添加新的GPA标准
        _vmState.value = VMState.PROCESSING
        service.addGpaStandard(standard)
        true
    }

    /**
     * 删除GPA标准
     */
    fun deleteStandard(uuid: UUID) = suspendProcessWithErrorHandling(VMErrorHandlerChooser.Modify) {
        _vmState.value = VMState.PROCESSING
        val result = service.deleteGpaStandard(uuid)
        // 如果删除的是当前选中的标准，则清空选中
        if (result && _selectedStandard.value?.uuid == uuid) {
            _selectedStandard.value = null
        }
        result
    }

    /**
     * 更新GPA标准
     */
    fun updateStandard(standard: GpaStandardDTO) = suspendProcessWithErrorHandling(VMErrorHandlerChooser.Modify) {
        _vmState.value = VMState.PROCESSING
        // 更新GPA标准
        service.updateGpaStandard(standard)
    }

    /**
     * 设置默认GPA标准
     */
    fun setDefaultStandard(uuid: UUID) = suspendProcessWithErrorHandling(VMErrorHandlerChooser.Modify) {
        _vmState.value = VMState.PROCESSING
        // 设置默认GPA标准
        service.setDefault(uuid)
    }

    /**
     * 切换GPA标准的重要状态
     */
    fun toggleLikeStandard(standard: GpaStandardDTO) {
        // 创建一个新的标准对象，切换isLike状态
        val updatedStandard = standard.copy(isLike = !standard.isLike)
        // 更新标准
        updateStandard(updatedStandard)
    }
}