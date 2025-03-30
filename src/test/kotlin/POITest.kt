import com.deepoove.poi.XWPFTemplate
import com.deepoove.poi.data.Documents
import java.io.FileOutputStream

fun main() {
    val template: XWPFTemplate = XWPFTemplate.compile("C:\\Users\\hhmcn\\Desktop\\model.docx").render(hashMapOf<String, Any>(
        "snameCHI" to "郑植",
        "snameENG" to "Zheng Zhi",
        "sgenderCHI" to "男",
        "sid" to 220153061,
        "sgrade" to 2022,
        "sprofCHI" to "计算机科学与技术（中法合作）",
        "sgradeweighted" to 4.0,
        "date" to "2023年10月01日",
    ))
    template.writeAndClose(FileOutputStream("C:\\Users\\hhmcn\\Desktop\\output.docx"))
}