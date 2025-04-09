package moe.hhm.parfait.ui.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import moe.hhm.parfait.app.service.StudentService
import moe.hhm.parfait.dto.StudentDTO
import moe.hhm.parfait.ui.state.StudentListUiState
import moe.hhm.parfait.viewmodel.BaseViewModel

/**
 * 学生列表ViewModel
 */
class StudentListViewModel(private val studentService: StudentService) : BaseViewModel() {
    private val _uiState = MutableStateFlow<StudentListUiState>(StudentListUiState.Loading)
    val uiState: StateFlow<StudentListUiState> = _uiState.asStateFlow()

    private val _currentPage = MutableStateFlow(1)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    private val _pageSize = MutableStateFlow(20)
    val pageSize: StateFlow<Int> = _pageSize.asStateFlow()

    init {
        refresh()
    }

    /**
     * 刷新数据
     */
    fun refresh() {
        viewModelScope.launch {
            _uiState.value = StudentListUiState.Loading
            try {
                val students = studentService.getStudentsPage(_currentPage.value, _pageSize.value)
                _uiState.value = StudentListUiState.Success(students)
            } catch (e: Exception) {
                _uiState.value = StudentListUiState.Error("加载学生列表失败：${e.message}")
            }
        }
    }

    /**
     * 更新页码
     */
    fun updatePage(page: Int) {
        if (page > 0) {
            _currentPage.value = page
            refresh()
        }
    }

    /**
     * 更新每页大小
     */
    fun updatePageSize(size: Int) {
        if (size > 0) {
            _pageSize.value = size
            _currentPage.value = 1
            refresh()
        }
    }

    /**
     * 删除学生
     */
    fun deleteStudent(studentId: String) {
        viewModelScope.launch {
            try {
                studentService.deleteStudent(studentId)
                refresh()
            } catch (e: Exception) {
                _uiState.value = StudentListUiState.Error("删除学生失败：${e.message}")
            }
        }
    }

    /**
     * 添加学生
     */
    fun addStudent(student: StudentDTO) {
        viewModelScope.launch {
            try {
                studentService.addStudent(student)
                refresh()
            } catch (e: Exception) {
                _uiState.value = StudentListUiState.Error("添加学生失败：${e.message}")
            }
        }
    }
} 