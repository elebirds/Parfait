/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.component.dialog

import kotlinx.coroutines.launch
import moe.hhm.parfait.app.service.CertificateTemplateService
import moe.hhm.parfait.app.service.GpaStandardService
import moe.hhm.parfait.dto.CertificateTemplateDTO
import moe.hhm.parfait.dto.GpaStandardDTO
import moe.hhm.parfait.dto.StudentDTO
import moe.hhm.parfait.exception.BusinessException
import moe.hhm.parfait.infra.i18n.I18nUtils
import moe.hhm.parfait.infra.i18n.I18nUtils.bindText
import moe.hhm.parfait.infra.i18n.I18nUtils.createButton
import moe.hhm.parfait.infra.i18n.I18nUtils.createLabel
import moe.hhm.parfait.ui.base.CoroutineComponent
import moe.hhm.parfait.ui.base.DefaultCoroutineComponent
import moe.hhm.parfait.ui.component.render.CertificateTemplateListCellRenderer
import moe.hhm.parfait.ui.component.render.GpaStandardListCellRenderer
import moe.hhm.parfait.ui.viewmodel.StudentDataViewModel
import net.miginfocom.swing.MigLayout
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.awt.Dimension
import java.awt.Window
import java.io.File
import javax.swing.*

/**
 * 证书生成对话框
 *
 * 用于选择证书模板、GPA标准等参数，并生成学生证书
 */
class CertificateGenerateDialog(
    private val selectedStudents: List<StudentDTO>,
    owner: Window? = null,
    parent: CoroutineComponent? = null
) : JDialog(owner),
    KoinComponent, CoroutineComponent by DefaultCoroutineComponent(parent) {

    // 通过Koin获取服务
    private val certificateService: CertificateTemplateService by inject()
    private val gpaService: GpaStandardService by inject()
    private val viewModel: StudentDataViewModel by inject()

    // 表单组件
    private val comboCertificateTemplate = JComboBox<CertificateTemplateDTO>()
    private val comboGpaStandard = JComboBox<GpaStandardDTO>()
    private val checkboxUseGpa = JCheckBox(I18nUtils.getText("certificate.dialog.use.gpa"))
    private val textIssuer = JTextField()
    private val textPurpose = JTextField()

    // 选择的模板和GPA标准
    private var selectedTemplate: CertificateTemplateDTO? = null
    private var selectedGpaStandard: GpaStandardDTO? = null

    // 按钮
    private val buttonGenerate = object : JButton() {
        override fun isDefaultButton(): Boolean = true
    }.apply {
        bindText(this, "button.ok")
        addActionListener { generateCertificate() }
    }

    private val buttonCancel = createButton("button.cancel").apply {
        addActionListener { dispose() }
    }

    init {
        initDialog()
        initComponents()
        loadData()
    }

    private fun initDialog() {
        title = I18nUtils.getFormattedText("certificate.dialog.generate.title", selectedStudents.size)
        // 设置对话框基本属性
        isModal = true
        defaultCloseOperation = DISPOSE_ON_CLOSE

        // 禁止调整窗口大小
        isResizable = false

        // 固定大小
        preferredSize = Dimension(500, 280)
        minimumSize = Dimension(500, 280)
        maximumSize = Dimension(500, 280)

        // 使用单一布局面板
        contentPane = JPanel(MigLayout("wrap 2, fillx, insets n 35 n 35", "[fill, 200]"))
    }

    private fun initComponents() {
        // 证书模板选择
        contentPane.add(createLabel("certificate.property.name"), "grow")
        comboCertificateTemplate.apply {
            renderer = CertificateTemplateListCellRenderer()
            addActionListener {
                selectedTemplate = selectedItem as? CertificateTemplateDTO
            }
        }
        contentPane.add(comboCertificateTemplate, "grow, wrap")

        // GPA标准选择
        contentPane.add(createLabel("certificate.dialog.generate.gpa.standard"), "grow")
        comboGpaStandard.apply {
            renderer = GpaStandardListCellRenderer()
            addActionListener {
                selectedGpaStandard = selectedItem as? GpaStandardDTO
            }
            isEnabled = false // 默认禁用
        }
        contentPane.add(comboGpaStandard, "grow, wrap")

        // 使用GPA复选框
        checkboxUseGpa.apply {
            addActionListener {
                comboGpaStandard.isEnabled = isSelected
            }
        }
        contentPane.add(checkboxUseGpa, "span 2, grow, wrap")

        // 签发人
        contentPane.add(createLabel("certificate.property.issuer"), "grow")
        contentPane.add(textIssuer, "grow, wrap")

        // 用途
        contentPane.add(createLabel("certificate.property.purpose"), "grow")
        contentPane.add(textPurpose, "grow, wrap")

        val buttonPanel = JPanel(MigLayout("insets 0, gap 10", "[fill, 50%][fill, 50%]"))
        buttonPanel.add(buttonCancel, "grow")
        buttonPanel.add(buttonGenerate, "grow")

        // 20px的间距
        contentPane.add(buttonPanel, "span 2, grow")
    }

    private fun loadData() {
        scope.launch {
            // 加载证书模板列表，按照先isLike排序，再按优先级排序
            val templates = certificateService.getActiveCertificates()
                .sortedWith(compareByDescending<CertificateTemplateDTO> { it.isLike }
                    .thenByDescending { it.priority })

            SwingUtilities.invokeLater {
                comboCertificateTemplate.removeAllItems()
                templates.forEach { comboCertificateTemplate.addItem(it) }
                if (templates.isNotEmpty()) {
                    comboCertificateTemplate.selectedIndex = 0
                    selectedTemplate = templates[0]
                }
            }

            // 加载GPA标准列表，按照默认-重要-普通排序
            val gpaStandards = gpaService.getAllGpaStandards()
                .sortedWith(compareByDescending<GpaStandardDTO> { it.isDefault }
                    .thenByDescending { it.isLike }
                    .thenBy { it.name })

            SwingUtilities.invokeLater {
                comboGpaStandard.removeAllItems()
                gpaStandards.forEach { comboGpaStandard.addItem(it) }
                if (gpaStandards.isNotEmpty()) {
                    comboGpaStandard.selectedIndex = 0
                    selectedGpaStandard = gpaStandards[0]
                }
            }
        }
    }

    private fun generateCertificate() {
        // 验证必填项
        if (selectedTemplate == null) throw BusinessException("certificate.error.no.template")

        if (checkboxUseGpa.isSelected && selectedGpaStandard == null) throw BusinessException("certificate.error.no.gpa.standard")
        if (textIssuer.text.isBlank()) throw BusinessException("certificate.error.no.issuer")

        // 选择保存目录
        val fileChooser = JFileChooser().apply {
            fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            dialogTitle = I18nUtils.getText("certificate.dialog.generate.save.directory")
        }

        val result = fileChooser.showSaveDialog(this)
        if (result != JFileChooser.APPROVE_OPTION) return
        val selectedDir = fileChooser.selectedFile

        // 收集生成证书所需的参数
        val params = CertificateGenerationParams(
            students = selectedStudents,
            template = selectedTemplate!!,
            gpaStandard = if (checkboxUseGpa.isSelected) selectedGpaStandard else null,
            issuer = textIssuer.text,
            purpose = textPurpose.text,
            outputDirectory = selectedDir
        )
        viewModel.generateCertificates(params)
        dispose()
    }

    // 显示对话框
    fun showDialog() {
        // 初始化界面
        pack()
        setLocationRelativeTo(owner)
        isVisible = true
    }

    /**
     * 证书生成参数数据类
     */
    data class CertificateGenerationParams(
        val students: List<StudentDTO>,
        val template: CertificateTemplateDTO,
        val gpaStandard: GpaStandardDTO?,
        val issuer: String,
        val purpose: String,
        val outputDirectory: File
    )

    companion object {
        // 静态方法便于在其他地方调用
        fun show(selectedStudents: List<StudentDTO>, owner: Window? = null) {
            if (selectedStudents.isEmpty()) throw BusinessException("certificate.error.needStudents")
            val dialog = CertificateGenerateDialog(selectedStudents, owner)
            dialog.showDialog()
        }
    }
} 