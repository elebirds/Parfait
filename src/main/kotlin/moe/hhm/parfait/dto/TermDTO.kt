/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.dto

import java.util.*

/**
 * 证书术语数据传输对象
 */
data class TermDTO(
    val uuid: UUID? = null,
    val field: String,
    val context: String? = null,
    val language: String? = null,
    val term: String
)
