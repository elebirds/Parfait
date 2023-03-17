package cc.eleb.parfait.entity

import com.alibaba.excel.annotation.ExcelProperty

class SimpleWriteStudent {
    @ExcelProperty("学号")
    var id: Int = 0

    @ExcelProperty("姓名")
    var name: String = ""

    @ExcelProperty("性别")
    var gender: String = ""

    @ExcelProperty("学籍状态")
    var status: String = "在籍"

    @ExcelProperty("班级")
    var clazz: String = ""

    @ExcelProperty("年级")
    var grade: Int = 0

    @ExcelProperty("学院")
    var school: String = "商学院"

    @ExcelProperty("专业")
    var profession: String = ""

    @ExcelProperty("GPA")
    var gpa = ""

    @ExcelProperty("加权平均分")
    var weightedMean = ""

    @ExcelProperty("算术平均分")
    var simpleMean = ""
}