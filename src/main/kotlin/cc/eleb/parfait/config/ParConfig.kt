package cc.eleb.parfait.config

import cc.eleb.parfait.entity.Certificate
import cc.eleb.parfait.entity.Student
import cc.eleb.parfait.infra.i18n.GenLanguage
import cc.eleb.parfait.infra.i18n.trs
import cc.eleb.parfait.ui.ParfaitFrame
import cc.eleb.parfait.ui.panel.StudentDataPanel
import cc.eleb.parfait.utils.file.ZipUtils
import java.io.File
import java.io.FileNotFoundException
import javax.swing.JOptionPane

class ParConfig(var file: File?) {
    var studentConfig: StudentConfig = StudentConfig()
    var i18nConfig: I18nConfig = I18nConfig()
    var gpaConfig: GPAConfig = GPAConfig()
    var certificateConfig: CertificateConfig = CertificateConfig()

    init {
        instance = this
        inited = true
        if (file == null) newed = true
        this.load()
    }

    private fun load() {
        val data = if (file != null) try {
            ZipUtils.zip2Strings(file!!)
        } catch (e: FileNotFoundException) {
            linkedMapOf()
        } else linkedMapOf()
        studentConfig.load(data["student"] ?: "")
        i18nConfig.load(data["i18n"] ?: "")
        gpaConfig.load(data["gpa"] ?: "")
        certificateConfig.load(data["certificate"] ?: "")
    }

    fun save() {
        file?.let {
            ZipUtils.strings2Zip(
                it, linkedMapOf(
                    "student" to studentConfig.toString(),
                    "i18n" to i18nConfig.toString(),
                    "gpa" to gpaConfig.toString(),
                    "certificate" to certificateConfig.toString()
                )
            )
        }
    }

    fun saveTo(file: File) {
        this.file = file
        newed = false
        ZipUtils.strings2Zip(
            file, linkedMapOf(
                "student" to studentConfig.toString(),
                "i18n" to i18nConfig.toString(),
                "gpa" to gpaConfig.toString(),
                "certificate" to certificateConfig.toString()
            )
        )
        ParfaitFrame.instance.title = if (inited) {
            if (newed) {
                "${"global-new-file".trs()} - Parfait"
            } else {
                file.name + " - Parfait"
            }
        } else "Parfait"
    }

    fun close() {
        newed = false
        inited = false
        instance = null
        Student.students.clear()
        GPAConfig.ranks.clear()
        GenLanguage.langs.clear()
        Certificate.ces.clear()
        StudentDataPanel.instance.table1.model.fireTableDataChanged()
    }

    companion object {
        var inited = false
        var newed = false
        var instance: ParConfig? = null

        fun checkInited(): Boolean {
            if (!inited) {
                JOptionPane.showMessageDialog(
                    null,
                    "请先新建或打开一个par文件！",
                    "处理失败",
                    JOptionPane.ERROR_MESSAGE
                )
            }
            return inited
        }
    }
}