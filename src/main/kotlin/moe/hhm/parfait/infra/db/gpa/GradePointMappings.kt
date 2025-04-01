package moe.hhm.parfait.infra.db.gpa

import org.jetbrains.exposed.dao.id.UUIDTable

object GradePointMappings : UUIDTable("grade_point_mappings") {
    val standardId = reference("standard_id", GpaStandards) // GPA标准ID
    val letterGrade = varchar("letter_grade", 5) // 字母等级
    val minScore = integer("min_score") // 最低分数
    val maxScore = integer("max_score") // 最高分数（不包括）
    val gradePoint = double("grade_point") // 对应绩点
}