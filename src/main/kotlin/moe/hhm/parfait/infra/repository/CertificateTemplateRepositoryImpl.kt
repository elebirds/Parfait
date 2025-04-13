/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.infra.repository

import moe.hhm.parfait.domain.model.certificate.CertificateTemplate
import moe.hhm.parfait.domain.repository.CertificateTemplateRepository
import moe.hhm.parfait.dto.CertificateTemplateDTO
import moe.hhm.parfait.exception.BusinessException
import moe.hhm.parfait.infra.db.DatabaseUtils
import moe.hhm.parfait.infra.db.certificate.CertificateTemplates
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import java.time.LocalDateTime
import java.util.*

class CertificateTemplateRepositoryImpl : CertificateTemplateRepository {
    override suspend fun count(): Long = DatabaseUtils.dbQuery {
        CertificateTemplate.count()
    }

    override suspend fun update(data: CertificateTemplateDTO): Boolean {
        if (data.uuid == null) throw BusinessException("certificate.error.not.exist")
        if (isExistByNameExceptMe(data.name, data.uuid)) throw BusinessException("certificate.error.name.exist")
        return DatabaseUtils.dbQuery {
            CertificateTemplates.update({ CertificateTemplates.id eq data.uuid }) {
                data.into(it)
                it[updatedAt] = LocalDateTime.now()
            } > 0
        }
    }
    override suspend fun isExistByName(name: String): Boolean = DatabaseUtils.dbQuery {
        CertificateTemplate.find { CertificateTemplates.name eq name }.count() > 0
    }
    override suspend fun isExistByNameExceptMe(name: String, uuid: UUID): Boolean = DatabaseUtils.dbQuery {
        CertificateTemplate.find { (CertificateTemplates.name eq name) and (CertificateTemplates.id neq uuid) }.count() > 0
    }
    override suspend fun findAll(): List<CertificateTemplate> = DatabaseUtils.dbQuery {
        CertificateTemplate.all().toList()
    }

    override suspend fun findActiveAll(): List<CertificateTemplate> = DatabaseUtils.dbQuery {
        CertificateTemplate.find { CertificateTemplates.isActive eq true }.toList()
    }

    override suspend fun findByUUID(uuid:UUID): CertificateTemplate? = DatabaseUtils.dbQuery {
        CertificateTemplate.findById(uuid)

    }

    override suspend fun findByName(name: String): CertificateTemplate? = DatabaseUtils.dbQuery {
        CertificateTemplate.find { CertificateTemplates.name eq name }.firstOrNull()
    }

    override suspend fun add(data: CertificateTemplateDTO): EntityID<UUID> {
        if(isExistByName((data.name))) throw BusinessException("certificate.error.name.exist")
        return DatabaseUtils.dbQuery {
            CertificateTemplates.insertAndGetId {
                data.into(it)
            }
        }
    }

    override suspend fun delete(uuid: UUID)= DatabaseUtils.dbQuery {
        CertificateTemplates.deleteWhere { Op.build {id eq uuid } }
    } > 0
}