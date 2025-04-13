/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.infra.db.term

import moe.hhm.parfait.domain.model.certificate.Term
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime

object Terms : UUIDTable("certificate_terms") {
    private val logger = LoggerFactory.getLogger(this::class.java)

    // 术语基本字段
    val field = varchar("field", 100)  // 字段名，如 department
    val context = varchar("context", 255).nullable()  // 上下文，如 science_physics
    val language = varchar("language", 20).nullable()  // 语言，如 zh
    val term = varchar("term", 255)  // 术语的值

    // 元信息字段
    val isSystem = bool("is_system").default(false)  // 是否为系统预设
    val createdAt = datetime("created_at").default(LocalDateTime.now())  // 创建日期
    val updatedAt = datetime("updated_at").default(LocalDateTime.now())  // 修改日期

    fun init() {
        transaction {
            if (Term.Companion.count() != 0L) return@transaction

            try {
                val yaml = Yaml()
                val resourceStream = Terms::class.java.classLoader.getResourceAsStream("terms.yaml")
                    ?: throw RuntimeException("资源文件未找到")

                // 新版术语格式是一个列表，每个元素是一个包含field、context、language和term的映射
                val termsList = yaml.load<List<Map<String, Any>>>(
                    resourceStream.bufferedReader(StandardCharsets.UTF_8)
                )

                termsList.forEach { termMap ->
                    if (termMap.containsKey("field")) {
                        // 新版术语格式
                        Term.new {
                            this.field = termMap["field"] as String
                            this.context = termMap["context"] as? String
                            this.language = termMap["language"] as? String
                            this.term = termMap["term"] as String
                            this.isSystem = true
                        }
                    } else if (termMap.containsKey("#")) {
                        // 跳过注释行
                        logger.debug("跳过注释: ${termMap["#"]}")
                    } else {
                        // 不符合格式的数据
                        logger.warn("跳过不符合格式的数据: $termMap")
                    }
                }
                logger.info("证书术语初始化成功，共加载 ${termsList.size} 条数据")
            } catch (e: Exception) {
                logger.error("初始化术语失败", e)
                throw e // 重新抛出异常，因为这是必要的配置
            }
        }
    }
}