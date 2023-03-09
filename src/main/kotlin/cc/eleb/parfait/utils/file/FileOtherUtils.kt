package cc.eleb.parfait.utils.file

import java.io.*
import java.util.*

/**
 * Project FoundHi-Apollo
 *
 * 你可以先去看 {@see org.apache.commons.io.FileUtils}
 * 如果没有再到这里来找
 * 如果还没有就自己写个 添加到这里
 *
 * @author hhm-GrowZheng
 * @createDate 2020/3/3 21:13
 */

object FileOtherUtils {
    @JvmOverloads
    fun getResource(filename: String, classLoader: ClassLoader = this.javaClass.classLoader): InputStream? {
        return classLoader.getResourceAsStream(filename)
    }

    fun saveResource(
        dataFolder: String,
        filename: String,
        classLoader: ClassLoader = this.javaClass.classLoader
    ): Boolean {
        return saveResource(dataFolder, filename, false)
    }

    fun saveResource(
        dataFolder: String,
        filename: String,
        replace: Boolean,
        classLoader: ClassLoader = this.javaClass.classLoader
    ): Boolean {
        return saveResource(dataFolder, filename, filename, replace)
    }

    fun saveResource(
        dataFolder: String,
        filename: String,
        outputName: String,
        replace: Boolean,
        classLoader: ClassLoader = this.javaClass.classLoader
    ): Boolean {
        val out = File(dataFolder, outputName)
        if (!out.exists() || replace) {
            getResource(filename, classLoader).use {
                if (it != null) {
                    val outFolder = out.parentFile
                    if (!outFolder.exists()) {
                        outFolder.mkdirs()
                    }
                    writeFile(out, it)
                    return true
                }
            }
        }
        return false
    }


    @Throws(IOException::class)
    fun writeFile(file: File, content: InputStream) {
        if (!file.exists()) {
            file.createNewFile()
        }
        val stream = FileOutputStream(file)
        val buffer = ByteArray(1024)
        var length: Int = content.read(buffer)
        while (length != -1) {
            stream.write(buffer, 0, length)
            length = content.read(buffer)
        }
        stream.close()
        content.close()
    }

    /**
     * 重命名文件
     * @param path 目录路径
     * @param oldName 源文件名
     * @param newName 目标文件名
     * @return
     */
    @Throws(IOException::class)
    fun renameFile(path: String, oldName: String, newName: String): Boolean {
        if (oldName != newName) {
            val oldFile = File("$path/$oldName")
            val newFile = File("$path/$newName")
            if (!oldFile.exists()) {
                throw IOException("重命名文件失败,原文件:" + newName + "不存在!")
            }
            if (newFile.exists()) {
                throw IOException("重命名文件失败,该目录下已经有一个文件和新文件名相同," + newName + "已经存在!")
            } else {
                oldFile.renameTo(newFile)
            }
        }
        return true
    }

    @JvmStatic
    @Throws(IOException::class)
    fun readProperties(file: File): Properties {
        val properties = Properties()
        properties.load(BufferedReader(FileReader(file)))
        return properties
    }
}

/**
 * 获取文件拓展名
 * */
fun File.getExtension(): String {
    var extension = ""
    if (this.name.lastIndexOf(".") != -1 && this.name.lastIndexOf(".") != 0) {
        extension = this.name.substring(this.name.lastIndexOf(".") + 1)
    }
    return extension
}