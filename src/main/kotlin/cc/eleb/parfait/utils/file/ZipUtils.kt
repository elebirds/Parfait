package cc.eleb.parfait.utils.file

import cc.eleb.parfait.utils.Charset
import java.io.*
import java.util.zip.*

object ZipUtils {
    /** 缓冲器大小  */
    private const val BUFFER = 512

    /**
     * 取的给定源目录下的所有文件及空的子目录
     * 递归实现
     *
     * @param srcFile
     *
     * @return
     */
    private fun getAllFiles(srcFile: File): List<File> {
        val fileList = ArrayList<File>()
        val tmp = srcFile.listFiles()!!

        for (aTmp in tmp) {
            if (aTmp.isFile) {
                fileList.add(aTmp)
                println("add file: " + aTmp.name)
            }
            if (aTmp.isDirectory) {
                if (aTmp.listFiles() != null && aTmp.listFiles().isNotEmpty()) {//若不是空目录，则递归添加其下的目录和文件
                    fileList.addAll(getAllFiles(aTmp))
                } else {//若是空目录，则添加这个目录到fileList
                    fileList.add(aTmp)
                    println("add empty dir: " + aTmp.name)
                }
            }
        }
        return fileList
    }

    /**
     * 取相对路径
     * 依据文件名和压缩源路径得到文件在压缩源路径下的相对路径
     *
     * @param dirPath 压缩源路径
     * @param file
     *
     * @return 相对路径
     */
    private fun getRelativePath(dirPath: String, file: File): String {
        var f: File? = file
        val dir = File(dirPath)
        val relativePath = StringBuilder(f!!.name)
        while (true) {
            f = f!!.parentFile
            if (f == null) {
                break
            }
            if (f == dir) {
                break
            } else {
                relativePath.insert(0, f.name + "/")
            }
        }
        return relativePath.toString()
    }

    /**
     * 压缩方法
     * （可以压缩空的子目录）
     * @param srcPath 压缩源路径
     * @param zipFileName 目标压缩文件
     * @return
     */
    @JvmStatic
    fun zip(srcPath: String, zipFileName: String): Boolean {
        val srcFile = File(srcPath)
        val fileList = getAllFiles(srcFile)//所有要压缩的文件
        val buffer = ByteArray(BUFFER)//缓冲器
        var zipEntry: ZipEntry?
        var readLength: Int //每次读出来的长度
        try {
            val zipOutputStream = ZipOutputStream(FileOutputStream(zipFileName))
            for (file in fileList) {
                if (file.isFile) {//若是文件，则压缩这个文件
                    zipEntry = ZipEntry(getRelativePath(srcPath, file))
                    zipEntry.size = file.length()
                    zipEntry.time = file.lastModified()
                    zipOutputStream.putNextEntry(zipEntry)
                    val inputStream = BufferedInputStream(FileInputStream(file))
                    readLength = inputStream.read(buffer, 0, BUFFER)
                    while (readLength != -1) {
                        zipOutputStream.write(buffer, 0, readLength)
                        readLength = inputStream.read(buffer, 0, BUFFER)
                    }
                    inputStream.close()
                    //println("file compressed: " + file.canonicalPath)
                } else {//若是目录（即空目录）则将这个目录写入zip条目
                    zipEntry = ZipEntry(getRelativePath(srcPath, file) + "/")
                    zipOutputStream.putNextEntry(zipEntry)
                    //println("dir compressed: " + file.canonicalPath + "/")
                }
            }
            zipOutputStream.close()
        } catch (e: IOException) {
            //println(e.message)
            e.printStackTrace()
            //println("zip fail!")
            return false
        }
        //println("zip success!")
        return true
    }

    /**
     * 解压文件
     * @param zipFile 目标文件
     * @param descDir 指定解压目录
     * @param urlList 存放解压后的文件目录（可选）
     * @return
     */
    @JvmStatic
    fun unZip(zipFile: File, descDir: String, urlList: MutableList<String> = ArrayList()): Boolean {
        var flag = false
        val pathFile = File(descDir)
        if (!pathFile.exists()) {
            pathFile.mkdirs()
        }
        val zip: ZipFile
        try {
            //指定编码，压缩包里面不能有中文目录
            zip = ZipFile(zipFile, Charset.defaultCharset)
            val entries = zip.entries()
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement() as ZipEntry
                val zipEntryName = entry.name
                val `in` = zip.getInputStream(entry)
                val outPath = (descDir + zipEntryName).replace("/", File.separator)
                //判断路径是否存在,不存在则创建文件路径
                val file = File(outPath.substring(0, outPath.lastIndexOf(File.separator)))
                if (!file.exists()) {
                    file.mkdirs()
                }
                //判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
                if (File(outPath).isDirectory) {
                    continue
                }
                //保存文件路径信息
                urlList.add(outPath)

                val out = FileOutputStream(outPath)
                val buf1 = ByteArray(2048)
                var len: Int = `in`.read(buf1)
                while (len > 0) {
                    out.write(buf1, 0, len)
                    len = `in`.read(buf1)
                }
                `in`.close()
                out.close()
            }
            flag = true
            //必须关闭，否则无法删除该zip文件
            zip.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return flag
    }

    @JvmStatic
    fun strings2Zip(file: File, data: LinkedHashMap<String, String>) {
        val f = FileOutputStream(file)
        val cos = CheckedOutputStream(f, Adler32())
        val out = ZipOutputStream(BufferedOutputStream(cos))
        out.setComment("ParfaitData.")
        data.forEach { (t, u) ->
            out.putNextEntry(ZipEntry(t))
            u.toByteArray(Charset.defaultCharset).forEach {
                out.write(it.toInt())
            }
        }
        out.close()
        cos.close()
        f.close()
    }

    @JvmStatic
    fun zip2Strings(file: File): LinkedHashMap<String, String> {
        val d = linkedMapOf<String, String>()
        val fi = FileInputStream(file)
        val cis = CheckedInputStream(fi, Adler32())
        val in2 = ZipInputStream(BufferedInputStream(cis))
        var ze: ZipEntry? = in2.nextEntry
        while (ze != null) {
            val by = ArrayList<Byte>()
            var x: Int
            while (in2.read().also { x = it } != -1) by.add(x.toByte())
            d[ze.name] = String(by.toByteArray(), Charset.defaultCharset)
            ze = in2.nextEntry
        }
        in2.close()
        cis.close()
        fi.close()
        return d
    }
}