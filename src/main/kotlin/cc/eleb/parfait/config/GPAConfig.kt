package cc.eleb.parfait.config

import cc.eleb.parfait.utils.config.Config
import cc.eleb.parfait.utils.config.ConfigData
import cc.eleb.parfait.utils.config.ConfigType

class GPAConfig {
    lateinit var config: Config

    fun load(content: String = "") {
        config = Config(content, ConfigType.YAML)
        if (content != "") {
            ranks.clear()
            config.data.forEach { (score, gpa) ->
                ranks[score.toInt()] = gpa.toString().toDouble()
            }
        }
        ranks.toSortedMap()
    }

    fun toMap(): LinkedHashMap<String, Any> {
        return linkedMapOf<String, Any>().apply {
            ranks.forEach { (t, u) ->
                this[t.toString()] = u.toString()
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

    companion object {
        val ranks: LinkedHashMap<Int, Double> = linkedMapOf(
            90 to 4.0,
            85 to 3.5,
            80 to 3.0,
            75 to 2.5,
            70 to 2.0,
            65 to 1.5,
            60 to 1.0,
            0 to 0.0
        )//大于等于此

        fun getGPA(score: Double): Double {
            ranks.forEach { (t, u) ->
                if (score >= t) return u
            }
            return 0.0
        }
    }
}