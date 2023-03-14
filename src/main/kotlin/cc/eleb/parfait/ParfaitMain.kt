package cc.eleb.parfait

import cc.eleb.parfait.config.ParConfig
import cc.eleb.parfait.ui.DemoPrefs.init
import cc.eleb.parfait.ui.DemoPrefs.setupLaf
import cc.eleb.parfait.ui.ParfaitFrame
import cc.eleb.parfait.utils.Charset
import com.formdev.flatlaf.FlatLaf
import com.formdev.flatlaf.extras.FlatInspector
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector
import com.formdev.flatlaf.fonts.inter.FlatInterFont
import com.formdev.flatlaf.fonts.jetbrains_mono.FlatJetBrainsMonoFont
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont
import com.formdev.flatlaf.fonts.roboto_mono.FlatRobotoMonoFont
import com.formdev.flatlaf.util.SystemInfo
import org.apache.commons.io.FileUtils
import java.awt.Dimension
import java.io.File
import java.lang.Boolean
import javax.swing.JDialog
import javax.swing.JFrame
import javax.swing.SwingUtilities
import kotlin.random.Random

const val PREFS_ROOT_PATH = "/flatlaf-demo"
const val KEY_TAB = "tab"

const val PARFAIT_FULL_NAME = "Parfait"

var screenshotsMode = Boolean.parseBoolean(System.getProperty("flatlaf.demo.screenshotsMode"))

data class Person(val department:String,val name:String,val gender:String,val id:String,val grade:String,val cit:String,val prof:String,val telep:String,val wechat:String){}

fun main() {
    // macOS  (see https://www.formdev.com/flatlaf/macos/)
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
    if (screenshotsMode && !SystemInfo.isJava_9_orLater && System.getProperty("flatlaf.uiScale") == null) System.setProperty(
        "flatlaf.uiScale",
        "2x"
    )
    SwingUtilities.invokeLater {
        init(PREFS_ROOT_PATH)
        // install fonts for lazy loading
        FlatInterFont.installLazy()
        FlatJetBrainsMonoFont.installLazy()
        FlatRobotoFont.installLazy()
        FlatRobotoMonoFont.installLazy()

        // use Inter font by default
        //			FlatLaf.setPreferredFontFamily( FlatInterFont.FAMILY );
        //			FlatLaf.setPreferredLightFontFamily( FlatInterFont.FAMILY_LIGHT );
        //			FlatLaf.setPreferredSemiboldFontFamily( FlatInterFont.FAMILY_SEMIBOLD );

        // use Roboto font by default
        //			FlatLaf.setPreferredFontFamily( FlatRobotoFont.FAMILY );
        //			FlatLaf.setPreferredLightFontFamily( FlatRobotoFont.FAMILY_LIGHT );
        //			FlatLaf.setPreferredSemiboldFontFamily( FlatRobotoFont.FAMILY_SEMIBOLD );

        // use JetBrains Mono font
        //			FlatLaf.setPreferredMonospacedFontFamily( FlatJetBrainsMonoFont.FAMILY );

        // use Roboto Mono font
        //			FlatLaf.setPreferredMonospacedFontFamily( FlatRobotoMonoFont.FAMILY );

        // application specific UI defaults
        FlatLaf.registerCustomDefaultsSource("com.formdev.flatlaf.demo")
        // set look and feel

        // set look and feel
        setupLaf()

        // install inspectors
        FlatInspector.install("ctrl shift alt X")
        FlatUIDefaultsInspector.install("ctrl shift alt Y")
        val frame = ParfaitFrame()
        if (screenshotsMode) {
            frame.preferredSize = if (SystemInfo.isJava_9_orLater) Dimension(830, 440) else Dimension(1660, 880)
        }

        // show frame
        frame.pack()
        frame.setLocationRelativeTo(null)
        frame.isVisible = true
    }

}