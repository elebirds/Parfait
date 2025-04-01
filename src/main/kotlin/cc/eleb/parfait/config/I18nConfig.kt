package cc.eleb.parfait.config

import cc.eleb.parfait.i18n.GenLanguage
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
            "金融学（数学金融人工智能实验班）" to "Finance (Mathematics， Finance and Artificial Intelligence Experimental Class)",
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
        ),
        "日语-にほんご" to linkedMapOf(
            "保险学" to "保険学",
            "投资学" to "投資学",
            "金融学" to "ファイナンス",
            "信用管理" to "クレジットマネジメント",
            "金融工程" to "金融工学",
            "金融学（拔尖人才实验班）" to "ファイナンス（トップタレントのための実験室）",
            "金融学（数学金融人工智能实验班）" to "ファイナンス（数学・金融の人工知能実験室）",
            "金融科技" to "金融技術",
            "经济学" to "経済学",
            "经济学（中美合作）" to "経済学（中米協力）",
            "经济学（中法合作）" to "経済学（中仏協力）",
            "计算机科学与技术（中法合作）" to "コンピュータサイエンスとテクノロジー（中仏協力）",
            "广告学（中法合作）" to "広告（中仏協力）",
            "财务管理" to "財務管理",
            "资产评估" to "資産評価",
            "电子商务" to "電子商取引",
            "男" to "男性",
            "女" to "女性",
            "他的" to "彼の",
            "她的" to "彼女の",
            "他" to "彼",
            "她" to "彼女",
            "商学院" to "ビジネススクール ",
            "加权" to "加重",
            "算数" to "算術",
            "未知" to "不明"
        ),
        "法语-Français" to linkedMapOf(
            "保险学" to "Assurance",
            "投资学" to "Investissement",
            "金融学" to "Finance",
            "信用管理" to "Gestion du crédit",
            "金融工程" to "Ingénierie financière",
            "金融学（拔尖人才实验班）" to "Finance (cours expérimental pour les meilleurs talents)",
            "金融学（数学金融人工智能实验班）" to "Finance (cours expérimental sur l'intelligence artificielle en mathématiques et en finance)",
            "金融科技" to "Technologie financière",
            "经济学" to "Économie",
            "经济学（中美合作）" to "Économie (coopération sino-américaine)",
            "经济学（中法合作）" to "Économie (coopération sino-française)",
            "计算机科学与技术（中法合作）" to "Informatique et technologie (coopération sino-française)",
            "广告学（中法合作）" to "Publicité (coopération sino-française)",
            "财务管理" to "Gestion financière",
            "资产评估" to "Évaluation des actifs",
            "电子商务" to "Commerce électronique",
            "男" to "Hommes",
            "女" to "Féminin",
            "他的" to "Son",
            "她的" to "Son",
            "他" to "Il",
            "她" to "Elle",
            "商学院" to "École de commerce",
            "加权" to "pondéré",
            "算数" to "Arithmétique",
            "未知" to "Inconnu"
        )
    )

    lateinit var config: Config
    fun load(content: String = "") {
        config = Config(content, ConfigType.YAML)
        GenLanguage.langs.clear()
        val dt = if (content == "") default else config.data
        dt.forEach { (id, data) ->
            val d: Map<*, *> = data as Map<*, *>
            GenLanguage.langs[id] = GenLanguage(
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
            GenLanguage.langs.forEach { (t, u) ->
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