/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.viewmodel

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import moe.hhm.parfait.app.service.StudentService
import moe.hhm.parfait.app.service.StudentSearchService
import moe.hhm.parfait.dto.StudentDTO
import moe.hhm.parfait.infra.db.DatabaseConnectionState
import moe.hhm.parfait.infra.db.DatabaseFactory
import moe.hhm.parfait.ui.component.dialog.SearchFilterCriteria
import moe.hhm.parfait.ui.component.dialog.AdvancedFilterCriteria
import moe.hhm.parfait.ui.state.FilterState
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

    // 通过Koin获取StudentSearchService实例
    private val studentSearchService: StudentSearchService by inject()

    // 学生列表数据状态
    private val _students = MutableStateFlow<List<StudentDTO>>(emptyList())
    val students: StateFlow<List<StudentDTO>> = _students.asStateFlow()

    // 分页状态
    private val _paginationState = MutableStateFlow(StudentDataPaginationState())
    val paginationState: StateFlow<StudentDataPaginationState> = _paginationState.asStateFlow()

    // 加载状态
    private val _loadState = MutableStateFlow(StudentDataLoadState.DISCONNECTED)
    val loadState: StateFlow<StudentDataLoadState> = _loadState.asStateFlow()

    // 当前选中的多个学生
    private val _selectedStudents = MutableStateFlow<List<StudentDTO>>(emptyList())
    val selectedStudents: StateFlow<List<StudentDTO>> = _selectedStudents.asStateFlow()

    // 筛选状态
    private val _filterState = MutableStateFlow(FilterState.NO_FILTER)
    val filterState: StateFlow<FilterState> = _filterState.asStateFlow()

    // 当前使用的筛选条件
    private val _currentFilterCriteria = MutableStateFlow<SearchFilterCriteria?>(null)
    val currentFilterCriteria: StateFlow<SearchFilterCriteria?> = _currentFilterCriteria.asStateFlow()

    // 当前使用的高级筛选条件
    private val _currentAdvancedFilterCriteria = MutableStateFlow<AdvancedFilterCriteria?>(null)
    val currentAdvancedFilterCriteria: StateFlow<AdvancedFilterCriteria?> = _currentAdvancedFilterCriteria.asStateFlow()

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
                        _selectedStudents.value = emptyList()
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
                val selectedStudentIds = _selectedStudents.value.map { it.studentId }

                // 获取总学生数量（未分页）
                val totalStudents = when {
                    _currentAdvancedFilterCriteria.value != null -> {
                        studentSearchService.countAdvancedSearchResults(_currentAdvancedFilterCriteria.value!!)
                    }
                    _currentFilterCriteria.value != null -> {
                        studentSearchService.countSearchResults(_currentFilterCriteria.value!!)
                    }
                    else -> {
                        studentService.count()
                    }
                }
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
                val pageStudents = when {
                    _currentAdvancedFilterCriteria.value != null -> {
                        studentSearchService.searchAdvancedStudentsPage(
                            _currentAdvancedFilterCriteria.value!!,
                            _paginationState.value.currentPage,
                            _paginationState.value.pageSize
                        )
                    }
                    _currentFilterCriteria.value != null -> {
                        studentSearchService.searchStudentsPage(
                            _currentFilterCriteria.value!!,
                            _paginationState.value.currentPage,
                            _paginationState.value.pageSize
                        )
                    }
                    else -> {
                        studentService.getStudentsPage(
                            _paginationState.value.currentPage,
                            _paginationState.value.pageSize
                        )
                    }
                }

                // 更新学生列表
                _students.value = pageStudents

                // 尝试在新数据中找回之前选中的学生
                if (selectedStudentIds.isNotEmpty()) {
                    val selectedStudents = pageStudents.filter { it.studentId in selectedStudentIds }
                    _selectedStudents.value = selectedStudents
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
        _selectedStudents.value = if (student == null) emptyList() else listOf(student)
    }

    /**
     * 设置选中的多个学生
     */
    fun setSelectedStudents(students: List<StudentDTO>) {
        _selectedStudents.value = students
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
        deleteStudents(listOf(uuid))
    }

    fun deleteStudents(uuids: List<UUID>) {
        // 检查数据库是否已连接
        if (_loadState.value != StudentDataLoadState.DONE) {
            logger.warn("在未初始化完毕时尝试删除学生")
            return
        }

        scope.launch {
            try {
                _loadState.value = StudentDataLoadState.PROCESSING
                uuids.forEach { uuid ->
                    val selectedUUIDs = _selectedStudents.value.mapNotNull { it.uuid }
                    if (uuid in selectedUUIDs) {
                        _selectedStudents.value = _selectedStudents.value.filter { it.uuid != uuid }
                    }
                }
                uuids.forEach { uuid -> studentService.deleteStudent(uuid) }
                _loadState.value = StudentDataLoadState.PRELOADING
                loadData()
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
                val currentSelectedStudent = selectedStudents.value.firstOrNull()
                if (currentSelectedStudent?.studentId == student.studentId) {
                    // 如果更新的是当前选中的学生，更新选择列表
                    _selectedStudents.value = listOf(student) + _selectedStudents.value.filter { it.studentId != student.studentId }
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
        _selectedStudents.value = emptyList()
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
        _selectedStudents.value = emptyList()
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

    /**
     * 搜索学生
     * @param criteria 搜索条件
     */
    fun search(criteria: SearchFilterCriteria) {
        // 检查数据库是否已连接
        if (_loadState.value != StudentDataLoadState.DONE) {
            logger.warn("在未初始化完毕时尝试搜索学生")
            return
        }

        scope.launch {
            try {
                _loadState.value = StudentDataLoadState.PROCESSING
                
                // 清空当前选中的学生
                _selectedStudents.value = emptyList()
                
                val searchResults = studentSearchService.searchStudents(criteria)
                
                // 更新学生列表，不应用为筛选条件
                _students.value = searchResults
                _filterState.value = FilterState.NO_FILTER
                _currentFilterCriteria.value = null
                
                // 更新分页状态（搜索结果不分页）
                _paginationState.update {
                    it.copy(
                        currentPage = 1,
                        totalStudents = searchResults.size.toLong(),
                        totalPages = 1
                    )
                }
                
                _loadState.value = StudentDataLoadState.DONE
            } catch (e: Exception) {
                _loadState.value = StudentDataLoadState.ERROR
                logger.error("搜索学生失败", e)
                e.printStackTrace()
            }
        }
    }
    
    /**
     * 应用筛选条件
     * @param criteria 筛选条件
     */
    fun applyFilter(criteria: SearchFilterCriteria) {
        // 检查数据库是否已连接
        if (_loadState.value != StudentDataLoadState.DONE) {
            logger.warn("在未初始化完毕时尝试筛选学生")
            return
        }
        
        scope.launch {
            try {
                _loadState.value = StudentDataLoadState.PROCESSING
                
                // 清空当前选中的学生
                _selectedStudents.value = emptyList()
                
                // 保存筛选条件
                _currentFilterCriteria.value = criteria
                _filterState.value = FilterState.FILTERED
                
                // 重置页码
                _paginationState.update {
                    it.copy(currentPage = 1)
                }
                
                // 重新加载数据
                _loadState.value = StudentDataLoadState.PRELOADING
                loadData()
            } catch (e: Exception) {
                _loadState.value = StudentDataLoadState.ERROR
                logger.error("应用筛选条件失败", e)
                e.printStackTrace()
            }
        }
    }
    
    /**
     * 应用高级筛选条件
     * @param criteria 高级筛选条件
     */
    fun applyAdvancedFilter(criteria: AdvancedFilterCriteria) {
        // 检查数据库是否已连接
        if (_loadState.value != StudentDataLoadState.DONE) {
            logger.warn("在未初始化完毕时尝试应用高级筛选条件")
            return
        }
        
        scope.launch {
            try {
                _loadState.value = StudentDataLoadState.PROCESSING
                
                // 清空当前选中的学生
                _selectedStudents.value = emptyList()
                
                // 保存高级筛选条件
                _currentAdvancedFilterCriteria.value = criteria
                // 清除普通筛选条件
                _currentFilterCriteria.value = null
                _filterState.value = FilterState.FILTERED
                
                // 重置页码
                _paginationState.update {
                    it.copy(currentPage = 1)
                }
                
                // 重新加载数据
                _loadState.value = StudentDataLoadState.PRELOADING
                loadData()
            } catch (e: Exception) {
                _loadState.value = StudentDataLoadState.ERROR
                logger.error("应用高级筛选条件失败", e)
                e.printStackTrace()
            }
        }
    }
    
    /**
     * 清除筛选条件
     */
    fun clearFilter() {
        // 检查数据库是否已连接
        if (_loadState.value != StudentDataLoadState.DONE) {
            logger.warn("在未初始化完毕时尝试清除筛选条件")
            return
        }
        
        // 如果当前没有筛选条件，不做任何操作
        if (_filterState.value == FilterState.NO_FILTER) {
            return
        }
        
        scope.launch {
            try {
                _loadState.value = StudentDataLoadState.PROCESSING
                
                // 清空当前选中的学生
                _selectedStudents.value = emptyList()
                
                // 清除筛选条件
                _currentFilterCriteria.value = null
                _currentAdvancedFilterCriteria.value = null
                _filterState.value = FilterState.NO_FILTER
                
                // 重置页码
                _paginationState.update {
                    it.copy(currentPage = 1)
                }
                
                // 重新加载数据
                _loadState.value = StudentDataLoadState.PRELOADING
                loadData()
            } catch (e: Exception) {
                _loadState.value = StudentDataLoadState.ERROR
                logger.error("清除筛选条件失败", e)
                e.printStackTrace()
            }
        }
    }

    /**
     * 准备重新加载数据
     * 当前状态为DONE时，将状态设置为PRELOADING，然后加载数据
     */
    fun prepareForReload() {
        if (_loadState.value == StudentDataLoadState.DONE) {
            _loadState.value = StudentDataLoadState.PRELOADING
            loadData()
        } else {
            logger.warn("无法准备重新加载数据，当前状态：${_loadState.value.name}")
        }
    }
}

