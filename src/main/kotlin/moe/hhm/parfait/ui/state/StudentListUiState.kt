package moe.hhm.parfait.ui.state

import moe.hhm.parfait.dto.StudentDTO


/**
 * 学生列表UI状态
 */
sealed class StudentListUiState {
    /**
     * 加载中
     */
    data object Loading : StudentListUiState()

    /**
     * 加载成功
     */
    data class Success(val students: List<StudentDTO>) : StudentListUiState()

    /**
     * 加载失败
     */
    data class Error(val message: String) : StudentListUiState()
} 