package cc.eleb.parfait.utils.file

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

object GZipUtils {
    @JvmStatic
    @Throws(Exception::class)
    fun gzip(data: ByteArray): ByteArray {
        val bos = ByteArrayOutputStream()
        val gzip = GZIPOutputStream(bos)
        gzip.write(data)
        gzip.finish()
        gzip.close()
        val ret = bos.toByteArray()
        bos.close()
        return ret
    }

    @JvmStatic
    @Throws(Exception::class)
    fun ungzip(data: ByteArray): ByteArray {
        val bis = ByteArrayInputStream(data)
        val gzip = GZIPInputStream(bis)
        val buf = ByteArray(1024)
        var num: Int
        val bos = ByteArrayOutputStream()
        num = gzip.read(buf, 0, buf.size)
        while (num != -1) {
            bos.write(buf, 0, num)
            num = gzip.read(buf, 0, buf.size)
        }
        gzip.close()
        bis.close()
        val ret = bos.toByteArray()
        bos.flush()
        bos.close()
        return ret
    }
}