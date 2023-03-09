package cc.eleb.parfait.config

import cc.eleb.parfait.i18n.Language
import cc.eleb.parfait.utils.config.Config
import cc.eleb.parfait.utils.config.ConfigData
import cc.eleb.parfait.utils.config.ConfigType

class I18nConfig {
    val default = linkedMapOf(
        "英语-English" to linkedMapOf(
            "保险学" to "Insurance",
            "投资学" to "Investment",
            "金融学" to "Finance",
            "信用管理" to "Credit Management",
            "金融工程" to "Financial Engineering",
            "金融学（拔尖人才实验班）" to "Finance (Top talent experimental class)",
            "金融学(数学金融人工智能实验班)" to "Finance (Mathematics， Finance and Artificial Intelligence Experimental Class)",
            "金融科技" to "Financial Technology",
            "经济学" to "Economics",
            "经济学（中美合作）" to "Economics (Sino-US Cooperation)",
            "经济学（中法合作）" to "Economics (Sino-French Cooperation)",
            "计算机科学与技术（中法合作）" to "Computer Science and Technology (Sino-French Cooperation)",
            "广告学（中法合作）" to "Advertising (Sino-French Cooperation)",
            "财务管理" to "Financial Management",
            "资产评估" to "Assets Appraisal",
            "电子商务" to "Electronic Business",
            "男" to "Male",
            "女" to "Female",
            "他的" to "his",
            "她的" to "her",
            "他" to "He",
            "她" to "She",
            "商学院" to "School of Finance and Business",
            "加权" to "weighted",
            "算数" to "simple",
            "未知" to "Unknown"
        )
    )

    lateinit var config: Config
    fun load(content: String = "") {
        config = Config(content, ConfigType.YAML)
        Language.langs.clear()
        val dt = if (content == "") default else config.data
        dt.forEach { (id, data) ->
            val d: Map<*, *> = data as Map<*, *>
            Language.langs[id] = Language(
                name = id,
                data = linkedMapOf<String, String>().apply {
                    d.forEach { (t, u) ->
                        this[t.toString()] = u.toString()
                    }
                }
            )
        }
    }

    fun toMap(): LinkedHashMap<String, Any> {
        return linkedMapOf<String, Any>().apply {
            Language.langs.forEach { (t, u) ->
                this[t] = u.data
            }
        }
    }

    fun save() {
        config.data = ConfigData(toMap())
    }

    override fun toString(): String {
        save()
        return config.saveAsString()
    }
}