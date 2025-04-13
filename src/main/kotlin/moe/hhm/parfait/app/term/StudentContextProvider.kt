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
     * 获取学生ID，用于缓存键
     * @return 学生ID，如果学生为null则返回null
     */
    fun getStudentId(): String? {
        return student?.studentId
    }
    
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
            // 基础信息字段
            "name" -> student?.name
            "studentId" -> student?.studentId
            "gender" -> student?.gender?.name?.lowercase()
            "status" -> student?.status?.name?.lowercase()
            
            // 学术信息字段
            "department" -> student?.department
            "major" -> student?.major
            "grade" -> student?.grade?.toString()
            "classGroup" -> student?.classGroup
            
            // 组合上下文
            "major_department" -> "${student?.major}_${student?.department}"
            "department_major" -> "${student?.department}_${student?.major}"
            "class" -> "${student?.grade}_${student?.classGroup}"
            
            // 其他特定格式
            "gradeYear" -> if (student?.grade != null) {
                (Calendar.getInstance().get(Calendar.YEAR) - student!!.grade + 1).toString()
            } else null
            
            // 默认情况
            else -> null
        }
    }
    
    /**
     * 获取所有可用的上下文映射
     * @return 字段名到上下文值的映射
     */
    fun getAllContexts(): Map<String, String> {
        val contexts = mutableMapOf<String, String>()
        
        if (student == null) return contexts
        
        // 基础信息字段
        student?.name?.let { contexts["name"] = it }
        student?.studentId?.let { contexts["studentId"] = it }
        student?.gender?.name?.lowercase()?.let { contexts["gender"] = it }
        student?.status?.name?.lowercase()?.let { contexts["status"] = it }
        
        // 学术信息字段
        student?.department?.let { contexts["department"] = it }
        student?.major?.let { contexts["major"] = it }
        student?.grade?.toString()?.let { contexts["grade"] = it }
        student?.classGroup?.let { contexts["classGroup"] = it }
        
        // 组合上下文
        if (student?.major != null && student?.department != null) {
            contexts["major_department"] = "${student?.major}_${student?.department}"
            contexts["department_major"] = "${student?.department}_${student?.major}"
        }
        
        if (student?.grade != null && student?.classGroup != null) {
            contexts["class"] = "${student?.grade}_${student?.classGroup}"
        }
        
        // 其他特定格式
        if (student?.grade != null) {
            contexts["gradeYear"] = (Calendar.getInstance().get(Calendar.YEAR) - student!!.grade + 1).toString()
        }
        
        return contexts
    }
    
    /**
     * 重置上下文数据
     */
    override fun reset() {
        student = null
    }
} 