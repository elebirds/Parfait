/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.viewmodel

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import moe.hhm.parfait.app.service.GpaStandardService
import moe.hhm.parfait.app.service.StudentSearchService
import moe.hhm.parfait.app.service.StudentService
import moe.hhm.parfait.dto.StudentDTO
import moe.hhm.parfait.infra.db.DatabaseConnectionState
import moe.hhm.parfait.infra.db.DatabaseFactory
import moe.hhm.parfait.ui.action.StudentAction
import moe.hhm.parfait.ui.component.dialog.AdvancedFilterCriteria
import moe.hhm.parfait.ui.component.dialog.SearchFilterCriteria
import moe.hhm.parfait.ui.state.FilterState
import moe.hhm.parfait.ui.state.VMState
import moe.hhm.parfait.ui.viewmodel.common.PaginationDataViewModel
import moe.hhm.parfait.ui.viewmodel.common.VMErrorHandlerChooser
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*
import java.io.File
import moe.hhm.parfait.ui.component.dialog.CertificateGenerateDialog

/**
 * 学生数据视图模型
 */
class StudentDataViewModel : PaginationDataViewModel<List<StudentDTO>>(emptyList()), KoinComponent {
    // 通过Koin获取StudentService实例
    private val studentService: StudentService by inject()

    // 通过Koin获取StudentSearchService实例
    private val studentSearchService: StudentSearchService by inject()

    // 通过Koin获取StudentSearchService实例
    private val gpaService: GpaStandardService by inject()

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
                        _vmState.value = VMState.PRELOADING
                        loadData()
                    }

                    is DatabaseConnectionState.Disconnected -> {
                        logger.info("数据库已断开连接")
                        _vmState.value = VMState.DISCONNECTED
                        _data.value = emptyList()
                        resetPaginationState()
                        _selectedStudents.value = emptyList()
                    }

                    is DatabaseConnectionState.Connecting -> _vmState.value = VMState.CONNECTING
                }
            }
        }
    }

    /**
     * 加载学生数据
     */
    override fun loadData() = suspendProcessWithErrorHandling(VMErrorHandlerChooser.LoadData) {
        _vmState.value = VMState.LOADING

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

            else -> studentService.count()

        }

        // 更新分页状态
        updatePaginationStateWithCount(totalStudents)

        // 获取当前页的学生数据
        val pageStudents = when {
            _currentAdvancedFilterCriteria.value != null -> studentSearchService.searchAdvancedStudentsPage(
                _currentAdvancedFilterCriteria.value!!,
                paginationState.value.currentPage,
                paginationState.value.pageSize
            )

            _currentFilterCriteria.value != null -> studentSearchService.searchStudentsPage(
                _currentFilterCriteria.value!!,
                paginationState.value.currentPage,
                paginationState.value.pageSize
            )

            else -> studentService.getStudentsPage(
                paginationState.value.currentPage,
                paginationState.value.pageSize
            )
        }

        // 更新学生列表
        _data.value = pageStudents

        // 尝试在新数据中找回之前选中的学生
        if (selectedStudentIds.isNotEmpty()) {
            val selectedStudents = pageStudents.filter { it.studentId in selectedStudentIds }
            _selectedStudents.value = selectedStudents
        }

        _vmState.value = VMState.DONE
        true
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
    fun addStudent(student: StudentDTO) = suspendProcessWithErrorHandling(VMErrorHandlerChooser.Modify) {
        _vmState.value = VMState.PROCESSING
        studentService.addStudent(student)
        true
    }

    /**
     * 删除学生
     */
    fun deleteStudent(uuid: UUID) {
        deleteStudents(listOf(uuid))
    }

    fun deleteStudents(uuids: List<UUID>) = suspendProcessWithErrorHandling(VMErrorHandlerChooser.Modify) {
        _vmState.value = VMState.PROCESSING
        uuids.forEach { uuid ->
            val selectedUUIDs = _selectedStudents.value.mapNotNull { it.uuid }
            if (uuid in selectedUUIDs) {
                _selectedStudents.value = _selectedStudents.value.filter { it.uuid != uuid }
            }
        }
        uuids.forEach { uuid -> studentService.deleteStudent(uuid) }
        true
    }

    /**
     * 更新学生成绩
     */
    fun updateStudent(student: StudentDTO, isScores: Boolean) =
        suspendProcessWithErrorHandling(VMErrorHandlerChooser.Modify) {
            _vmState.value = VMState.PROCESSING
            if (isScores) studentService.updateScore(student)
            else studentService.updateInfo(student)

            // 更新当前选中的学生
            val currentSelectedStudent = selectedStudents.value.firstOrNull()
            if (currentSelectedStudent?.studentId == student.studentId) {
                // 如果更新的是当前选中的学生，更新选择列表
                _selectedStudents.value =
                    listOf(student) + _selectedStudents.value.filter { it.studentId != student.studentId }
            }
            true
        }

    fun exportStudentToExcel() = suspendProcessWithErrorHandling(VMErrorHandlerChooser.Process) {
        _vmState.value = VMState.PROCESSING
        val students = when {
            _selectedStudents.value.isNotEmpty()-> _selectedStudents.value
            _currentAdvancedFilterCriteria.value != null -> studentSearchService.searchAdvancedStudents(
                _currentAdvancedFilterCriteria.value!!
            )
            _currentFilterCriteria.value != null -> studentSearchService.searchStudents(_currentFilterCriteria.value!!)
            else -> studentService.getAllStudents()
        }
        StudentAction.exportToExcel(students, gpaService.getDefault())
        _vmState.value = VMState.DONE
        true
    }

    /**
     * 导出学生信息到文本
     * @param format 文本格式
     */
    fun exportStudentToText(format: String) = suspendProcessWithErrorHandling(VMErrorHandlerChooser.Process) {
        _vmState.value = VMState.PROCESSING
        val students = when {
            _selectedStudents.value.isNotEmpty()-> _selectedStudents.value
            _currentAdvancedFilterCriteria.value != null -> studentSearchService.searchAdvancedStudents(
                _currentAdvancedFilterCriteria.value!!
            )
            _currentFilterCriteria.value != null -> studentSearchService.searchStudents(_currentFilterCriteria.value!!)
            else -> studentService.getAllStudents()
        }
        StudentAction.exportToText(students, gpaService.getDefault(), format)
        _vmState.value = VMState.DONE
        true
    }

    /**
     * 生成学生证书
     * @param params 证书生成参数
     * @return 生成的证书文件列表
     */
    fun generateCertificates(params: CertificateGenerateDialog.CertificateGenerationParams)
        = suspendProcessWithErrorHandling(
        VMErrorHandlerChooser.Process){
        _vmState.value = VMState.PROCESSING
        StudentAction.generateCertificates(params)
        _vmState.value = VMState.DONE
        true
    }

    /**
     * 搜索学生
     * @param criteria 搜索条件
     */
    fun search(criteria: SearchFilterCriteria) {
        // 检查数据库是否已连接
        if (_vmState.value != VMState.DONE) {
            logger.warn("在未初始化完毕时尝试搜索学生")
            return
        }

        scope.launch {
            try {
                _vmState.value = VMState.PROCESSING

                // 清空当前选中的学生
                _selectedStudents.value = emptyList()

                val searchResults = studentSearchService.searchStudents(criteria)

                // 更新学生列表，不应用为筛选条件
                _data.value = searchResults
                _filterState.value = FilterState.FILTERED
                _currentFilterCriteria.value = criteria

                // 更新分页状态（搜索结果不分页）
                _paginationState.update {
                    it.copy(
                        currentPage = 1,
                        totalCount = searchResults.size.toLong(),
                        totalPages = 1
                    )
                }

                _vmState.value = VMState.DONE
            } catch (e: Exception) {
                _vmState.value = VMState.ERROR
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
        if (_vmState.value != VMState.DONE) {
            logger.warn("在未初始化完毕时尝试筛选学生")
            return
        }

        scope.launch {
            try {
                _vmState.value = VMState.PROCESSING

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
                _vmState.value = VMState.PRELOADING
                loadData()
            } catch (e: Exception) {
                _vmState.value = VMState.ERROR
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
        if (_vmState.value != VMState.DONE) {
            logger.warn("在未初始化完毕时尝试应用高级筛选条件")
            return
        }

        scope.launch {
            try {
                _vmState.value = VMState.PROCESSING

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
                _vmState.value = VMState.PRELOADING
                loadData()
            } catch (e: Exception) {
                _vmState.value = VMState.ERROR
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
        if (_vmState.value != VMState.DONE) {
            logger.warn("在未初始化完毕时尝试清除筛选条件")
            return
        }

        // 如果当前没有筛选条件，不做任何操作
        if (_filterState.value == FilterState.NO_FILTER) {
            return
        }

        scope.launch {
            try {
                _vmState.value = VMState.PROCESSING

                // 清空当前选中的学生
                _selectedStudents.value = emptyList()

                // 清除筛选条件
                _currentFilterCriteria.value = null
                _currentAdvancedFilterCriteria.value = null
                _filterState.value = FilterState.NO_FILTER
                setCurrentPage(1, checkState = false)
            } catch (e: Exception) {
                _vmState.value = VMState.ERROR
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
        if (_vmState.value == VMState.DONE) {
            _vmState.value = VMState.PRELOADING
            loadData()
        } else {
            logger.warn("无法准备重新加载数据，当前状态：${_vmState.value.name}")
        }
    }
}

