package cc.eleb.parfait.i18n

class Language(val name: String, val data: LinkedHashMap<String, String> = linkedMapOf()) {
    fun get(chinese: String): String {
        return data[chinese] ?: "未找到 $name 的配置项:$chinese"
    }

    companion object {
        @JvmStatic
        val langs: LinkedHashMap<String, Language> = linkedMapOf()

        var nowLanguage: String = "英语-English"
    }
}

fun String.translateTo(): String {
    if (!Language.langs.containsKey(Language.nowLanguage)) return "未找到 ${Language.nowLanguage} 的配置"
    return Language.langs[Language.nowLanguage]!!.get(this)
}