package cc.eleb.parfait.utils.config

import cc.eleb.parfait.utils.Charset
import cc.eleb.parfait.utils.DateUtils
import cc.eleb.parfait.utils.file.getExtension
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.moandjiezana.toml.Toml
import com.moandjiezana.toml.TomlWriter
import org.apache.commons.io.FileUtils
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.IOException
import java.util.*
import java.util.regex.Pattern

class Config {
    var type: ConfigType
    var file: File
    var isFile = true
    var data: ConfigData = ConfigData()
    private var modified = false

    constructor(content: String, type: ConfigType) {
        this.type = type
        this.file = File("")
        this.isFile = false
        this.readContent(content)
    }

    constructor(file: File) : this(file = file, type = smartCheckType(file))

    constructor(file: File, type: ConfigType) : this(file, type, ConfigData())

    /**
     * FoundHi 配置文件类
     *
     * @param file 文件
     * @param type 配置类型
     * @param default 若未被创建的默认内容
     * */
    constructor(file: File, type: ConfigType, default: ConfigData) {
        this.type = type
        this.file = file
        this.data = default
        this.load()
    }

    fun reload() {
        this.data.clear()
        this.modified = false
        this.load()
    }

    fun load(): Boolean {
        return this.load(ConfigData())
    }

    fun load(defaultMap: ConfigData): Boolean {
        this.modified = true
        if (!this.file.exists()) {
            try {
                this.file.createNewFile()
            } catch (e: IOException) {
                //MainLogger.getLogger().error("Could not create Config " + this.file.toString())
                //TODO LOG
            }
            this.data = defaultMap
            this.save()
        } else {
            var content = ""
            try {
                content = FileUtils.readFileToString(this.file, Charset.defaultCharset)
            } catch (e: IOException) {
                //APIFunction.getInstance().getLogger().logException(e)
                //TODO LOG
            }
            this.readContent(content)
        }
        return true
    }

    fun getAll(): LinkedHashMap<String, Any> {
        return this.data
    }

    operator fun get(key: String): Any? {
        return this.data[key]
    }

    operator fun <T> get(key: String, defaultValue: T?): T? {
        return this.data[key, defaultValue]
    }

    fun getKeys(child: Boolean): Set<String> {
        return this.data.getKeys(child)
    }

    fun set(key: String, value: Any) {
        this.data[key] = value
    }

    fun remove(key: String) {
        this.data.remove(key)
    }

    private fun readContent(content: String) {
        when (this.type) {
            ConfigType.PROPERTIES -> this.parseProperties(content)
            ConfigType.JSON -> {
                val builder = GsonBuilder()
                val gson = builder.create()
                this.data = ConfigData(gson.fromJson(content, object : TypeToken<LinkedHashMap<String, Any>>() {}.type))
            }

            ConfigType.YAML -> {
                val dumperOptions = DumperOptions()
                dumperOptions.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
                val yaml = Yaml(dumperOptions)
                val dt = yaml.loadAs(content, LinkedHashMap::class.java)
                this.data = if (dt == null) {
                    ConfigData()
                } else {
                    ConfigData(dt)
                }
            }
            // case ConfigType.SERIALIZED
            ConfigType.ENUM -> this.parseList(content)
            ConfigType.TOML -> this.data = try {
                val toml = Toml().read(content)
                if (toml == null) {
                    ConfigData()
                } else {
                    val dt = linkedMapOf<String, Any>()
                    toml.toMap().forEach { (t, u) -> dt[t.toString()] = u }
                    ConfigData(dt)
                }
            } catch (e: Exception) {
                ConfigData()
            }
        }
    }

    private fun fillDefaults(defaultMap: ConfigData, data: ConfigData): ConfigData {
        for (key in defaultMap.keys) {
            if (!data.containsKey(key)) {
                data[key] = defaultMap[key]!!
            }
        }
        return data
    }

    private fun parseList(content: String) {
        var s = content
        s = s.replace("\r\n", "\n")
        for (v in s.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
            if (v.trim { it <= ' ' }.isEmpty()) {
                continue
            }
            data[v] = true
        }
    }

    private fun writeProperties(): String {
        var content = "#FoundHiConfig Created on ${DateUtils.getCurrentFormattedDate()}\n"
        for (entry in this.data.entries) {
            var v = entry.value
            val k = entry.key
            if (v is Boolean) {
                v = if (v) "on" else "off"
            }
            content += "$k=$v\n"
        }
        return content
    }

    private fun parseProperties(content: String) {
        for (line in content.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
            if (Pattern.compile("[a-zA-Z0-9\\-_.]*+=+[^\\r\\n]*").matcher(line).matches()) {
                val b = line.split("=".toRegex()).toTypedArray()
                val k = b[0]
                val v = b[1].trim { it <= ' ' }
                val vLower = v.lowercase(Locale.getDefault())
                if (this.data.containsKey(k)) {
                    //MainLogger.getLogger().debug("[Config] Repeated property " + k + " on file " + this.file.toString())
                    //todo log
                }
                when (vLower) {
                    "on", "true", "yes" -> this.data[k] = true
                    "off", "false", "no" -> this.data[k] = false
                    else -> this.data[k] = v
                }
            }
        }
    }

    fun save(file: File): Boolean {
        this.file = file
        return save()
    }

    fun save(): Boolean {
        if (!isFile) return false
        try {
            FileUtils.writeStringToFile(file, saveAsString(), Charset.defaultCharset)
        } catch (e: IOException) {
            //TODO LOG
        }
        return true
    }

    fun saveAsString(): String {
        return when (this.type) {
            ConfigType.PROPERTIES -> this.writeProperties()
            ConfigType.JSON -> GsonBuilder().setPrettyPrinting().create().toJson(this.getAll())
            ConfigType.YAML -> {
                val dumperOptions = DumperOptions()
                dumperOptions.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
                val yaml = Yaml(dumperOptions)
                yaml.dump(this.getAll())
            }

            ConfigType.ENUM -> {
                var content = ""
                this.data.entries.forEach {
                    content += it.key + "\r\n"
                }
                content
            }

            ConfigType.TOML -> TomlWriter().write(this.getAll())
        }
    }

    companion object {
        val format: MutableMap<String, ConfigType> = TreeMap()

        init {
            format["properties"] = ConfigType.PROPERTIES
            format["con"] = ConfigType.PROPERTIES
            format["conf"] = ConfigType.PROPERTIES
            format["config"] = ConfigType.PROPERTIES
            format["js"] = ConfigType.JSON
            format["json"] = ConfigType.JSON
            format["yml"] = ConfigType.YAML
            format["yaml"] = ConfigType.YAML
            format["toml"] = ConfigType.TOML
            format["txt"] = ConfigType.ENUM
            format["list"] = ConfigType.ENUM
            format["enum"] = ConfigType.ENUM
        }

        fun smartCheckType(file: File): ConfigType {
            return format[file.getExtension()] ?: ConfigType.PROPERTIES
        }
    }
}