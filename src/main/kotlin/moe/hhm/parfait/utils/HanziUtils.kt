package moe.hhm.parfait.utils

import net.sourceforge.pinyin4j.PinyinHelper
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType
import kotlin.apply
import kotlin.text.forEachIndexed
import kotlin.text.removeRange
import kotlin.text.substring
import kotlin.text.uppercase

object HanziUtils {
    val of = HanyuPinyinOutputFormat().apply {
        this.toneType = HanyuPinyinToneType.WITHOUT_TONE
    }

    fun hanzi2English(s: String): String {
        var a = ""
        s.forEachIndexed { index, c ->
            var b = PinyinHelper.toHanyuPinyinStringArray(c, of)[0]
            b = b[0].uppercase() + b.substring(1)
            a += b
            if (index == 0) a = a.uppercase()
            a += ' '
        }
        a.removeRange(0, a.length - 2)
        return a
    }
}