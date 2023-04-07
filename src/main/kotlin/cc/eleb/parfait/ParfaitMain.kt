package cc.eleb.parfait

import cc.eleb.parfait.i18n.Language
import cc.eleb.parfait.i18n.trs
import cc.eleb.parfait.ui.ParfaitFrame
import cc.eleb.parfait.utils.GlobalSettings
import cc.eleb.parfait.utils.ParfaitPrefs.init
import cc.eleb.parfait.utils.ParfaitPrefs.setupLaf
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
import javax.swing.JOptionPane
import javax.swing.SwingUtilities

const val KEY_TAB = "tab"

const val PARFAIT_FULL_NAME = "Parfait"

fun main() {
    try {
        if (SystemInfo.isMacOS) {
            System.setProperty("apple.laf.useScreenMenuBar", "true")
            System.setProperty("apple.awt.application.name", PARFAIT_FULL_NAME)
            System.setProperty("apple.awt.application.appearance", "system")
        }
        if (SystemInfo.isLinux) {
            JFrame.setDefaultLookAndFeelDecorated(true)
            JDialog.setDefaultLookAndFeelDecorated(true)
        }
        SwingUtilities.invokeLater {
            init("Parfait C")
            GlobalSettings.loadFromPrefs()
            Language.load()
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
    } catch (e: Exception) {
        JOptionPane.showMessageDialog(
            null, e.stackTraceToString(), "global-error".trs(),
            JOptionPane.ERROR_MESSAGE
        )
    }
}