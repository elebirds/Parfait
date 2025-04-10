/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.viewmodel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moe.hhm.parfait.app.service.StudentService
import moe.hhm.parfait.dto.StudentDTO
import moe.hhm.parfait.infra.db.DatabaseConnectionState
import moe.hhm.parfait.infra.db.DatabaseFactory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.LoggerFactory
import kotlin.math.ceil

/**
 * 学生数据视图模型
 */
class StudentDataViewModel : BaseViewModel(), KoinComponent {
    // 日志
    private val logger = LoggerFactory.getLogger(this::class.java)
    
    // 通过Koin获取StudentService实例
    private val studentService: StudentService by inject()
    
    // 学生数据状态
    private val _uiState = MutableStateFlow(StudentDataUiState())
    val uiState: StateFlow<StudentDataUiState> = _uiState.asStateFlow()
    
    init {
        // 监听数据库连接状态
        observeDatabaseConnectionState()
    }
    
    /**
     * 监听数据库连接状态
     */
    private fun observeDatabaseConnectionState() {
        viewModelScope.launch {
            DatabaseFactory.connectionState.collectLatest { state ->
                when (state) {
                    is DatabaseConnectionState.Connected -> {
                        logger.info("数据库已连接，准备加载学生数据")
                        _uiState.update { it.copy(databaseConnected = true) }
                        // 数据库连接成功后加载数据
                        loadData()
                    }
                    is DatabaseConnectionState.Disconnected -> {
                        logger.info("数据库已断开连接")
                        _uiState.update { 
                            it.copy(
                                databaseConnected = false,
                                students = emptyList(),
                                totalStudents = 0,
                                totalPages = 1,
                                currentPage = 1,
                                isLoading = false
                            )
                        }
                    }
                    is DatabaseConnectionState.Connecting -> {
                        logger.info("数据库连接中...")
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }
    
    /**
     * 加载学生数据
     */
    fun loadData() {
        // 检查数据库是否已连接
        if (!_uiState.value.databaseConnected) {
            logger.warn("尝试加载数据，但数据库未连接")
            return
        }
        
        viewModelScope.launch {
            try {
                // 显示加载状态
                _uiState.update { it.copy(isLoading = true) }
                
                // 保存当前选中的学生ID，用于后续恢复选中状态
                val selectedStudentId = _uiState.value.selectedStudent?.studentId
                
                // 获取总学生数量（未分页）
                val allStudents = withContext(Dispatchers.IO) {
                    studentService.getAllStudents()
                }
                val totalStudents = allStudents.size
                val totalPages = calculateTotalPages(totalStudents, _uiState.value.pageSize)
                
                // 获取当前页的学生数据
                val students = studentService.getStudentsPage(_uiState.value.currentPage, _uiState.value.pageSize)
                
                // 尝试在新数据中找回之前选中的学生
                val selectedStudent = if (selectedStudentId != null) {
                    students.find { it.studentId == selectedStudentId }
                } else {
                    null
                }
                
                // 更新UI状态
                _uiState.update { 
                    it.copy(
                        students = students,
                        totalStudents = totalStudents,
                        totalPages = totalPages,
                        isLoading = false,
                        selectedStudent = selectedStudent
                    ) 
                }
            } catch (e: Exception) {
                // 处理错误
                _uiState.update { it.copy(isLoading = false) }
                logger.error("加载学生数据失败", e)
                e.printStackTrace()
            }
        }
    }
    
    /**
     * 添加新学生
     */
    fun addStudent(student: StudentDTO) {
        // 检查数据库是否已连接
        if (!_uiState.value.databaseConnected) {
            logger.warn("尝试添加学生，但数据库未连接")
            return
        }
        
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                studentService.addStudent(student)
                
                // 重新加载数据
                loadData()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                logger.error("添加学生失败", e)
                e.printStackTrace()
            }
        }
    }
    
    /**
     * 删除学生
     */
    fun deleteStudent(studentId: String) {
        // 检查数据库是否已连接
        if (!_uiState.value.databaseConnected) {
            logger.warn("尝试删除学生，但数据库未连接")
            return
        }
        
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val result = studentService.deleteStudent(studentId)
                
                if (result) {
                    // 删除成功，重新加载数据
                    loadData()
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                logger.error("删除学生失败", e)
                e.printStackTrace()
            }
        }
    }
    
    /**
     * 设置当前页
     */
    fun setCurrentPage(page: Int) {
        // 检查数据库是否已连接
        if (!_uiState.value.databaseConnected) {
            logger.warn("尝试设置当前页，但数据库未连接")
            return
        }
        
        if (page < 1 || page > _uiState.value.totalPages) return
        
        _uiState.update { it.copy(currentPage = page) }
        loadData()
    }
    
    /**
     * 转到上一页
     */
    fun previousPage() {
        if (_uiState.value.currentPage > 1) {
            setCurrentPage(_uiState.value.currentPage - 1)
        }
    }
    
    /**
     * 转到下一页
     */
    fun nextPage() {
        if (_uiState.value.currentPage < _uiState.value.totalPages) {
            setCurrentPage(_uiState.value.currentPage + 1)
        }
    }
    
    /**
     * 设置每页显示数量
     */
    fun setPageSize(size: Int) {
        // 检查数据库是否已连接
        if (!_uiState.value.databaseConnected) {
            logger.warn("尝试设置页面大小，但数据库未连接")
            return
        }
        
        _uiState.update { 
            it.copy(
                pageSize = size,
                currentPage = 1 // 重置为第一页
            ) 
        }
        loadData()
    }
    
    /**
     * 计算总页数
     */
    private fun calculateTotalPages(totalItems: Int, pageSize: Int): Int {
        return ceil(totalItems.toDouble() / pageSize).toInt().coerceAtLeast(1)
    }
    
    /**
     * 设置选中的学生
     */
    fun setSelectedStudent(student: StudentDTO?) {
        _uiState.update { it.copy(selectedStudent = student) }
    }
}

/**
 * 学生数据UI状态类
 */
data class StudentDataUiState(
    val students: List<StudentDTO> = emptyList(),
    val currentPage: Int = 1,
    val pageSize: Int = 20,
    val totalStudents: Int = 0,
    val totalPages: Int = 1,
    val isLoading: Boolean = false,
    val selectedStudent: StudentDTO? = null,
    val databaseConnected: Boolean = false
) 