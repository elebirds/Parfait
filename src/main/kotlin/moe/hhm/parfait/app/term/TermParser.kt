/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.app.term

/**
 * 术语表达式数据类
 */
data class TermExpression(
    val oriKey: String,
    val field: String,
    val context: String?,
    val language: String?,
    val defaultValue: String?
)

/**
 * 术语解析器
 * 解析term::field[/lang][::default]格式的术语
 */
class TermParser {
    companion object {
        const val TERM_PREFIX = "term::"
        const val DEFAULT_SEPARATOR = "::"
        const val LANGUAGE_PREFIX = "/"
    }

    /**
     * 解析术语表达式
     * @param expression 术语表达式，如：term::department_science_physics/zh::物理系
     * @return 解析结果
     */
    fun parse(expression: String): TermExpression? {
        // 检查前缀
        if (!expression.startsWith(TERM_PREFIX)) {
            return null
        }

        // 移除前缀
        val expressionWithoutPrefix = expression.substring(TERM_PREFIX.length)

        // 处理默认值
        val parts = expressionWithoutPrefix.split(DEFAULT_SEPARATOR, limit = 2)
        val fieldWithLang = parts[0]
        val defaultValue = if (parts.size > 1) parts[1] else null

        // 处理字段和语言
        val langParts = fieldWithLang.split(LANGUAGE_PREFIX, limit = 2)
        val fieldWithContext = langParts[0]
        val language = if (langParts.size > 1) langParts[1] else null

        // 处理字段和上下文
        val contextParts = fieldWithContext.split("_")
        val field = contextParts[0]
        val context = if (contextParts.size > 1) {
            contextParts.subList(1, contextParts.size).joinToString("_")
        } else null

        return TermExpression(expression, field, context, language, defaultValue)
    }

    /**
     * 生成术语表达式
     * @param field 字段名
     * @param context 上下文，可为null
     * @param language 语言，可为null
     * @param defaultValue 默认值，可为null
     * @return 术语表达式
     */
    fun generateExpression(
        field: String,
        context: String? = null,
        language: String? = null,
        defaultValue: String? = null
    ): String {
        val sb = StringBuilder(TERM_PREFIX)

        // 添加字段和上下文
        sb.append(field)
        if (!context.isNullOrBlank()) {
            sb.append("_").append(context)
        }

        // 添加语言
        if (!language.isNullOrBlank()) {
            sb.append(LANGUAGE_PREFIX).append(language)
        }

        // 添加默认值
        if (!defaultValue.isNullOrBlank()) {
            sb.append(DEFAULT_SEPARATOR).append(defaultValue)
        }

        return sb.toString()
    }
}