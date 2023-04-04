package cc.eleb.parfait.ui

import cc.eleb.parfait.KEY_TAB
import cc.eleb.parfait.config.ParConfig
import cc.eleb.parfait.i18n.GenLanguage
import cc.eleb.parfait.i18n.trs
import cc.eleb.parfait.theme.ColorUtils
import cc.eleb.parfait.theme.FontUtils
import cc.eleb.parfait.theme.ThemeUtils
import cc.eleb.parfait.ui.dialog.GlobalSettingDialog
import cc.eleb.parfait.ui.panel.GPAPanel
import cc.eleb.parfait.ui.panel.I18nPanel
import cc.eleb.parfait.ui.panel.StudentDataPanel
import cc.eleb.parfait.ui.panel.WelcomePanel
import cc.eleb.parfait.utils.ParfaitPrefs
import com.formdev.flatlaf.FlatClientProperties
import com.formdev.flatlaf.FlatLaf
import com.formdev.flatlaf.extras.FlatDesktop
import com.formdev.flatlaf.extras.FlatSVGIcon
import com.formdev.flatlaf.extras.FlatSVGUtils
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector
import com.formdev.flatlaf.extras.components.FlatButton
import com.formdev.flatlaf.ui.JBRCustomDecorations
import com.formdev.flatlaf.util.SystemInfo
import net.miginfocom.layout.ConstraintParser
import net.miginfocom.layout.LC
import net.miginfocom.layout.UnitValue
import net.miginfocom.swing.MigLayout
import java.awt.*
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
import kotlin.system.exitProcess

class ParfaitFrame : JFrame() {
    override fun dispose() {
        super.dispose()
        FlatUIDefaultsInspector.hide()
    }

    private fun switchTabbed(flag:Boolean){
        if(flag){
            tabbedPane.setEnabledAt(1,true)
            tabbedPane.setEnabledAt(2,true)
            tabbedPane.setEnabledAt(3,true)
            tabbedPane.selectedIndex = 1
        }else {
            tabbedPane.setEnabledAt(1, false)
            tabbedPane.setEnabledAt(2, false)
            tabbedPane.setEnabledAt(3, false)
            tabbedPane.selectedIndex = 0
        }
    }

    private fun showHints(flag: Boolean) {
        if (flag && ParfaitPrefs.state.getBoolean("InitHitss", false)) return
        ParfaitPrefs.state.putBoolean("InitHitss", true)
        val optionsMenuHint: HintManager.Hint = HintManager.Hint(
            "hit-4".trs(), optionsMenu,
            SwingConstants.BOTTOM, "hint.optionsMenu", null
        )
        val fontMenuHint: HintManager.Hint = HintManager.Hint(
            "hit-3".trs(),
            FontUtils.fontMenu, SwingConstants.BOTTOM, "hint.fontMenu", optionsMenuHint
        )
        val themeMenuHint: HintManager.Hint = HintManager.Hint(
            "hit-2".trs(),
            ThemeUtils.themeMenu, SwingConstants.BOTTOM, "hint.themeMenu", fontMenuHint
        )
        val fileMenuHint: HintManager.Hint = HintManager.Hint(
            "hit-1".trs(),
            fileMenu, SwingConstants.BOTTOM, "hint.themeMenu", themeMenuHint
        )
        HintManager.showHint(fileMenuHint)
    }

    private fun clearHints() {
        HintManager.hideAllHints()
    }

    private fun showUIDefaultsInspector() {
        FlatUIDefaultsInspector.show()
    }

    private fun reloadAllFrame() {
        title = if (ParConfig.inited) {
            if (ParConfig.newed) {
                "${"global-new-file".trs()} - Parfait"
            } else {
                ParConfig.instance!!.file!!.name + " - Parfait"
            }
        } else "Parfait"
        GenLanguage.nowGenLanguage = "英语-English"
        StudentDataPanel.instance.table1.model.fireTableDataChanged()
        GPAPanel.instance.reload()
        I18nPanel.instance.reload()
    }

    private fun newActionPerformed() {
        checkToSave()
        ParConfig(null)
        reloadAllFrame()
        switchTabbed(true)
    }

    private fun checkToSave() {
        if (ParConfig.inited) {
            if (JOptionPane.showConfirmDialog(
                    this,
                    "frame-check-save".trs(),
                    "global-warning".trs(),
                    JOptionPane.YES_NO_OPTION
                ) == JOptionPane.YES_OPTION
            ) {
                save()
            } else close()
        }
    }

    private fun saveTo() {
        val fd = JFileChooser()
        fd.fileFilter = object : FileFilter() {
            override fun accept(f: File): Boolean {
                return f.isDirectory || f.toString().endsWith(".par")
            }

            override fun getDescription(): String {
                return "global-par-file".trs()
            }
        }
        fd.isMultiSelectionEnabled = false
        fd.fileSelectionMode = JFileChooser.FILES_ONLY
        if (fd.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                ParConfig.instance?.saveTo(
                    if (fd.selectedFile.endsWith(".par")) fd.selectedFile
                    else File(fd.selectedFile.absolutePath + ".par")
                )
            } catch (e: Exception) {
                JOptionPane.showMessageDialog(
                    this,
                    "${"global-error-unknown".trs()}\n${e.stackTraceToString()}",
                    "global-error".trs(),
                    JOptionPane.ERROR_MESSAGE
                )
            }
        }
    }

    private fun save() {
        if (ParConfig.newed) {
            saveTo()
            return
        }
        try {
            ParConfig.instance?.save()
        } catch (e: Exception) {
            JOptionPane.showMessageDialog(
                this,
                "${"global-error-unknown".trs()}\n${e.stackTraceToString()}",
                "global-error".trs(),
                JOptionPane.ERROR_MESSAGE
            )
        }
    }

    private fun close() {
        if (ParConfig.instance == null) return
        ParConfig.instance!!.close()
        reloadAllFrame()
        switchTabbed(false)
    }

    private fun openActionPerformed() {
        checkToSave()
        val fd = JFileChooser()
        fd.fileFilter = object : FileFilter() {
            override fun accept(f: File): Boolean {
                return f.isDirectory || f.name.endsWith(".par")
            }

            override fun getDescription(): String {
                return "global-par-file".trs()
            }
        }
        fd.isMultiSelectionEnabled = false
        fd.fileSelectionMode = JFileChooser.FILES_ONLY
        if (fd.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                if (fd.selectedFile.name.endsWith(".par")) {
                    ParConfig(fd.selectedFile)
                    this.reloadAllFrame()
                    switchTabbed(true)
                } else JOptionPane.showMessageDialog(
                    this,
                    "frame-open-error-1".trs(),
                    "global-error".trs(),
                    JOptionPane.ERROR_MESSAGE
                )
            } catch (e: Exception) {
                JOptionPane.showMessageDialog(
                    this,
                    "${"global-error-unknown".trs()}\n${e.stackTraceToString()}",
                    "global-error".trs(),
                    JOptionPane.ERROR_MESSAGE
                )
            }
        }
    }

    private fun saveAsActionPerformed() {
        if (ParConfig.checkInited()) save()
    }

    private fun closeActionPerformed() {
        checkToSave()
        close()
    }

    private fun exitActionPerformed() {
        this.dispose()
        exitProcess(0)
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
                } catch (_: IOException) {
                } catch (_: URISyntaxException) {
                }
            }
        })
        JOptionPane.showMessageDialog(
            this, arrayOf<Any>(
                titleLabel, "frame-about".trs(), " ",
                "Copyright 2023-" + Year.now() + " Elebird(Grow Zheng).", "All rights reserved.", linkLabel
            ), "frame-about-title".trs(),
            JOptionPane.PLAIN_MESSAGE
        )
    }

    private fun windowDecorationsChanged() {
        val windowDecorations: Boolean = windowDecorationsCheckBoxMenuItem.isSelected
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

    private fun showHintsChanged() {
        clearHints()
        showHints(false)
    }

    fun reloadTranslation(flag: Boolean = false){
        fileMenu.text = "frame-menu-file".trs()
        newMenuItem.text = "frame-menu-file-new".trs()
        openMenuItem.text = "frame-menu-file-open".trs()
        saveAsMenuItem.text = "frame-menu-file-save".trs()
        closeMenuItem.text = "frame-menu-file-close".trs()
        exitMenuItem.text = "frame-menu-file-quit".trs()
        settingMenuItem.text = "frame-menu-file-settings".trs()
        optionsMenu.text = "frame-menu-option".trs()
        windowDecorationsCheckBoxMenuItem.text = "frame-menu-option-1".trs()
        menuBarEmbeddedCheckBoxMenuItem.text = "frame-menu-option-2".trs()
        unifiedTitleBarMenuItem.text = "frame-menu-option-3".trs()
        showTitleBarIconMenuItem.text = "frame-menu-option-4".trs()
        showHintsMenuItem.text = "frame-menu-option-5".trs()
        showUIDefaultsInspectorMenuItem.text = "frame-menu-option-6".trs()
        helpMenu.text = "frame-menu-help".trs()
        aboutMenuItem.text = "frame-about-title".trs()
        ColorUtils.reloadTranslation()
        FontUtils.reloadTranslation()
        ThemeUtils.reloadTranslation()
        panel3.reloadTranslation()
        panel1.reloadTranslation()
        panel2.reloadTranslation()
        studentDataPanel.reloadTranslation()
        if(flag){
            tabbedPane.setTitleAt(0,"frame-pane-1".trs())
            tabbedPane.setTitleAt(1,"frame-pane-2".trs())
            tabbedPane.setTitleAt(2,"frame-pane-3".trs())
            tabbedPane.setTitleAt(3,"frame-pane-4".trs())
        }
        this.repaint()
    }

    private fun initComponents() {
        this.reloadTranslation()
        title = "Parfait"
        val contentPane: Container = contentPane
        contentPane.layout = BorderLayout()
        fileMenu.setMnemonic('F')
        newMenuItem.accelerator = KeyStroke.getKeyStroke(
            KeyEvent.VK_N,
            Toolkit.getDefaultToolkit().menuShortcutKeyMask
        )
        newMenuItem.setMnemonic('N')
        newMenuItem.addActionListener { newActionPerformed() }
        fileMenu.add(newMenuItem)
        openMenuItem.accelerator = KeyStroke.getKeyStroke(
            KeyEvent.VK_O,
            Toolkit.getDefaultToolkit().menuShortcutKeyMask
        )
        openMenuItem.setMnemonic('O')
        openMenuItem.addActionListener { openActionPerformed() }
        fileMenu.add(openMenuItem)
        saveAsMenuItem.accelerator = KeyStroke.getKeyStroke(
            KeyEvent.VK_S,
            Toolkit.getDefaultToolkit().menuShortcutKeyMask
        )
        saveAsMenuItem.setMnemonic('S')
        saveAsMenuItem.addActionListener { saveAsActionPerformed() }
        fileMenu.add(saveAsMenuItem)
        closeMenuItem.accelerator = KeyStroke.getKeyStroke(
            KeyEvent.VK_W,
            Toolkit.getDefaultToolkit().menuShortcutKeyMask
        )
        closeMenuItem.setMnemonic('C')
        closeMenuItem.addActionListener { closeActionPerformed() }
        exitMenuItem.accelerator = KeyStroke.getKeyStroke(
            KeyEvent.VK_Q,
            Toolkit.getDefaultToolkit().menuShortcutKeyMask
        )
        fileMenu.add(closeMenuItem)
        exitMenuItem.setMnemonic('X')
        exitMenuItem.addActionListener { exitActionPerformed() }
        fileMenu.addSeparator()
        settingMenuItem.accelerator = KeyStroke.getKeyStroke(
            KeyEvent.VK_T,
            Toolkit.getDefaultToolkit().menuShortcutKeyMask
        )
        settingMenuItem.setMnemonic('T')
        settingMenuItem.addActionListener {
            GlobalSettingDialog().isVisible = true
        }
        fileMenu.add(settingMenuItem)
        fileMenu.add(exitMenuItem)
        menuBar1.add(fileMenu)
        menuBar1.add(ThemeUtils.themeMenu)
        menuBar1.add(FontUtils.fontMenu)
        windowDecorationsCheckBoxMenuItem.addActionListener { windowDecorationsChanged() }
        optionsMenu.add(windowDecorationsCheckBoxMenuItem)
        menuBarEmbeddedCheckBoxMenuItem.addActionListener { menuBarEmbeddedChanged() }
        optionsMenu.add(menuBarEmbeddedCheckBoxMenuItem)
        unifiedTitleBarMenuItem.addActionListener { unifiedTitleBar() }
        optionsMenu.add(unifiedTitleBarMenuItem)
        showTitleBarIconMenuItem.addActionListener { showTitleBarIcon() }
        optionsMenu.add(showTitleBarIconMenuItem)
        showHintsMenuItem.addActionListener { showHintsChanged() }
        optionsMenu.add(showHintsMenuItem)
        showUIDefaultsInspectorMenuItem.addActionListener { showUIDefaultsInspector() }
        optionsMenu.add(showUIDefaultsInspectorMenuItem)
        menuBar1.add(optionsMenu)
        helpMenu.setMnemonic('H')
        aboutMenuItem.setMnemonic('A')
        aboutMenuItem.addActionListener { aboutActionPerformed() }
        helpMenu.add(aboutMenuItem)
        menuBar1.add(helpMenu)
        jMenuBar = menuBar1
        toolBar.margin = Insets(3, 3, 3, 3)
        contentPane.add(toolBar, BorderLayout.NORTH)
        contentPanel.layout = MigLayout("insets dialog,hidemode 3", "[grow,fill]", "[][grow,fill]")
        tabbedPane.addTab("frame-pane-1".trs(), panel1)
        tabbedPane.addTab("frame-pane-2".trs(), studentDataPanel)
        tabbedPane.addTab("frame-pane-3".trs(), panel2)
        tabbedPane.addTab("frame-pane-4".trs(), panel3)
        switchTabbed(false)
        contentPanel.add(tabbedPane, "cell 0 0")
        contentPane.add(contentPanel, BorderLayout.CENTER)
        val usersButton = FlatButton()
        usersButton.icon = FlatSVGIcon("cc/eleb/parfait/icons/users.svg")
        usersButton.buttonType = FlatButton.ButtonType.toolBarButton
        usersButton.isFocusable = false
        usersButton.addActionListener {
            JOptionPane.showMessageDialog(null, "哈喽啊(,,･∀･)ﾉ゛hello", "Test", JOptionPane.INFORMATION_MESSAGE)
        }
        menuBar1.add(Box.createGlue())
        menuBar1.add(usersButton)
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

    private val exitMenuItem = JMenuItem()
    private val optionsMenu = JMenu()
    private val windowDecorationsCheckBoxMenuItem = JCheckBoxMenuItem()
    private val menuBarEmbeddedCheckBoxMenuItem = JCheckBoxMenuItem()
    private val unifiedTitleBarMenuItem = JCheckBoxMenuItem()
    private val showTitleBarIconMenuItem = JCheckBoxMenuItem()
    private val underlineMenuSelectionMenuItem = JCheckBoxMenuItem()
    private val aboutMenuItem = JMenuItem()
    val toolBar = JToolBar()
    private val tabbedPane = JTabbedPane()
    private val fileMenu = JMenu()
    val menuBar1 = JMenuBar()
    val newMenuItem = JMenuItem()
    val openMenuItem = JMenuItem()
    val settingMenuItem = JMenuItem()
    val saveAsMenuItem = JMenuItem()
    val closeMenuItem = JMenuItem()
    val showHintsMenuItem = JMenuItem()
    val showUIDefaultsInspectorMenuItem = JMenuItem()
    val helpMenu = JMenu()
    val contentPanel = JPanel()
    val panel1 = WelcomePanel()
    val studentDataPanel = StudentDataPanel()
    val panel2 = I18nPanel()
    val panel3 = GPAPanel()

    init {
        instance = this
        defaultCloseOperation = EXIT_ON_CLOSE
        ThemeUtils.init()
        FontUtils.init()
        initComponents()
        ColorUtils.init()
        val tabIndex: Int = ParfaitPrefs.state.getInt(KEY_TAB, 0)
        if (tabIndex >= 0 && tabIndex < tabbedPane.tabCount && tabIndex != tabbedPane.selectedIndex) tabbedPane.selectedIndex =
            tabIndex
        this.iconImages = listOf(FlatSVGIcon("cc/eleb/parfait/FlatLaf.svg").image)
        if (SystemInfo.isMacOS) {
            exitMenuItem.isVisible = false
            aboutMenuItem.isVisible = false
            if (SystemInfo.isMacFullWindowContentSupported) {
                getRootPane().putClientProperty("apple.awt.fullWindowContent", true)
                getRootPane().putClientProperty("apple.awt.transparentTitleBar", true)
                if (SystemInfo.isJava_17_orLater) getRootPane().putClientProperty(
                    "apple.awt.windowTitleVisible",
                    false
                ) else title = null
                toolBar.add(Box.createHorizontalStrut(70), 0)
            }
            if (!SystemInfo.isJava_11_orLater) getRootPane().putClientProperty("apple.awt.fullscreenable", true)
        }
        FlatDesktop.setAboutHandler { aboutActionPerformed() }
        SwingUtilities.invokeLater { showHints(true) }
    }

    companion object {
        val THEMES_PACKAGE: String = "/cc/eleb/parfait/intellijthemes/themes/"
        lateinit var instance: ParfaitFrame
    }
}