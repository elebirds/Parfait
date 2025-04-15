/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.infra.persist

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.prefs.Preferences

/**
 * 登录首选项管理器
 * 用于保存和恢复登录信息，支持记住密码功能
 */
object LoginPrefsManager {
    private const val KEY_ADDRESS = "login.address"
    private const val KEY_DATABASE = "login.database"
    private const val KEY_USERNAME = "login.username"
    private const val KEY_PASSWORD = "login.password"
    private const val KEY_REMEMBER = "login.remember"
    private const val KEY_EXPIRY = "login.expiry"

    // 存储首选项的过期时间（30天）
    private const val EXPIRY_DAYS = 30L

    // 获取用户首选项节点
    private val prefs = Preferences.userNodeForPackage(LoginPrefsManager::class.java)

    /**
     * 保存登录信息
     *
     * @param address 服务器地址
     * @param database 数据库名称
     * @param username 用户名
     * @param password 密码
     * @param remember 是否记住
     */
    fun saveLoginInfo(
        address: String,
        database: String,
        username: String,
        password: String,
        remember: Boolean
    ) {
        if (remember) {
            prefs.put(KEY_ADDRESS, address)
            prefs.put(KEY_DATABASE, database)
            prefs.put(KEY_USERNAME, username)
            prefs.put(KEY_PASSWORD, password)
            prefs.putBoolean(KEY_REMEMBER, true)

            // 设置过期时间（当前时间 + 30天）
            val expiryDate = LocalDateTime.now().plus(EXPIRY_DAYS, ChronoUnit.DAYS)
            prefs.put(KEY_EXPIRY, expiryDate.toString())
        } else {
            // 如果不记住密码，则清除所有存储的信息
            clearLoginInfo()
        }

        // 确保首选项被立即写入磁盘
        try {
            prefs.flush()
        } catch (e: Exception) {
            // 记录错误，但不中断程序流程
            println("保存登录首选项时出错: ${e.message}")
        }
    }

    /**
     * 清除所有登录信息
     */
    fun clearLoginInfo() {
        prefs.remove(KEY_ADDRESS)
        prefs.remove(KEY_DATABASE)
        prefs.remove(KEY_USERNAME)
        prefs.remove(KEY_PASSWORD)
        prefs.remove(KEY_REMEMBER)
        prefs.remove(KEY_EXPIRY)

        try {
            prefs.flush()
        } catch (e: Exception) {
            println("清除登录首选项时出错: ${e.message}")
        }
    }

    /**
     * 获取已保存的登录信息
     *
     * @return 包含登录信息的数据类，如果没有保存或已过期则返回null
     */
    fun getLoginInfo(): LoginInfo? {
        // 检查是否有已保存的登录信息
        if (!prefs.getBoolean(KEY_REMEMBER, false)) {
            return null
        }

        // 检查过期时间
        val expiryString = prefs.get(KEY_EXPIRY, null) ?: return null
        try {
            val expiryDate = LocalDateTime.parse(expiryString)
            if (LocalDateTime.now().isAfter(expiryDate)) {
                // 已过期，清除数据
                clearLoginInfo()
                return null
            }
        } catch (e: Exception) {
            // 日期解析错误，视为已过期
            clearLoginInfo()
            return null
        }

        // 返回保存的信息
        return LoginInfo(
            address = prefs.get(KEY_ADDRESS, ""),
            database = prefs.get(KEY_DATABASE, ""),
            username = prefs.get(KEY_USERNAME, ""),
            password = prefs.get(KEY_PASSWORD, ""),
            remember = true
        )
    }

    /**
     * 登录信息数据类
     */
    data class LoginInfo(
        val address: String,
        val database: String,
        val username: String,
        val password: String,
        val remember: Boolean
    )
} 