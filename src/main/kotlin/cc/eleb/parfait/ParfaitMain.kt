package cc.eleb.parfait

import cc.eleb.parfait.ui.DemoPrefs.init
import cc.eleb.parfait.ui.DemoPrefs.setupLaf
import cc.eleb.parfait.ui.ParfaitFrame

import com.formdev.flatlaf.FlatLaf
import com.formdev.flatlaf.extras.FlatInspector
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector
import com.formdev.flatlaf.fonts.inter.FlatInterFont
import com.formdev.flatlaf.fonts.jetbrains_mono.FlatJetBrainsMonoFont
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont
import com.formdev.flatlaf.fonts.roboto_mono.FlatRobotoMonoFont
import com.formdev.flatlaf.util.SystemInfo

import javax.swing.JDialog
import javax.swing.JFrame
import javax.swing.SwingUtilities

const val KEY_TAB = "tab"

const val PARFAIT_FULL_NAME = "Parfait"

fun main() {
    if (SystemInfo.isMacOS) {
        System.setProperty("apple.laf.useScreenMenuBar", "true")
        System.setProperty("apple.awt.application.name", "FlatLaf Demo")
        System.setProperty("apple.awt.application.appearance", "system")
    }
    if (SystemInfo.isLinux) {
        JFrame.setDefaultLookAndFeelDecorated(true)
        JDialog.setDefaultLookAndFeelDecorated(true)
    }
    SwingUtilities.invokeLater {
        init("Parfait B")
        FlatInterFont.installLazy()
        FlatJetBrainsMonoFont.installLazy()
        FlatRobotoFont.installLazy()
        FlatRobotoMonoFont.installLazy()
        FlatLaf.registerCustomDefaultsSource("cc.eleb.parfait")
        setupLaf()
        FlatInspector.install("ctrl shift alt X")
        FlatUIDefaultsInspector.install("ctrl shift alt Y")
        val frame = ParfaitFrame()
        frame.pack()
        frame.setLocationRelativeTo(null)
        frame.isVisible = true
    }
}