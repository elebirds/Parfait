/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.component.table

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import moe.hhm.parfait.dto.StudentDTO
import moe.hhm.parfait.infra.i18n.I18nUtils
import moe.hhm.parfait.ui.base.CoroutineComponent
import moe.hhm.parfait.ui.base.DefaultCoroutineComponent
import moe.hhm.parfait.ui.viewmodel.StudentDataViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.awt.Dimension
import java.util.*
import javax.swing.JTable
import javax.swing.ListSelectionModel
import javax.swing.table.DefaultTableModel

class StudentDataTable(parent: CoroutineComponent? = null) : JTable(), KoinComponent,
    CoroutineComponent by DefaultCoroutineComponent(parent) {
    private val viewModel: StudentDataViewModel by inject()

    // 自定义表格模型，禁止直接编辑单元格
    private val tableModel = object : DefaultTableModel() {
        override fun isCellEditable(row: Int, column: Int): Boolean = false
    }

    // 保存学生数据的列表，用于获取选中的学生信息
    private var studentList: List<StudentDTO> = emptyList()

    // 表格列名称
    private val columnKeys = listOf(
        "student.property.id", "student.property.name", "student.property.gender", "student.property.department",
        "student.property.major", "student.property.grade", "student.property.classGroup", "student.property.status"
    )

    init {
        // 设置表格模型和基本属性
        model = tableModel
        preferredViewportSize = Dimension(800, 600)
        autoResizeMode = AUTO_RESIZE_SUBSEQUENT_COLUMNS
        selectionModel.selectionMode = ListSelectionModel.SINGLE_SELECTION
        rowHeight = 25

        // 初始化表格列
        initColumns()

        // 表格行选择监听器
        this.setRowSelectionListener { selectedStudent ->
            viewModel.setSelectedStudent(selectedStudent)
        }
    }

    override fun observer() {
        // 监听表格数据更新（高频更新）
        scope.launch {
            viewModel.students.collectLatest { students ->
                // 更新表格数据
                this@StudentDataTable.updateData(students)
            }
        }
        // TODO: 监听选中学生变化?
    }

    // 初始化表格列
    private fun initColumns() {
        I18nUtils.bindProperty(this, columnKeys) { table, resources ->
            table.tableModel.setColumnIdentifiers(resources.toTypedArray())
        }
    }

    // 将学生DTO对象转换为表格行数据
    private fun studentToRow(student: StudentDTO): Array<Any?> {
        return arrayOf(
            student.studentId,
            student.name,
            when (student.gender) {
                StudentDTO.Gender.MALE -> "男"
                StudentDTO.Gender.FEMALE -> "女"
                else -> "未知"
            },
            student.department,
            student.major,
            student.grade,
            student.classGroup,
            when (student.status) {
                StudentDTO.Status.ENROLLED -> "在籍"
                StudentDTO.Status.SUSPENDED -> "休学"
                StudentDTO.Status.GRADUATED -> "毕业"
                StudentDTO.Status.ABNORMAL -> "异常"
            }
        )
    }

    // 更新表格数据
    fun updateData(students: List<StudentDTO>) {
        // 保存学生列表引用
        studentList = students

        // 清空现有数据
        tableModel.rowCount = 0

        // 添加新数据
        students.forEach { student ->
            tableModel.addRow(studentToRow(student))
        }
    }

    // 获取当前选中的学生
    fun getSelectedStudent(): StudentDTO? {
        val selectedRow = selectedRow
        return if (selectedRow >= 0 && selectedRow < studentList.size) {
            studentList[selectedRow]
        } else {
            null
        }
    }

    // 获取当前选中的学生ID
    fun getSelectedStudentId(): String? {
        return getSelectedStudent()?.studentId
    }

    // 获取当前选中的学生UUID
    fun getSelectedStudentUUID(): UUID? {
        return getSelectedStudent()?.uuid
    }

    // 设置行选择监听器
    fun setRowSelectionListener(action: (StudentDTO?) -> Unit) {
        selectionModel.addListSelectionListener { event ->
            if (!event.valueIsAdjusting) {
                action(getSelectedStudent())
            }
        }
    }

    // 选择指定学生的行
    fun selectStudent(student: StudentDTO?) {
        if (student == null) {
            clearSelection()
            return
        }

        // 查找学生在列表中的索引
        val index = studentList.indexOfFirst { it.studentId == student.studentId }
        if (index >= 0) {
            // 选择找到的行
            setRowSelectionInterval(index, index)
            // 确保选中的行可见
            scrollRectToVisible(getCellRect(index, 0, true))
        } else {
            // 未找到匹配的学生，清除选择
            clearSelection()
        }
    }

    // 根据学生ID选择行
    fun selectStudentById(studentId: String?) {
        if (studentId == null) {
            clearSelection()
            return
        }

        // 查找学生在列表中的索引
        val index = studentList.indexOfFirst { it.studentId == studentId }
        if (index >= 0) {
            // 选择找到的行
            setRowSelectionInterval(index, index)
            // 确保选中的行可见
            scrollRectToVisible(getCellRect(index, 0, true))
        } else {
            // 未找到匹配的学生，清除选择
            clearSelection()
        }
    }
}