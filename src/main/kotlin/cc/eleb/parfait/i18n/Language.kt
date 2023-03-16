package cc.eleb.parfait.i18n

import cc.eleb.parfait.utils.Charset
import cc.eleb.parfait.utils.GlobalSettings
import cc.eleb.parfait.utils.config.Config
import cc.eleb.parfait.utils.config.ConfigType
import org.apache.commons.io.IOUtils

object Language {
    val data: LinkedHashMap<Int, LinkedHashMap<String, String>> = linkedMapOf()
    fun load() {
        for (i in 0..3) {
            val config = Config(
                IOUtils.toString(
                    Language::class.java.classLoader.getResourceAsStream("lang/${i}.yml"),
                    Charset.defaultCharset
                ), ConfigType.YAML
            )
            this.data[i] = linkedMapOf<String, String>().apply {
                config.data.forEach { t, u ->
                    this[t] = u.toString()
                }
            }
        }
    }

    @JvmStatic
    fun trs(s: String): String = s.trs()
}

fun String.trs(): String = Language.data[GlobalSettings.LANGUAGE]!![this] ?: "语言配置错误"