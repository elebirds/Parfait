package moe.hhm.parfait.utils

import org.slf4j.LoggerFactory
import java.io.IOException
import java.util.Properties

/**
 * 版本工具类
 *
 * 用于获取应用程序版本和Git提交信息
 */
object VersionUtils {
    private val logger = LoggerFactory.getLogger(VersionUtils::class.java)
    private val gitProperties = Properties()

    private val VERSION_MAJOR
        get() = gitProperties.getProperty("application.version")

    init {
        try {
            VersionUtils::class.java.getResourceAsStream("/git.properties")?.use {
                gitProperties.load(it)
            } ?: logger.warn("无法加载git.properties文件")
        } catch (e: IOException) {
            logger.error("读取git.properties文件失败", e)
        }
    }

    /**
     * 获取应用程序完整版本号
     * @return 格式为"主版本号-Git简短提交ID"的版本号字符串
     */
    fun getFullVersion(): String {
        val gitCommit = gitProperties.getProperty("git.commit.id.abbrev", "dev")
        return "$VERSION_MAJOR-$gitCommit"
    }

    /**
     * 获取Git分支名称
     */
    fun getGitBranch(): String {
        return gitProperties.getProperty("git.branch", "unknown")
    }

    /**
     * 获取Git提交时间
     */
    fun getGitCommitTime(): String {
        return gitProperties.getProperty("git.commit.time", "unknown")
    }

    /**
     * 获取Git简短提交ID
     */
    fun getGitCommitId(): String {
        return gitProperties.getProperty("git.commit.id.abbrev", "unknown")
    }

    /**
     * 获取应用程序版本信息
     */
    fun getFullInfo(): String {
        return "Parfait ${getFullVersion()} (分支: ${getGitBranch()}, 提交时间: ${getGitCommitTime()})"
    }
}