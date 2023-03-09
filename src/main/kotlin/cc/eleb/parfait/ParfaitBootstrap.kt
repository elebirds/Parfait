package cc.eleb.parfait

import cc.eleb.parfait.config.ParConfig
import cc.eleb.parfait.entity.Certificate
import cc.eleb.parfait.entity.Student
import cc.eleb.parfait.utils.HanziUtils
import com.formdev.flatlaf.util.SystemInfo
import java.io.File
import javax.swing.JDialog
import javax.swing.JFrame

const val PARFAIT_FULL_NAME = "PARFAIT V1.0.0"

fun main() {
    if (SystemInfo.isMacOS) {
        // enable screen menu bar
        // (moves menu bar from JFrame window to top of screen)
        System.setProperty("apple.laf.useScreenMenuBar", "true")

        // application name used in screen menu bar
        // (in first menu after the "apple" menu)
        System.setProperty("apple.awt.application.name", "FlatLaf Demo")

        // appearance of window title bars
        // possible values:
        //   - "system": use current macOS appearance (light or dark)
        //   - "NSAppearanceNameAqua": use light appearance
        //   - "NSAppearanceNameDarkAqua": use dark appearance
        // (needs to be set on main thread; setting it on AWT thread does not work)
        System.setProperty("apple.awt.application.appearance", "system")
    }

    // Linux
    if (SystemInfo.isLinux) {
        // enable custom window decorations
        JFrame.setDefaultLookAndFeelDecorated(true)
        JDialog.setDefaultLookAndFeelDecorated(true)
    }

    //Application.launch(ParfaitApplication::class.java)
    println(HanziUtils.hanzi2English("郑植"))
    val pf = ParConfig(File("def.par"))
    Student.addStudentsFromFile(File("C:\\Users\\hhmcn\\Documents\\tencent files\\1752144732\\filerecv\\测试名单.xlsx"))
    Student.students[170144789]!!.addScoresFromFile(File("C:\\Users\\hhmcn\\Desktop\\成绩单.xlsx"))
    Certificate.ces["certificateB-英语-English"]!!.replaceAndGenerate(
        File("D:\\Coding\\Parfait\\Fl.docx"),
        Student.students[170144789]!!
    )
    pf.save()
}