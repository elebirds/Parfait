/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.app.term

/**
 * 上下文提供者接口
 * 用于为术语处理器提供动态上下文
 */
interface ContextProvider {
    /**
     * 获取给定字段的上下文
     * @param field 字段名
     * @return 上下文，如果没有相关上下文则返回null
     */
    fun getContext(field: String): String?
    
    /**
     * 重置上下文数据
     */
    fun reset()
}

/**
 * 基于映射表的上下文提供者
 * 简单的字段-上下文映射实现
 */
class MapBasedContextProvider : ContextProvider {
    private val contextMap = mutableMapOf<String, String>()
    
    /**
     * 设置字段的上下文
     * @param field 字段名
     * @param context 上下文值
     */
    fun setContext(field: String, context: String) {
        contextMap[field] = context
    }
    
    /**
     * 批量设置多个字段的上下文
     * @param contexts 字段-上下文映射
     */
    fun setContexts(contexts: Map<String, String>) {
        contextMap.putAll(contexts)
    }
    
    override fun getContext(field: String): String? {
        return contextMap[field]
    }
    
    override fun reset() {
        contextMap.clear()
    }
} 