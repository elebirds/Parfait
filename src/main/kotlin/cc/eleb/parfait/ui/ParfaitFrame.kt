package cc.eleb.parfait.ui


import cc.eleb.parfait.KEY_TAB
import cc.eleb.parfait.ui.panel.GPAPanel
import cc.eleb.parfait.ui.panel.I18nPanel
import cc.eleb.parfait.ui.panel.StudentDataPanel
import cc.eleb.parfait.ui.panel.WelcomePanel
import cc.eleb.parfait.ui.theme.IJThemeInfo
import com.formdev.flatlaf.*
import com.formdev.flatlaf.extras.FlatAnimatedLafChange
import com.formdev.flatlaf.extras.FlatDesktop
import com.formdev.flatlaf.extras.FlatSVGIcon
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector
import com.formdev.flatlaf.extras.components.FlatButton
import com.formdev.flatlaf.icons.FlatAbstractIcon
import com.formdev.flatlaf.themes.FlatMacDarkLaf
import com.formdev.flatlaf.themes.FlatMacLightLaf
import com.formdev.flatlaf.ui.JBRCustomDecorations
import com.formdev.flatlaf.util.ColorFunctions
import com.formdev.flatlaf.util.FontUtils
import com.formdev.flatlaf.util.LoggingFacade
import com.formdev.flatlaf.util.SystemInfo
import net.miginfocom.layout.ConstraintParser
import net.miginfocom.layout.LC
import net.miginfocom.layout.UnitValue
import net.miginfocom.swing.MigLayout
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.beans.PropertyChangeEvent
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import java.nio.file.Files
import java.time.Year
import java.util.*
import javax.swing.*
import javax.swing.plaf.metal.MetalLookAndFeel
import javax.swing.plaf.nimbus.NimbusLookAndFeel

class ParfaitFrame : JFrame() {
    private val availableFontFamilyNames: Array<String> = FontUtils.getAvailableFontFamilyNames().clone()
    private var initialFontMenuItemCount: Int = -1
    private lateinit var themes: LinkedHashMap<String, IJThemeInfo>
    private lateinit var rtl: JCheckBoxMenuItem
    private fun rightToLeftChanged(c: Container, rightToLeft: Boolean) {
        c.applyComponentOrientation(
            if (rightToLeft) ComponentOrientation.RIGHT_TO_LEFT else ComponentOrientation.LEFT_TO_RIGHT
        )
        c.revalidate()
        c.repaint()
    }

    private var rtlNow: Boolean = false
    private fun rightToLeftChanged() {
        rtlNow = !rtlNow
        rightToLeftChanged(this, rtlNow)
    }

    private fun initThemeMenu() {
        themes = LinkedHashMap<String, IJThemeInfo>()
        themes["Light"] = IJThemeInfo("Light", null, false, null, null, null, null, null, FlatLightLaf::class.java.name)
        themes["Dark"] = IJThemeInfo("Dark", null, true, null, null, null, null, null, FlatDarkLaf::class.java.name)
        themes["IntelliJ"] = IJThemeInfo("IntelliJ", null, false, null, null, null, null, null, FlatIntelliJLaf::class.java.name)
        themes["Darcula"] = IJThemeInfo("Darcula", null, true, null, null, null, null, null, FlatDarculaLaf::class.java.name)
        themes["macOS Light"] = IJThemeInfo("macOS Light", null, false, null, null, null, null, null, FlatMacLightLaf::class.java.name)
        themes["macOS Dark"] = IJThemeInfo("macOS Dark", null, true, null, null, null, null, null, FlatMacDarkLaf::class.java.name)
        if (SystemInfo.isWindows) themes["Windows"] = IJThemeInfo("Windows", null, true, null, null, null, null, null, "com.sun.java.swing.plaf.windows.WindowsLookAndFeel")
        else if (SystemInfo.isMacOS) themes["Aqua"] = IJThemeInfo("Aqua", null, true, null, null, null, null, null, "com.apple.laf.AquaLookAndFeel")
        else if (SystemInfo.isLinux) themes["GTK"] = IJThemeInfo("GTK", null, true, null, null, null, null, null, "com.sun.java.swing.plaf.gtk.GTKLookAndFeel")
        themes["Metal"] = IJThemeInfo("Metal", null, true, null, null, null, null, null, MetalLookAndFeel::class.java.name)
        themes["Nimbus"] = IJThemeInfo("Nimbus", null, true, null, null, null, null, null, NimbusLookAndFeel::class.java.name)

        //Theme menu
        val bg = ButtonGroup()
        for (i in 0 until themes.size) {
            val tf = JRadioButtonMenuItem()
            tf.text = themes.values.toTypedArray()[i].name
            tf.addActionListener { e: ActionEvent? -> this.setTheme(e!!) }
            bg.add(tf)
            themeMenu.add(tf)
        }
        themeMenu.add(JPopupMenu.Separator())
        rtl = JCheckBoxMenuItem()
        rtl.text = "文字从右向左"
        rtl.isSelected = false
        rtl.addActionListener { this.rightToLeftChanged() }
        themeMenu.add(rtl)
    }

    private fun showInformationDialog(message: String, ex: Exception) {
        JOptionPane.showMessageDialog(
            SwingUtilities.windowForComponent(this), message + "\n\n" + ex.message,
            "Parfait", JOptionPane.INFORMATION_MESSAGE
        )
    }

    private fun setTheme(e: ActionEvent) {
        val themeInfo: IJThemeInfo? = themes[e.actionCommand]
        EventQueue.invokeLater { setTheme(themeInfo) }
    }

    private fun setTheme(themeInfo: IJThemeInfo?) {
        if (themeInfo == null) return

        // change look and feel

        // change look and feel
        if (themeInfo.lafClassName != null) {
            if (themeInfo.lafClassName == UIManager.getLookAndFeel().javaClass.name) return
            FlatAnimatedLafChange.showSnapshot()
            try {
                UIManager.setLookAndFeel(themeInfo.lafClassName)
            } catch (ex: java.lang.Exception) {
                LoggingFacade.INSTANCE.logSevere(null, ex)
                showInformationDialog("Failed to create '" + themeInfo.lafClassName + "'.", ex)
            }
        } else if (themeInfo.themeFile != null) {
            FlatAnimatedLafChange.showSnapshot()
            try {
                if (themeInfo.themeFile.name.endsWith(".properties")) {
                    FlatLaf.setup(FlatPropertiesLaf(themeInfo.name, themeInfo.themeFile))
                } else FlatLaf.setup(IntelliJTheme.createLaf(Files.newInputStream(themeInfo.themeFile.toPath())))
                DemoPrefs.state.put(DemoPrefs.KEY_LAF_THEME, DemoPrefs.FILE_PREFIX + themeInfo.themeFile)
            } catch (ex: Exception) {
                LoggingFacade.INSTANCE.logSevere(null, ex)
                showInformationDialog("Failed to load '" + themeInfo.themeFile + "'.", ex)
            }
        } else {
            FlatAnimatedLafChange.showSnapshot()
            IntelliJTheme.setup(javaClass.getResourceAsStream(THEMES_PACKAGE + themeInfo.resourceName))
            DemoPrefs.state.put(DemoPrefs.KEY_LAF_THEME, DemoPrefs.RESOURCE_PREFIX + themeInfo.resourceName)
        }

        // update all components

        // update all components
        FlatLaf.updateUI()
        FlatAnimatedLafChange.hideSnapshotWithAnimation()
        this.updateFontMenuItems()
    }

    public override fun dispose() {
        super.dispose()
        FlatUIDefaultsInspector.hide()
    }

    private fun showHints() {
        val fontMenuHint: HintManager.Hint = HintManager.Hint(
            "Use 'Font' menu to increase/decrease font size or try different fonts.",
            fontMenu, SwingConstants.BOTTOM, "hint.fontMenu", null
        )
        val optionsMenuHint: HintManager.Hint = HintManager.Hint(
            "Use 'Options' menu to try out various FlatLaf options.", optionsMenu,
            SwingConstants.BOTTOM, "hint.optionsMenu", fontMenuHint
        )
        HintManager.showHint(optionsMenuHint)
    }

    private fun clearHints() {
        HintManager.hideAllHints()
    }

    private fun showUIDefaultsInspector() {
        FlatUIDefaultsInspector.show()
    }

    private fun newActionPerformed() {
        val newDialog = NewDialog(this)
        newDialog.isVisible = true
    }

    private fun openActionPerformed() {
        val chooser = JFileChooser()
        chooser.showOpenDialog(this)
    }

    private fun saveAsActionPerformed() {
        val chooser = JFileChooser()
        chooser.showSaveDialog(this)
    }

    private fun exitActionPerformed() {
        dispose()
    }

    private fun aboutActionPerformed() {
        val titleLabel: JLabel = JLabel("Parfait Demo")
        titleLabel.putClientProperty(FlatClientProperties.STYLE_CLASS, "h1")
        val link: String = "https://eleb.cc/"
        val linkLabel: JLabel = JLabel("<html><a href=\"#\">$link</a></html>")
        linkLabel.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        linkLabel.addMouseListener(object : MouseAdapter() {
            public override fun mouseClicked(e: MouseEvent) {
                try {
                    Desktop.getDesktop().browse(URI(link))
                } catch (ex: IOException) {
                    JOptionPane.showMessageDialog(
                        linkLabel, "无法在浏览器中打开 '$link'.", "关于",
                        JOptionPane.PLAIN_MESSAGE
                    )
                } catch (ex: URISyntaxException) {
                    JOptionPane.showMessageDialog(
                        linkLabel, "无法在浏览器中打开 '$link'.", "关于",
                        JOptionPane.PLAIN_MESSAGE
                    )
                }
            }
        })
        JOptionPane.showMessageDialog(
            this, arrayOf<Any>(
                titleLabel, "简易的学生数据管理系统.", " ",
                "Copyright 2023-" + Year.now() + " Elebird(Grow Zheng).", "All rights reserved.", linkLabel
            ), "关于",
            JOptionPane.PLAIN_MESSAGE
        )
    }

    private fun showPreferences() {
        JOptionPane.showMessageDialog(
            this,
            "Sorry, but FlatLaf Demo does not have preferences. :(\n" + "This dialog is here to demonstrate usage of class 'FlatDesktop' on macOS.",
            "Preferences", JOptionPane.PLAIN_MESSAGE
        )
    }

    private fun menuItemActionPerformed(e: ActionEvent) {
        SwingUtilities.invokeLater {
            JOptionPane.showMessageDialog(
                this,
                e.actionCommand,
                "Menu Item",
                JOptionPane.PLAIN_MESSAGE
            )
        }
    }

    private fun windowDecorationsChanged() {
        val windowDecorations: Boolean = windowDecorationsCheckBoxMenuItem.isSelected

        // change window decoration of all frames and dialogs
        FlatLaf.setUseNativeWindowDecorations(windowDecorations)
        menuBarEmbeddedCheckBoxMenuItem.isEnabled = windowDecorations
        unifiedTitleBarMenuItem.isEnabled = windowDecorations
        showTitleBarIconMenuItem.isEnabled = windowDecorations
    }

    private fun menuBarEmbeddedChanged() {
        UIManager.put("TitlePane.menuBarEmbedded", menuBarEmbeddedCheckBoxMenuItem.isSelected)
        FlatLaf.revalidateAndRepaintAllFramesAndDialogs()
    }

    private fun unifiedTitleBar() {
        UIManager.put("TitlePane.unifiedBackground", unifiedTitleBarMenuItem.isSelected)
        FlatLaf.repaintAllFramesAndDialogs()
    }

    private fun showTitleBarIcon() {
        val showIcon: Boolean = showTitleBarIconMenuItem.isSelected

        // for main frame (because already created)
        getRootPane().putClientProperty(FlatClientProperties.TITLE_BAR_SHOW_ICON, showIcon)

        // for other not yet created frames/dialogs
        UIManager.put("TitlePane.showIcon", showIcon)
    }

    private fun underlineMenuSelection() {
        UIManager.put("MenuItem.selectionType", if (underlineMenuSelectionMenuItem.isSelected) "underline" else null)
    }

    private fun alwaysShowMnemonics() {
        UIManager.put("Component.hideMnemonics", !alwaysShowMnemonicsMenuItem.isSelected)
        repaint()
    }

    private fun animatedLafChangeChanged() {
        System.setProperty("flatlaf.animatedLafChange", animatedLafChangeMenuItem.isSelected.toString())
    }

    private fun showHintsChanged() {
        clearHints()
        showHints()
    }

    private fun fontFamilyChanged(e: ActionEvent) {
        val fontFamily: String = e.actionCommand
        FlatAnimatedLafChange.showSnapshot()
        val font: Font = UIManager.getFont("defaultFont")
        val newFont: Font = FontUtils.getCompositeFont(fontFamily, font.style, font.size)
        UIManager.put("defaultFont", newFont)
        FlatLaf.updateUI()
        FlatAnimatedLafChange.hideSnapshotWithAnimation()
    }

    private fun fontSizeChanged(e: ActionEvent) {
        val fontSizeStr: String = e.actionCommand
        val font: Font = UIManager.getFont("defaultFont")
        val newFont: Font = font.deriveFont(fontSizeStr.toInt().toFloat())
        UIManager.put("defaultFont", newFont)
        FlatLaf.updateUI()
    }

    private fun restoreFont() {
        UIManager.put("defaultFont", null)
        updateFontMenuItems()
        FlatLaf.updateUI()
    }

    private fun incrFont() {
        val font: Font = UIManager.getFont("defaultFont")
        val newFont: Font = font.deriveFont((font.size + 1).toFloat())
        UIManager.put("defaultFont", newFont)
        updateFontMenuItems()
        FlatLaf.updateUI()
    }

    private fun decrFont() {
        val font: Font = UIManager.getFont("defaultFont")
        val newFont: Font = font.deriveFont((font.size - 1).coerceAtLeast(10).toFloat())
        UIManager.put("defaultFont", newFont)
        updateFontMenuItems()
        FlatLaf.updateUI()
    }

    fun updateFontMenuItems() {
        if (initialFontMenuItemCount < 0) initialFontMenuItemCount = fontMenu.itemCount else {
            // remove old font items
            for (i in fontMenu.itemCount - 1 downTo initialFontMenuItemCount) fontMenu.remove(i)
        }

        // get current font
        val currentFont: Font = UIManager.getFont("Label.font")
        val currentFamily: String = currentFont.family
        val currentSize: String = currentFont.size.toString()

        // add font families
        fontMenu.addSeparator()
        val families: ArrayList<String> = ArrayList(
            mutableListOf(
                "Arial", "Cantarell", "Comic Sans MS", "DejaVu Sans", "Dialog", "Inter", "Liberation Sans",
                "Noto Sans", "Open Sans", "Roboto", "SansSerif", "Segoe UI", "Serif", "Tahoma", "Ubuntu", "Verdana"
            )
        )
        if (!families.contains(currentFamily)) families.add(currentFamily)
        families.sortWith(java.lang.String.CASE_INSENSITIVE_ORDER)
        val familiesGroup = ButtonGroup()
        for (family: String in families) {
            if (Arrays.binarySearch(availableFontFamilyNames, family) < 0) continue  // not available
            val item = JCheckBoxMenuItem(family)
            item.isSelected = (family == currentFamily)
            item.addActionListener { e: ActionEvent -> fontFamilyChanged(e) }
            fontMenu.add(item)
            familiesGroup.add(item)
        }

        // add font sizes
        fontMenu.addSeparator()
        val sizes: ArrayList<String> = ArrayList(mutableListOf("10", "11", "12", "14", "16", "18", "20", "24", "28"))
        if (!sizes.contains(currentSize)) sizes.add(currentSize)
        sizes.sortWith(java.lang.String.CASE_INSENSITIVE_ORDER)
        val sizesGroup = ButtonGroup()
        for (size: String in sizes) {
            val item = JCheckBoxMenuItem(size)
            item.isSelected = (size == currentSize)
            item.addActionListener { e: ActionEvent -> fontSizeChanged(e) }
            fontMenu.add(item)
            sizesGroup.add(item)
        }

        // enabled/disable items
        val enabled: Boolean = UIManager.getLookAndFeel() is FlatLaf
        for (item: Component in fontMenu.menuComponents) item.isEnabled = enabled
    }

    private val accentColorButtons: ArrayList<JToggleButton> = arrayListOf()
    private lateinit var accentColorLabel: JLabel
    private var accentColor: Color? = null

    private fun initAccentColors() {
        accentColorLabel = JLabel("Accent color: ")
        toolBar.add(Box.createHorizontalGlue())
        toolBar.add(accentColorLabel)
        val group = ButtonGroup()
        for (i in accentColorKeys.indices) {
            val jtb = JToggleButton(AccentColorIcon(accentColorKeys[i]))
            jtb.toolTipText = accentColorNames[i]
            jtb.addActionListener { e: ActionEvent? ->
                accentColorChanged(
                    e!!
                )
            }
            accentColorButtons.add(jtb);
            toolBar.add(jtb)
            group.add(jtb);
        }
        accentColorButtons[0].isSelected = true
        FlatLaf.setSystemColorGetter { name: String -> if (name == "accent") accentColor else null }
        UIManager.addPropertyChangeListener { e: PropertyChangeEvent -> if ("lookAndFeel" == e.propertyName) updateAccentColorButtons() }
        updateAccentColorButtons()
    }

    private fun accentColorChanged(e: ActionEvent) {
        var accentColorKey: String? = null
        for (i in accentColorButtons.indices) {
            if (accentColorButtons[i].isSelected) {
                accentColorKey = accentColorKeys[i]
                break
            }
        }
        accentColor = if (accentColorKey != null && accentColorKey !== accentColorKeys[0]) UIManager.getColor(
            accentColorKey
        ) else null
        val lafClass: Class<out LookAndFeel?> = UIManager.getLookAndFeel().javaClass
        try {
            FlatLaf.setup(lafClass.newInstance())
            FlatLaf.updateUI()
        } catch (ex: InstantiationException) {
            LoggingFacade.INSTANCE.logSevere(null, ex)
        } catch (ex: IllegalAccessException) {
            LoggingFacade.INSTANCE.logSevere(null, ex)
        }
    }

    private fun updateAccentColorButtons() {
        val lafClass: Class<out LookAndFeel?> = UIManager.getLookAndFeel().javaClass
        val isAccentColorSupported: Boolean =
            (lafClass == FlatLightLaf::class.java) || (lafClass == FlatDarkLaf::class.java) || (lafClass == FlatIntelliJLaf::class.java) || (lafClass == FlatDarculaLaf::class.java) || (lafClass == FlatMacLightLaf::class.java) || (lafClass == FlatMacDarkLaf::class.java)
        accentColorLabel.isVisible = isAccentColorSupported
        for (i in accentColorButtons.indices) accentColorButtons[i].isVisible = isAccentColorSupported
    }

    private fun initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - csgo fps
        val menuBar1 = JMenuBar()
        val fileMenu = JMenu()
        val newMenuItem = JMenuItem()
        val openMenuItem = JMenuItem()
        val saveAsMenuItem = JMenuItem()
        val closeMenuItem = JMenuItem()
        exitMenuItem = JMenuItem()
        themeMenu = JMenu()
        fontMenu = JMenu()
        val restoreFontMenuItem = JMenuItem()
        val incrFontMenuItem = JMenuItem()
        val decrFontMenuItem = JMenuItem()
        optionsMenu = JMenu()
        windowDecorationsCheckBoxMenuItem = JCheckBoxMenuItem()
        menuBarEmbeddedCheckBoxMenuItem = JCheckBoxMenuItem()
        unifiedTitleBarMenuItem = JCheckBoxMenuItem()
        showTitleBarIconMenuItem = JCheckBoxMenuItem()
        underlineMenuSelectionMenuItem = JCheckBoxMenuItem()
        alwaysShowMnemonicsMenuItem = JCheckBoxMenuItem()
        animatedLafChangeMenuItem = JCheckBoxMenuItem()
        val showHintsMenuItem = JMenuItem()
        val showUIDefaultsInspectorMenuItem = JMenuItem()
        val helpMenu = JMenu()
        aboutMenuItem = JMenuItem()
        toolBar = JToolBar()
        val backButton = JButton()
        val forwardButton = JButton()
        val cutButton = JButton()
        val copyButton = JButton()
        val pasteButton = JButton()
        val refreshButton = JButton()
        val showToggleButton = JToggleButton()
        val contentPanel = JPanel()
        tabbedPane = JTabbedPane()
        val panel1 = WelcomePanel()
        val studentDataPanel = StudentDataPanel()
        val panel2 = I18nPanel()
        val panel3 = GPAPanel()

        //======== this ========
        title = "Parfait Demo"
        defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
        val contentPane: Container = contentPane
        contentPane.layout = BorderLayout()

        //======== menuBar1 ========


        //======== fileMenu ========
        fileMenu.text = "\u6587\u4ef6"
        fileMenu.setMnemonic('F')

        //---- newMenuItem ----
        newMenuItem.text = "\u65b0\u5efa"
        newMenuItem.accelerator = KeyStroke.getKeyStroke(
            KeyEvent.VK_N,
            Toolkit.getDefaultToolkit().menuShortcutKeyMask
        )
        newMenuItem.setMnemonic('N')
        newMenuItem.addActionListener { newActionPerformed() }
        fileMenu.add(newMenuItem)

        //---- openMenuItem ----
        openMenuItem.text = "\u6253\u5f00"
        openMenuItem.accelerator = KeyStroke.getKeyStroke(
            KeyEvent.VK_O,
            Toolkit.getDefaultToolkit().menuShortcutKeyMask
        )
        openMenuItem.setMnemonic('O')
        openMenuItem.addActionListener { openActionPerformed() }
        fileMenu.add(openMenuItem)

        //---- saveAsMenuItem ----
        saveAsMenuItem.text = "\u4fdd\u5b58"
        saveAsMenuItem.accelerator = KeyStroke.getKeyStroke(
            KeyEvent.VK_S,
            Toolkit.getDefaultToolkit().menuShortcutKeyMask
        )
        saveAsMenuItem.setMnemonic('S')
        saveAsMenuItem.addActionListener { saveAsActionPerformed() }
        fileMenu.add(saveAsMenuItem)
        fileMenu.addSeparator()

        //---- closeMenuItem ----
        closeMenuItem.text = "\u5173\u95ed"
        closeMenuItem.accelerator = KeyStroke.getKeyStroke(
            KeyEvent.VK_W,
            Toolkit.getDefaultToolkit().menuShortcutKeyMask
        )
        closeMenuItem.setMnemonic('C')
        closeMenuItem.addActionListener { e: ActionEvent -> menuItemActionPerformed(e) }
        fileMenu.add(closeMenuItem)
        fileMenu.addSeparator()

        //---- exitMenuItem ----
        exitMenuItem.text = "\u9000\u51fa"
        exitMenuItem.accelerator = KeyStroke.getKeyStroke(
            KeyEvent.VK_Q,
            Toolkit.getDefaultToolkit().menuShortcutKeyMask
        )
        exitMenuItem.setMnemonic('X')
        exitMenuItem.addActionListener { exitActionPerformed() }
        fileMenu.add(exitMenuItem)

        menuBar1.add(fileMenu)

        //======== themeMenu ========
        themeMenu.text = "\u4e3b\u9898"
        menuBar1.add(themeMenu)

        //======== fontMenu ========
        fontMenu.text = "\u5b57\u4f53"

        //---- restoreFontMenuItem ----
        restoreFontMenuItem.text = "\u8fd8\u539f\u5b57\u4f53"
        restoreFontMenuItem.accelerator = KeyStroke.getKeyStroke(
            KeyEvent.VK_0,
            Toolkit.getDefaultToolkit().menuShortcutKeyMask
        )
        restoreFontMenuItem.addActionListener { restoreFont() }
        fontMenu.add(restoreFontMenuItem)

        //---- incrFontMenuItem ----
        incrFontMenuItem.text = "\u52a0\u5927\u5b57\u4f53"
        incrFontMenuItem.accelerator = KeyStroke.getKeyStroke(
            KeyEvent.VK_PLUS,
            Toolkit.getDefaultToolkit().menuShortcutKeyMask
        )
        incrFontMenuItem.addActionListener { incrFont() }
        fontMenu.add(incrFontMenuItem)

        //---- decrFontMenuItem ----
        decrFontMenuItem.text = "\u51cf\u5c0f\u5b57\u4f53"
        decrFontMenuItem.accelerator = KeyStroke.getKeyStroke(
            KeyEvent.VK_MINUS,
            Toolkit.getDefaultToolkit().menuShortcutKeyMask
        )
        decrFontMenuItem.addActionListener { decrFont() }
        fontMenu.add(decrFontMenuItem)

        menuBar1.add(fontMenu)

        //======== optionsMenu ========
        optionsMenu.text = "\u9009\u9879"

        //---- windowDecorationsCheckBoxMenuItem ----
        windowDecorationsCheckBoxMenuItem.text = "Window decorations"
        windowDecorationsCheckBoxMenuItem.addActionListener { windowDecorationsChanged() }
        optionsMenu.add(windowDecorationsCheckBoxMenuItem)

        //---- menuBarEmbeddedCheckBoxMenuItem ----
        menuBarEmbeddedCheckBoxMenuItem.text = "Embedded menu bar"
        menuBarEmbeddedCheckBoxMenuItem.addActionListener { menuBarEmbeddedChanged() }
        optionsMenu.add(menuBarEmbeddedCheckBoxMenuItem)

        //---- unifiedTitleBarMenuItem ----
        unifiedTitleBarMenuItem.text = "Unified window title bar"
        unifiedTitleBarMenuItem.addActionListener { unifiedTitleBar() }
        optionsMenu.add(unifiedTitleBarMenuItem)

        //---- showTitleBarIconMenuItem ----
        showTitleBarIconMenuItem.text = "Show window title bar icon"
        showTitleBarIconMenuItem.addActionListener {  showTitleBarIcon() }
        optionsMenu.add(showTitleBarIconMenuItem)

        //---- underlineMenuSelectionMenuItem ----
        underlineMenuSelectionMenuItem.text = "Use underline menu selection"
        underlineMenuSelectionMenuItem.addActionListener { underlineMenuSelection() }
        optionsMenu.add(underlineMenuSelectionMenuItem)

        //---- alwaysShowMnemonicsMenuItem ----
        alwaysShowMnemonicsMenuItem.text = "Always show mnemonics"
        alwaysShowMnemonicsMenuItem.addActionListener {  alwaysShowMnemonics() }
        optionsMenu.add(alwaysShowMnemonicsMenuItem)

        //---- animatedLafChangeMenuItem ----
        animatedLafChangeMenuItem.text = "Animated Laf Change"
        animatedLafChangeMenuItem.isSelected = true
        animatedLafChangeMenuItem.addActionListener { animatedLafChangeChanged() }
        optionsMenu.add(animatedLafChangeMenuItem)

        //---- showHintsMenuItem ----
        showHintsMenuItem.text = "Show hints"
        showHintsMenuItem.addActionListener { showHintsChanged() }
        optionsMenu.add(showHintsMenuItem)

        //---- showUIDefaultsInspectorMenuItem ----
        showUIDefaultsInspectorMenuItem.text = "Show UI Defaults Inspector"
        showUIDefaultsInspectorMenuItem.addActionListener { showUIDefaultsInspector() }
        optionsMenu.add(showUIDefaultsInspectorMenuItem)

        menuBar1.add(optionsMenu)

        //======== helpMenu ========
        helpMenu.text = "\u5e2e\u52a9"
        helpMenu.setMnemonic('H')

        //---- aboutMenuItem ----
        aboutMenuItem.text = "\u5173\u4e8e"
        aboutMenuItem.setMnemonic('A')
        aboutMenuItem.addActionListener { aboutActionPerformed() }
        helpMenu.add(aboutMenuItem)
        menuBar1.add(helpMenu)

        jMenuBar = menuBar1

        //======== toolBar ========
        toolBar.margin = Insets(3, 3, 3, 3)

        //---- backButton ----
        backButton.toolTipText = "Back"
        toolBar.add(backButton)

        //---- forwardButton ----
        forwardButton.toolTipText = "Forward"
        toolBar.add(forwardButton)
        toolBar.addSeparator()

        //---- cutButton ----
        cutButton.toolTipText = "Cut"
        toolBar.add(cutButton)

        //---- copyButton ----
        copyButton.toolTipText = "Copy"
        toolBar.add(copyButton)

        //---- pasteButton ----
        pasteButton.toolTipText = "Paste"
        toolBar.add(pasteButton)
        toolBar.addSeparator()

        //---- refreshButton ----
        refreshButton.toolTipText = "Refresh"
        toolBar.add(refreshButton)
        toolBar.addSeparator()

        //---- showToggleButton ----
        showToggleButton.isSelected = true
        showToggleButton.toolTipText = "Show Details"
        toolBar.add(showToggleButton)
        contentPane.add(toolBar, BorderLayout.NORTH)

        //======== contentPanel ========
        contentPanel.layout = MigLayout(
            "insets dialog,hidemode 3",  // columns
            "[grow,fill]",  // rows
            "[]" +
                    "[grow,fill]"
        )

        //======== tabbedPane ========
        tabbedPane.addTab("\u6b22\u8fce", panel1)
        tabbedPane.addTab("\u5b66\u751f\u7ba1\u7406", studentDataPanel)
        tabbedPane.addTab("\u7ffb\u8bd1\u7ba1\u7406", panel2)
        tabbedPane.addTab("GPA\u6807\u51c6\u7ba1\u7406", panel3)

        contentPanel.add(tabbedPane, "cell 0 0")

        contentPane.add(contentPanel, BorderLayout.CENTER)
        // JFormDesigner - End of component initialization  //GEN-END:initComponents

        // add "Users" button to menubar
        val usersButton: FlatButton = FlatButton()
        usersButton.icon = FlatSVGIcon("com/formdev/flatlaf/demo/icons/users.svg")
        usersButton.buttonType = FlatButton.ButtonType.toolBarButton
        usersButton.isFocusable = false
        usersButton.addActionListener { e: ActionEvent? ->
            JOptionPane.showMessageDialog(
                null, "Hello User! How are you?", "User",
                JOptionPane.INFORMATION_MESSAGE
            )
        }
        menuBar1.add(Box.createGlue())
        menuBar1.add(usersButton)
        backButton.icon = FlatSVGIcon("com/formdev/flatlaf/demo/icons/back.svg")
        forwardButton.icon = FlatSVGIcon("com/formdev/flatlaf/demo/icons/forward.svg")
        cutButton.icon = FlatSVGIcon("com/formdev/flatlaf/demo/icons/menu-cut.svg")
        copyButton.icon = FlatSVGIcon("com/formdev/flatlaf/demo/icons/copy.svg")
        pasteButton.icon = FlatSVGIcon("com/formdev/flatlaf/demo/icons/menu-paste.svg")
        refreshButton.icon = FlatSVGIcon("com/formdev/flatlaf/demo/icons/refresh.svg")
        showToggleButton.icon = FlatSVGIcon("com/formdev/flatlaf/demo/icons/show.svg")
        if (FlatLaf.supportsNativeWindowDecorations() || (SystemInfo.isLinux && JFrame.isDefaultLookAndFeelDecorated())) {
            if (SystemInfo.isLinux) unsupported(windowDecorationsCheckBoxMenuItem) else windowDecorationsCheckBoxMenuItem.isSelected =
                FlatLaf.isUseNativeWindowDecorations()
            menuBarEmbeddedCheckBoxMenuItem.isSelected = UIManager.getBoolean("TitlePane.menuBarEmbedded")
            unifiedTitleBarMenuItem.isSelected = UIManager.getBoolean("TitlePane.unifiedBackground")
            showTitleBarIconMenuItem.isSelected = UIManager.getBoolean("TitlePane.showIcon")
            if (JBRCustomDecorations.isSupported()) {
                // If the JetBrains Runtime is used, it forces the use of it's own custom
                // window decoration, which can not disabled.
                windowDecorationsCheckBoxMenuItem.isEnabled = false
            }
        } else {
            unsupported(windowDecorationsCheckBoxMenuItem)
            unsupported(menuBarEmbeddedCheckBoxMenuItem)
            unsupported(unifiedTitleBarMenuItem)
            unsupported(showTitleBarIconMenuItem)
        }
        if (SystemInfo.isMacOS) unsupported(underlineMenuSelectionMenuItem)

        // remove contentPanel bottom insets
        val layout: MigLayout = contentPanel.layout as MigLayout
        val lc: LC = ConstraintParser.parseLayoutConstraint(layout.layoutConstraints as String?)
        val insets: Array<UnitValue> = lc.insets
        lc.insets = arrayOf(
            insets[0],
            insets[1],
            UnitValue(0f, UnitValue.PIXEL, null),
            insets[3]
        )
        layout.layoutConstraints = lc
    }

    private fun unsupported(menuItem: JCheckBoxMenuItem) {
        menuItem.isEnabled = false
        menuItem.isSelected = false
        menuItem.toolTipText = "Not supported on your system."
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - csgo fps
    private lateinit var exitMenuItem: JMenuItem
    private lateinit var themeMenu: JMenu
    private lateinit var fontMenu: JMenu
    private lateinit var optionsMenu: JMenu
    private lateinit var windowDecorationsCheckBoxMenuItem: JCheckBoxMenuItem
    private lateinit var menuBarEmbeddedCheckBoxMenuItem: JCheckBoxMenuItem
    private lateinit var unifiedTitleBarMenuItem: JCheckBoxMenuItem
    private lateinit var showTitleBarIconMenuItem: JCheckBoxMenuItem
    private lateinit var underlineMenuSelectionMenuItem: JCheckBoxMenuItem
    private lateinit var alwaysShowMnemonicsMenuItem: JCheckBoxMenuItem
    private lateinit var animatedLafChangeMenuItem: JCheckBoxMenuItem
    private lateinit var aboutMenuItem: JMenuItem
    private lateinit var toolBar: JToolBar
    private lateinit var tabbedPane: JTabbedPane

    init {
        Arrays.sort(availableFontFamilyNames)
        initComponents()
        val tabIndex: Int = DemoPrefs.state.getInt(KEY_TAB, 0)
        if (tabIndex >= 0 && tabIndex < tabbedPane.tabCount && tabIndex != tabbedPane.selectedIndex) tabbedPane.selectedIndex =
            tabIndex
        initThemeMenu()
        updateFontMenuItems()
        initAccentColors()
        //TODO:
        //setIconImages(FlatSVGUtils.createWindowIconImages("/com/formdev/flatlaf/demo/FlatLaf.svg"))

        // macOS  (see https://www.formdev.com/flatlaf/macos/)
        if (SystemInfo.isMacOS) {
            // hide menu items that are in macOS application menu
            exitMenuItem.isVisible = false
            aboutMenuItem.isVisible = false
            if (SystemInfo.isMacFullWindowContentSupported) {
                // expand window content into window title bar and make title bar transparent
                getRootPane().putClientProperty("apple.awt.fullWindowContent", true)
                getRootPane().putClientProperty("apple.awt.transparentTitleBar", true)

                // hide window title
                if (SystemInfo.isJava_17_orLater) getRootPane().putClientProperty(
                    "apple.awt.windowTitleVisible",
                    false
                ) else title = null

                // add gap to left side of toolbar
                toolBar.add(Box.createHorizontalStrut(70), 0)
            }

            // enable full screen mode for this window (for Java 8 - 10; not necessary for Java 11+)
            if (!SystemInfo.isJava_11_orLater) getRootPane().putClientProperty("apple.awt.fullscreenable", true)
        }

        // integrate into macOS screen menu
        FlatDesktop.setAboutHandler { aboutActionPerformed() }
        FlatDesktop.setPreferencesHandler { showPreferences() }
        //FlatDesktop.setQuitHandler { FlatDesktop.QuitResponse.performQuit() }
        //SwingUtilities.invokeLater(Runnable({ showHints() }))

        //Elebird
    }

    // JFormDesigner - End of variables declaration  //GEN-END:variables
    //---- class AccentColorIcon ----------------------------------------------
    private class AccentColorIcon internal constructor(private val colorKey: String) : FlatAbstractIcon(16, 16, null) {
        protected override fun paintIcon(c: Component, g: Graphics2D) {
            var color: Color? = UIManager.getColor(colorKey)
            if (color == null) color = Color.lightGray else if (!c.isEnabled()) {
                color = if (FlatLaf.isLafDark()) ColorFunctions.shade(color, 0.5f) else ColorFunctions.tint(color, 0.6f)
            }
            g.color = color
            g.fillRoundRect(1, 1, width - 2, height - 2, 5, 5)
        }
    }

    companion object {
        val THEMES_PACKAGE: String = "/com/formdev/flatlaf/intellijthemes/themes/"

        // the real colors are defined in
        // flatlaf-demo/src/main/resources/com/formdev/flatlaf/demo/FlatLightLaf.properties and
        // flatlaf-demo/src/main/resources/com/formdev/flatlaf/demo/FlatDarkLaf.properties
        private val accentColorKeys: Array<String> = arrayOf(
            "Demo.accent.default", "Demo.accent.blue", "Demo.accent.purple",
            "Demo.accent.red", "Demo.accent.orange", "Demo.accent.yellow", "Demo.accent.green"
        )
        private val accentColorNames: Array<String> =
            arrayOf("Default", "Blue", "Purple", "Red", "Orange", "Yellow", "Green")
    }
}