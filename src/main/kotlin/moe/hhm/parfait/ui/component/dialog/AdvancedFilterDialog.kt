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
import java.awt.*
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

    // 性别改为复选框
    private val chkMale = JCheckBox(I18nUtils.getText("student.gender.male"))
    private val chkFemale = JCheckBox(I18nUtils.getText("student.gender.female"))
    private val chkUnknown = JCheckBox(I18nUtils.getText("student.gender.unknown"))

    // 组织信息筛选组件
    private val cboDepartment = MultiSelectionComboBox()
    private val cboMajor = MultiSelectionComboBox()

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

    // 状态改为复选框
    private val chkEnrolled = JCheckBox(I18nUtils.getText("student.status.enrolled"))
    private val chkGraduated = JCheckBox(I18nUtils.getText("student.status.graduated"))
    private val chkAbnormal = JCheckBox(I18nUtils.getText("student.status.abnormal"))

    // 按钮
    private val buttonSubmit = object : JButton() {
        override fun isDefaultButton(): Boolean = true
    }.apply {
        bindText(this, "button.filter")
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

        // 允许调整窗口大小
        isResizable = true

        // 获取屏幕尺寸
        val screenSize = Toolkit.getDefaultToolkit().screenSize
        val screenHeight = screenSize.height
        screenSize.width

        // 进一步优化宽度，确保所有内容都能显示
        val dialogWidth = 750
        // 高度适当调整，避免超出屏幕，但保证足够的显示空间
        val dialogHeight = (screenHeight * 0.85).toInt().coerceAtMost(800).coerceAtLeast(650)

        // 设置对话框大小
        size = Dimension(dialogWidth, dialogHeight)
        preferredSize = Dimension(dialogWidth, dialogHeight)

        // 创建带滚动条的内容面板，增加滚动单位
        val scrollPane = JScrollPane(contentPanel).apply {
            border = BorderFactory.createEmptyBorder()
            verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
            horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER

            // 优化滚动体验
            getVerticalScrollBar().unitIncrement = 25
            getVerticalScrollBar().blockIncrement = 100
        }

        // 设置主布局，增加内容面板的内边距
        contentPane = JPanel(MigLayout("insets 0, fill"))
        contentPane.add(scrollPane, "grow")
    }

    private fun initComponents() {
        // 添加顶部标题和按钮面板(固定在顶部)
        val headerPanel = JPanel(MigLayout("fillx, insets 15 15 5 15", "[fill, grow]"))
        headerPanel.add(JLabel(I18nUtils.getText("filter.dialog.advanced.title")).apply {
            putClientProperty(FlatClientProperties.STYLE, "font:+5")
        }, "wrap")
        contentPanel.add(headerPanel, "growx")

        // 创建内容区域，减小内边距使布局更紧凑
        val mainPanel = JPanel(MigLayout("wrap 1, fillx, insets 15", "[fill]"))

        // 添加基本信息面板
        mainPanel.add(createBasicInfoPanel(), "growx")

        // 添加组织信息面板
        mainPanel.add(createOrgInfoPanel(), "growx, gapy 5 0")

        // 添加提示信息
        val tipLabel = JLabel(I18nUtils.getText("filter.dialog.advanced.tip"))
        tipLabel.putClientProperty(
            FlatClientProperties.STYLE, "" +
                    "border:6,6,6,6;" +
                    "arc:8;" +
                    "background:fade(#1a7aad,10%);"
        )
        mainPanel.add(tipLabel, "gapy 10 5")

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
        // 增加按钮间距，减小按钮宽度，让布局更加紧凑
        val panel = JPanel(MigLayout("fillx, insets 10 20 20 20", "[fill,grow][100][100]"))
        panel.add(Box.createHorizontalGlue(), "grow")
        panel.add(buttonCancel, "height 36!, width 100!")
        panel.add(buttonSubmit, "height 36!, width 100!")

        return panel
    }

    /**
     * 创建基本信息面板
     */
    private fun createBasicInfoPanel(): JPanel {
        val panel = JPanel(MigLayout("wrap 1, fillx, insets 8", "[fill]"))
        panel.border = BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            I18nUtils.getText("filter.dialog.basic.info"),
            TitledBorder.LEFT,
            TitledBorder.TOP
        )

        // 学号和姓名放在同一行
        val idNamePanel = JPanel(MigLayout("insets 0, fillx", "[fill, 48%][grow 5][fill, 48%]"))

        // 学号区域
        val studentIdPanel = JPanel(MigLayout("wrap 1, fillx, insets 0", "[fill]"))
        studentIdPanel.add(JLabel(I18nUtils.getText("student.property.id")))

        val studentIdInputPanel = JPanel(MigLayout("wrap 3, fillx, insets 0", "[fill][fill][fill]"))
        studentIdInputPanel.add(textStudentId, "span 3, growx")
        studentIdInputPanel.add(rdoStudentIdExact)
        studentIdInputPanel.add(rdoStudentIdFuzzy)
        studentIdInputPanel.add(rdoStudentIdGreater)

        studentIdPanel.add(studentIdInputPanel, "growx")
        idNamePanel.add(studentIdPanel, "growx")

        // 中间间隔
        idNamePanel.add(Box.createHorizontalStrut(8), "")

        // 姓名区域
        val namePanel = JPanel(MigLayout("wrap 1, fillx, insets 0", "[fill]"))
        namePanel.add(JLabel(I18nUtils.getText("student.property.name")))

        val nameInputPanel = JPanel(MigLayout("wrap 2, fillx, insets 0", "[fill][fill]"))
        nameInputPanel.add(textName, "span 2, growx")
        nameInputPanel.add(rdoNameExact)
        nameInputPanel.add(rdoNameFuzzy)

        namePanel.add(nameInputPanel, "growx")
        idNamePanel.add(namePanel, "growx")

        panel.add(idNamePanel, "growx")

        // 性别（标签和选项在同一行）
        val genderPanel = JPanel(MigLayout("fillx, insets 0", "[80!][fill][fill][fill]"))
        genderPanel.add(JLabel(I18nUtils.getText("student.property.gender")), "gapright 10")
        genderPanel.add(chkMale, "sg gender")
        genderPanel.add(chkFemale, "sg gender, gapx 5 5")
        genderPanel.add(chkUnknown, "sg gender")

        panel.add(genderPanel, "growx, gapy 10 0")

        return panel
    }

    /**
     * 创建组织信息面板
     */
    private fun createOrgInfoPanel(): JPanel {
        val panel = JPanel(MigLayout("wrap 1, fillx, insets 8", "[fill]"))
        panel.border = BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            I18nUtils.getText("filter.dialog.org.info"),
            TitledBorder.LEFT,
            TitledBorder.TOP
        )

        // 第一行：状态和年级放在同一行
        val statusGradePanel = JPanel(MigLayout("insets 0, fillx", "[fill, 48%][grow 5][fill, 48%]"))

        // 状态区域（多选按钮）
        val statusPanel = JPanel(MigLayout("fillx, insets 0", "[80!][fill][fill][fill]"))
        statusPanel.add(JLabel(I18nUtils.getText("student.property.status")), "gapright 10")
        statusPanel.add(chkEnrolled, "sg status")
        statusPanel.add(chkGraduated, "sg status, gapx 5 5")
        statusPanel.add(chkAbnormal, "sg status")
        statusGradePanel.add(statusPanel, "growx")

        // 中间间隔
        statusGradePanel.add(Box.createHorizontalStrut(8), "")

        // 年级区域
        val gradePanel = JPanel(MigLayout("wrap 1, fillx, insets 0", "[fill]"))
        gradePanel.add(JLabel(I18nUtils.getText("student.property.grade")))

        val gradeInputPanel = JPanel(MigLayout("wrap 3, fillx, insets 0", "[fill][fill][fill]"))
        gradeInputPanel.add(textGrade, "span 3, growx")
        gradeInputPanel.add(rdoGradeExact)
        gradeInputPanel.add(rdoGradeGreater)
        gradeInputPanel.add(rdoGradeLess)

        gradePanel.add(gradeInputPanel, "growx")
        statusGradePanel.add(gradePanel, "growx")

        panel.add(statusGradePanel, "growx")

        // 第二行：学院和专业放在同一行（改为下拉框）
        val deptMajorPanel = JPanel(MigLayout("insets 0, fillx", "[fill, 48%][grow 5][fill, 48%]"))

        // 学院区域
        val deptPanel = JPanel(MigLayout("wrap 1, fillx, insets 0", "[fill]"))
        deptPanel.add(JLabel(I18nUtils.getText("student.property.department")))
        deptPanel.add(cboDepartment, "growx, h 30!")
        deptMajorPanel.add(deptPanel, "growx")

        // 中间间隔
        deptMajorPanel.add(Box.createHorizontalStrut(8), "")

        // 专业区域
        val majorPanel = JPanel(MigLayout("wrap 1, fillx, insets 0", "[fill]"))
        majorPanel.add(JLabel(I18nUtils.getText("student.property.major")))
        majorPanel.add(cboMajor, "growx, h 30!")
        deptMajorPanel.add(majorPanel, "growx")

        panel.add(deptMajorPanel, "growx, gapy 10 0")

        // 第三行：班级（增大显示区域为1.5倍）
        val classGroupPanel = JPanel(MigLayout("wrap 1, fillx, insets 0", "[fill]"))
        classGroupPanel.add(JLabel(I18nUtils.getText("student.property.classGroup")))
        classGroupPanel.add(JScrollPane(lstClassGroup), "height 100:120:150") // 增加班级列表的高度
        panel.add(classGroupPanel, "growx, gapy 10 0")

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
                if (departments.isEmpty()) {
                    cboDepartment.setItems(listOf("-- 无可用数据 --"))
                } else {
                    cboDepartment.setItems(departments)
                }

                // 提取所有不重复的专业
                val majors = students.map { it.major }.distinct().sorted()
                if (majors.isEmpty()) {
                    cboMajor.setItems(listOf("-- 无可用数据 --"))
                } else {
                    cboMajor.setItems(majors)
                }

                // 提取所有不重复的班级
                val classGroups = students.map { it.classGroup }.distinct().sorted()
                (lstClassGroup.model as DefaultListModel<String>).apply {
                    clear()
                    // 如果为空，添加提示项
                    if (classGroups.isEmpty()) {
                        addElement("-- 无可用数据 --")
                    } else {
                        classGroups.forEach { addElement(it) }
                    }
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

        // 获取性别（多选按钮）
        val genders = mutableListOf<StudentDTO.Gender>()
        if (chkMale.isSelected) genders.add(StudentDTO.Gender.MALE)
        if (chkFemale.isSelected) genders.add(StudentDTO.Gender.FEMALE)
        if (chkUnknown.isSelected) genders.add(StudentDTO.Gender.UNKNOWN)

        // 获取组织信息筛选条件
        val departments = cboDepartment.getSelectedItems()

        val majors = cboMajor.getSelectedItems()

        val grade = textGrade.text.trim().toIntOrNull()
        val gradeMatchType = when {
            rdoGradeExact.isSelected -> MatchType.EXACT
            rdoGradeGreater.isSelected -> MatchType.GREATER_THAN
            else -> MatchType.LESS_THAN
        }

        val classGroups = lstClassGroup.selectedValuesList

        // 获取状态（多选按钮）
        val statuses = mutableListOf<StudentDTO.Status>()
        if (chkEnrolled.isSelected) statuses.add(StudentDTO.Status.ENROLLED)
        if (chkGraduated.isSelected) statuses.add(StudentDTO.Status.GRADUATED)
        if (chkAbnormal.isSelected) statuses.add(StudentDTO.Status.ABNORMAL)

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
                    I18nUtils.getText("error.generic.title"),
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

        // 确保对话框不会被缩得太小
        minimumSize = Dimension(750, 650)

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

/**
 * 多选组合框
 *
 * 支持多选的下拉组合框组件
 */
class MultiSelectionComboBox : JPanel() {
    private val selection = mutableSetOf<String>()
    private val checkBoxes = mutableMapOf<String, JCheckBox>()
    private val items = mutableListOf<String>()
    private val popupMenu = JPopupMenu()
    private val selectionPanel = JPanel()
    private val displayLabel = JLabel("请选择")
    private val dropDownButton = JButton("▼")

    init {
        layout = BorderLayout(5, 0)

        // 设置显示标签
        displayLabel.border = BorderFactory.createEmptyBorder(0, 5, 0, 0)
        add(displayLabel, BorderLayout.CENTER)

        // 设置下拉按钮
        dropDownButton.preferredSize = Dimension(25, 25)
        dropDownButton.isFocusPainted = false
        dropDownButton.margin = Insets(0, 0, 0, 0)
        add(dropDownButton, BorderLayout.EAST)

        // 设置下拉面板
        selectionPanel.layout = BoxLayout(selectionPanel, BoxLayout.Y_AXIS)
        JScrollPane(selectionPanel).let {
            it.preferredSize = Dimension(250, 200)
            popupMenu.add(it)
        }

        // 设置点击事件
        dropDownButton.addActionListener {
            if (popupMenu.isVisible) {
                popupMenu.isVisible = false
            } else {
                // 只有在组件显示时才显示弹出菜单
                if (isShowing) {
                    popupMenu.show(this, 0, height)
                }
            }
        }

        // 设置边框和最小尺寸
        border = BorderFactory.createLineBorder(Color.LIGHT_GRAY)
        preferredSize = Dimension(200, 30)
        minimumSize = Dimension(100, 30)
    }

    /**
     * 设置可选项列表
     */
    fun setItems(newItems: List<String>) {
        items.clear()
        checkBoxes.clear()
        selection.clear()
        selectionPanel.removeAll()
        items.addAll(newItems)

        for (item in items) {
            val checkBox = JCheckBox(item)
            checkBox.addActionListener {
                if (checkBox.isSelected) {
                    selection.add(item)
                } else {
                    selection.remove(item)
                }
                updateDisplayText()
            }
            checkBoxes[item] = checkBox
            selectionPanel.add(checkBox)
        }

        // 添加全选/取消全选选项
        if (items.size > 1) {
            val selectAllCheckBox = JCheckBox("全选/取消全选")
            selectAllCheckBox.addActionListener {
                val selectAll = selectAllCheckBox.isSelected
                checkBoxes.values.forEach { it.isSelected = selectAll }
                if (selectAll) {
                    selection.addAll(items)
                } else {
                    selection.clear()
                }
                updateDisplayText()
            }
            selectionPanel.add(Box.createVerticalStrut(5))
            selectionPanel.add(JSeparator())
            selectionPanel.add(selectAllCheckBox)
        }

        updateDisplayText()
    }

    /**
     * 获取选中的项列表
     */
    fun getSelectedItems(): List<String> {
        return selection.toList()
    }

    /**
     * 更新显示文本
     */
    private fun updateDisplayText() {
        val text = when {
            selection.isEmpty() -> "请选择"
            selection.size == 1 -> selection.first()
            selection.size == items.size -> "已全选"
            else -> "已选择 ${selection.size} 项"
        }
        displayLabel.text = text
    }
} 