import com.deepoove.poi.XWPFTemplate
import com.deepoove.poi.config.Configure
import com.deepoove.poi.util.RegexUtils
import java.io.FileOutputStream


fun main() {
    val fileName = "C:\\Users\\hhmcn\\Desktop\\在读证明文档.docx"
    val builder = Configure.builder()
    builder.buildGrammerRegex(RegexUtils.createGeneral("{{", "}}"));
    val template = XWPFTemplate.compile(fileName, builder.build())
    try {
        // 获取所有模板变量的标签名
        template.elementTemplates.forEach { element ->
            println("模板变量: ${element.variable()}")
        }
        template.render(hashMapOf(
            "term::field" to "term::field",
            "term:name" to "term:name",
            "term::field/lang::defaultValue" to "term::field/lang::defaultValue",
            "term/field_lang_defaultValue" to "term/field_lang_defaultValue",
            "major.zh" to "major.zh",
            "year|a" to "year|a",
            "term::department_science_physics/de::Physik" to "term::department_science_physics/de::Physik"
        )).writeAndClose(FileOutputStream("C:\\Users\\hhmcn\\Desktop\\测试.docx"))
    } finally {
        template.close()
    }
}