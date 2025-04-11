/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.viewmodel

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import moe.hhm.parfait.app.service.StudentService
import moe.hhm.parfait.dto.StudentDTO
import moe.hhm.parfait.infra.db.DatabaseConnectionState
import moe.hhm.parfait.infra.db.DatabaseFactory
import moe.hhm.parfait.ui.state.StudentDataLoadState
import moe.hhm.parfait.ui.state.StudentDataPaginationState
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.LoggerFactory
import java.util.UUID
import kotlin.math.ceil

/**
 * 学生数据视图模型
 */
class StudentDataViewModel : BaseViewModel(), KoinComponent {
    // 日志
    private val logger = LoggerFactory.getLogger(this::class.java)

    // 通过Koin获取StudentService实例
    private val studentService: StudentService by inject()

    // 学生列表数据状态
    private val _students = MutableStateFlow<List<StudentDTO>>(emptyList())
    val students: StateFlow<List<StudentDTO>> = _students.asStateFlow()

    // 分页状态
    private val _paginationState = MutableStateFlow(StudentDataPaginationState())
    val paginationState: StateFlow<StudentDataPaginationState> = _paginationState.asStateFlow()

    // 加载状态
    private val _loadState = MutableStateFlow(StudentDataLoadState.DISCONNECTED)
    val loadState: StateFlow<StudentDataLoadState> = _loadState.asStateFlow()

    // 当前选中的学生
    private val _selectedStudent = MutableStateFlow<StudentDTO?>(null)
    val selectedStudent: StateFlow<StudentDTO?> = _selectedStudent.asStateFlow()

    init {
        // 监听数据库连接状态
        observeDatabaseConnectionState()
    }

    /**
     * 监听数据库连接状态
     */
    private fun observeDatabaseConnectionState() {
        scope.launch {
            DatabaseFactory.connectionState.collectLatest { state ->
                when (state) {
                    is DatabaseConnectionState.Connected -> {
                        logger.info("数据库已连接，准备加载学生数据")
                        _loadState.value = StudentDataLoadState.PRELOADING
                        loadData()
                    }

                    is DatabaseConnectionState.Disconnected -> {
                        logger.info("数据库已断开连接")
                        _loadState.value = StudentDataLoadState.DISCONNECTED
                        _students.value = emptyList()
                        _paginationState.value = StudentDataPaginationState()
                        _selectedStudent.value = null
                    }

                    is DatabaseConnectionState.Connecting -> _loadState.value = StudentDataLoadState.CONNECTING
                }
            }
        }
    }

    /**
     * 加载学生数据
     */
    fun loadData() {
        // 检查数据库是否已连接
        if (StudentDataLoadState.PRELOADING != _loadState.value) {
            logger.error("非法加载数据，当前状态：${_loadState.value.name}")
            return
        }

        scope.launch {
            try {
                // 设置加载状态
                _loadState.value = StudentDataLoadState.LOADING

                // 保存当前选中的学生ID，用于后续恢复选中状态
                val selectedStudentId = _selectedStudent.value?.studentId

                // 获取总学生数量（未分页）
                // TODO: 每一次加载都查询总数，可能会影响性能？ 但是目前没有更好的方法
                val totalStudents = studentService.count()
                val totalPages = calculateTotalPages(totalStudents, _paginationState.value.pageSize)

                // 更新分页状态
                _paginationState.update {
                    it.copy(
                        // 如果当前页数大于总页数，重置为第一页
                        currentPage = if (_paginationState.value.currentPage > totalPages) 1 else _paginationState.value.currentPage,
                        totalStudents = totalStudents,
                        totalPages = totalPages
                    )
                }

                // 获取当前页的学生数据
                val pageStudents = studentService.getStudentsPage(
                    _paginationState.value.currentPage,
                    _paginationState.value.pageSize
                )

                // 更新学生列表
                _students.value = pageStudents

                // 尝试在新数据中找回之前选中的学生
                if (selectedStudentId != null) {
                    val student = pageStudents.find { it.studentId == selectedStudentId }
                    _selectedStudent.value = student
                }

                _loadState.value = StudentDataLoadState.DONE
            } catch (e: Exception) {
                _loadState.value = StudentDataLoadState.ERROR
                logger.error("加载学生数据失败", e)
                e.printStackTrace()
            }
        }
    }

    /// 选中操作
    /**
     * 设置选中的学生
     */
    fun setSelectedStudent(student: StudentDTO?) {
        _selectedStudent.value = student
    }


    /// 学生操作
    /**
     * 添加新学生
     */
    fun addStudent(student: StudentDTO) {
        // 检查数据库是否已连接
        if (_loadState.value != StudentDataLoadState.DONE) {
            logger.warn("在未初始化完毕时尝试添加学生")
            return
        }

        scope.launch {
            try {
                _loadState.value = StudentDataLoadState.PROCESSING
                studentService.addStudent(student)
                _loadState.value = StudentDataLoadState.PRELOADING
                loadData()
            } catch (e: Exception) {
                _loadState.value = StudentDataLoadState.ERROR
                logger.error("添加学生失败", e)
                e.printStackTrace()
            }
        }
    }

    /**
     * 删除学生
     */
    fun deleteStudent(uuid: UUID) {
        // 检查数据库是否已连接
        if (_loadState.value != StudentDataLoadState.DONE) {
            logger.warn("在未初始化完毕时尝试删除学生")
            return
        }

        scope.launch {
            try {
                _loadState.value = StudentDataLoadState.PROCESSING
                val result = studentService.deleteStudent(uuid)
                if (result) {
                    // 如果删除的是当前选中的学生，取消选择
                    if (_selectedStudent.value?.uuid == uuid) {
                        _selectedStudent.value = null
                    }
                    _loadState.value = StudentDataLoadState.PRELOADING
                    loadData()
                }
            } catch (e: Exception) {
                _loadState.value = StudentDataLoadState.ERROR
                logger.error("删除学生失败", e)
                e.printStackTrace()
            }
        }
    }

    /**
     * 更新学生成绩
     */
    fun updateStudent(student: StudentDTO, isScores: Boolean) {
        // 检查数据库是否已连接
        if (_loadState.value != StudentDataLoadState.DONE) {
            logger.warn("在未初始化完毕时尝试更新学生成绩")
            return
        }

        scope.launch {
            try {
                _loadState.value = StudentDataLoadState.PROCESSING
                if(isScores) {
                    studentService.updateScore(student)
                } else {
                    studentService.updateInfo(student)
                }
                
                // 更新当前选中的学生
                if (_selectedStudent.value?.studentId == student.studentId) {
                    _selectedStudent.value = student
                }
                _loadState.value = StudentDataLoadState.PRELOADING
                loadData()
            } catch (e: Exception) {
                _loadState.value = StudentDataLoadState.ERROR
                logger.error("更新学生成绩失败", e)
                e.printStackTrace()
            }
        }
    }

    /// 分页操作

    /**
     * 设置当前页
     */
    fun setCurrentPage(page: Int) {
        if (_loadState.value != StudentDataLoadState.DONE) {
            logger.warn("在未初始化完毕时尝试设置页数")
            return
        }
        if (!isPageValid(page)) return // 非法页数

        _paginationState.update { it.copy(currentPage = page) }
        // 切换页面时清除选中状态
        _selectedStudent.value = null
        _loadState.value = StudentDataLoadState.PRELOADING
        loadData()
    }

    /**
     * 转到上一页
     */
    fun previousPage() {
        if (_paginationState.value.currentPage > 1) {
            setCurrentPage(_paginationState.value.currentPage - 1)
        }
    }

    /**
     * 转到下一页
     */
    fun nextPage() {
        if (_paginationState.value.currentPage < _paginationState.value.totalPages) {
            setCurrentPage(_paginationState.value.currentPage + 1)
        }
    }

    /**
     * 转到首页
     */
    fun firstPage() {
        setCurrentPage(1)
    }

    /**
     * 转到末页
     */
    fun lastPage() {
        setCurrentPage(_paginationState.value.totalPages)
    }

    /**
     * 设置每页显示数量
     */
    fun setPageSize(size: Int) {
        if (_loadState.value != StudentDataLoadState.DONE) {
            logger.warn("在未初始化完毕时尝试设置页面大小")
            return
        }
        // 计算之前那个页面的新页数
        val newPage = (_paginationState.value.currentPage - 1) * _paginationState.value.pageSize / size
        _paginationState.update {
            it.copy(
                pageSize = size,
                currentPage = newPage + 1
            )
        }
        // 切换页面大小时清除选中状态
        _selectedStudent.value = null
        // 重新加载数据
        _loadState.value = StudentDataLoadState.PRELOADING
        loadData()
    }

    fun isPageValid(page: Int): Boolean {
        return page in 1.._paginationState.value.totalPages
    }

    /**
     * 计算总页数
     */
    private fun calculateTotalPages(totalItems: Long, pageSize: Int): Int {
        return ceil(totalItems.toDouble() / pageSize).toInt().coerceAtLeast(1)
    }
}

