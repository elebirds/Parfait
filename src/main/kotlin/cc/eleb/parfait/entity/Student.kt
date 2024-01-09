package cc.eleb.parfait.entity

import cc.eleb.parfait.config.GPAConfig
import cc.eleb.parfait.i18n.translateTo
import cc.eleb.parfait.i18n.trs
import cc.eleb.parfait.utils.castTo
import com.alibaba.excel.EasyExcel
import com.alibaba.excel.read.listener.PageReadListener
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode
import javax.swing.JOptionPane

data class Student(
    val id: Int,//学号
    var name: String,//姓名
    var gender: Int,//性别,0未知,1男,2女
    var status: Int,//学籍状态,0在籍,1已毕业
    var grade: Int,//年级,于班级中拆分
    var school: String,//学院,于班级中拆分
    var profession: String,//专业,于班级中拆分
    var clazz: String,//班级
    var scores: ArrayList<Score>//成绩
) {
    val weightedMean: Double
        get() {
            if (scores.isEmpty()) return 0.0
            var a = 0.0
            var b = 0.0
            scores.forEach { u ->
                a += (u.score * u.credit)
                b += (u.credit)
            }
            return a.castTo(2)/b.castTo(2)
        }

    val genderT: String
        get() {
            return when (gender) {
                0 -> "未知"
                1 -> "男"
                else -> "女"
            }
        }
    val genderS: String
        get() {
            return when (gender) {
                0 -> "global-unknown".trs()
                1 -> "global-sex-m".trs()
                else -> "global-sex-f".trs()
            }
        }

    val statusT: String
        get() {
            return when (status) {
                0 -> "在籍"
                else -> "毕业"
            }
        }

    val statusS: String
        get() {
            return when (status) {
                0 -> "global-status-in".trs()
                else -> "global-status-out".trs()
            }
        }

    val simpleMean: Double
        get() {
            if (scores.isEmpty()) return 0.0
            var a = 0.0
            var b = 0
            scores.forEach { u ->
                a += u.score
                b += 1
            }
            return a.castTo(2)/b
        }

    val gpa: Double
        get() {
            if (scores.isEmpty()) return 0.0
            var a = 0.0
            var b = 0.0
            scores.forEach { u ->
                if (u.gpa) {
                    a += GPAConfig.getGPA(u.score) * u.credit
                    b += u.credit
                }
            }
            return a.castTo(2)/b.castTo(2)
        }

    fun clearScores() = scores.clear()

    fun addScoresFromFile(f: File) {
        EasyExcel.read(f, Score::class.java, PageReadListener {
            it.forEach { i: Score ->
                i.gpa = i.gpaString == "是"
            }
            it.forEach(scores::add)
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
                scores.forEach {
                    this[it.id.toString()] = it.toMap()
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
            EasyExcel.read(f, SimpleReadStudent::class.java, PageReadListener<SimpleReadStudent> {
                var a = 0
                it.forEach { t ->
                    t.clazz = t.clazz.replace("(", "（").replace(")", "）")
                    if(students.containsKey(t.id))a++
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
                        scores = arrayListOf(),
                        grade = t.clazz.substring(0, 4).toInt(),
                        school = t.school,
                        profession = t.clazz.replace(Regex("本科\\d班\$"), "").replace(Regex("^\\d{4}级"), "")
                            .replace(Regex("^\\d{4}"), "")
                    )
                    //println(students[t.id]!!.profession.translateTo())
                }
                JOptionPane.showMessageDialog(null,"impo-file-result".trs().replace("%i",a.toString()),"global-success".trs(),JOptionPane.INFORMATION_MESSAGE)
            }).sheet().doRead()
        }
    }
}
