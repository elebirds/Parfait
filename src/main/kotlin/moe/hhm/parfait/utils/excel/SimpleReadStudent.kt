/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.utils.excel

import com.alibaba.excel.annotation.ExcelProperty

data class SimpleReadStudent(
    @ExcelProperty("学号")
    val studentId: String,
    @ExcelProperty("姓名")
    val name: String,
    @ExcelProperty("性别")
    val gender: String,
    @ExcelProperty("状态")
    val status: String,
    @ExcelProperty("学院")
    val department: String,
    @ExcelProperty("专业")
    val major: String,
    @ExcelProperty("年级")
    val grade: Int,
    @ExcelProperty("班级")
    val classGroup: String
)