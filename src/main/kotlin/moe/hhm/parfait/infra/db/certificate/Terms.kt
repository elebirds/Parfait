/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.infra.db.certificate

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
            if (Term.count() != 0L) return@transaction

            try {
                val yaml = Yaml()
                val resourceStream = Terms::class.java.classLoader.getResourceAsStream("terms.yaml")
                    ?: throw RuntimeException("资源文件未找到")

                val terms = yaml.load<Map<String, String>>(
                    resourceStream.bufferedReader(StandardCharsets.UTF_8)
                )

                val now = LocalDateTime.now()
                
                terms.forEach { (oldKey, value) ->
                    // 旧版本的术语只有key和value，兼容处理
                    Term.new {
                        this.field = oldKey  // 向后兼容：旧版本的key直接作为field
                        this.context = null
                        this.language = null
                        this.term = value
                        this.isSystem = true
                        this.createdAt = now
                        this.updatedAt = now
                    }
                }
                logger.info("证书术语初始化成功，共加载 ${terms.size} 条数据")
            } catch (e: Exception) {
                logger.error("初始化术语失败", e)
                throw e // 重新抛出异常，因为这是必要的配置
            }
        }
    }
}