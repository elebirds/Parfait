package cc.eleb.parfait.utils

import cc.eleb.parfait.exception.DateFormatException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
val formatcf = SimpleDateFormat("yyyy-MM-dd￥HH:mm:ss&")
val format2 = SimpleDateFormat("yyyy年MM月dd日")

fun Long.toDate(): Date {
    return Date(this)
}

fun Date.format2(): String {
    return format2.format(this)
}

fun Date.format(): String {
    return format.format(this)
}

object DateUtils {
    init {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8:00"))
    }

    fun getCurrentFormattedDate(): String {
        return Date(System.currentTimeMillis()).format()
    }

    fun getCurrentFormattedDateCF(): String {
        return formatcf.format(Date(System.currentTimeMillis() - 8 * 60 * 60 * 1000)).replace('￥', 'T')
            .replace('&', 'Z')
    }

    fun getCurrentFormattedDate2(): String {
        return Date(System.currentTimeMillis()).format2()
    }

    fun getCurrentFormattedDateEnglish(): String {
        val d = LocalDate.now()
        return "${d.dayOfMonth} ${d.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)} ${d.year}"
    }

    fun getCurrentFormattedDateFrench(): String {
        val d = LocalDate.now()
        return "${d.dayOfMonth} ${d.month.getDisplayName(TextStyle.FULL, Locale.FRENCH)} ${d.year}"
    }

    fun getCurrentFormattedDateJapanese(): String {
        val d = LocalDate.now()
        return "${d.dayOfMonth} ${d.month.getDisplayName(TextStyle.FULL, Locale.JAPANESE)} ${d.year}"
    }

    @Throws(DateFormatException::class)
    fun getTimeDifference(input: String): Long {
        var pos = 0
        var time = 0.toLong()
        try {
            input.toCharArray().forEach {
                if (it !in '0'..'9') {
                    if ((it != 'y' && it != 's' && it != 'm' && it != 'd' && it != 'h' && it != 'i') || pos == 0) {
                        throw DateFormatException("时间格式不正确!")
                    } else {
                        var string = ""
                        var i = pos
                        while (i > 0) {
                            val t = input[i - 1]
                            if (t in '0'..'9') {
                                string = t + string
                            } else {
                                break
                            }
                            i--
                        }
                        when (it) {
                            'y' -> time += (string.toInt() * 31536000000) // year
                            'm' -> time += (string.toInt() * 2592000000) // month
                            'd' -> time += (string.toInt() * 86400000) // day
                            'h' -> time += (string.toInt() * 3600000) // hour
                            'i' -> time += (string.toInt() * 60000) // min
                            's' -> time += (string.toInt() * 1000) // second
                        }
                    }
                }
                pos++
            }
            return time
        } catch (e: Exception) {
            if (e.message != null) throw DateFormatException(e.message!!)
            else throw DateFormatException()
        }
    }
}