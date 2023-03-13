package cc.eleb.parfait.ui


import cc.eleb.parfait.KEY_TAB
import cc.eleb.parfait.config.ParConfig
import cc.eleb.parfait.theme.ColorUtils
import cc.eleb.parfait.theme.FontUtils
import cc.eleb.parfait.ui.panel.GPAPanel
import cc.eleb.parfait.ui.panel.I18nPanel
import cc.eleb.parfait.ui.panel.WelcomePanel
import cc.eleb.parfait.theme.ThemeUtils
import cc.eleb.parfait.ui.panel.StudentDataPanel
import com.formdev.flatlaf.*
import com.formdev.flatlaf.extras.FlatDesktop
import com.formdev.flatlaf.extras.FlatSVGIcon
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector
import com.formdev.flatlaf.extras.components.FlatButton
import com.formdev.flatlaf.ui.JBRCustomDecorations
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
import java.io.File
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import java.time.Year
import javax.swing.*
import javax.swing.filechooser.FileFilter

class ParfaitFrame : JFrame() {
    override fun dispose() {
        super.dispose()
        FlatUIDefaultsInspector.hide()
    }

    private fun showHints() {
        val fontMenuHint: HintManager.Hint = HintManager.Hint(
            "Use 'Font' menu to increase/decrease font size or try different fonts.",
            FontUtils.fontMenu, SwingConstants.BOTTOM, "hint.fontMenu", null
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

    private fun reloadAllFrame(){
        title = if(ParConfig.inited){
            if(ParConfig.newed){
                "新建文件 - Parfait Demo"
            }else{
                ParConfig.instance!!.file!!.name + " - Parfait Demo"
            }
        }else "Parfait Demo"
        StudentDataPanel.instance.table1.model.fireTableDataChanged()
    }

    private fun newActionPerformed() {
        checkToSave()
        ParConfig(null)
        reloadAllFrame()
    }

    private fun checkToSave(){
        if(ParConfig.inited){
            if(JOptionPane.showConfirmDialog(
                this,
                "您已经打开了一个par文件，是否要保存？",
                "警告",
                JOptionPane.YES_NO_OPTION
            )==JOptionPane.YES_OPTION){
                save()
            }else close()
        }
    }

    private fun saveTo(){
        val fd = JFileChooser()
        fd.fileFilter = object : FileFilter() {
            override fun accept(f: File): Boolean {
                return f.isDirectory || f.toString().endsWith(".par")
            }

            override fun getDescription(): String {
                return "Parfait文件(.par)"
            }
        }
        fd.isMultiSelectionEnabled = false
        fd.fileSelectionMode = JFileChooser.FILES_ONLY
        if (fd.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                ParConfig.instance?.saveTo(
                    if(fd.selectedFile.endsWith(".par"))fd.selectedFile
                    else File(fd.selectedFile.absolutePath+".par")
                )
            } catch (e: Exception) {
                JOptionPane.showMessageDialog(
                    this,
                    "未知错误。\n${e.stackTraceToString()}",
                    "保存失败",
                    JOptionPane.ERROR_MESSAGE
                )
            }
        }
    }

    private fun save(){
        if(ParConfig.newed){
            saveTo()
            return
        }
        try {
            ParConfig.instance?.save()
        } catch (e: Exception) {
            JOptionPane.showMessageDialog(
                this,
                "未知错误。\n${e.stackTraceToString()}",
                "保存失败",
                JOptionPane.ERROR_MESSAGE
            )
        }
    }

    private fun close(){
        ParConfig.instance?.close()
        reloadAllFrame()
    }

    private fun openActionPerformed() {
        checkToSave()
        val fd = JFileChooser()
        fd.fileFilter = object : FileFilter() {
            override fun accept(f: File): Boolean {
                return f.isDirectory || f.name.endsWith(".par")
            }

            override fun getDescription(): String {
                return "Parfait文件(.par)"
            }
        }
        fd.isMultiSelectionEnabled = false
        fd.fileSelectionMode = JFileChooser.FILES_ONLY
        if (fd.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                if(fd.selectedFile.name.endsWith(".par")){
                    ParConfig(fd.selectedFile)
                    this.reloadAllFrame()
                }
                else JOptionPane.showMessageDialog(
                    this,
                    "请选择一个Parfait文件(.par)。",
                    "打开失败",
                    JOptionPane.ERROR_MESSAGE
                )
            } catch (e: Exception) {
                JOptionPane.showMessageDialog(
                    this,
                    "未知错误。\n${e.stackTraceToString()}",
                    "打开失败",
                    JOptionPane.ERROR_MESSAGE
                )
            }
        }
    }

    private fun saveAsActionPerformed() {
        if(ParConfig.checkInited())save()
    }

    private fun closeActionPerformed(){
        checkToSave()
        close()
    }

    private fun exitActionPerformed() {
        dispose()
    }

    private fun aboutActionPerformed() {
        val titleLabel = JLabel("Parfait Demo")
        titleLabel.putClientProperty(FlatClientProperties.STYLE_CLASS, "h1")
        val link = "https://eleb.cc/"
        val linkLabel = JLabel("<html><a href=\"#\">$link</a></html>")
        linkLabel.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        linkLabel.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
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
    
    private fun initComponents() {
        val menuBar1 = JMenuBar()
        val fileMenu = JMenu()
        val newMenuItem = JMenuItem()
        val openMenuItem = JMenuItem()
        val saveAsMenuItem = JMenuItem()
        val closeMenuItem = JMenuItem()
        val showHintsMenuItem = JMenuItem()
        val showUIDefaultsInspectorMenuItem = JMenuItem()
        val helpMenu = JMenu()
        val backButton = JButton()
        val forwardButton = JButton()
        val cutButton = JButton()
        val copyButton = JButton()
        val pasteButton = JButton()
        val refreshButton = JButton()
        val showToggleButton = JToggleButton()
        val contentPanel = JPanel()
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
        fileMenu.text = "文件"
        fileMenu.setMnemonic('F')

        //---- newMenuItem ----
        newMenuItem.text = "新建"
        newMenuItem.accelerator = KeyStroke.getKeyStroke(
            KeyEvent.VK_N,
            Toolkit.getDefaultToolkit().menuShortcutKeyMask
        )
        newMenuItem.setMnemonic('N')
        newMenuItem.addActionListener { newActionPerformed() }
        fileMenu.add(newMenuItem)

        //---- openMenuItem ----
        openMenuItem.text = "打开"
        openMenuItem.accelerator = KeyStroke.getKeyStroke(
            KeyEvent.VK_O,
            Toolkit.getDefaultToolkit().menuShortcutKeyMask
        )
        openMenuItem.setMnemonic('O')
        openMenuItem.addActionListener { openActionPerformed() }
        fileMenu.add(openMenuItem)

        //---- saveAsMenuItem ----
        saveAsMenuItem.text = "保存"
        saveAsMenuItem.accelerator = KeyStroke.getKeyStroke(
            KeyEvent.VK_S,
            Toolkit.getDefaultToolkit().menuShortcutKeyMask
        )
        saveAsMenuItem.setMnemonic('S')
        saveAsMenuItem.addActionListener { saveAsActionPerformed() }
        fileMenu.add(saveAsMenuItem)
        fileMenu.addSeparator()

        //---- closeMenuItem ----
        closeMenuItem.text = "关闭"
        closeMenuItem.accelerator = KeyStroke.getKeyStroke(
            KeyEvent.VK_W,
            Toolkit.getDefaultToolkit().menuShortcutKeyMask
        )
        closeMenuItem.setMnemonic('C')
        closeMenuItem.addActionListener { closeActionPerformed() }
        fileMenu.add(closeMenuItem)
        fileMenu.addSeparator()

        //---- exitMenuItem ----
        exitMenuItem.text = "退出"
        exitMenuItem.accelerator = KeyStroke.getKeyStroke(
            KeyEvent.VK_Q,
            Toolkit.getDefaultToolkit().menuShortcutKeyMask
        )
        exitMenuItem.setMnemonic('X')
        exitMenuItem.addActionListener { exitActionPerformed() }
        fileMenu.add(exitMenuItem)

        menuBar1.add(fileMenu)

        menuBar1.add(ThemeUtils.themeMenu)
        menuBar1.add(FontUtils.fontMenu)

        //======== optionsMenu ========
        optionsMenu.text = "选项"

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
        helpMenu.text = "帮助"
        helpMenu.setMnemonic('H')

        //---- aboutMenuItem ----
        aboutMenuItem.text = "关于"
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
        refreshButton.toolTipText = "从文件重新读取"
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
        tabbedPane.addTab("欢迎", panel1)
        tabbedPane.addTab("学生管理", studentDataPanel)
        tabbedPane.addTab("翻译管理", panel2)
        tabbedPane.addTab("GPA标准管理", panel3)

        contentPanel.add(tabbedPane, "cell 0 0")

        contentPane.add(contentPanel, BorderLayout.CENTER)
        // JFormDesigner - End of component initialization  //GEN-END:initComponents

        // add "Users" button to menubar
        val usersButton = FlatButton()
        usersButton.icon = FlatSVGIcon("com/formdev/flatlaf/demo/icons/users.svg")
        usersButton.buttonType = FlatButton.ButtonType.toolBarButton
        usersButton.isFocusable = false
        usersButton.addActionListener {
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
        if (FlatLaf.supportsNativeWindowDecorations() || (SystemInfo.isLinux && isDefaultLookAndFeelDecorated())) {
            if (SystemInfo.isLinux) unsupported(windowDecorationsCheckBoxMenuItem) else windowDecorationsCheckBoxMenuItem.isSelected =
                FlatLaf.isUseNativeWindowDecorations()
            menuBarEmbeddedCheckBoxMenuItem.isSelected = UIManager.getBoolean("TitlePane.menuBarEmbedded")
            unifiedTitleBarMenuItem.isSelected = UIManager.getBoolean("TitlePane.unifiedBackground")
            showTitleBarIconMenuItem.isSelected = UIManager.getBoolean("TitlePane.showIcon")
            if (JBRCustomDecorations.isSupported()) {
                // If the JetBrains Runtime is used, it forces the use of its own custom
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
    val exitMenuItem = JMenuItem()
    val optionsMenu = JMenu()
    val windowDecorationsCheckBoxMenuItem = JCheckBoxMenuItem()
    val menuBarEmbeddedCheckBoxMenuItem = JCheckBoxMenuItem()
    val unifiedTitleBarMenuItem = JCheckBoxMenuItem()
    val showTitleBarIconMenuItem = JCheckBoxMenuItem()
    val underlineMenuSelectionMenuItem = JCheckBoxMenuItem()
    val alwaysShowMnemonicsMenuItem = JCheckBoxMenuItem()
    val animatedLafChangeMenuItem = JCheckBoxMenuItem()
    val aboutMenuItem = JMenuItem()
    val toolBar = JToolBar()
    val tabbedPane = JTabbedPane()

    init {
        instance = this
        ThemeUtils.init()
        FontUtils.init()
        initComponents()
        ColorUtils.init()
        val tabIndex: Int = DemoPrefs.state.getInt(KEY_TAB, 0)
        if (tabIndex >= 0 && tabIndex < tabbedPane.tabCount && tabIndex != tabbedPane.selectedIndex) tabbedPane.selectedIndex =
            tabIndex
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
    }
    companion object {
        val THEMES_PACKAGE: String = "/com/formdev/flatlaf/intellijthemes/themes/"

        lateinit var instance:ParfaitFrame
    }
}