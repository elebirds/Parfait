import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.regex.Pattern

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class I18nConfigTest {

    /**
     * 正则表达式模式，用于匹配格式化字符串中的占位符，如 %s, %d, %f 等
     */
    private val formatSpecifierPattern = Pattern.compile("%([1-9]\\$)?[sdfox]")

    @Test
    fun `测试所有语言文件中键的一致性`() {
        // 加载所有语言文件
        val englishProps = loadPropertiesFile("i18n/messages_en.properties")
        val chineseProps = loadPropertiesFile("i18n/messages_zh.properties")
        val frenchProps = loadPropertiesFile("i18n/messages_fr.properties")

        // 获取所有语言文件中的键集合
        val englishKeys = englishProps.stringPropertyNames()
        val chineseKeys = chineseProps.stringPropertyNames()
        val frenchKeys = frenchProps.stringPropertyNames()

        // 检查每个语言文件的键是否在其他语言文件中存在
        val inconsistentKeys = mutableListOf<String>()

        // 以中文为基准，检查其他语言文件的键
        for (key in chineseKeys) {
            if (!englishKeys.contains(key)) {
                inconsistentKeys.add("在英文中缺少键: $key")
            }
            if (!frenchKeys.contains(key)) {
                inconsistentKeys.add("在法文中缺少键: $key")
            }
        }

        // 断言所有语言文件中的键应该一致
        assertTrue(inconsistentKeys.isEmpty(),
            "有 ${inconsistentKeys.size} 个键在不同语言文件中不一致: ${inconsistentKeys.joinToString()}")
    }

    @Test
    fun `测试所有语言文件中格式化字符串的一致性`() {
        // 加载所有语言文件
        val englishProps = loadPropertiesFile("i18n/messages_en.properties")
        val chineseProps = loadPropertiesFile("i18n/messages_zh.properties")
        val frenchProps = loadPropertiesFile("i18n/messages_fr.properties")

        // 查找所有包含格式说明符的键
        val keysWithFormatSpecifiers = findKeysWithFormatSpecifiers(englishProps)
        keysWithFormatSpecifiers.addAll(findKeysWithFormatSpecifiers(chineseProps))
        keysWithFormatSpecifiers.addAll(findKeysWithFormatSpecifiers(frenchProps))

        // 检查每个包含格式说明符的键在各语言文件中是否有相同数量和类型的格式说明符
        val inconsistentKeys = mutableListOf<String>()

        for (key in keysWithFormatSpecifiers) {
            val formatSpecifiersMap = mutableMapOf<String, List<String>>()
            
            if (englishProps.containsKey(key)) {
                formatSpecifiersMap["英文"] = extractFormatSpecifiers(englishProps.getProperty(key))
            }
            
            if (chineseProps.containsKey(key)) {
                formatSpecifiersMap["中文"] = extractFormatSpecifiers(chineseProps.getProperty(key))
            }
            
            if (frenchProps.containsKey(key)) {
                formatSpecifiersMap["法文"] = extractFormatSpecifiers(frenchProps.getProperty(key))
            }
            
            // 检查所有语言文件中的格式说明符是否一致
            if (!areFormatSpecifiersConsistent(formatSpecifiersMap)) {
                inconsistentKeys.add(key)
                println("键 '$key' 在不同语言中的格式说明符不一致:")
                formatSpecifiersMap.forEach { (lang, specifiers) ->
                    println("  $lang: ${specifiers.joinToString()}")
                }
            }
        }

        // 断言所有格式说明符应该一致
        assertTrue(inconsistentKeys.isEmpty(), 
            "有 ${inconsistentKeys.size} 个键在不同语言中的格式说明符不一致: ${inconsistentKeys.joinToString()}")
    }

    @Test
    fun `测试变量占位符一致性`() {
        // 加载所有语言文件
        val englishProps = loadPropertiesFile("i18n/messages_en.properties")
        val chineseProps = loadPropertiesFile("i18n/messages_zh.properties")
        val frenchProps = loadPropertiesFile("i18n/messages_fr.properties")

        // 查找所有包含变量占位符的键 (如 {id}, {name} 等)
        val placeholderPattern = Pattern.compile("\\{([^}]+)\\}")
        val keysWithPlaceholders = findKeysWithPattern(englishProps, placeholderPattern)
        keysWithPlaceholders.addAll(findKeysWithPattern(chineseProps, placeholderPattern))
        keysWithPlaceholders.addAll(findKeysWithPattern(frenchProps, placeholderPattern))

        // 检查每个包含变量占位符的键在各语言文件中是否有相同的变量占位符
        val inconsistentKeys = mutableListOf<String>()

        for (key in keysWithPlaceholders) {
            val placeholdersMap = mutableMapOf<String, Set<String>>()
            
            if (englishProps.containsKey(key)) {
                placeholdersMap["英文"] = extractPlaceholders(englishProps.getProperty(key), placeholderPattern)
            }
            
            if (chineseProps.containsKey(key)) {
                placeholdersMap["中文"] = extractPlaceholders(chineseProps.getProperty(key), placeholderPattern)
            }
            
            if (frenchProps.containsKey(key)) {
                placeholdersMap["法文"] = extractPlaceholders(frenchProps.getProperty(key), placeholderPattern)
            }
            
            // 检查所有语言文件中的变量占位符是否一致
            if (!arePlaceholdersConsistent(placeholdersMap)) {
                inconsistentKeys.add(key)
                println("键 '$key' 在不同语言中的变量占位符不一致:")
                placeholdersMap.forEach { (lang, placeholders) ->
                    println("  $lang: ${placeholders.joinToString()}")
                }
            }
        }

        // 断言所有变量占位符应该一致
        assertTrue(inconsistentKeys.isEmpty(), 
            "有 ${inconsistentKeys.size} 个键在不同语言中的变量占位符不一致: ${inconsistentKeys.joinToString()}")
    }

    /**
     * 查找属性文件中所有包含格式说明符的键
     */
    private fun findKeysWithFormatSpecifiers(props: Properties): MutableSet<String> {
        val keysWithFormatSpecifiers = mutableSetOf<String>()
        props.stringPropertyNames().forEach { key ->
            val value = props.getProperty(key)
            if (formatSpecifierPattern.matcher(value).find()) {
                keysWithFormatSpecifiers.add(key)
            }
        }
        return keysWithFormatSpecifiers
    }

    /**
     * 查找属性文件中所有包含特定模式的键
     */
    private fun findKeysWithPattern(props: Properties, pattern: Pattern): MutableSet<String> {
        val keysWithPattern = mutableSetOf<String>()
        props.stringPropertyNames().forEach { key ->
            val value = props.getProperty(key)
            if (pattern.matcher(value).find()) {
                keysWithPattern.add(key)
            }
        }
        return keysWithPattern
    }

    /**
     * 提取字符串中的所有格式说明符
     */
    private fun extractFormatSpecifiers(text: String): List<String> {
        val matcher = formatSpecifierPattern.matcher(text)
        val formatSpecifiers = mutableListOf<String>()
        while (matcher.find()) {
            formatSpecifiers.add(matcher.group())
        }
        formatSpecifiers.sort()
        return formatSpecifiers
    }

    /**
     * 提取字符串中的所有变量占位符
     */
    private fun extractPlaceholders(text: String, pattern: Pattern): Set<String> {
        val matcher = pattern.matcher(text)
        val placeholders = mutableSetOf<String>()
        while (matcher.find()) {
            placeholders.add(matcher.group(1))
        }
        return placeholders
    }

    /**
     * 检查多种语言中的格式说明符是否一致
     */
    private fun areFormatSpecifiersConsistent(formatSpecifiersMap: Map<String, List<String>>): Boolean {
        if (formatSpecifiersMap.size <= 1) return true
        
        // 获取第一个语言的格式说明符作为参考
        val referenceSpecifiers = formatSpecifiersMap.values.first()
        
        // 检查所有其他语言的格式说明符是否与参考相同
        return formatSpecifiersMap.values.all { specifiers ->
            specifiers.size == referenceSpecifiers.size &&
                    specifiers.toTypedArray().contentEquals(referenceSpecifiers.toTypedArray())
        }
    }

    /**
     * 检查多种语言中的变量占位符是否一致
     */
    private fun arePlaceholdersConsistent(placeholdersMap: Map<String, Set<String>>): Boolean {
        if (placeholdersMap.size <= 1) return true
        
        // 获取第一个语言的占位符作为参考
        val referencePlaceholders = placeholdersMap.values.first()
        
        // 检查所有其他语言的占位符是否与参考相同
        return placeholdersMap.values.all { placeholders ->
            placeholders == referencePlaceholders
        }
    }

    private fun loadPropertiesFile(path: String): Properties {
        val props = Properties()
        javaClass.classLoader.getResourceAsStream(path).use { inputStream ->
            if (inputStream != null) {
                InputStreamReader(inputStream, StandardCharsets.UTF_8).use { reader ->
                    props.load(reader)
                }
            } else {
                fail("无法加载资源文件: $path")
            }
        }
        return props
    }
} 