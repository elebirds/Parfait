package cc.eleb.parfait.entity

import cc.eleb.parfait.config.GPAConfig
import cc.eleb.parfait.i18n.translateTo

import com.alibaba.excel.EasyExcel
import com.alibaba.excel.read.listener.PageReadListener

import java.io.File

data class Student(
    val id: Int,//学号
    val name: String,//姓名
    var gender: Int,//性别,0未知,1男,2女
    var status: Int,//学籍状态,0在籍,1已毕业
    var grade: Int,//年级,于班级中拆分
    var school: String,//学院,于班级中拆分
    var profession: String,//专业,于班级中拆分
    var clazz: String,//班级
    var scores: LinkedHashMap<String, Score>//成绩
) {
    val weightedMean: Double
        get() {
            var a = 0.0
            var b = 0.0
            scores.forEach { (_, u) ->
                a += u.score * u.credit
                b += u.credit
            }
            return a / b
        }

    val genderT: String
        get() {
            return when (gender) {
                0 -> "未知"
                1 -> "男"
                else -> "女"
            }
        }

    val simpleMean: Double
        get() {
            var a = 0.0
            var b = 0
            scores.forEach { (_, u) ->
                a += u.score
                b += 1
            }
            return a / b
        }

    val gpa: Double
        get() {
            var a = 0.0
            var b = 0.0
            scores.forEach { (_, u) ->
                if (u.gpa) {
                    a += GPAConfig.getGPA(u.score) * u.credit
                    b += u.credit
                }
            }
            return a / b
        }

    fun clearScores() = scores.clear()

    fun addScoresFromFile(f: File) {
        EasyExcel.read(f, Score::class.java, PageReadListener<Score> {
            it.forEach { t ->
                scores[t.name] = t
            }
        }).sheet().doRead()
    }

    fun toMap(): LinkedHashMap<String, Any> {
        return linkedMapOf(
            "name" to name,
            "gender" to gender,
            "status" to status,
            "school" to school,
            "grade" to grade,
            "prof" to profession,
            "class" to clazz,
            "scores" to linkedMapOf<String, Any>().apply {
                scores.forEach { (t, u) ->
                    this[t] = u.toMap()
                }
            }
        )
    }

    override fun toString(): String {
        return super.toString()
    }

    companion object {
        @JvmStatic
        val students: LinkedHashMap<Int, Student> = linkedMapOf()

        fun addStudentsFromFile(f: File) {
            EasyExcel.read(f, SimpleStudent::class.java, PageReadListener<SimpleStudent> {
                it.forEach { t ->
                    t.clazz = t.clazz.replace("(", "（").replace(")", "）")
                    students[t.id] = Student(
                        id = t.id,
                        name = t.name,
                        gender = if (t.gender.contains('女')) {
                            2
                        } else if (t.gender.contains('男')) {
                            1
                        } else {
                            0
                        },
                        status = if (t.status == "在籍") 0 else 1,
                        clazz = t.clazz,
                        scores = linkedMapOf(),
                        grade = t.clazz.substring(0, 4).toInt(),
                        school = t.school,
                        profession = t.clazz.replace(Regex("本科\\d班\$"), "").replace(Regex("^\\d{4}级"), "")
                            .replace(Regex("^\\d{4}"), "")
                    )
                    println(students[t.id]!!.profession.translateTo())
                }
            }).sheet().doRead()
        }
    }
}