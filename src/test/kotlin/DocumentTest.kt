import com.deepoove.poi.XWPFTemplate


fun main() {
    val fileName = "C:\\Users\\Polo\\Desktop\\在读证明文档.docx"
    val template = XWPFTemplate.compile(fileName)
    try {
        // 获取所有模板变量的标签名
        template.elementTemplates.forEach { element ->
            println("模板变量: ${element.variable()}")
        }
    } finally {
        template.close()
    }
}