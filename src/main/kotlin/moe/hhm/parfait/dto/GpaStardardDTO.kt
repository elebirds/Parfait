/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

 package moe.hhm.parfait.dto

 import moe.hhm.parfait.infra.db.gpa.GpaStandards
 import org.jetbrains.exposed.sql.statements.UpdateBuilder
 import java.util.*
 
 data class GpaStandardDTO(
     val uuid: UUID? = null,
     val name: String,
     val description: String,
     val category: String,
     val mapping: GpaMappingDTO
 ) {
     fun <T : Any> into(it: UpdateBuilder<T>) {
         it[GpaStandards.name] = name
         it[GpaStandards.category] = category
         it[GpaStandards.description] = description
         it[GpaStandards.mapping] = mapping.toString()
     }
 }