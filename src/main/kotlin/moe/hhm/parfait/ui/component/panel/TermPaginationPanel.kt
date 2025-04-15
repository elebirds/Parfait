/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.component.panel

import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import moe.hhm.parfait.ui.base.CoroutineComponent
import moe.hhm.parfait.ui.viewmodel.TermViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * 术语分页面板
 */
class TermPaginationPanel(parent: CoroutineComponent? = null) :
    PaginationPanel(parent), KoinComponent {

    // 通过Koin获取ViewModel
    private val viewModel: TermViewModel by inject()

    init {
        // 设置回调
        setCallbacks(
            onFirstPage = { viewModel.firstPage() },
            onPrevPage = { viewModel.previousPage() },
            onNextPage = { viewModel.nextPage() },
            onLastPage = { viewModel.lastPage() },
            onPageSizeChange = { viewModel.setPageSize(it) }
        )
    }

    override fun observer() {
        scope.launch {
            combine(viewModel.vmState, viewModel.paginationState) { loadState, paginationState ->
                Pair(loadState, paginationState)
            }.collect { (loadState, paginationState) ->
                // 更新分页状态
                updateState(
                    connected = loadState.isConnected(),
                    currentPage = paginationState.currentPage,
                    totalPages = paginationState.totalPages
                )
            }
        }
    }
} 