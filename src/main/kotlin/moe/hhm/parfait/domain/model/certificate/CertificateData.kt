/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.domain.model.certificate

import moe.hhm.parfait.infra.db.certificate.CertificateDatas
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class CertificateData(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<CertificateData>(CertificateDatas)

    var template by CertificateTemplate referencedOn CertificateDatas.template
    var data by CertificateDatas.data
}