/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.state

data class PaginationState(
    val currentPage: Int = 1,
    val pageSize: Int = 20,
    val totalCount: Long = 0,
    val totalPages: Int = 1
)