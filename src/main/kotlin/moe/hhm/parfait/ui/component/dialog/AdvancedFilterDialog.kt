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

    // 性别改为复选框
    private val chkMale = JCheckBox(I18nUtils.getText("student.gender.male"))
    private val chkFemale = JCheckBox(I18nUtils.getText("student.gender.female"))
    private val chkUnknown = JCheckBox(I18nUtils.getText("student.gender.unknown"))

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

        // 允许调整窗口大小
        isResizable = true

        // 获取屏幕尺寸
        val screenSize = Toolkit.getDefaultToolkit().screenSize
        val screenHeight = screenSize.height
        val screenWidth = screenSize.width

        // 进一步优化宽度，确保所有内容都能显示
        val dialogWidth = 750
        // 高度适当调整，避免超出屏幕，但保证足够的显示空间
        val dialogHeight = (screenHeight * 0.65).toInt().coerceAtMost(620).coerceAtLeast(500)

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

        // 将JList高度减小，适应小屏幕
        val listHeight = "height 45:45:45"

        // 年级和学院放在同一行
        val gradeDeptPanel = JPanel(MigLayout("insets 0, fillx", "[fill, 48%][grow 5][fill, 48%]"))
        
        // 年级区域
        val gradePanel = JPanel(MigLayout("wrap 1, fillx, insets 0", "[fill]"))
        gradePanel.add(JLabel(I18nUtils.getText("student.property.grade")))
        
        val gradeInputPanel = JPanel(MigLayout("wrap 3, fillx, insets 0", "[fill][fill][fill]"))
        gradeInputPanel.add(textGrade, "span 3, growx")
        gradeInputPanel.add(rdoGradeExact)
        gradeInputPanel.add(rdoGradeGreater)
        gradeInputPanel.add(rdoGradeLess)
        
        gradePanel.add(gradeInputPanel, "growx")
        gradeDeptPanel.add(gradePanel, "growx")
        
        // 中间间隔
        gradeDeptPanel.add(Box.createHorizontalStrut(8), "")
        
        // 学院区域
        val deptPanel = JPanel(MigLayout("wrap 1, fillx, insets 0", "[fill]"))
        deptPanel.add(JLabel(I18nUtils.getText("student.property.department")))
        deptPanel.add(JScrollPane(lstDepartment), listHeight)
        gradeDeptPanel.add(deptPanel, "growx")
        
        panel.add(gradeDeptPanel, "growx")

        // 专业和班级放在同一行
        val majorClassPanel = JPanel(MigLayout("insets 0, fillx", "[fill, 48%][grow 5][fill, 48%]"))
        
        // 专业区域
        val majorPanel = JPanel(MigLayout("wrap 1, fillx, insets 0", "[fill]"))
        majorPanel.add(JLabel(I18nUtils.getText("student.property.major")))
        majorPanel.add(JScrollPane(lstMajor), listHeight)
        majorClassPanel.add(majorPanel, "growx")
        
        // 中间间隔
        majorClassPanel.add(Box.createHorizontalStrut(8), "")
        
        // 班级区域
        val classGroupPanel = JPanel(MigLayout("wrap 1, fillx, insets 0", "[fill]"))
        classGroupPanel.add(JLabel(I18nUtils.getText("student.property.classGroup")))
        classGroupPanel.add(JScrollPane(lstClassGroup), listHeight)
        majorClassPanel.add(classGroupPanel, "growx")
        
        panel.add(majorClassPanel, "growx, gapy 5 0")

        // 状态
        val statusPanel = JPanel(MigLayout("wrap 1, fillx, insets 0", "[fill]"))
        statusPanel.add(JLabel(I18nUtils.getText("student.property.status")))
        statusPanel.add(JScrollPane(lstStatus), listHeight)
        panel.add(statusPanel, "growx, gapy 5 0")

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
                    // 如果为空，添加一个提示项
                    if (departments.isEmpty()) {
                        addElement("-- 无可用数据 --")
                    } else {
                        departments.forEach { addElement(it) }
                    }
                }

                // 提取所有不重复的专业
                val majors = students.map { it.major }.distinct().sorted()
                (lstMajor.model as DefaultListModel<String>).apply {
                    clear()
                    // A如果为空，添加提示项
                    if (majors.isEmpty()) {
                        addElement("-- 无可用数据 --")
                    } else {
                        majors.forEach { addElement(it) }
                    }
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
        
        // 确保对话框不会被缩得太小
        minimumSize = Dimension(700, 500)
        
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