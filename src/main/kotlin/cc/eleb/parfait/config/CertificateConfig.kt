package cc.eleb.parfait.config

import cc.eleb.parfait.entity.Certificate
import cc.eleb.parfait.utils.Charset
import cc.eleb.parfait.utils.config.Config
import cc.eleb.parfait.utils.config.ConfigData
import cc.eleb.parfait.utils.config.ConfigType
import org.apache.commons.io.IOUtils

class CertificateConfig {
    val default = linkedMapOf(
        "a" to linkedMapOf(//在校
            "英语-English" to "certificateA-英语-English"
        ),
        "b" to linkedMapOf(//毕业
            "英语-English" to "certificateB-英语-English"
        )
    )

    lateinit var config: Config
    fun load(content: String = "") {
        config = Config(content, ConfigType.YAML)
        Certificate.ces.clear()
        val dt = if (content == "") default else config.data
        (dt["a"] as LinkedHashMap<*, *>).forEach { (lang, fl) ->
            Certificate.ces[fl.toString()] =
                Certificate(lang.toString(), fl.toString(), hashMapOf<String, String>().apply {
                    wordReadFiles.forEach {
                        this[it] = IOUtils.toString(
                            CertificateConfig::class.java.classLoader.getResourceAsStream("certificate/$fl$it"),
                            Charset.defaultCharset
                        )
                    }
                })
        }
        (dt["b"] as LinkedHashMap<*, *>).forEach { (lang, fl) ->
            Certificate.ces[fl.toString()] =
                Certificate(lang.toString(), fl.toString(), hashMapOf<String, String>().apply {
                    wordReadFiles.forEach {
                        this[it] = IOUtils.toString(
                            CertificateConfig::class.java.classLoader.getResourceAsStream("certificate/$fl$it"),
                            Charset.defaultCharset
                        )
                    }
                })
        }
    }

    fun toMap(): LinkedHashMap<String, LinkedHashMap<String, String>> {
        return default
    }

    fun save() {
        config.data = ConfigData(toMap())
    }

    override fun toString(): String {
        save()
        return config.saveAsString()
    }

    companion object {
        val wordReadFiles = mutableListOf(
            "/docProps/app.xml",
            "/docProps/core.xml",
            "/docProps/custom.xml",
            "/word/document.xml",
            "/word/endnotes.xml",
            "/word/footnotes.xml",
            "/word/fontTable.xml",
            "/word/settings.xml",
            "/word/styles.xml",
            "/word/theme/theme1.xml",
            "/word/webSettings.xml",
            "/word/_rels/document.xml.rels",
            "/[Content_Types].xml",
            "/_rels/.rels"
        )
    }
}