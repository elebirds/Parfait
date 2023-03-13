package cc.eleb.parfait.config

import cc.eleb.parfait.entity.Score
import cc.eleb.parfait.entity.Student
import cc.eleb.parfait.utils.cast
import cc.eleb.parfait.utils.config.Config
import cc.eleb.parfait.utils.config.ConfigData
import cc.eleb.parfait.utils.config.ConfigType

class StudentConfig {
    lateinit var config: Config
    fun load(content: String = "") {
        config = Config(content, ConfigType.YAML)
        Student.students.clear()
        config.data.forEach { id, data ->
            val d: Map<*, *> = data as Map<*, *>
            Student.students[id.toInt()] = Student(
                id = id.toInt(),
                name = d["name"].cast(),
                gender = d["gender"].cast(),
                status = d["status"].cast(),
                grade = d["grade"].cast(),
                school = d["school"].cast(),
                profession = d["prof"].cast(),
                clazz = d["class"].cast(),
                scores = arrayListOf<Score>().apply {
                    (d["scores"] as Map<*, *>).forEach { (sid, sd) ->
                        val sf = sd as Map<*, *>
                        this.add(Score().apply {
                            name = sid.cast()
                            cType = sf["ct"].cast()
                            aType = sf["at"].cast()
                            credit = sf["credit"].cast()
                            score = sf["score"].cast()
                        })
                    }
                }
            )
        }
    }

    fun toMap(): LinkedHashMap<String, Any> {
        return linkedMapOf<String, Any>().apply {
            Student.students.forEach { (t, u) ->
                this[t.cast()] = u.toMap()
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