/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.utils.excel

import com.alibaba.excel.annotation.ExcelProperty

class SimpleReadStudent {
    @ExcelProperty("学号")
    var studentId: String = ""
    @ExcelProperty("姓名")
    var name: String = ""
    @ExcelProperty("性别")
    var gender: String = "未知"
    @ExcelProperty("状态")
    var status: String = "在籍"
    @ExcelProperty("学院")
    var department: String = ""
    @ExcelProperty("专业")
    var major: String = ""
    @ExcelProperty("年级")
    var grade: Int = 2022
    @ExcelProperty("班级")
    var classGroup: String = ""
}