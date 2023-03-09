package cc.eleb.parfait.utils

import java.util.concurrent.ThreadLocalRandom

/**
 * Project FoundHi-Apollo
 *
 * @author hhm-GrowZheng
 * @createDate 2020/3/3 21:13
 */

@Suppress("UNCHECKED_CAST")

fun <T> Any?.cast(): T = this as T

object Utilities {

    fun randomString(lenMin: Int, lenMax: Int): String {
        val n = ThreadLocalRandom.current().nextInt(lenMax - lenMin)
        return randomString(n + lenMin)
    }

    fun randomString(len: Int): String {
        val leftLimit = 97 // letter 'a'
        val rightLimit = 122 // letter 'z'
        val random = ThreadLocalRandom.current()
        val buffer = StringBuilder(len)
        for (i in 0 until len) {
            val randomLimitedInt = leftLimit + (random.nextFloat() * (rightLimit - leftLimit + 1)).toInt()
            buffer.append(randomLimitedInt.toChar())
        }
        return buffer.toString()
    }

    fun randomIPAddress(): String {
        val a = ThreadLocalRandom.current().nextInt(256)
        val b = ThreadLocalRandom.current().nextInt(256)
        val c = ThreadLocalRandom.current().nextInt(256)
        val d = ThreadLocalRandom.current().nextInt(256)
        return "$a.$b.$c.$d"
    }
}