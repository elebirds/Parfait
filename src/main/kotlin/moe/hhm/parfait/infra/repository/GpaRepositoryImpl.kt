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
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime
import java.util.*

class GpaRepositoryImpl : GpaRepository {
    override suspend fun count(): Long = DatabaseUtils.dbQuery {
        GpaStandard.count()
    }

    override suspend fun update(gpaStandard: GpaStandardDTO): Boolean = DatabaseUtils.dbQuery {
        GpaStandards.update({ GpaStandards.id eq gpaStandard.uuid }) {
            gpaStandard.into(it)
            it[updatedAt] = LocalDateTime.now()
        } > 0
    }

    override suspend fun isExistByName(name: String): Boolean = DatabaseUtils.dbQuery {
        GpaStandard.find { GpaStandards.name eq name }.count() > 0
    }

    override suspend fun getDefault(): GpaStandard = DatabaseUtils.dbQuery {
        GpaStandard.find {
            GpaStandards.isDefault eq true
        }.firstOrNull() ?: throw BusinessException("gpa.error.default.not.exist")
    }

    override fun getDefaultSync(): GpaStandard = DatabaseUtils.dbQuerySync {
        GpaStandard.find {
            GpaStandards.isDefault eq true
        }.firstOrNull() ?: throw BusinessException("gpa.error.default.not.exist")
    }

    override suspend fun findAll(): List<GpaStandard> = DatabaseUtils.dbQuery {
        GpaStandard.all().toList()
    }

    override suspend fun findByUUID(uuid: UUID): GpaStandard? = DatabaseUtils.dbQuery {
        GpaStandard.findById(uuid)
    }

    override suspend fun findByName(name: String): GpaStandard? = DatabaseUtils.dbQuery{
        GpaStandard.find { GpaStandards.name eq name }.firstOrNull()
    }

    override suspend fun add(gpaStandard: GpaStandardDTO): EntityID<UUID>  {
        if (isExistByName(gpaStandard.name)) throw BusinessException("gpa.error.name.exist")
        return DatabaseUtils.dbQuery {
            GpaStandards.insertAndGetId {
                gpaStandard.into(it)
            }
        }
    }

    override suspend fun delete(uuid: UUID) = DatabaseUtils.dbQuery {
        GpaStandards.deleteWhere { Op.build {id eq uuid}}
    } > 0
}