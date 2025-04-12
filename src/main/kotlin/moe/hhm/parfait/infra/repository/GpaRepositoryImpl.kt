/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.infra.repository

import moe.hhm.parfait.domain.model.gpa.GpaStandard
import moe.hhm.parfait.domain.repository.GpaRepository
import moe.hhm.parfait.dto.GpaStandardDTO
import moe.hhm.parfait.exception.BusinessException
import moe.hhm.parfait.infra.db.DatabaseUtils
import moe.hhm.parfait.infra.db.gpa.GpaStandards
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import java.time.LocalDateTime
import java.util.*

class GpaRepositoryImpl : GpaRepository {
    override suspend fun count(): Long = DatabaseUtils.dbQuery {
        GpaStandard.count()
    }

    override suspend fun update(gpaStandard: GpaStandardDTO): Boolean {
        if (gpaStandard.uuid == null) throw BusinessException("gpa.error.not.exist")
        if (isExistByNameExceptMe(gpaStandard.name, gpaStandard.uuid)) throw BusinessException("gpa.error.name.exist")
        return DatabaseUtils.dbQuery {
            GpaStandards.update({ GpaStandards.id eq gpaStandard.uuid }) {
                gpaStandard.into(it)
                it[updatedAt] = LocalDateTime.now()
            } > 0
        }
    }

    override suspend fun isExistByName(name: String): Boolean = DatabaseUtils.dbQuery {
        GpaStandard.find { GpaStandards.name eq name }.count() > 0
    }

    override suspend fun getDefault(): GpaStandard = DatabaseUtils.dbQuery {
        GpaStandard.find {
            GpaStandards.isDefault eq true
        }.firstOrNull() ?: throw BusinessException("gpa.error.default.not.exist")
    }

    override suspend fun setDefault(uuid: UUID): Boolean = DatabaseUtils.dbQuery {
        // 先把所有默认的设置为非默认
        GpaStandards.update({ GpaStandards.isDefault eq true }) {
            it[isDefault] = false
        }
        // 再把当前的设置为默认
        GpaStandards.update({ GpaStandards.id eq uuid }) {
            it[isDefault] = true
        } > 0
    }

    override fun getDefaultSync(): GpaStandard = DatabaseUtils.dbQuerySync {
        GpaStandard.find {
            GpaStandards.isDefault eq true
        }.firstOrNull() ?: throw BusinessException("gpa.error.default.not.exist")
    }

    override suspend fun isExistByNameExceptMe(name: String, uuid: UUID): Boolean = DatabaseUtils.dbQuery {
        GpaStandard.find { (GpaStandards.name eq name) and (GpaStandards.id neq uuid) }.count() > 0
    }

    override suspend fun findAll(): List<GpaStandard> = DatabaseUtils.dbQuery {
        GpaStandard.all().toList()
    }

    override suspend fun findByUUID(uuid: UUID): GpaStandard? = DatabaseUtils.dbQuery {
        GpaStandard.findById(uuid)
    }

    override suspend fun findByName(name: String): GpaStandard? = DatabaseUtils.dbQuery {
        GpaStandard.find { GpaStandards.name eq name }.firstOrNull()
    }

    override suspend fun add(gpaStandard: GpaStandardDTO): EntityID<UUID> {
        if (isExistByName(gpaStandard.name)) throw BusinessException("gpa.error.name.exist")
        return DatabaseUtils.dbQuery {
            GpaStandards.insertAndGetId {
                gpaStandard.into(it)
            }
        }
    }

    override suspend fun delete(uuid: UUID) = DatabaseUtils.dbQuery {
        GpaStandards.deleteWhere { Op.build { id eq uuid } }
    } > 0
}