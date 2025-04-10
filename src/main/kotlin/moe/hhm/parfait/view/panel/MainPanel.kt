/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.view.panel

import moe.hhm.parfait.utils.i18n.I18nUtils
import moe.hhm.parfait.view.component.tool.MainToolBar
import moe.hhm.parfait.view.view.StudentDataView
import net.miginfocom.swing.MigLayout
import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.JTabbedPane

class MainPanel : JPanel() {
    private val toolBar = MainToolBar()
    private val studentData = StudentDataView()
    private val contentPane = JTabbedPane().apply {
        addTab("", studentData)
        I18nUtils.bindProperty(this, "main.students") { c, v -> setTitleAt(0, v) }
    }
    private val contentPanel = JPanel().apply {
        this.layout = MigLayout("insets dialog,hidemode 3", "[grow,fill]", "[][grow,fill]")
        this.add(contentPane, "cell 0 0")
    }

    init {
        this.layout = BorderLayout()
        this.add(toolBar, BorderLayout.NORTH)
        this.add(contentPanel, BorderLayout.CENTER)
    }
}