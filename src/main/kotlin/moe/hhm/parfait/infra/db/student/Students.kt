package moe.hhm.parfait.infra.db.student

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object Students : UUIDTable("students") {
    val studentId = varchar("student_id", 20).uniqueIndex() // 学号
    val name = varchar("name", 50) // 姓名
    val gender = integer("gender") // 0未知,1男,2女
    val status = integer("status") // 状态（在读、休学、退学等）
    val department = varchar("department", 100) // 院系
    val major = varchar("major", 100) // 专业
    val grade = integer("grade") // 年级
    val classGroup = varchar("class_group", 50) // 班级
    val scores = text("scores") // 成绩, 字符串存储

    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() } // 创建时间
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.now() } // 更新时间
}