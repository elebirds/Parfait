/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.infra.repository

import moe.hhm.parfait.domain.model.certificate.CertificateRecord
import moe.hhm.parfait.domain.repository.CertificateRecordRepository
import moe.hhm.parfait.dto.CertificateRecordDTO
import moe.hhm.parfait.infra.db.DatabaseUtils
import moe.hhm.parfait.infra.db.certificate.CertificateRecords
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import java.util.UUID

class CertificateRecordRepositoryImpl : CertificateRecordRepository {
    override suspend fun count(): Long = DatabaseUtils.dbQuery {
        CertificateRecord.count()
    }

    override suspend fun findAll(): List<CertificateRecord> = DatabaseUtils.dbQuery {
        CertificateRecord.all().toList()
    }

    override suspend fun findByUUID(uuid: UUID): CertificateRecord? = DatabaseUtils.dbQuery {
        CertificateRecord.findById(uuid)
    }

    override suspend fun findByTemplateId(templateId: UUID): List<CertificateRecord> = DatabaseUtils.dbQuery {
        CertificateRecord.find { CertificateRecords.templateId eq templateId }.toList()
    }

    override suspend fun add(record: CertificateRecordDTO): EntityID<UUID> = DatabaseUtils.dbQuery {
        CertificateRecords.insertAndGetId {
            it[templateId] = record.templateId
            it[issuedDate] = record.issuedDate
            it[issuedBy] = record.issuedBy
            it[content] = record.content
            it[purpose] = record.purpose
        }
    }

    override suspend fun delete(uuid: UUID): Boolean = DatabaseUtils.dbQuery {
        CertificateRecords.deleteWhere { Op.build { id eq uuid } } > 0
    }
} 