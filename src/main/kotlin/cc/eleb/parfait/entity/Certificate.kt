package cc.eleb.parfait.entity

import cc.eleb.parfait.PARFAIT_FULL_NAME
import cc.eleb.parfait.config.CertificateConfig
import cc.eleb.parfait.i18n.GenLanguage
import cc.eleb.parfait.i18n.translateTo
import cc.eleb.parfait.utils.DateUtils
import cc.eleb.parfait.utils.GlobalSettings
import cc.eleb.parfait.utils.HanziUtils
import cc.eleb.parfait.utils.file.ZipUtils
import java.io.File
import java.text.NumberFormat

class Certificate(val lang: String, val name: String, val data: HashMap<String, String>) {
    fun replaceAndGenerate(outFile: File, st: Student,weighted:Boolean) {
        ZipUtils.strings2Zip(outFile, linkedMapOf<String, String>().apply {
            CertificateConfig.wordReadFiles.forEach { ef ->
                if (ef != REPLACE_FILE && ef != CORE_FILE) this[ef.substring(1)] = data[ef]!!
            }
            this[REPLACE_FILE.substring(1)] = data[REPLACE_FILE].toString()
                .replace("\${name}", st.name)
                .replace("\${name_for}", HanziUtils.hanzi2English(st.name))
                .replace("\${grade}", st.grade.toString())
                .replace("\${id}", st.id.toString())
                .replace("\${tit_for}", if (st.gender == 2) "her" else "his")
                .replace("\${ti_for}", if (st.gender == 2) "She" else "He")
                .replace("\${gender}", st.genderT)
                .replace("\${gender_for}", st.genderT.translateTo())
                .replace("\${school}", st.school)
                .replace("\${school_for}", st.school.translateTo())
                .replace("\${prof}", st.profession)
                .replace("\${prof_for}", st.profession.translateTo())
                .replace("\${stype}", if (!weighted) "算数" else "加权")
                .replace("\${stype_for}", (if (!weighted) "算数" else "加权").translateTo())
                .replace("\${score}", nf.format(if (!weighted) st.simpleMean else st.weightedMean))
                .replace("\${date}", DateUtils.getCurrentFormattedDate2())
                .replace("\${date_for}", DateUtils.getCurrentFormattedDateEnglish())
                .replace("\${gpa}", nf.format(st.gpa))
            this[CORE_FILE.substring(1)] = data[CORE_FILE].toString()
                .replace("\${author}", PARFAIT_FULL_NAME)
                .replace("\${date}", DateUtils.getCurrentFormattedDateCF())
        })
    }


    companion object {
        val ces: LinkedHashMap<String, Certificate> = linkedMapOf()
        val nf: NumberFormat = NumberFormat.getInstance().apply {
            this.minimumFractionDigits = 2
            this.maximumFractionDigits = 2
            this.isGroupingUsed = false
        }
        const val REPLACE_FILE = "/word/document.xml"

        const val CORE_FILE = "/docProps/core.xml"

        fun generate(outFile: File, student: Student,weighted:Boolean) {
            val cer =
                if (ces.containsKey("certificate${if (student.status == 0) "A" else "B"}-${GenLanguage.nowGenLanguage}")) ces["certificate${if (student.status == 0) "A" else "B"}-${GenLanguage.nowGenLanguage}"]!!
                else ces["certificate${if (student.status == 0) "A" else "B"}-英语-English"]!!
            cer.replaceAndGenerate(outFile, student,weighted)
        }
    }
}