/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.state

/**
 * 术语分页状态类
 */
data class TermPaginationState(
    val currentPage: Int = 1,
    val pageSize: Int = 20,
    val totalTerms: Long = 0,
    val totalPages: Int = 1
)

/**
 * 术语加载状态
 */
enum class TermLoadState {
    DISCONNECTED,
    CONNECTING,
    LOADING,
    DONE,
    ERROR,
    PROCESSING,
    PRELOADING;

    fun isConnected() = this != DISCONNECTED && this != CONNECTING
} 