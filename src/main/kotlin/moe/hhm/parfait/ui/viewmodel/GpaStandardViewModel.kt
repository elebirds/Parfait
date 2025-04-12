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
import moe.hhm.parfait.ui.state.GpaStandardLoadState
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.LoggerFactory
import java.util.UUID

class GpaStandardViewModel : BaseViewModel(), KoinComponent {
    // 日志
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val service: GpaStandardService by inject()

    // 加载状态
    private val _loadState = MutableStateFlow(GpaStandardLoadState.DISCONNECTED)
    val loadState: StateFlow<GpaStandardLoadState> = _loadState.asStateFlow()

    // GPA标准列表
    private val _standards = MutableStateFlow<List<GpaStandardDTO>>(emptyList())
    val standards: StateFlow<List<GpaStandardDTO>> = _standards.asStateFlow()

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
                        _loadState.value = GpaStandardLoadState.PRELOADING
                        loadAllStandards()
                    }

                    is DatabaseConnectionState.Disconnected -> {
                        logger.info("数据库已断开连接")
                        _loadState.value = GpaStandardLoadState.DISCONNECTED
                        _standards.value = emptyList()
                        _selectedStandard.value = null
                    }

                    is DatabaseConnectionState.Connecting -> _loadState.value = GpaStandardLoadState.CONNECTING
                }
            }
        }
    }

    /**
     * 加载所有GPA标准
     */
    fun loadAllStandards() {
        // 检查数据库是否已连接
        if (GpaStandardLoadState.PRELOADING != _loadState.value) {
            logger.error("非法加载数据，当前状态：${_loadState.value.name}")
            return
        }

        // 设置加载状态为加载中
        _loadState.value = GpaStandardLoadState.LOADING
        
        scope.launch {
            try {
                // 从服务加载所有GPA标准
                val standardsList = service.getAllGpaStandards()
                _standards.value = standardsList
                
                // 如果标准列表不为空且没有选中的标准，则选择默认标准
                if (standardsList.isNotEmpty() && _selectedStandard.value == null) {
                    // 优先选择默认标准
                    val defaultStandard = standardsList.find { it.isDefault }
                    _selectedStandard.value = defaultStandard ?: standardsList.first()
                }
                
                // 设置加载状态为已加载
                _loadState.value = GpaStandardLoadState.DONE
            } catch (e: Exception) {
                // 记录错误日志
                logger.error("Failed to load GPA standards", e)
                // 设置加载状态为错误
                _loadState.value = GpaStandardLoadState.ERROR
            }
        }
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
    fun addStandard(standard: GpaStandardDTO) {
        scope.launch {
            try {
                // 添加新的GPA标准
                service.addGpaStandard(standard)
                // 重新加载所有标准
                _loadState.value = GpaStandardLoadState.PRELOADING
                loadAllStandards()
            } catch (e: Exception) {
                logger.error("Failed to add GPA standard", e)
                _loadState.value = GpaStandardLoadState.ERROR
            }
        }
    }

    /**
     * 删除GPA标准
     */
    fun deleteStandard(uuid: UUID) {
        scope.launch {
            try {
                // 删除GPA标准
                val result = service.deleteGpaStandard(uuid)
                if (result) {
                    // 删除成功，重新加载所有标准
                    _loadState.value = GpaStandardLoadState.PRELOADING
                    loadAllStandards()
                    // 如果删除的是当前选中的标准，则清空选中
                    if (_selectedStandard.value?.uuid == uuid) {
                        _selectedStandard.value = null
                    }
                }
            } catch (e: Exception) {
                logger.error("Failed to delete GPA standard", e)
                _loadState.value = GpaStandardLoadState.ERROR
            }
        }
    }

    /**
     * 更新GPA标准
     */
    fun updateStandard(standard: GpaStandardDTO) {
        scope.launch {
            try {
                // 更新GPA标准
                val result = service.updateGpaStandard(standard)
                if (result) {
                    // 更新成功，重新加载所有标准
                    _loadState.value = GpaStandardLoadState.PRELOADING
                    loadAllStandards()
                }
            } catch (e: Exception) {
                logger.error("Failed to update GPA standard", e)
                _loadState.value = GpaStandardLoadState.ERROR
            }
        }
    }

    /**
     * 设置默认GPA标准
     */
    fun setDefaultStandard(uuid: UUID) {
        scope.launch {
            try {
                // 设置默认GPA标准
                val result = service.setDefault(uuid)
                if (result) {
                    // 设置成功，重新加载所有标准
                    _loadState.value = GpaStandardLoadState.PRELOADING
                    loadAllStandards()
                }
            } catch (e: Exception) {
                logger.error("Failed to set default GPA standard", e)
                _loadState.value = GpaStandardLoadState.ERROR
            }
        }
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