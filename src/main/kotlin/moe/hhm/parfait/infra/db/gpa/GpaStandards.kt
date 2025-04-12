/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.infra.db.gpa

import moe.hhm.parfait.domain.model.gpa.GpaStandard
import moe.hhm.parfait.dto.GpaMappingDTO
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object GpaStandards : UUIDTable("gpa_standards") {
    val name = varchar("name", 100).uniqueIndex() // 名称
    val description = text("description") // 描述
    val category = varchar("category", 100) // 类别
    val purpose = varchar("purpose", 50)
    val mapping = text("mapping") // 标准映射
    val isDefault = bool("is_default").default(false) // 是否默认
    val isLike = bool("is_like").default(false)

    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() } // 创建时间
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.now() } // 更新时间

    fun init() {
        if (GpaStandard.count() == 0L) {
            // 默认绩点映射，左闭右开
            val defaultGradePointMappings = GpaMappingDTO(listOf(
                Triple("A", 90..100, 4.0),
                Triple("B+", 85..<90, 3.5),
                Triple("B", 80..<85, 3.0),
                Triple("C+", 75..<80, 2.5),
                Triple("C", 70..<75, 2.0),
                Triple("D+", 65..<70, 1.5),
                Triple("D", 60..<65, 1.0),
                Triple("F", 0..<60, 0.0)
            ))
            val defaultGpaStandard = GpaStandard.new {
                name = "默认标准"
                description = "基础四分制标准"
                category = "基础"
                purpose = "用于学生默认绩点计算"
                mapping = defaultGradePointMappings.toString()
                isDefault = true
                isLike = true
            }
            val pekingUniversityGPMapping = GpaMappingDTO(listOf(
                Triple("A", 90..100, 4.0),
                Triple("B+", 85..<90, 3.7),
                Triple("B", 82..<85, 3.3),
                Triple("B-", 78..<82, 3.0),
                Triple("C+", 75..<78, 2.7),
                Triple("C", 72..<75, 2.3),
                Triple("C-", 68..<72, 2.0),
                Triple("D+", 64..<68, 1.5),
                Triple("D", 60..<64, 1.0),
                Triple("F", 0..<60, 0.0)
            ))
            val pekingUniversityStandard = GpaStandard.new {
                name = "北京大学4.0标准"
                description = "北京大学4.0标准"
                category = "基础"
                purpose = "国内优秀大学绩点标准"
                mapping = pekingUniversityGPMapping.toString()
                isDefault = false
                isLike = false
            }
            val canadaGPMapping = GpaMappingDTO(listOf(
                Triple("A", 90..100, 4.3),
                Triple("B+", 85..<90, 4.0),
                Triple("B", 80..<85, 3.7),
                Triple("C+", 75..<80, 3.3),
                Triple("C", 70..<75, 3.0),
                Triple("C-", 65..<70, 2.7),
                Triple("D+", 60..<65, 2.3),
                Triple("D", 0..<60, 0.0)
            ))
            val canadaStandard = GpaStandard.new {
                name = "加拿大4.3标准"
                description = "加拿大标准"
                category = "留学"
                purpose = "加拿大大学绩点标准"
                mapping = canadaGPMapping.toString()
                isDefault = false
                isLike = true
            }
        }
    }
}