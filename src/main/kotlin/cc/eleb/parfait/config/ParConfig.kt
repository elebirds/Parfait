package cc.eleb.parfait.config

import cc.eleb.parfait.utils.file.ZipUtils
import java.io.File
import java.io.FileNotFoundException

class ParConfig(var file: File) {
    var studentConfig: StudentConfig = StudentConfig()
    var i18nConfig: I18nConfig = I18nConfig()
    var gpaConfig: GPAConfig = GPAConfig()
    var certificateConfig: CertificateConfig = CertificateConfig()

    init {
        this.load()
    }

    private fun load() {
        val data = try {
            ZipUtils.zip2Strings(file)
        } catch (e: FileNotFoundException) {
            linkedMapOf()
        }
        studentConfig.load(data["student"] ?: "")
        i18nConfig.load(data["i18n"] ?: "")
        gpaConfig.load(data["gpa"] ?: "")
        certificateConfig.load(data["certificate"] ?: "")
    }

    fun save() {
        ZipUtils.strings2Zip(
            file, linkedMapOf(
                "student" to studentConfig.toString(),
                "i18n" to i18nConfig.toString(),
                "gpa" to gpaConfig.toString(),
                "certificate" to certificateConfig.toString()
            )
        )
    }
}