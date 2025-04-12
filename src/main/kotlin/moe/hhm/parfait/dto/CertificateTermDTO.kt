/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.dto

import java.util.UUID

/**
 * 证书术语数据传输对象
 */
data class CertificateTermDTO(
    val uuid: UUID? = null,
    val key: String,
    val term: String
)
