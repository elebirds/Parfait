/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.dto

/**
 * 证明内容存储类型
 */
enum class CertificateContentType {
    /**
     * JAR资源文件，内部资源文件
     */
    JAR_RESOURCE,
    
    /**
     * 本地文件，存储文件路径
     */
    LOCAL_FILE,
    
    /**
     * 数据库存储，将文件内容存储在数据库中
     */
    DATABASE;
    
    companion object {
        /**
         * 从字符串转换为枚举类型
         */
        fun fromString(value: String): CertificateContentType {
            return when (value.uppercase()) {
                "JAR_RESOURCE" -> JAR_RESOURCE
                "LOCAL_FILE" -> LOCAL_FILE
                "DATABASE" -> DATABASE
                else -> LOCAL_FILE // 默认为本地文件
            }
        }
        
        /**
         * 从前缀识别类型
         */
        fun fromPath(path: String): CertificateContentType {
            return when {
                path.startsWith("jar::") -> JAR_RESOURCE
                path.startsWith("db::") -> DATABASE
                else -> LOCAL_FILE
            }
        }
        
        /**
         * 生成路径
         */
        fun generatePath(type: CertificateContentType, path: String): String {
            return when (type) {
                JAR_RESOURCE -> "jar::$path"
                DATABASE -> "db::$path"
                LOCAL_FILE -> path
            }
        }
    }
} 