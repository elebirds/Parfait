package cc.eleb.parfait.theme

import java.io.File

class ThemeInfo(
    val name: String, val resourceName: String?, val dark: Boolean?, val license: String?, val licenseFile: String?,
    val sourceCodeUrl: String?, val sourceCodePath: String?, val themeFile: File?, val lafClassName: String?
)