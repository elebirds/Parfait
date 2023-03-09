package cc.eleb.parfait.entity

import com.alibaba.excel.annotation.ExcelProperty

class SimpleStudent {
    @ExcelProperty("学号")
    var id: Int = 0

    @ExcelProperty("姓名")
    var name: String = ""

    @ExcelProperty("性别")
    var gender: String = ""

    @ExcelProperty("班级")
    var clazz: String = ""

    @ExcelProperty("学籍状态")
    var status: String = "在籍"

    @ExcelProperty("学院")
    var school: String = "商学院"
}