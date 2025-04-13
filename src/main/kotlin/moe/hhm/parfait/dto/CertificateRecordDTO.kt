/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.dto

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

/**
 * 证书记录DTO
 */
data class CertificateRecordDTO(
    val uuid: UUID? = null,
    val templateId: UUID,
    val issuedDate: LocalDate = LocalDate.now(),
    val issuedBy: String,
    val content: String,
    val purpose: String? = null
) 