package cc.eleb.parfait.config

import cc.eleb.parfait.entity.Certificate
import cc.eleb.parfait.utils.config.Config
import cc.eleb.parfait.utils.config.ConfigData
import cc.eleb.parfait.utils.config.ConfigType

class CertificateConfig {
    val default = linkedMapOf(
        "默认模板：中英双语-在校生-加权平均分" to "jar/default_english.docx",
    )

    lateinit var config: Config
    fun load(content: String = "") {
        config = Config(content, ConfigType.YAML)
        Certificate.ces.clear()
        val dt = if (content == "") default else config.data
        dt.forEach { (k, v) ->
            Certificate.ces[k] = Certificate(k, v.toString()).apply {
                init()
            }
        }
    }

    fun toMap(): LinkedHashMap<String, String> {
        return default
    }

    fun save() {
        config.data = ConfigData(toMap())
    }

    override fun toString(): String {
        save()
        return config.saveAsString()
    }
}