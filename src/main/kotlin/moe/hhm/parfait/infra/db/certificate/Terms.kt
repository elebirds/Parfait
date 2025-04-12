/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.infra.db.certificate

import moe.hhm.parfait.domain.model.certificate.Term
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml
import java.nio.charset.StandardCharsets

object Terms : UUIDTable("certificate_terms") {
    private val logger = LoggerFactory.getLogger(this::class.java)

    val key = varchar("key", 255)
    val term = varchar("term", 255)

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

                terms.forEach { (key, value) ->
                    Term.new {
                        this.key = key
                        this.term = value
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