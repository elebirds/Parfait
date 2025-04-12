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
import moe.hhm.parfait.app.service.TermService
import moe.hhm.parfait.dto.TermDTO
import moe.hhm.parfait.infra.db.DatabaseConnectionState
import moe.hhm.parfait.infra.db.DatabaseFactory
import moe.hhm.parfait.ui.state.PaginationState
import moe.hhm.parfait.ui.state.VMState
import moe.hhm.parfait.ui.viewmodel.common.PaginationDataViewModel
import moe.hhm.parfait.ui.viewmodel.common.VMErrorHandlerChooser
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

/**
 * 术语视图模型
 */
class TermViewModel : PaginationDataViewModel<List<TermDTO>>(emptyList()), KoinComponent {
    private val service: TermService by inject()

    // 当前选中的多个术语
    private val _selectedTerms = MutableStateFlow<List<TermDTO>>(emptyList())
    val selectedTerms: StateFlow<List<TermDTO>> = _selectedTerms.asStateFlow()

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
                        logger.info("数据库已连接，准备加载术语数据")
                        _vmState.value = VMState.PRELOADING
                        loadData()
                    }

                    is DatabaseConnectionState.Disconnected -> {
                        logger.info("数据库已断开连接")
                        _vmState.value = VMState.DISCONNECTED
                        _data.value = emptyList()
                        _paginationState.value = PaginationState()
                        _selectedTerms.value = emptyList()
                    }

                    is DatabaseConnectionState.Connecting -> _vmState.value = VMState.CONNECTING
                }
            }
        }
    }

    /**
     * 加载术语数据
     */
    override fun loadData() = suspendProcessWithErrorHandling(VMErrorHandlerChooser.LoadData) {
        // 设置加载状态
        _vmState.value = VMState.LOADING
        // 保存当前选中的术语ID，用于后续恢复选中状态
        val selectedTermIds = _selectedTerms.value.mapNotNull { it.uuid }

        // 获取总术语数量
        val totalCount = service.count()

        val terms = service.getPage(
            paginationState.value.currentPage,
            paginationState.value.pageSize
        )

        updatePaginationStateWithCount(totalCount)

        // 更新术语列表
        _data.value = terms

        // 尝试在新数据中找回之前选中的术语
        if (selectedTermIds.isNotEmpty()) {
            val selectedTerms = terms.filter { it.uuid in selectedTermIds }
            _selectedTerms.value = selectedTerms
        }

        _vmState.value = VMState.DONE
        true
    }

    /**
     * 设置选中的术语
     */
    fun setSelectedTerm(term: TermDTO?) {
        _selectedTerms.value = if (term == null) emptyList() else listOf(term)
    }

    /**
     * 设置选中的多个术语
     */
    fun setSelectedTerms(terms: List<TermDTO>) {
        _selectedTerms.value = terms
    }

    /**
     * 添加术语
     */
    fun addTerm(term: TermDTO) = suspendProcessWithErrorHandling(VMErrorHandlerChooser.Modify) {
        _vmState.value = VMState.PROCESSING
        service.add(term)
        true
    }

    /**
     * 更新术语
     */
    fun updateTerm(term: TermDTO) = suspendProcessWithErrorHandling(VMErrorHandlerChooser.Modify) {
        _vmState.value = VMState.PROCESSING
        service.update(term)
    }

    /**
     * 删除术语
     */
    fun deleteTerm(uuid: UUID) {
        deleteTerms(listOf(uuid))
    }

    /**
     * 批量删除术语
     */
    fun deleteTerms(uuids: List<UUID>) = suspendProcessWithErrorHandling(VMErrorHandlerChooser.Modify) {
        _vmState.value = VMState.PROCESSING

        // 先从选中集合中移除
        uuids.forEach { uuid ->
            val selectedUUIDs = _selectedTerms.value.mapNotNull { it.uuid }
            if (uuid in selectedUUIDs) {
                _selectedTerms.value = _selectedTerms.value.filter { it.uuid != uuid }
            }
        }

        // 删除术语
        uuids.forEach { service.delete(it) }
        true
    }
} 