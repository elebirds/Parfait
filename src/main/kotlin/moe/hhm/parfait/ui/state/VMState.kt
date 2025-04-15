/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.state

enum class VMState {
    DISCONNECTED,
    CONNECTING,
    PRELOADING,
    LOADING,
    PROCESSING,
    DONE,
    ERROR,
    FATAL;

    fun isConnected() = this != DISCONNECTED && this != CONNECTING
}