package moe.hhm.parfait.ui.view

import moe.hhm.parfait.dto.StudentDTO
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.*

/**
 * 添加学生对话框
 */
class AddStudentDialog(parent: JFrame) : JDialog(parent, "添加学生", true) {
    private val studentIdField = JTextField(20)
    private val nameField = JTextField(20)
    private val genderComboBox = JComboBox<String>(arrayOf("男", "女"))
    private val departmentField = JTextField(20)
    private val majorField = JTextField(20)
    private val gradeField = JTextField(20)
    private val classGroupField = JTextField(20)
    private val statusComboBox = JComboBox<String>(arrayOf("在读", "休学", "毕业", "异常"))
    
    private var student: StudentDTO? = null
    
    init {
        setupUI()
        pack()
        setLocationRelativeTo(parent)
    }
    
    private fun setupUI() {
        layout = BorderLayout()
        
        // 创建表单面板
        val formPanel = JPanel(GridBagLayout())
        val constraints = GridBagConstraints().apply {
            insets = Insets(5, 5, 5, 5)
            anchor = GridBagConstraints.WEST
        }
        
        // 添加表单字段
        addFormField(formPanel, constraints, "学号:", studentIdField, 0)
        addFormField(formPanel, constraints, "姓名:", nameField, 1)
        addFormField(formPanel, constraints, "性别:", genderComboBox, 2)
        addFormField(formPanel, constraints, "院系:", departmentField, 3)
        addFormField(formPanel, constraints, "专业:", majorField, 4)
        addFormField(formPanel, constraints, "年级:", gradeField, 5)
        addFormField(formPanel, constraints, "班级:", classGroupField, 6)
        addFormField(formPanel, constraints, "状态:", statusComboBox, 7)
        
        // 创建按钮面板
        val buttonPanel = JPanel()
        val okButton = JButton("确定")
        val cancelButton = JButton("取消")
        
        okButton.addActionListener {
            if (validateInput()) {
                student = createStudentDTO()
                dispose()
            }
        }
        
        cancelButton.addActionListener {
            dispose()
        }
        
        buttonPanel.add(okButton)
        buttonPanel.add(cancelButton)
        
        // 添加组件到对话框
        add(formPanel, BorderLayout.CENTER)
        add(buttonPanel, BorderLayout.SOUTH)
    }
    
    private fun addFormField(
        panel: JPanel,
        constraints: GridBagConstraints,
        label: String,
        component: JComponent,
        row: Int
    ) {
        constraints.gridx = 0
        constraints.gridy = row
        panel.add(JLabel(label), constraints)
        
        constraints.gridx = 1
        constraints.fill = GridBagConstraints.HORIZONTAL
        panel.add(component, constraints)
    }
    
    private fun validateInput(): Boolean {
        if (studentIdField.text.isBlank()) {
            showError("学号不能为空")
            return false
        }
        
        if (nameField.text.isBlank()) {
            showError("姓名不能为空")
            return false
        }
        
        if (departmentField.text.isBlank()) {
            showError("院系不能为空")
            return false
        }
        
        if (majorField.text.isBlank()) {
            showError("专业不能为空")
            return false
        }
        
        if (gradeField.text.isBlank()) {
            showError("年级不能为空")
            return false
        }
        
        try {
            gradeField.text.toInt()
        } catch (e: NumberFormatException) {
            showError("年级必须是数字")
            return false
        }
        
        if (classGroupField.text.isBlank()) {
            showError("班级不能为空")
            return false
        }
        
        return true
    }
    
    private fun showError(message: String) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "输入错误",
            JOptionPane.ERROR_MESSAGE
        )
    }
    
    private fun createStudentDTO(): StudentDTO {
        val gender = when (genderComboBox.selectedItem) {
            "男" -> StudentDTO.Gender.MALE
            "女" -> StudentDTO.Gender.FEMALE
            else -> StudentDTO.Gender.UNKNOWN
        }
        
        val status = when (statusComboBox.selectedItem) {
            "在读" -> StudentDTO.Status.ENROLLED
            "休学" -> StudentDTO.Status.SUSPENDED
            "毕业" -> StudentDTO.Status.GRADUATED
            else -> StudentDTO.Status.ABNORMAL
        }
        
        return StudentDTO(
            studentId = studentIdField.text,
            name = nameField.text,
            gender = gender,
            department = departmentField.text,
            major = majorField.text,
            grade = gradeField.text.toInt(),
            classGroup = classGroupField.text,
            status = status,
            scores = emptyList()
        )
    }
    
    fun getStudent(): StudentDTO? = student
} 