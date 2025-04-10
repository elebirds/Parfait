/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.lib

import com.formdev.flatlaf.FlatLaf
import com.formdev.flatlaf.extras.FlatInspector
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector
import com.formdev.flatlaf.fonts.inter.FlatInterFont
import com.formdev.flatlaf.fonts.jetbrains_mono.FlatJetBrainsMonoFont
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont
import com.formdev.flatlaf.fonts.roboto_mono.FlatRobotoMonoFont
import com.formdev.flatlaf.util.SystemInfo
import moe.hhm.parfait.PARFAIT_FULL_NAME
import javax.swing.JDialog
import javax.swing.JFrame

object FlatLafUtils {
    fun specialSystemConfigure() {
        // macOS 针对设置 (详见 https://www.formdev.com/flatlaf/macos/)
        if (SystemInfo.isMacOS) {
            // 启用屏幕菜单栏
            System.setProperty("apple.laf.useScreenMenuBar", "true")
            // 设置应用程序名称（在屏幕菜单栏中显示）
            System.setProperty("apple.awt.application.name", PARFAIT_FULL_NAME)
            // 设置窗口标题栏的外观
            // possible values:
            //   - "system": use current macOS appearance (light or dark)
            //   - "NSAppearanceNameAqua": use light appearance
            //   - "NSAppearanceNameDarkAqua": use dark appearance
            // (must be set on main thread and before AWT/Swing is initialized;
            //  setting it on AWT thread does not work)
            System.setProperty("apple.awt.application.appearance", "system")
        }
        // Linux 针对设置
        if (SystemInfo.isLinux) {
            // 启用自定义窗口装饰
            JFrame.setDefaultLookAndFeelDecorated(true)
            JDialog.setDefaultLookAndFeelDecorated(true)
        }
    }

    fun fontInit() {
        FlatInterFont.installLazy()
        FlatJetBrainsMonoFont.installLazy()
        FlatRobotoFont.installLazy()
        FlatRobotoMonoFont.installLazy()
    }

    fun preferenceInit() {
        FlatLafPrefs.init("ParfaitMOE");
        FlatLafPrefs.initSystemScale();
    }

    fun setLookAndFeel() {
        FlatLaf.registerCustomDefaultsSource("ui")
        FlatLafPrefs.setupLaf()
    }

    fun installInspector() {
        FlatInspector.install("ctrl shift alt X");
        FlatUIDefaultsInspector.install("ctrl shift alt Y");
    }
}