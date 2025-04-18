/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.dto

import java.time.LocalDateTime
import java.util.*

/**
 * 证书记录DTO
 */
data class CertificateRecordDTO(
    val uuid: UUID? = null,
    val templateId: UUID,
    val issuedDate: LocalDateTime = LocalDateTime.now(),
    val issuedBy: String,
    val content: String,
    val purpose: String? = null
) 