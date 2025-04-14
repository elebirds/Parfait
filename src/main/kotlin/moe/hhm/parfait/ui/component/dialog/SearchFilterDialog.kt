/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.component.dialog

import com.formdev.flatlaf.FlatClientProperties
import kotlinx.coroutines.launch
import moe.hhm.parfait.dto.StudentDTO
import moe.hhm.parfait.infra.i18n.I18nUtils
import moe.hhm.parfait.infra.i18n.I18nUtils.bindText
import moe.hhm.parfait.ui.base.CoroutineComponent
import moe.hhm.parfait.ui.base.DefaultCoroutineComponent
import moe.hhm.parfait.ui.viewmodel.StudentDataViewModel
import net.miginfocom.swing.MigLayout
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.awt.Dimension
import java.awt.Window
import javax.swing.*

/**
 * 搜索和筛选对话框
 *
 * 提供搜索学生和筛选学生列表的功能
 */
class SearchFilterDialog(
    owner: Window? = null,
    parent: CoroutineComponent? = null,
    private val isFilter: Boolean = false // true表示筛选模式，false表示搜索模式
) : JDialog(owner),
    KoinComponent, CoroutineComponent by DefaultCoroutineComponent(parent) {

    // 通过Koin获取ViewModel
    private val viewModel: StudentDataViewModel by inject()

    // 表单组件
    private val textStudentId = JTextField()
    private val textName = JTextField()
    private val comboGender = JComboBox<String>().apply {
        addItem("") // 空选项表示不筛选
        StudentDTO.Gender.entries.forEach {
            this.addItem(I18nUtils.getText(it.i18nKey))
        }
    }
    private val textDepartment = JTextField()
    private val textMajor = JTextField()
    private val textGrade = JTextField()
    private val textClassGroup = JTextField()
    private val comboStatus = JComboBox<String>().apply {
        addItem("") // 空选项表示不筛选
        StudentDTO.Status.entries.forEach {
            this.addItem(I18nUtils.getText(it.i18nKey))
        }
    }

    // 按钮
    private val buttonSubmit = object : JButton() {
        override fun isDefaultButton(): Boolean = true
    }.apply {
        bindText(this, if (isFilter) "button.filter" else "button.search")
        addActionListener { submitForm() }
    }

    private val buttonCancel = JButton().apply {
        bindText(this, "button.cancel")
        addActionListener { dispose() }
    }

    init {
        initDialog()
        initComponents()
    }

    private fun initDialog() {
        // 设置对话框基本属性
        isModal = true
        defaultCloseOperation = DISPOSE_ON_CLOSE

        // 禁止调整窗口大小
        isResizable = false

        // 固定大小
        preferredSize = Dimension(550, 620)
        minimumSize = Dimension(550, 620)
        maximumSize = Dimension(550, 620)

        // 使用单一布局面板
        contentPane = JPanel(MigLayout("wrap 2, fillx, insets n 35 n 35", "[fill, 200]"))
    }

    private fun initComponents() {
        // 设置输入框提示文本
        textStudentId.putClientProperty(
            FlatClientProperties.PLACEHOLDER_TEXT,
            I18nUtils.getText("search.dialog.placeholder.id")
        )
        textName.putClientProperty(
            FlatClientProperties.PLACEHOLDER_TEXT,
            I18nUtils.getText("search.dialog.placeholder.name")
        )
        textDepartment.putClientProperty(
            FlatClientProperties.PLACEHOLDER_TEXT,
            I18nUtils.getText("search.dialog.placeholder.department")
        )
        textMajor.putClientProperty(
            FlatClientProperties.PLACEHOLDER_TEXT,
            I18nUtils.getText("search.dialog.placeholder.major")
        )
        textClassGroup.putClientProperty(
            FlatClientProperties.PLACEHOLDER_TEXT,
            I18nUtils.getText("search.dialog.placeholder.classGroup")
        )
        textGrade.putClientProperty(
            FlatClientProperties.PLACEHOLDER_TEXT,
            I18nUtils.getText("search.dialog.placeholder.grade")
        )

        // 设置标题
        add(JLabel(I18nUtils.getText(if (isFilter) "search.dialog.filter.title" else "search.dialog.search.title")).apply {
            putClientProperty(FlatClientProperties.STYLE, "font:+6")
        }, "span 2")

        // 基本信息组标题
        val basicInfoLabel = JLabel(I18nUtils.getText("search.dialog.basic.info"))
        basicInfoLabel.putClientProperty(FlatClientProperties.STYLE, "font:bold +2;")
        add(basicInfoLabel, "gapy 10 10, span 2")

        // 学号
        add(JLabel(I18nUtils.getText("student.property.id")), "span 2")
        add(textStudentId, "gapy n 5,span 2")

        // 姓名和性别（同一行标签，下一行字段）
        add(JLabel(I18nUtils.getText("student.property.name")))
        add(JLabel(I18nUtils.getText("student.property.gender")))
        add(textName)
        add(comboGender)

        // 组织信息组标题
        val orgInfoLabel = JLabel(I18nUtils.getText("search.dialog.org.info"))
        orgInfoLabel.putClientProperty(FlatClientProperties.STYLE, "font:bold +2;")
        add(orgInfoLabel, "gapy 10 10, span 2")

        // 学院和年级（同一行标签，下一行字段）
        add(JLabel(I18nUtils.getText("student.property.department")))
        add(JLabel(I18nUtils.getText("student.property.grade")))
        add(textDepartment)
        add(textGrade)

        // 专业（单独一行）
        add(JLabel(I18nUtils.getText("student.property.major")), "span 2")
        add(textMajor, "gapy n 5,span 2")

        // 班级和状态（同一行标签，下一行字段）
        add(JLabel(I18nUtils.getText("student.property.classGroup")))
        add(JLabel(I18nUtils.getText("student.property.status")))
        add(textClassGroup)
        add(comboStatus)

        // 添加提示信息
        val tipLabel =
            JLabel(I18nUtils.getText(if (isFilter) "search.dialog.filter.tip" else "search.dialog.search.tip"))
        tipLabel.putClientProperty(
            FlatClientProperties.STYLE, "" +
                    "border:8,8,8,8;" +
                    "arc:10;" +
                    "background:fade(#1a7aad,10%);"
        )
        add(tipLabel, "gapy 15 10, span 2")

        // 按钮
        add(buttonCancel, "gapy 10, grow 0")
        add(buttonSubmit, "grow 0, al trailing")
    }

    private fun submitForm() {
        // 获取表单数据
        val criteria = SearchFilterCriteria(
            studentId = textStudentId.text.trim(),
            name = textName.text.trim(),
            gender = if (comboGender.selectedIndex <= 0) null else StudentDTO.Gender.entries[comboGender.selectedIndex - 1],
            department = textDepartment.text.trim(),
            major = textMajor.text.trim(),
            grade = textGrade.text.trim().toIntOrNull(),
            classGroup = textClassGroup.text.trim(),
            status = if (comboStatus.selectedIndex <= 0) null else StudentDTO.Status.entries[comboStatus.selectedIndex - 1]
        )

        // 提交到ViewModel
        scope.launch {
            try {
                if (isFilter) {
                    viewModel.applyFilter(criteria)
                } else {
                    viewModel.search(criteria)
                }
                dispose()
            } catch (e: Exception) {
                JOptionPane.showMessageDialog(
                    this@SearchFilterDialog,
                    e.message,
                    I18nUtils.getText("error.generic"),
                    JOptionPane.ERROR_MESSAGE
                )
            }
        }
    }

    // 显示对话框
    fun showDialog() {
        // 初始化界面
        pack()
        setLocationRelativeTo(owner)
        isVisible = true
    }

    companion object {
        // 静态方法便于在其他地方调用
        fun showSearch(owner: Window? = null) {
            val dialog = SearchFilterDialog(owner, isFilter = false)
            dialog.showDialog()
        }

        fun showFilter(owner: Window? = null) {
            val dialog = SearchFilterDialog(owner, isFilter = true)
            dialog.showDialog()
        }
    }
}

/**
 * 搜索筛选条件数据类
 */
data class SearchFilterCriteria(
    val studentId: String = "",
    val name: String = "",
    val gender: StudentDTO.Gender? = null,
    val department: String = "",
    val major: String = "",
    val grade: Int? = null,
    val classGroup: String = "",
    val status: StudentDTO.Status? = null
) 