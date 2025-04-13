/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.app.term

import moe.hhm.parfait.app.service.TermService
import org.slf4j.LoggerFactory

/**
 * 术语处理器
 * 负责查找和替换文本中的术语表达式
 */
class TermProcessor(
    private val termService: TermService,
    val contextProvider: ContextProvider,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    /**
     * 查找术语替换值
     * @param expr 术语表达式
     * @param defaultLanguage 默认语言
     * @return 替换值
     */
    suspend fun findTermReplacement(expr: TermExpression, defaultLanguage: String?): String {
        try {
            // 确定查询语言
            val queryLanguage = expr.language ?: defaultLanguage

            // 确定上下文
            val queryContext = expr.context ?: contextProvider.getContext(expr.field)

            // 查询数据库
            val term = termService.getTerm(expr.field, queryContext, queryLanguage)

            // 如果找到术语，返回值；否则返回默认值或原表达式
            return term?.term ?: (expr.defaultValue ?: createMissingTermMessage(expr))
        } catch (e: Exception) {
            logger.warn("查找术语替换值失败: ${expr.field}", e)
            return expr.defaultValue ?: createMissingTermMessage(expr)
        }
    }

    /**
     * 创建缺失术语的消息
     * @param expr 术语表达式
     * @return 消息
     */
    private fun createMissingTermMessage(expr: TermExpression): String {
        return "[${expr.field}${if (expr.context != null) "_${expr.context}" else ""}${if (expr.language != null) "/${expr.language}" else ""}]"
    }
}