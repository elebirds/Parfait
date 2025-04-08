package moe.hhm.parfait.ui.view

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import moe.hhm.parfait.dto.StudentDTO
import moe.hhm.parfait.ui.state.StudentListUiState
import moe.hhm.parfait.ui.viewmodel.StudentListViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.awt.BorderLayout
import javax.swing.*
import javax.swing.table.DefaultTableModel

/**
 * 学生列表视图
 */
class StudentListView : JFrame(), KoinComponent {
    private val viewModel: StudentListViewModel by inject()
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    private val tableModel = object : DefaultTableModel(
        arrayOf("学号", "姓名", "性别", "院系", "专业", "年级", "班级", "状态"),
        0
    ){
        override fun isCellEditable(row: Int, column: Int): Boolean {
            return false // 禁止编辑
        }
    }
    
    private val table = JTable(tableModel).apply {
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
        fillsViewportHeight = true
        autoResizeMode = JTable.AUTO_RESIZE_ALL_COLUMNS
    }

    init {
        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        title = "学生成绩管理系统"
        defaultCloseOperation = EXIT_ON_CLOSE
        setSize(800, 600)
        setLocationRelativeTo(null)

        // 设置UI外观
        try {
            //UIManager.setLookAndFeel(FlatLightLaf())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 主面板
        val mainPanel = JPanel(BorderLayout())

        // 工具栏
        val toolBar = JToolBar()
        val addButton = JButton("添加学生")
        val refreshButton = JButton("刷新")
        val deleteButton = JButton("删除学生")

        addButton.addActionListener {
            showAddStudentDialog()
        }

        refreshButton.addActionListener {
            viewModel.refresh()
        }
        
        deleteButton.addActionListener {
            val selectedRow = table.selectedRow
            if (selectedRow >= 0) {
                val studentId = tableModel.getValueAt(selectedRow, 0) as String
                val confirmResult = JOptionPane.showConfirmDialog(
                    this,
                    "确定要删除学号为 $studentId 的学生吗？",
                    "确认删除",
                    JOptionPane.YES_NO_OPTION
                )
                if (confirmResult == JOptionPane.YES_OPTION) {
                    viewModel.deleteStudent(studentId)
                }
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "请先选择要删除的学生",
                    "提示",
                    JOptionPane.INFORMATION_MESSAGE
                )
            }
        }

        toolBar.add(addButton)
        toolBar.add(refreshButton)
        toolBar.add(deleteButton)

        // 学生表格
        val scrollPane = JScrollPane(table)

        // 添加组件到主面板
        mainPanel.add(toolBar, BorderLayout.NORTH)
        mainPanel.add(scrollPane, BorderLayout.CENTER)

        contentPane.add(mainPanel)
    }
    
    private fun showAddStudentDialog() {
        val dialog = AddStudentDialog(this)
        dialog.isVisible = true
        
        val student = dialog.getStudent()
        if (student != null) {
            viewModel.addStudent(student)
        }
    }

    private fun observeViewModel() {
        coroutineScope.launch {
            viewModel.uiState.collect { state ->
                SwingUtilities.invokeLater {
                    when (state) {
                        is StudentListUiState.Loading -> {
                            // 显示加载中
                            tableModel.setRowCount(0)
                            tableModel.addRow(arrayOf("加载中...", "", "", "", "", "", "", ""))
                        }
                        is StudentListUiState.Success -> {
                            // 更新表格数据
                            tableModel.setRowCount(0)
                            state.students.forEach { student ->
                                tableModel.addRow(arrayOf(
                                    student.studentId,
                                    student.name,
                                    when(student.gender) {
                                        StudentDTO.Gender.MALE -> "男"
                                        StudentDTO.Gender.FEMALE -> "女"
                                        else -> "未知"
                                    },
                                    student.department,
                                    student.major,
                                    student.grade.toString(),
                                    student.classGroup,
                                    when(student.status) {
                                        StudentDTO.Status.ENROLLED -> "在读"
                                        StudentDTO.Status.SUSPENDED -> "休学"
                                        StudentDTO.Status.GRADUATED -> "毕业"
                                        StudentDTO.Status.ABNORMAL -> "异常"
                                    }
                                ))
                            }
                        }
                        is StudentListUiState.Error -> {
                            // 显示错误信息
                            JOptionPane.showMessageDialog(
                                this@StudentListView,
                                state.message,
                                "错误",
                                JOptionPane.ERROR_MESSAGE
                            )
                        }
                    }
                }
            }
        }
    }
} 