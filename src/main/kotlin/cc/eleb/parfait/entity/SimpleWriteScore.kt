package cc.eleb.parfait.entity

import com.alibaba.excel.annotation.ExcelProperty

class SimpleWriteScore {
    @ExcelProperty("课程名称")
    var name: String = ""

    @ExcelProperty("课程类别")
    var cType: String = ""

    @ExcelProperty("考核类型")
    var aType: String = ""

    @ExcelProperty("学分")
    var credit: Double = 0.0

    @ExcelProperty("成绩")
    var score: Double = 0.0

    @ExcelProperty("学位课")
    var gpa: String = "是"
}