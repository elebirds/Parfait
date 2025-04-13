/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.dto

import moe.hhm.parfait.infra.db.certificate.CertificateDatas
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import java.util.UUID

data class CertificateDataDTO (
    val uuid: UUID?,
    val filename: String,
    val data: ByteArray,
    val used: Boolean = false
) {
    fun <T : Any> into(it: UpdateBuilder<T>) {
        it[CertificateDatas.filename] = filename
        it[CertificateDatas.data] = data
        it[CertificateDatas.used] = used
    }

    fun getStream() = data.inputStream()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CertificateDataDTO

        if (uuid != other.uuid) return false
        if (filename != other.filename) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uuid?.hashCode() ?: 0
        result = 31 * result + filename.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}