package cc.eleb.parfait.infra.db

import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

object DatabaseUtils {
    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * 执行数据库事务并处理异常
     */
    fun <T> dbQuery(block: () -> T): T {
        return try {
            transaction {
                block()
            }
        } catch (e: Exception) {
            logger.error("数据库操作失败", e)
            throw e
        }
    }
}