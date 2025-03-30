package cc.eleb.parfait.entity

import cc.eleb.parfait.infra.i18n.translateTo
import cc.eleb.parfait.utils.DateUtils
import cc.eleb.parfait.utils.HanziUtils
import com.deepoove.poi.XWPFTemplate
import java.io.File
import java.io.FileOutputStream
import java.security.InvalidParameterException
import java.text.NumberFormat

class Certificate(val name: String, val path: String) {
    lateinit var template: XWPFTemplate

    fun generate(outFile: File, st: Student) {
        if (!::template.isInitialized) init()
        template.render(
            hashMapOf<String, Any>(
                "name" to st.name,
                "name_ENG" to HanziUtils.hanzi2English(st.name),
                "grade" to st.grade,
                "id" to st.id.toString(),
                "pron_ENG" to if (st.gender == 2) "She" else "He",
                "possPron_ENG" to if (st.gender == 2) "her" else "his",
                "gender" to st.genderT,
                "gender_ENG" to st.genderT.translateTo(),
                "school" to st.school,
                "school_ENG" to st.school.translateTo(),
                "prof" to st.profession,
                "prof_ENG" to st.profession.translateTo(),
                "date" to DateUtils.getCurrentFormattedDate2(),
                "date_ENG" to DateUtils.getCurrentFormattedDateEnglish(),
                "gpa" to nf.format(st.gpa),
                "score_weighted" to nf.format(st.weightedMean),
                "score_simple" to nf.format(st.simpleMean),
            )
        ).writeAndClose(FileOutputStream(outFile))
    }

    fun init() {
        val sp = path.split("/").takeIf { it.size == 2 } ?: throw InvalidParameterException("非法模板路径: $path")
        when (sp[0]) {
            "jar" -> {
                val inputStream = this::class.java.classLoader.getResourceAsStream("certificate/${sp[1]}")
                if (inputStream == null) throw InvalidParameterException("模板不存在: $path")
                template = XWPFTemplate.compile(inputStream)
            }

            "par" -> { // TODO: 在par文件中(实质为zip文件)

            }

            "file" -> {
                val file = File(sp[1])
                if (!file.exists()) throw InvalidParameterException("模板不存在: $path")
                template = XWPFTemplate.compile(file)
            }

            else -> throw InvalidParameterException("非法模板路径: $path")
        }
    }

    companion object {
        val ces: LinkedHashMap<String, Certificate> = linkedMapOf()
        val nf: NumberFormat = NumberFormat.getInstance().apply {
            this.minimumFractionDigits = 2
            this.maximumFractionDigits = 2
            this.isGroupingUsed = false
        }
    }
}