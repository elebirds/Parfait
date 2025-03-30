package cc.eleb.parfait.utils

import java.nio.charset.Charset

object Charset {
    @JvmStatic
    val defaultCharset: Charset = Charset.forName("UTF-8")
}