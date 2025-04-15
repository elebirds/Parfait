/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.viewmodel.common

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import moe.hhm.parfait.ui.state.PaginationState
import moe.hhm.parfait.ui.state.VMState
import kotlin.math.ceil


abstract class PaginationDataViewModel<T>(defaultState: T) : DataViewModel<T>(defaultState) {
    // 分页状态
    protected val _paginationState = MutableStateFlow(PaginationState())
    val paginationState: StateFlow<PaginationState> = _paginationState.asStateFlow()

    fun clearSelected() {
        // No-op
    }

    /// 分页操作
    fun resetPaginationState() {
        _paginationState.value = PaginationState()
    }

    fun updatePaginationStateWithCount(totalCount: Long) {
        val totalPages = calculateTotalPages(totalCount, _paginationState.value.pageSize)

        // 更新分页状态
        _paginationState.update {
            it.copy(
                // 如果当前页数大于总页数，重置为第一页
                currentPage = if (_paginationState.value.currentPage > totalPages) 1 else _paginationState.value.currentPage,
                totalCount = totalCount,
                totalPages = totalPages
            )
        }
    }

    /**
     * 设置当前页
     */
    fun setCurrentPage(page: Int, checkState: Boolean = true) {
        if (checkState && _vmState.value != VMState.DONE) {
            logger.warn("在未初始化完毕时尝试设置页数")
            return
        }
        if (!isPageValid(page)) return // 非法页数

        _paginationState.update { it.copy(currentPage = page) }
        // 切换页面时清除选中状态
        clearSelected()
        _vmState.value = VMState.PRELOADING
        loadData()
    }

    /**
     * 设置每页显示数量
     */
    fun setPageSize(size: Int) {
        if (_vmState.value != VMState.DONE) {
            logger.warn("在未初始化完毕时尝试设置页面大小")
            return
        }
        // 计算之前那个页面的新页数
        val newPage = (_paginationState.value.currentPage - 1) * _paginationState.value.pageSize / size
        _paginationState.update {
            it.copy(
                pageSize = size,
                currentPage = newPage + 1
            )
        }
        // 切换页面大小时清除选中状态
        clearSelected()
        // 重新加载数据
        _vmState.value = VMState.PRELOADING
        loadData()
    }

    fun isPageValid(page: Int): Boolean {
        return page in 1.._paginationState.value.totalPages
    }

    /**
     * 计算总页数
     */
    private fun calculateTotalPages(totalItems: Long, pageSize: Int): Int {
        return ceil(totalItems.toDouble() / pageSize).toInt().coerceAtLeast(1)
    }

    /**
     * 转到上一页
     */
    fun previousPage() {
        if (_paginationState.value.currentPage > 1) {
            setCurrentPage(_paginationState.value.currentPage - 1)
        }
    }

    /**
     * 转到下一页
     */
    fun nextPage() {
        if (_paginationState.value.currentPage < _paginationState.value.totalPages) {
            setCurrentPage(_paginationState.value.currentPage + 1)
        }
    }

    /**
     * 转到首页
     */
    fun firstPage() {
        setCurrentPage(1)
    }

    /**
     * 转到末页
     */
    fun lastPage() {
        setCurrentPage(_paginationState.value.totalPages)
    }
}