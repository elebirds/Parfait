package cc.eleb.parfait.infra.i18n

class GenLanguage(val name: String, val data: LinkedHashMap<String, String> = linkedMapOf()) {
    fun get(chinese: String): String {
        return data[chinese] ?: "未找到 $name 的配置项:$chinese"
    }

    companion object {
        @JvmStatic
        val langs: LinkedHashMap<String, GenLanguage> = linkedMapOf()

        var nowGenLanguage: String = "英语-English"

        fun tst(s: String) = s.translateTo()
    }
}

fun String.translateTo(): String {
    if (!GenLanguage.langs.containsKey(GenLanguage.nowGenLanguage)) return "未找到 ${GenLanguage.nowGenLanguage} 的配置"
    return GenLanguage.langs[GenLanguage.nowGenLanguage]!!.get(this)
}