/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.panel

import moe.hhm.parfait.infra.i18n.I18nUtils
import moe.hhm.parfait.ui.component.tool.MainToolBar
import moe.hhm.parfait.ui.view.CertificateTemplateView
import moe.hhm.parfait.ui.view.GpaStandardView
import moe.hhm.parfait.ui.view.StatisticsView
import moe.hhm.parfait.ui.view.StudentDataView
import moe.hhm.parfait.ui.view.TermView
import moe.hhm.parfait.ui.viewmodel.StatisticsViewModel
import net.miginfocom.swing.MigLayout
import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.JTabbedPane

class MainPanel : JPanel() {
    private val toolBar = MainToolBar()
    private val studentData = StudentDataView()
    private val gpaStandard = GpaStandardView()
    private val certificateTemplate = CertificateTemplateView()
    private val statistics = StatisticsView()
    private val termView = TermView()
    private val contentPane = JTabbedPane().apply {
        addTab("", studentData)
        I18nUtils.bindProperty(this, "main.students") { c, v -> setTitleAt(0, v) }
        addTab("", gpaStandard)
        I18nUtils.bindProperty(this, "main.gpa") { c, v -> setTitleAt(1, v) }
        addTab("", certificateTemplate)
        I18nUtils.bindProperty(this, "main.certificate") { c, v -> setTitleAt(2, v) }
        addTab("", termView)
        I18nUtils.bindProperty(this, "main.term") { c, v -> setTitleAt(3, v) }
        addTab("", statistics)
        I18nUtils.bindProperty(this, "main.statistics") { c, v -> setTitleAt(4, v) }
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