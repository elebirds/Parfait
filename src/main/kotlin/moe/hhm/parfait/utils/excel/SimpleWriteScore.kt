/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.utils.excel

import com.alibaba.excel.annotation.ExcelProperty

data class SimpleWriteScore(
    @ExcelProperty("课程名称")
    var name: String,
    @ExcelProperty("课程类型")
    var type: String,
    @ExcelProperty("考核方式")
    var exam: String,
    @ExcelProperty("学分")
    var credit: Int,
    @ExcelProperty("成绩")
    var score: Double,
    @ExcelProperty("计入GPA")
    var gpa: String
)