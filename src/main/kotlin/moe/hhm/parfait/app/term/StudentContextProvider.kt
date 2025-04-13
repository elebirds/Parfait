/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.app.term

import moe.hhm.parfait.dto.StudentDTO
import java.util.*

/**
 * 学生上下文提供者
 * 从StudentDTO中提取上下文信息用于术语替换
 */
class StudentContextProvider : ContextProvider {
    private var student: StudentDTO? = null
    /**
     * 设置学生对象
     * @param studentDTO 学生数据传输对象
     */
    fun setStudent(studentDTO: StudentDTO?) {
        this.student = studentDTO
    }
    
    /**
     * 根据字段名从学生对象中获取上下文信息
     * @param field 字段名
     * @return 对应的上下文值，如果没有匹配或学生对象为空则返回null
     */
    override fun getContext(field: String): String? {
        if (student == null) return null
        
        return when (field) {
            "gender" -> student?.gender?.name
            "status" -> student?.status?.name
            "department" -> student?.department
            "major" -> student?.major
            else -> null
        }
    }

    /**
     * 重置上下文数据
     */
    override fun reset() {
        student = null
    }
} 