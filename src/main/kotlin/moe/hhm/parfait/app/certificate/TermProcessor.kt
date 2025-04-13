/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.app.term

import moe.hhm.parfait.app.service.TermService
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern

/**
 * 术语处理器
 * 负责查找和替换文本中的术语表达式
 */
class TermProcessor(
    private val termService: TermService,
    private val contextProvider: ContextProvider = MapBasedContextProvider(),
    private val parser: TermParser = TermParser()
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    // 缓存编译后的正则表达式
    private val patternCache = ConcurrentHashMap<String, Pattern>()

    /**
     * 处理文本，替换其中的术语表达式
     * @param text 待处理的文本
     * @param defaultLanguage 默认语言，如果术语没有指定语言，则使用此语言
     * @return 处理后的文本
     */
    suspend fun process(text: String, defaultLanguage: String? = null): String {
            // 术语表达式的正则表达式
        // 术语表达式的正则表达式
        val pattern = getOrCompilePattern("term::\\w+(?:_[\\w_]+)?(?:/\\w+)?(?:::[^\\s]+)?")
        val matcher = pattern.matcher(text)

        val result = StringBuilder()
        var lastEnd = 0

        while (matcher.find()) {
            // 添加前面未匹配的文本
            result.append(text, lastEnd, matcher.start())

            // 获取匹配的表达式
            val expression = matcher.group()

            // 解析表达式
            val termExpr = parser.parse(expression)

            if (termExpr != null) {
                // 查找并替换术语
                val replacement = findTermReplacement(termExpr, defaultLanguage)
                result.append(replacement)
            } else {
                // 如果无法解析，保留原表达式
                result.append(expression)
            }

            lastEnd = matcher.end()
        }

        // 添加剩余的文本
        if (lastEnd < text.length) {
            result.append(text, lastEnd, text.length)
        }

        return result.toString()

    }

    /**
     * 查找术语替换值
     * @param expr 术语表达式
     * @param defaultLanguage 默认语言
     * @return 替换值
     */
    private suspend fun findTermReplacement(expr: TermExpression, defaultLanguage: String?): String {
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

    /**
     * 获取或编译正则表达式
     * @param regex 正则表达式字符串
     * @return 编译后的Pattern对象
     */
    private fun getOrCompilePattern(regex: String): Pattern {
        return patternCache.computeIfAbsent(regex) { Pattern.compile(it) }
    }
}