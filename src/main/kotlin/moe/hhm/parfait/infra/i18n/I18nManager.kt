/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.infra.i18n

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.slf4j.LoggerFactory
import java.util.*

/**
 * 国际化管理器
 *
 * 负责管理应用程序的多语言支持和语言切换
 */
object I18nManager {
    private val logger = LoggerFactory.getLogger(I18nManager::class.java)

    // 支持的语言
    enum class Language(val code: String, val displayName: String) {
        ENGLISH("en", "English"),
        CHINESE("zh", "中文")
    }

    // 资源文件基础名
    private const val RESOURCE_BUNDLE_BASE_NAME = "i18n.messages"

    // 当前语言
    private val _currentLanguage = MutableStateFlow(Language.ENGLISH)
    val currentLanguage: StateFlow<Language> = _currentLanguage.asStateFlow()

    // 当前资源包
    private var resourceBundle: ResourceBundle = loadResourceBundle(Language.ENGLISH)

    /**
     * 初始化语言管理器
     *
     * 尝试从系统默认语言加载适当的语言设置
     */
    fun init() {
        val systemLocale = Locale.getDefault()

        // 根据系统语言设置初始语言
        val initialLanguage = when (systemLocale.language) {
            "zh" -> Language.CHINESE
            else -> Language.ENGLISH
        }

        switchLanguage(initialLanguage)
        logger.info("初始化国际化管理器，当前语言: ${initialLanguage.displayName}")
    }

    /**
     * 切换应用程序语言
     *
     * @param language 目标语言
     */
    fun switchLanguage(language: Language) {
        resourceBundle = loadResourceBundle(language)
        _currentLanguage.value = language
        logger.info("切换语言到: ${language.displayName}")
    }

    /**
     * 获取指定键的国际化文本
     *
     * @param key 文本键
     * @param defaultValue 如果键不存在时的默认值
     * @return 国际化文本
     */
    fun getMessage(key: String, defaultValue: String = key): String {
        return try {
            resourceBundle.getString(key)
        } catch (e: MissingResourceException) {
            logger.warn("未找到国际化键: $key，使用默认值")
            defaultValue
        }
    }

    /**
     * 获取指定键的国际化文本，并进行参数替换
     *
     * @param key 文本键
     * @param args 替换参数
     * @return 格式化后的国际化文本
     */
    fun getMessageFormatted(key: String, vararg args: Any): String {
        val pattern = getMessage(key)
        return String.format(pattern, *args)
    }

    /**
     * 加载指定语言的资源包
     *
     * @param language 语言
     * @return 资源包
     */
    private fun loadResourceBundle(language: Language): ResourceBundle {
        val locale = when (language) {
            Language.ENGLISH -> Locale.ENGLISH
            Language.CHINESE -> Locale.CHINESE
        }
        return try {
            ResourceBundle.getBundle(RESOURCE_BUNDLE_BASE_NAME, locale)
        } catch (e: Exception) {
            logger.error("加载语言资源失败: ${language.code}", e)
            // 如果加载失败，返回默认资源
            ResourceBundle.getBundle(RESOURCE_BUNDLE_BASE_NAME)
        }
    }
} 