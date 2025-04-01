package moe.hhm.parfait.infra.db.gpa

import moe.hhm.parfait.domain.model.gpa.GpaStandard
import moe.hhm.parfait.domain.model.gpa.GradePointMapping
import org.jetbrains.exposed.dao.id.UUIDTable

object GpaStandards : UUIDTable("gpa_standards") {
    val standardName = varchar("standard_name", 100).uniqueIndex() // 标准名称
    val description = text("description").nullable() // 描述
    val isDefault = bool("is_default").default(false) // 是否为默认标准

    fun init() {
        if (GpaStandard.count() == 0L) {
            val defaultGpaStandard = GpaStandard.new {
                standardName = "默认标准"
                description = "基础四分制标准"
                isDefault = true
            }
            val defaultGradePointMappings = listOf(
                Triple("A", 90..100, 4.0),
                Triple("B+", 85..90, 3.5),
                Triple("B", 80..85, 3.0),
                Triple("C+", 75..80, 2.5),
                Triple("C", 70..75, 2.0),
                Triple("D+", 75..80, 1.5),
                Triple("D", 60..65, 1.0),
                Triple("F", 0..60, 0.0)
            )
            defaultGradePointMappings.forEach { (grade, range, point) ->
                GradePointMapping.new {
                    standard = defaultGpaStandard
                    letterGrade = grade
                    minScore = range.start
                    maxScore = range.endInclusive
                    gradePoint = point
                }
            }
        }
    }
}