/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.component.dialog

import com.formdev.flatlaf.FlatClientProperties
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import moe.hhm.parfait.dto.CertificateContentType
import moe.hhm.parfait.dto.CertificateTemplateDTO
import moe.hhm.parfait.infra.i18n.I18nUtils
import moe.hhm.parfait.infra.i18n.I18nUtils.bindText
import moe.hhm.parfait.infra.i18n.I18nUtils.createButton
import moe.hhm.parfait.infra.i18n.I18nUtils.createLabel
import moe.hhm.parfait.ui.base.CoroutineComponent
import moe.hhm.parfait.ui.base.DefaultCoroutineComponent
import moe.hhm.parfait.ui.viewmodel.CertificateTemplateViewModel
import net.miginfocom.swing.MigLayout
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.awt.CardLayout
import java.awt.Dimension
import java.awt.Window
import javax.swing.*
import javax.swing.filechooser.FileNameExtensionFilter

/**
 * 证明模板添加与修改对话框
 */
class CertificateTemplateDialog(
    private val existingTemplate: CertificateTemplateDTO? = null,
    owner: Window? = null,
    parent: CoroutineComponent? = null
) : JDialog(owner),
    KoinComponent, CoroutineComponent by DefaultCoroutineComponent(parent) {

    // 通过Koin获取ViewModel
    private val viewModel: CertificateTemplateViewModel by inject()
    private val isAlreadyExists = existingTemplate != null
    private val existsUUID = existingTemplate?.uuid

    // 表单组件 - 基础信息
    private val textName = JTextField()
    private val textCategory = JTextField()
    private val textAreaDescription = JTextArea(3, 20).apply {
        lineWrap = true
        wrapStyleWord = true
    }
    private val textPriority = JSpinner(SpinnerNumberModel(50, 1, 100, 1))

    // 内容组件
    private val radioJar = JRadioButton(I18nUtils.getText("certificate.dialog.contentType.jar"))
    private val radioFile = JRadioButton(I18nUtils.getText("certificate.dialog.contentType.file"))
    private val radioDB = JRadioButton(I18nUtils.getText("certificate.dialog.contentType.db"))
    private val resourceTypeGroup = ButtonGroup().apply {
        add(radioJar)
        add(radioFile)
        add(radioDB)
    }

    // JAR资源下拉框
    private val comboJarResources = JComboBox<String>()
    private val jarResourcesPanel = JPanel(MigLayout("insets 0", "[grow, fill]", "[]"))

    // 文件选择组件
    private val textFilePath = JTextField().apply { isEditable = false }
    private val buttonSelectFile = createButton("certificate.dialog.selectFile")
    private val filePanel = JPanel(MigLayout("insets 0", "[grow, fill][]", "[]"))

    // 数据库文件选择组件
    private val textDBFilePath = JTextField().apply { isEditable = false }
    private val buttonSelectDBFile = createButton("certificate.dialog.selectFile")
    private val dbPanel = JPanel(MigLayout("insets 0", "[grow, fill][]", "[]"))

    // 所有内容面板的容器
    private val contentSwitchPanel = JPanel(CardLayout())

    // 当前选择的内容路径
    private var selectedContentPath: String = ""
    private var contentType: CertificateContentType = CertificateContentType.JAR_RESOURCE

    // 对话框按钮
    private val buttonSubmit = object : JButton() {
        override fun isDefaultButton(): Boolean = true
    }.apply {
        bindText(this, "button.ok")
        addActionListener { submitForm() }
    }
    private val buttonCancel = JButton().apply {
        bindText(this, "button.cancel")
        addActionListener { dispose() }
    }

    init {
        initDialog()
        initComponents()
        setupListeners()
        setupContentPanels()
        loadJarResources()

        // 如果存在证明模板，则设置信息
        if (existingTemplate != null) {
            textName.text = existingTemplate.name
            textCategory.text = existingTemplate.category
            textAreaDescription.text = existingTemplate.description
            textPriority.value = existingTemplate.priority

            // 设置内容路径和类型
            setContentPathAndType(existingTemplate.contentPath)
        } else {
            // 默认选择JAR资源
            radioJar.isSelected = true
            updateContentPanel()
        }
    }

    private fun initDialog() {
        // 设置对话框基本属性
        title =
            I18nUtils.getText(if (isAlreadyExists) "certificate.dialog.modify.title" else "certificate.dialog.add.title")
        isModal = true
        defaultCloseOperation = DISPOSE_ON_CLOSE

        // 禁止调整窗口大小
        isResizable = false

        // 固定大小
        preferredSize = Dimension(800, 700)
        minimumSize = Dimension(800, 700)
        maximumSize = Dimension(800, 700)

        // 使用单一布局面板
        contentPane = JPanel(MigLayout("wrap 2, fillx, insets n 25 n 25", "[fill, 220][fill]"))
    }

    private fun initComponents() {
        // 设置输入框提示文本
        textName.putClientProperty(
            FlatClientProperties.PLACEHOLDER_TEXT,
            I18nUtils.getText("certificate.dialog.placeholder.name")
        )
        textCategory.putClientProperty(
            FlatClientProperties.PLACEHOLDER_TEXT,
            I18nUtils.getText("certificate.dialog.placeholder.category")
        )
        textAreaDescription.putClientProperty(
            FlatClientProperties.PLACEHOLDER_TEXT,
            I18nUtils.getText("certificate.dialog.placeholder.description")
        )

        add(JLabel(I18nUtils.getText(if (isAlreadyExists) "certificate.dialog.modify.title" else "certificate.dialog.add.title")).apply {
            putClientProperty(FlatClientProperties.STYLE, "font:+6")
        }, "span 2")

        // 基础信息组标题
        val basicInfoLabel = createLabel("certificate.dialog.info.basic")
        basicInfoLabel.putClientProperty(FlatClientProperties.STYLE, "font:bold +2;")
        add(basicInfoLabel, "gapy 10 10, span 2")

        // 名称（单独一行）
        add(createLabel("certificate.property.name"), "span 2")
        add(textName, "gapy n 5,span 2")

        // 分类和优先级（同一行）
        add(createLabel("certificate.property.category"))
        add(createLabel("certificate.property.priority"))
        add(textCategory)
        add(textPriority)

        // 描述（单独一行）
        add(createLabel("certificate.property.description"), "span 2")
        add(JScrollPane(textAreaDescription), "span 2, gapy n 5, height 80:80:80")

        // 内容信息组标题
        val contentLabel = createLabel("certificate.dialog.info.content")
        contentLabel.putClientProperty(FlatClientProperties.STYLE, "font:bold +2;")
        add(contentLabel, "gapy 15 10, span 2")

        // 内容类型选择
        val typePanel = JPanel(MigLayout("insets 0", "[grow][grow][grow]", "[]"))
        typePanel.add(radioJar, "grow")
        typePanel.add(radioFile, "grow")
        typePanel.add(radioDB, "grow")
        add(typePanel, "span 2")

        // 内容面板
        add(contentSwitchPanel, "span 2, grow, height 80:80:80")

        // 添加提示信息
        val tipLabel = createLabel("certificate.dialog.tip")
        tipLabel.putClientProperty(
            FlatClientProperties.STYLE, "" +
                    "border:8,8,8,8;" +
                    "arc:10;" +
                    "background:fade(#1a7aad,10%);"
        )
        add(tipLabel, "gapy 15 10, span 2")

        // 对话框按钮
        add(buttonCancel, "gapy 10, grow 0")
        add(buttonSubmit, "grow 0, al trailing")
    }

    private fun setupListeners() {
        // 内容类型切换监听
        radioJar.addActionListener { updateContentPanel() }
        radioFile.addActionListener { updateContentPanel() }
        radioDB.addActionListener { updateContentPanel() }

        // 文件选择按钮
        buttonSelectFile.addActionListener { selectLocalFile() }
        buttonSelectDBFile.addActionListener { selectDatabaseFile() }

        // JAR资源下拉框
        comboJarResources.addActionListener {
            if (comboJarResources.selectedItem != null) {
                selectedContentPath = "jar::${comboJarResources.selectedItem}"
                contentType = CertificateContentType.JAR_RESOURCE
            }
        }
    }

    private fun setupContentPanels() {
        // JAR资源面板
        jarResourcesPanel.add(comboJarResources, "growx")

        // 本地文件面板
        filePanel.add(textFilePath, "growx")
        filePanel.add(buttonSelectFile)

        // 数据库文件面板
        dbPanel.add(textDBFilePath, "growx")
        dbPanel.add(buttonSelectDBFile)

        // 将面板添加到切换容器
        contentSwitchPanel.add(jarResourcesPanel, CertificateContentType.JAR_RESOURCE.name)
        contentSwitchPanel.add(filePanel, CertificateContentType.LOCAL_FILE.name)
        contentSwitchPanel.add(dbPanel, CertificateContentType.DATABASE.name)
    }

    private fun loadJarResources() {
        // 加载JAR资源列表
        scope.launch {
            viewModel.jarResources.collectLatest { resources ->
                comboJarResources.removeAllItems()
                resources.forEach { comboJarResources.addItem(it) }
                if (resources.isNotEmpty()) {
                    comboJarResources.selectedIndex = 0
                    selectedContentPath = "jar::${resources[0]}"
                }
            }
        }
    }

    private fun updateContentPanel() {
        val layout = contentSwitchPanel.layout as CardLayout
        when {
            radioJar.isSelected -> {
                layout.show(contentSwitchPanel, CertificateContentType.JAR_RESOURCE.name)
                contentType = CertificateContentType.JAR_RESOURCE
                if (comboJarResources.selectedItem != null) {
                    selectedContentPath = "jar::${comboJarResources.selectedItem}"
                }
            }

            radioFile.isSelected -> {
                layout.show(contentSwitchPanel, CertificateContentType.LOCAL_FILE.name)
                contentType = CertificateContentType.LOCAL_FILE
                selectedContentPath = textFilePath.text
            }

            radioDB.isSelected -> {
                layout.show(contentSwitchPanel, CertificateContentType.DATABASE.name)
                contentType = CertificateContentType.DATABASE
                selectedContentPath = textDBFilePath.text
            }
        }
    }

    private fun setContentPathAndType(contentPath: String) {
        when {
            contentPath.startsWith("jar::") -> {
                radioJar.isSelected = true
                val resourceName = contentPath.substringAfter("jar::")
                for (i in 0 until comboJarResources.itemCount) {
                    if (comboJarResources.getItemAt(i) == resourceName) {
                        comboJarResources.selectedIndex = i
                        break
                    }
                }
                contentType = CertificateContentType.JAR_RESOURCE
                selectedContentPath = contentPath
            }

            contentPath.startsWith("db::") -> {
                radioDB.isSelected = true
                textDBFilePath.text = contentPath
                contentType = CertificateContentType.DATABASE
                selectedContentPath = contentPath
            }

            else -> {
                radioFile.isSelected = true
                textFilePath.text = contentPath
                contentType = CertificateContentType.LOCAL_FILE
                selectedContentPath = contentPath
            }
        }
        updateContentPanel()
    }

    private fun selectLocalFile() {
        val fileChooser = JFileChooser().apply {
            fileFilter = FileNameExtensionFilter("Word Documents", "docx", "doc")
            fileSelectionMode = JFileChooser.FILES_ONLY
        }

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            val selectedFile = fileChooser.selectedFile
            textFilePath.text = selectedFile.absolutePath
            selectedContentPath = selectedFile.absolutePath
            contentType = CertificateContentType.LOCAL_FILE
        }
    }

    private fun selectDatabaseFile() {
        val fileChooser = JFileChooser().apply {
            fileFilter = FileNameExtensionFilter("Word Documents", "docx")
            fileSelectionMode = JFileChooser.FILES_ONLY
        }

        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return
        val selectedFile = fileChooser.selectedFile
        // 处理上传到数据库的逻辑
        scope.launch {
            try {
                // 显示进度对话框
                setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR))

                // 上传文件到数据库，不再传递templateUuid
                val dbPathResult = viewModel.uploadFileToDb(selectedFile)

                // 更新界面
                textDBFilePath.text = dbPathResult
                selectedContentPath = dbPathResult
                contentType = CertificateContentType.DATABASE
            } catch (e: Exception) {
                JOptionPane.showMessageDialog(
                    this@CertificateTemplateDialog,
                    e.message,
                    I18nUtils.getText("error.generic.title"),
                    JOptionPane.ERROR_MESSAGE
                )
            } finally {
                setCursor(java.awt.Cursor.getDefaultCursor())
            }
        }
    }

    private fun submitForm() {
        // 获取表单数据
        val name = textName.text
        val category = textCategory.text
        val description = textAreaDescription.text
        val priority = textPriority.value as Int

        // 验证表单数据
        if (name.isEmpty() || category.isEmpty() || description.isEmpty() || selectedContentPath.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                I18nUtils.getText("certificate.dialog.validation.required"),
                I18nUtils.getText("error.generic.title"),
                JOptionPane.ERROR_MESSAGE
            )
            return
        }

        // 创建证明模板DTO
        val templateDTO = CertificateTemplateDTO(
            uuid = existsUUID,
            name = name,
            category = category,
            description = description,
            contentPath = selectedContentPath,
            isLike = existingTemplate?.isLike == true,
            isActive = existingTemplate?.isActive != false,
            priority = priority
        )

        val beforePath = existingTemplate?.contentPath
        if (isAlreadyExists) {
            if (beforePath != null && beforePath != selectedContentPath) {
                viewModel.updateTemplateAndPath(templateDTO, beforePath)
            } else viewModel.updateTemplate(templateDTO)
        } else {
            viewModel.addTemplate(templateDTO)
        }
        dispose()
    }

    companion object {
        /**
         * 显示添加或修改证明模板对话框
         * @param existingTemplate 现有的证明模板（传null表示添加新模板）
         * @param owner 父窗口
         */
        fun show(existingTemplate: CertificateTemplateDTO? = null, owner: Window? = null) {
            val dialog = CertificateTemplateDialog(existingTemplate, owner)
            dialog.pack()
            dialog.setLocationRelativeTo(owner)
            dialog.isVisible = true
        }
    }
} 