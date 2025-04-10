/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.state

/**
 * 分页状态类
 */
data class StudentDataPaginationState(
    val currentPage: Int = 1,
    val pageSize: Int = 20,
    val totalStudents: Long = 0,
    val totalPages: Int = 1
)

enum class StudentDataLoadState {
    DISCONNECTED,
    CONNECTING,
    LOADING,
    DONE,
    ERROR,
    PROCESSING,
    PRELOADING;

    fun isConnected() = this != DISCONNECTED && this != CONNECTING
}