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
import java.awt.Font
import java.awt.Toolkit
import java.awt.Window
import javax.swing.*
import javax.swing.border.TitledBorder

/**
 * 高级筛选对话框
 *
 * 提供高级筛选功能，包括模糊/精准搜索、多选和范围筛选
 */
class AdvancedFilterDialog(
    owner: Window? = null,
    parent: CoroutineComponent? = null
) : JDialog(owner),
    KoinComponent, CoroutineComponent by DefaultCoroutineComponent(parent) {

    // 通过Koin获取ViewModel
    private val viewModel: StudentDataViewModel by inject()

    // 基础信息筛选组件
    private val textStudentId = JTextField()
    private val rdoStudentIdExact = JRadioButton(I18nUtils.getText("filter.exactMatch"))
    private val rdoStudentIdFuzzy = JRadioButton(I18nUtils.getText("filter.fuzzyMatch"))
    private val rdoStudentIdGreater = JRadioButton(I18nUtils.getText("filter.greaterThan"))
    private val btnGroupStudentId = ButtonGroup().apply {
        add(rdoStudentIdExact)
        add(rdoStudentIdFuzzy)
        add(rdoStudentIdGreater)
    }

    private val textName = JTextField()
    private val rdoNameExact = JRadioButton(I18nUtils.getText("filter.exactMatch"))
    private val rdoNameFuzzy = JRadioButton(I18nUtils.getText("filter.fuzzyMatch"))
    private val btnGroupName = ButtonGroup().apply {
        add(rdoNameExact)
        add(rdoNameFuzzy)
    }

    private val lstGender = JList<String>().apply {
        model = DefaultListModel<String>().apply {
            addElement("")  // 空选项
            StudentDTO.Gender.entries.forEach {
                addElement(I18nUtils.getText(it.i18nKey))
            }
        }
        selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
        selectedIndex = 0
    }

    // 组织信息筛选组件
    private val lstDepartment = JList<String>().apply {
        model = DefaultListModel<String>()
        selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
    }

    private val lstMajor = JList<String>().apply {
        model = DefaultListModel<String>()
        selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
    }

    private val textGrade = JTextField()
    private val rdoGradeExact = JRadioButton(I18nUtils.getText("filter.exactMatch"))
    private val rdoGradeGreater = JRadioButton(I18nUtils.getText("filter.greaterThan"))
    private val rdoGradeLess = JRadioButton(I18nUtils.getText("filter.lessThan"))
    private val btnGroupGrade = ButtonGroup().apply {
        add(rdoGradeExact)
        add(rdoGradeGreater)
        add(rdoGradeLess)
    }

    private val lstClassGroup = JList<String>().apply {
        model = DefaultListModel<String>()
        selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
    }

    private val lstStatus = JList<String>().apply {
        model = DefaultListModel<String>().apply {
            addElement("")  // 空选项
            StudentDTO.Status.entries.forEach {
                addElement(I18nUtils.getText(it.i18nKey))
            }
        }
        selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
        selectedIndex = 0
    }

    // 按钮
    private val buttonConfirm = JButton().apply {
        bindText(this, "button.confirm")
        font = font.deriveFont(Font.BOLD)
        putClientProperty(
            FlatClientProperties.STYLE, "" +
                    "background:$#4CAF50;" +
                    "foreground:$#FFFFFF;" +
                    "borderWidth:0;" +
                    "focusWidth:1;" +
                    "arc:10"
        )
        addActionListener {
            JOptionPane.showMessageDialog(
                this@AdvancedFilterDialog,
                I18nUtils.getText("filter.dialog.confirmed"),
                I18nUtils.getText("info.title"),
                JOptionPane.INFORMATION_MESSAGE
            )
            submitForm()
        }
    }

    private val buttonSubmit = object : JButton() {
        override fun isDefaultButton(): Boolean = true
    }.apply {
        bindText(this, "button.filter")
        putClientProperty(
            FlatClientProperties.STYLE, "" +
                    "background:$#2196F3;" +
                    "foreground:$#FFFFFF;" +
                    "borderWidth:0;" +
                    "focusWidth:1;" +
                    "arc:10"
        )
        addActionListener { submitForm() }
    }

    private val buttonCancel = JButton().apply {
        bindText(this, "button.cancel")
        putClientProperty(
            FlatClientProperties.STYLE, "" +
                    "borderWidth:1;" +
                    "focusWidth:1;" +
                    "arc:10"
        )
        addActionListener { dispose() }
    }

    // 主内容面板
    private val contentPanel = JPanel(MigLayout("wrap 1, fillx, insets 0", "[fill]"))

    init {
        initDialog()
        initComponents()
        loadSelectionData()
    }

    private fun initDialog() {
        // 设置对话框基本属性
        isModal = true
        defaultCloseOperation = DISPOSE_ON_CLOSE

        // 禁止调整窗口大小
        isResizable = true

        // 获取屏幕尺寸
        val screenSize = Toolkit.getDefaultToolkit().screenSize
        val screenHeight = screenSize.height
        val screenWidth = screenSize.width

        // 设置对话框大小为屏幕高度的80%，宽度为650像素
        val dialogWidth = 650
        val dialogHeight = (screenHeight * 0.8).toInt().coerceAtMost(700)

        // 适应屏幕大小
        preferredSize = Dimension(dialogWidth, dialogHeight)

        // 创建带滚动条的内容面板
        val scrollPane = JScrollPane(contentPanel).apply {
            border = BorderFactory.createEmptyBorder()
            verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
            horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        }

        // 设置主布局
        contentPane = JPanel(MigLayout("insets 0, fill"))
        contentPane.add(scrollPane, "grow")
    }

    private fun initComponents() {
        // 添加顶部标题和按钮面板(固定在顶部)
        val headerPanel = JPanel(MigLayout("fillx, insets 20 20 10 20", "[fill, grow]"))
        headerPanel.add(JLabel(I18nUtils.getText("filter.dialog.advanced.title")).apply {
            putClientProperty(FlatClientProperties.STYLE, "font:+6")
        }, "wrap")
        contentPanel.add(headerPanel, "growx")

        // 创建内容区域
        val mainPanel = JPanel(MigLayout("wrap 1, fillx, insets 20", "[fill]"))

        // 添加基本信息面板
        mainPanel.add(createBasicInfoPanel(), "growx")

        // 添加组织信息面板
        mainPanel.add(createOrgInfoPanel(), "growx")

        // 添加提示信息
        val tipLabel = JLabel(I18nUtils.getText("filter.dialog.advanced.tip"))
        tipLabel.putClientProperty(
            FlatClientProperties.STYLE, "" +
                    "border:8,8,8,8;" +
                    "arc:10;" +
                    "background:fade(#1a7aad,10%);"
        )
        mainPanel.add(tipLabel, "gapy 15 10")

        // 将主面板添加到内容面板
        contentPanel.add(mainPanel, "growx")

        // 添加按钮面板(固定在底部)
        val buttonPanel = createButtonPanel()
        contentPanel.add(buttonPanel, "growx")

        // 设置默认选中的单选按钮
        rdoStudentIdFuzzy.isSelected = true
        rdoNameFuzzy.isSelected = true
        rdoGradeExact.isSelected = true
    }

    /**
     * 创建按钮面板
     */
    private fun createButtonPanel(): JPanel {
        val panel = JPanel(MigLayout("fillx, insets 10 20 20 20", "[fill,grow][110][110][110]"))
        panel.add(Box.createHorizontalGlue(), "grow")
        panel.add(buttonCancel, "height 36!, width 100!")
        panel.add(buttonConfirm, "height 36!, width 100!")
        panel.add(buttonSubmit, "height 36!, width 100!")

        return panel
    }

    /**
     * 创建基本信息面板
     */
    private fun createBasicInfoPanel(): JPanel {
        val panel = JPanel(MigLayout("wrap 1, fillx, insets 10", "[fill]"))
        panel.border = BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            I18nUtils.getText("filter.dialog.basic.info"),
            TitledBorder.LEFT,
            TitledBorder.TOP
        )

        // 学号
        val studentIdPanel = JPanel(MigLayout("wrap 1, fillx, insets 0", "[fill]"))
        studentIdPanel.add(JLabel(I18nUtils.getText("student.property.id")))

        val studentIdInputPanel = JPanel(MigLayout("wrap 3, fillx, insets 0", "[fill][fill][fill]"))
        studentIdInputPanel.add(textStudentId, "span 3, growx")
        studentIdInputPanel.add(rdoStudentIdExact)
        studentIdInputPanel.add(rdoStudentIdFuzzy)
        studentIdInputPanel.add(rdoStudentIdGreater)

        studentIdPanel.add(studentIdInputPanel, "growx")
        panel.add(studentIdPanel, "growx")

        // 姓名
        val namePanel = JPanel(MigLayout("wrap 1, fillx, insets 0", "[fill]"))
        namePanel.add(JLabel(I18nUtils.getText("student.property.name")))

        val nameInputPanel = JPanel(MigLayout("wrap 2, fillx, insets 0", "[fill][fill]"))
        nameInputPanel.add(textName, "span 2, growx")
        nameInputPanel.add(rdoNameExact)
        nameInputPanel.add(rdoNameFuzzy)

        namePanel.add(nameInputPanel, "growx")
        panel.add(namePanel, "growx, gapy 10 0")

        // 性别（多选）
        val genderPanel = JPanel(MigLayout("wrap 1, fillx, insets 0", "[fill]"))
        genderPanel.add(JLabel(I18nUtils.getText("student.property.gender")))
        genderPanel.add(JScrollPane(lstGender), "height 70:70:70")
        panel.add(genderPanel, "growx, gapy 10 0")

        return panel
    }

    /**
     * 创建组织信息面板
     */
    private fun createOrgInfoPanel(): JPanel {
        val panel = JPanel(MigLayout("wrap 1, fillx, insets 10", "[fill]"))
        panel.border = BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            I18nUtils.getText("filter.dialog.org.info"),
            TitledBorder.LEFT,
            TitledBorder.TOP
        )

        // 将JList高度减小，适应小屏幕
        val listHeight = "height 60:60:60"

        // 年级 - 移到第一位
        val gradePanel = JPanel(MigLayout("wrap 1, fillx, insets 0", "[fill]"))
        gradePanel.add(JLabel(I18nUtils.getText("student.property.grade")))

        val gradeInputPanel = JPanel(MigLayout("wrap 3, fillx, insets 0", "[fill][fill][fill]"))
        gradeInputPanel.add(textGrade, "span 3, growx")
        gradeInputPanel.add(rdoGradeExact)
        gradeInputPanel.add(rdoGradeGreater)
        gradeInputPanel.add(rdoGradeLess)

        gradePanel.add(gradeInputPanel, "growx")
        panel.add(gradePanel, "growx")

        // 学院
        val departmentPanel = JPanel(MigLayout("wrap 1, fillx, insets 0", "[fill]"))
        departmentPanel.add(JLabel(I18nUtils.getText("student.property.department")))
        departmentPanel.add(JScrollPane(lstDepartment), listHeight)
        panel.add(departmentPanel, "growx, gapy 10 0")

        // 专业
        val majorPanel = JPanel(MigLayout("wrap 1, fillx, insets 0", "[fill]"))
        majorPanel.add(JLabel(I18nUtils.getText("student.property.major")))
        majorPanel.add(JScrollPane(lstMajor), listHeight)
        panel.add(majorPanel, "growx, gapy 10 0")

        // 班级
        val classGroupPanel = JPanel(MigLayout("wrap 1, fillx, insets 0", "[fill]"))
        classGroupPanel.add(JLabel(I18nUtils.getText("student.property.classGroup")))
        classGroupPanel.add(JScrollPane(lstClassGroup), listHeight)
        panel.add(classGroupPanel, "growx, gapy 10 0")

        // 状态
        val statusPanel = JPanel(MigLayout("wrap 1, fillx, insets 0", "[fill]"))
        statusPanel.add(JLabel(I18nUtils.getText("student.property.status")))
        statusPanel.add(JScrollPane(lstStatus), listHeight)
        panel.add(statusPanel, "growx, gapy 10 0")

        return panel
    }

    /**
     * 加载选择数据（如学院、专业、班级等）
     */
    private fun loadSelectionData() {
        scope.launch {
            try {
                // 获取全部学生数据
                val students = viewModel.data.value

                // 提取所有不重复的院系
                val departments = students.map { it.department }.distinct().sorted()
                (lstDepartment.model as DefaultListModel<String>).apply {
                    clear()
                    departments.forEach { addElement(it) }
                }

                // 提取所有不重复的专业
                val majors = students.map { it.major }.distinct().sorted()
                (lstMajor.model as DefaultListModel<String>).apply {
                    clear()
                    majors.forEach { addElement(it) }
                }

                // 提取所有不重复的班级
                val classGroups = students.map { it.classGroup }.distinct().sorted()
                (lstClassGroup.model as DefaultListModel<String>).apply {
                    clear()
                    classGroups.forEach { addElement(it) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun submitForm() {
        // 获取基本信息筛选条件
        val studentId = textStudentId.text.trim()
        val studentIdMatchType = when {
            rdoStudentIdExact.isSelected -> MatchType.EXACT
            rdoStudentIdFuzzy.isSelected -> MatchType.FUZZY
            else -> MatchType.GREATER_THAN
        }

        val name = textName.text.trim()
        val nameMatchType = if (rdoNameExact.isSelected) MatchType.EXACT else MatchType.FUZZY

        // 获取性别（多选）
        val genderIndices = lstGender.selectedIndices
        val genders = if (genderIndices.isEmpty() || genderIndices.contains(0)) {
            emptyList()
        } else {
            genderIndices.map { index -> StudentDTO.Gender.entries[index - 1] }
        }

        // 获取组织信息筛选条件
        val departments = lstDepartment.selectedValuesList

        val majors = lstMajor.selectedValuesList

        val grade = textGrade.text.trim().toIntOrNull()
        val gradeMatchType = when {
            rdoGradeExact.isSelected -> MatchType.EXACT
            rdoGradeGreater.isSelected -> MatchType.GREATER_THAN
            else -> MatchType.LESS_THAN
        }

        val classGroups = lstClassGroup.selectedValuesList

        val statusIndices = lstStatus.selectedIndices
        val statuses = if (statusIndices.isEmpty() || statusIndices.contains(0)) {
            emptyList()
        } else {
            statusIndices.map { index -> StudentDTO.Status.entries[index - 1] }
        }

        // 创建高级筛选条件
        val criteria = AdvancedFilterCriteria(
            studentId = studentId,
            studentIdMatchType = studentIdMatchType,
            name = name,
            nameMatchType = nameMatchType,
            genders = genders,
            departments = departments,
            majors = majors,
            grade = grade,
            gradeMatchType = gradeMatchType,
            classGroups = classGroups,
            statuses = statuses
        )

        // 提交到ViewModel
        scope.launch {
            try {
                viewModel.applyAdvancedFilter(criteria)
                dispose()
            } catch (e: Exception) {
                JOptionPane.showMessageDialog(
                    this@AdvancedFilterDialog,
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
        fun show(owner: Window? = null) {
            val dialog = AdvancedFilterDialog(owner)
            dialog.showDialog()
        }
    }
}

/**
 * 匹配类型枚举
 */
enum class MatchType {
    EXACT,        // 精确匹配
    FUZZY,        // 模糊匹配
    GREATER_THAN, // 大于匹配
    LESS_THAN     // 小于匹配
}

/**
 * 高级筛选条件数据类
 */
data class AdvancedFilterCriteria(
    val studentId: String = "",
    val studentIdMatchType: MatchType = MatchType.FUZZY,
    val name: String = "",
    val nameMatchType: MatchType = MatchType.FUZZY,
    val genders: List<StudentDTO.Gender> = emptyList(),
    val departments: List<String> = emptyList(),
    val majors: List<String> = emptyList(),
    val grade: Int? = null,
    val gradeMatchType: MatchType = MatchType.EXACT,
    val classGroups: List<String> = emptyList(),
    val statuses: List<StudentDTO.Status> = emptyList()
) 