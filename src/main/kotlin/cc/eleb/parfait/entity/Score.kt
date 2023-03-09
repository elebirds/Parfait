package cc.eleb.parfait.entity

import com.alibaba.excel.annotation.ExcelProperty

class Score {
    @ExcelProperty("课程名称")
    var name: String = ""

    @ExcelProperty("课程类别")
    var cType: String = ""

    @ExcelProperty("考核类型")
    var aType: String = ""

    @ExcelProperty("学分")
    var credit: Double = 0.0

    @ExcelProperty("成绩")
    var score: Int = 0

    var gpa: Boolean = true

    fun toMap(): LinkedHashMap<String, Any> {
        return linkedMapOf(
            "ct" to cType,
            "at" to aType,
            "credit" to credit,
            "score" to score,
            "gpa" to gpa
        )
    }
}
