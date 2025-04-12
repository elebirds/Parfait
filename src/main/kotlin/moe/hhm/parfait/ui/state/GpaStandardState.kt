/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.state

/**
 * GPA标准加载状态
 */
enum class GpaStandardLoadState {
    DISCONNECTED,
    CONNECTING,
    LOADING,
    DONE,
    ERROR,
    PROCESSING,
    PRELOADING;

    fun isConnected() = this != DISCONNECTED && this != CONNECTING
} 