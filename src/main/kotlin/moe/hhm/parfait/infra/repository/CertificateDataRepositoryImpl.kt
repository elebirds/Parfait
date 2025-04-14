/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.infra.repository

import moe.hhm.parfait.domain.model.certificate.CertificateData
import moe.hhm.parfait.domain.repository.CertificateDataRepository
import moe.hhm.parfait.dto.CertificateDataDTO
import moe.hhm.parfait.infra.db.DatabaseUtils
import moe.hhm.parfait.infra.db.certificate.CertificateDatas
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.update
import java.util.*

class CertificateDataRepositoryImpl : CertificateDataRepository {
    override suspend fun count(): Long = DatabaseUtils.dbQuery {
        CertificateData.count()
    }

    override suspend fun findAll(): List<CertificateData> = DatabaseUtils.dbQuery {
        CertificateData.all().toList()
    }

    override suspend fun findByUUID(uuid: UUID): CertificateData? = DatabaseUtils.dbQuery {
        CertificateData.findById(uuid)
    }

    override suspend fun add(data: CertificateDataDTO): EntityID<UUID> = DatabaseUtils.dbQuery {
        CertificateDatas.insertAndGetId {
            data.into(it)
        }
    }

    override suspend fun delete(uuid: UUID): Boolean = DatabaseUtils.dbQuery {
        CertificateDatas.deleteWhere { Op.build { id eq uuid } } > 0
    }

    override suspend fun deleteUnused(): Int = DatabaseUtils.dbQuery {
        CertificateDatas.deleteWhere { Op.build { used eq false } }
    }

    override suspend fun updateUsed(uuid: UUID, used: Boolean): Boolean = DatabaseUtils.dbQuery {
        CertificateDatas.update({ CertificateDatas.id eq uuid }) {
            it[CertificateDatas.used] = used
        } > 0
    }
}