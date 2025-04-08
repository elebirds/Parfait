/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.infra.db

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Slf4jSqlDebugLogger
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

object DatabaseUtils {
    private val logger = LoggerFactory.getLogger(this::class.java)
    /**
     * 执行数据库事务并处理异常
     */
    suspend fun <T> dbQuery(block: () -> T): T = withContext(Dispatchers.IO) {
        try {
            transaction {
                addLogger(Slf4jSqlDebugLogger)
                block()
            }
        } catch (e: Exception) {
            logger.error("数据库操作失败", e)
            throw e
        }
    }

    fun <T> dbQuerySync(block: () -> T): T = try {
        transaction {
            addLogger(Slf4jSqlDebugLogger)
            block()
        }
    } catch (e: Exception) {
        logger.error("数据库操作失败", e)
        throw e
    }
}