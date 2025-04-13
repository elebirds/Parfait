/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.infra.db.certificate

import org.jetbrains.exposed.dao.id.UUIDTable


object CertificateDatas : UUIDTable("certificate_datas") {
    val filename = varchar("filename", 255)
    val used = bool("used").default(false)
    val data = binary("data", 10 * 1024 * 1024) // 10MB
}