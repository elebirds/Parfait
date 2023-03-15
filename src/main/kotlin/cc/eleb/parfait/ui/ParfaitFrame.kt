package cc.eleb.parfait.ui


import cc.eleb.parfait.KEY_TAB
import cc.eleb.parfait.config.ParConfig
import cc.eleb.parfait.i18n.Language
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
import kotlin.system.exitProcess

class ParfaitFrame : JFrame() {
    override fun dispose() {
        super.dispose()
        FlatUIDefaultsInspector.hide()
    }

    private fun showHints(flag:Boolean) {
        if(flag&&DemoPrefs.state.getBoolean("InitHitss",false))return
        DemoPrefs.state.putBoolean("InitHitss",true)
        val optionsMenuHint: HintManager.Hint = HintManager.Hint(
            "使用“选项”菜单来切换偏好样式或者再次显示提示", optionsMenu,
            SwingConstants.BOTTOM, "hint.optionsMenu", null
        )
        val fontMenuHint: HintManager.Hint = HintManager.Hint(
            "使用“字体”菜单来改变字体大小或者切换字体",
            FontUtils.fontMenu, SwingConstants.BOTTOM, "hint.fontMenu", optionsMenuHint
        )
        val themeMenuHint: HintManager.Hint = HintManager.Hint(
            "使用“主题”菜单来改变界面主题",
            ThemeUtils.themeMenu, SwingConstants.BOTTOM, "hint.themeMenu", fontMenuHint
        )
        val fileMenuHint: HintManager.Hint = HintManager.Hint(
            "使用“文件”菜单来新建、打开、保存、关闭一个Parfait文件(.par)以开始学生管理",
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

    private fun reloadAllFrame(){
        title = if(ParConfig.inited){
            if(ParConfig.newed){
                "新建文件 - Parfait Demo"
            }else{
                ParConfig.instance!!.file!!.name + " - Parfait Demo"
            }
        }else "Parfait Demo"
        Language.nowLanguage = "英语-English"
        StudentDataPanel.instance.table1.model.fireTableDataChanged()
        GPAPanel.instance.reload()
        I18nPanel.instance.reload()
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
        if(ParConfig.instance==null)return
        ParConfig.instance!!.close()
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

    private fun showHintsChanged() {
        clearHints()
        showHints(false)
    }
    
    private fun initComponents() {
        val menuBar1 = JMenuBar()
        val newMenuItem = JMenuItem()
        val openMenuItem = JMenuItem()
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
        title = "Parfait"
        defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
        val contentPane: Container = contentPane
        contentPane.layout = BorderLayout()
        fileMenu.text = "文件"
        fileMenu.setMnemonic('F')
        newMenuItem.text = "新建"
        newMenuItem.accelerator = KeyStroke.getKeyStroke(
            KeyEvent.VK_N,
            Toolkit.getDefaultToolkit().menuShortcutKeyMask
        )
        newMenuItem.setMnemonic('N')
        newMenuItem.addActionListener { newActionPerformed() }
        fileMenu.add(newMenuItem)
        openMenuItem.text = "打开"
        openMenuItem.accelerator = KeyStroke.getKeyStroke(
            KeyEvent.VK_O,
            Toolkit.getDefaultToolkit().menuShortcutKeyMask
        )
        openMenuItem.setMnemonic('O')
        openMenuItem.addActionListener { openActionPerformed() }
        fileMenu.add(openMenuItem)
        saveAsMenuItem.text = "保存"
        saveAsMenuItem.accelerator = KeyStroke.getKeyStroke(
            KeyEvent.VK_S,
            Toolkit.getDefaultToolkit().menuShortcutKeyMask
        )
        saveAsMenuItem.setMnemonic('S')
        saveAsMenuItem.addActionListener { saveAsActionPerformed() }
        fileMenu.add(saveAsMenuItem)
        fileMenu.addSeparator()
        closeMenuItem.text = "关闭"
        closeMenuItem.accelerator = KeyStroke.getKeyStroke(
            KeyEvent.VK_W,
            Toolkit.getDefaultToolkit().menuShortcutKeyMask
        )
        closeMenuItem.setMnemonic('C')
        closeMenuItem.addActionListener { closeActionPerformed() }
        fileMenu.add(closeMenuItem)
        fileMenu.addSeparator()
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
        optionsMenu.text = "选项"
        windowDecorationsCheckBoxMenuItem.text = "窗口装饰"
        windowDecorationsCheckBoxMenuItem.addActionListener { windowDecorationsChanged() }
        optionsMenu.add(windowDecorationsCheckBoxMenuItem)
        menuBarEmbeddedCheckBoxMenuItem.text = "嵌入式菜单栏"
        menuBarEmbeddedCheckBoxMenuItem.addActionListener { menuBarEmbeddedChanged() }
        optionsMenu.add(menuBarEmbeddedCheckBoxMenuItem)
        unifiedTitleBarMenuItem.text = "统一窗口标题栏"
        unifiedTitleBarMenuItem.addActionListener { unifiedTitleBar() }
        optionsMenu.add(unifiedTitleBarMenuItem)
        showTitleBarIconMenuItem.text = "显示窗口标题栏图标"
        showTitleBarIconMenuItem.addActionListener {  showTitleBarIcon() }
        optionsMenu.add(showTitleBarIconMenuItem)
        showHintsMenuItem.text = "显示提示"
        showHintsMenuItem.addActionListener { showHintsChanged() }
        optionsMenu.add(showHintsMenuItem)
        showUIDefaultsInspectorMenuItem.text = "UI默认值检查器"
        showUIDefaultsInspectorMenuItem.addActionListener { showUIDefaultsInspector() }
        optionsMenu.add(showUIDefaultsInspectorMenuItem)
        menuBar1.add(optionsMenu)
        helpMenu.text = "帮助"
        helpMenu.setMnemonic('H')
        aboutMenuItem.text = "关于"
        aboutMenuItem.setMnemonic('A')
        aboutMenuItem.addActionListener { aboutActionPerformed() }
        helpMenu.add(aboutMenuItem)
        menuBar1.add(helpMenu)
        jMenuBar = menuBar1
        toolBar.margin = Insets(3, 3, 3, 3)
        contentPane.add(toolBar, BorderLayout.NORTH)
        contentPanel.layout = MigLayout("insets dialog,hidemode 3", "[grow,fill]", "[][grow,fill]")
        tabbedPane.addTab("欢迎", panel1)
        tabbedPane.addTab("学生管理", studentDataPanel)
        tabbedPane.addTab("翻译管理", panel2)
        tabbedPane.addTab("GPA标准管理", panel3)
        contentPanel.add(tabbedPane, "cell 0 0")
        contentPane.add(contentPanel, BorderLayout.CENTER)
        val usersButton = FlatButton()
        usersButton.icon = FlatSVGIcon("cc/eleb/parfait/icons/users.svg")
        usersButton.buttonType = FlatButton.ButtonType.toolBarButton
        usersButton.isFocusable = false
        usersButton.addActionListener {
            JOptionPane.showMessageDialog(null, "哈喽啊(,,･∀･)ﾉ゛hello", "菜单", JOptionPane.INFORMATION_MESSAGE)
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

    init {
        instance = this
        ThemeUtils.init()
        FontUtils.init()
        initComponents()
        ColorUtils.init()
        val tabIndex: Int = DemoPrefs.state.getInt(KEY_TAB, 0)
        if (tabIndex >= 0 && tabIndex < tabbedPane.tabCount && tabIndex != tabbedPane.selectedIndex) tabbedPane.selectedIndex =
            tabIndex
        this.iconImages = FlatSVGUtils.createWindowIconImages("/cc/eleb/parfait/FlatLaf.svg")
        if (SystemInfo.isMacOS) {
            exitMenuItem.isVisible = false
            aboutMenuItem.isVisible = false
            if (SystemInfo.isMacFullWindowContentSupported) {
                getRootPane().putClientProperty("apple.awt.fullWindowContent", true)
                getRootPane().putClientProperty("apple.awt.transparentTitleBar", true)

                // hide window title
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
        lateinit var instance:ParfaitFrame
    }
}