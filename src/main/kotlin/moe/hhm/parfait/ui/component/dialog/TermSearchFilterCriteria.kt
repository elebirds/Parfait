/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.component.dialog

/**
 * 术语搜索筛选条件数据类
 */
data class TermSearchFilterCriteria(
    val field: String = "",
    val context: String = "",
    val language: String = "",
    val term: String = ""
) 