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
 * 添加学生对话框
 *
 * 提供添加新学生的功能
 */
class StudentModifyDialog(
    existingStudent: StudentDTO? = null,
    owner: Window? = null,
    parent: CoroutineComponent? = null
) : JDialog(owner),
    KoinComponent, CoroutineComponent by DefaultCoroutineComponent(parent) {
    // 通过Koin获取ViewModel
    private val viewModel: StudentDataViewModel by inject()
    private val isAlreadyExists = existingStudent != null
    private val existsUUID = existingStudent?.uuid

    // 表单组件
    private val textStudentId = JTextField()
    private val textName = JTextField()
    private val comboGender = JComboBox<String>().apply {
        StudentDTO.Gender.entries.forEach {
            this.addItem(I18nUtils.getText(it.i18nKey))
        }
    }
    private val textDepartment = JTextField()
    private val textMajor = JTextField()
    private val textGrade = JTextField()
    private val textClassGroup = JTextField()
    private val comboStatus = JComboBox<String>().apply {
        StudentDTO.Status.entries.forEach {
            this.addItem(I18nUtils.getText(it.i18nKey))
        }
    }

    // 按钮
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

        // 如果存在学生，则设置学生信息
        existingStudent?.let {
            textStudentId.text = it.studentId
            textName.text = it.name
            comboGender.selectedIndex = it.gender.ordinal
            textDepartment.text = it.department
            textMajor.text = it.major
            textGrade.text = it.grade.toString()
            textClassGroup.text = it.classGroup
            comboStatus.selectedIndex = it.status.ordinal
        }
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
            I18nUtils.getText("student.dialog.placeholder.id")
        )
        textName.putClientProperty(
            FlatClientProperties.PLACEHOLDER_TEXT,
            I18nUtils.getText("student.dialog.placeholder.name")
        )
        textDepartment.putClientProperty(
            FlatClientProperties.PLACEHOLDER_TEXT,
            I18nUtils.getText("student.dialog.placeholder.department")
        )
        textMajor.putClientProperty(
            FlatClientProperties.PLACEHOLDER_TEXT,
            I18nUtils.getText("student.dialog.placeholder.major")
        )
        textClassGroup.putClientProperty(
            FlatClientProperties.PLACEHOLDER_TEXT,
            I18nUtils.getText("student.dialog.placeholder.classGroup")
        )
        textGrade.putClientProperty(
            FlatClientProperties.PLACEHOLDER_TEXT,
            I18nUtils.getText("student.dialog.placeholder.grade")
        )

        add(JLabel(I18nUtils.getText(if (isAlreadyExists) "student.dialog.modify.title" else "student.dialog.add.title")).apply {
            putClientProperty(FlatClientProperties.STYLE, "font:+6");
        }, "span 2")

        // 个人信息组标题
        val personalInfoLabel = JLabel(I18nUtils.getText("student.dialog.personal.info"))
        personalInfoLabel.putClientProperty(FlatClientProperties.STYLE, "font:bold +2;")
        add(personalInfoLabel, "gapy 10 10, span 2")

        // 学号
        add(JLabel(I18nUtils.getText("student.property.id")), "span 2")
        add(textStudentId, "gapy n 5,span 2")
        // 姓名和性别（同一行标签，下一行字段）
        add(JLabel(I18nUtils.getText("student.property.name")))
        add(JLabel(I18nUtils.getText("student.property.gender")))
        add(textName)
        add(comboGender)

        // 组织信息组标题
        val orgInfoLabel = JLabel(I18nUtils.getText("student.dialog.org.info"))
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
        val tipLabel = JLabel(I18nUtils.getText("student.dialog.tip"))
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
        val studentId = textStudentId.text
        val name = textName.text
        val genderIndex = comboGender.selectedIndex
        val department = textDepartment.text
        val major = textMajor.text
        val grade = textGrade.text.toIntOrNull()
        val classGroup = textClassGroup.text
        val statusIndex = comboStatus.selectedIndex

        // 验证表单数据
        if (studentId.isEmpty() || name.isEmpty() || department.isEmpty() || major.isEmpty() || classGroup.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                I18nUtils.getText("student.dialog.validation.required"),
                I18nUtils.getText("error.generic"),
                JOptionPane.ERROR_MESSAGE
            )
            return
        }

        if (grade == null) {
            JOptionPane.showMessageDialog(
                this,
                I18nUtils.getText("student.dialog.validation.grade.needInteger"),
                I18nUtils.getText("error.generic"),
                JOptionPane.ERROR_MESSAGE
            )
            return
        }

        // 创建学生DTO
        val student = StudentDTO(
            uuid = existsUUID,
            studentId = studentId,
            name = name,
            gender = StudentDTO.Gender.entries[genderIndex],
            department = department,
            major = major,
            grade = grade,
            classGroup = classGroup,
            status = StudentDTO.Status.entries[statusIndex],
            scores = emptyList()
        )

        // 提交到ViewModel
        scope.launch {
            try {
                if (isAlreadyExists) {
                    viewModel.updateStudent(student, false)
                } else {
                    viewModel.addStudent(student)
                }
                dispose()
            } catch (e: Exception) {
                JOptionPane.showMessageDialog(
                    this@StudentModifyDialog,
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
        fun show(existingStudent: StudentDTO? = null, owner: Window? = null) {
            val dialog = StudentModifyDialog(existingStudent, owner)
            dialog.showDialog()
        }
    }
} 