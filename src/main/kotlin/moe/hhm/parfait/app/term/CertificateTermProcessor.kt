/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.app.term

import moe.hhm.parfait.app.service.TermService
import moe.hhm.parfait.dto.StudentDTO
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.LoggerFactory

/**
 * 证书术语处理器
 * 用于证书生成过程中的术语处理，集成了学生上下文
 */
class CertificateTermProcessor : KoinComponent {
    private val logger = LoggerFactory.getLogger(this::class.java)
    
    private val termProcessor: TermProcessor by inject()
    private val studentContextProvider: StudentContextProvider by inject()
    private val termService: TermService by inject()
    
    /**
     * 预加载指定语言的术语
     * @param language 语言代码
     */
    suspend fun preloadTerms(language: String? = null) {
        termService.preloadTerms(language)
    }
    
    /**
     * 使用学生上下文处理证书文本
     * @param text 待处理的文本
     * @param student 学生信息，用于提供上下文
     * @param language 语言代码，如"zh"、"en"等
     * @return 处理后的文本
     */
    suspend fun process(
        text: String, 
        student: StudentDTO?, 
        language: String? = null
    ): String {
        try {
            // 设置学生上下文
            studentContextProvider.setStudent(student)
            
            // 处理文本并返回结果
            return termProcessor.process(text, language)
        } catch (e: Exception) {
            logger.error("处理证书文本失败: $text", e)
            return text // 发生错误时返回原文本
        } finally {
            // 清理上下文，避免内存泄漏
            studentContextProvider.reset()
        }
    }
    
    /**
     * 批量处理多个文本
     * @param texts 待处理的文本列表
     * @param student 学生信息，用于提供上下文
     * @param language 语言代码
     * @return 处理后的文本列表
     */
    suspend fun processAll(
        texts: List<String>, 
        student: StudentDTO?, 
        language: String? = null
    ): List<String> {
        try {
            // 设置学生上下文
            studentContextProvider.setStudent(student)
            
            // 处理每个文本
            return texts.map { text ->
                try {
                    termProcessor.process(text, language)
                } catch (e: Exception) {
                    logger.error("处理证书文本失败: $text", e)
                    text // 发生错误时返回原文本
                }
            }
        } finally {
            // 清理上下文，避免内存泄漏
            studentContextProvider.reset()
        }
    }
} 