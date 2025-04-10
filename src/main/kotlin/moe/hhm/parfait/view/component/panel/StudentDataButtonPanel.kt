/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.view.component.panel

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import moe.hhm.parfait.utils.i18n.I18nUtils.createButton
import moe.hhm.parfait.view.base.CoroutineComponent
import moe.hhm.parfait.view.base.DefaultCoroutineComponent
import moe.hhm.parfait.view.state.StudentDataLoadState
import moe.hhm.parfait.viewmodel.StudentDataViewModel
import net.miginfocom.swing.MigLayout
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import javax.swing.JButton
import javax.swing.JPanel

class StudentDataButtonPanel(parent: CoroutineComponent? = null) : JPanel(), KoinComponent, CoroutineComponent by DefaultCoroutineComponent(parent) {
    // 通过Koin获取ViewModel
    private val viewModel: StudentDataViewModel by inject()

    private val buttonAdd: JButton = createButton("student.add").apply {
        addActionListener {
            // 打开添加学生对话框
            // TODO: 实现添加学生对话框
        }
    }
    
    private val buttonDel: JButton = createButton("student.delete").apply {
        addActionListener {
            val studentId = viewModel.selectedStudent.value?.studentId
            if (studentId != null) {
                viewModel.deleteStudent(studentId)
            }
        }
    }
    
    private val buttonEditScore: JButton = createButton("grades.edit").apply {
        addActionListener {
            val student = viewModel.selectedStudent.value
            if (student != null) {
                // 打开编辑成绩对话框
                // TODO: 实现编辑成绩对话框
            }
        }
    }
    
    private val buttonImport: JButton = createButton("button.import").apply {
        addActionListener {
            // 打开导入对话框
            // TODO: 实现导入功能
        }
    }
    
    private val buttonExport: JButton = createButton("button.export").apply {
        addActionListener {
            // 打开导出对话框
            // TODO: 实现导出功能
        }
    }
    
    private val buttonGenerateDocument: JButton = createButton("button.generate").apply {
        addActionListener {
            // 打开生成文档对话框
            // TODO: 实现生成文档功能
        }
    }

    init {
        this.layout = MigLayout("hidemode 3", "[fill]", "[][][][][][][][][][][][][]")
        this.add(buttonAdd, "cell 0 0")
        this.add(buttonDel, "cell 0 1")
        this.add(buttonEditScore, "cell 0 2")
        this.add(buttonImport, "cell 0 3")
        this.add(buttonExport, "cell 0 4")
        this.add(buttonGenerateDocument, "cell 0 5")
    }

    override fun observer() {
        // 订阅ViewModel的加载状态和选中学生
        scope.launch {
            combine(viewModel.loadState, viewModel.selectedStudent) { loadState, student ->
                loadState to (student != null)
            }.collectLatest { (loadState, isSelected) ->
                // 更新按钮状态
                updateState(loadState, isSelected)
            }
        }
    }

    /**
     * 更新按钮状态
     */
    fun updateState(state: StudentDataLoadState, selected: Boolean) {
        // 根据数据库连接状态和加载状态确定按钮启用状态
        val enabled = state == StudentDataLoadState.DONE

        // 设置按钮启用状态
        buttonAdd.isEnabled = enabled
        buttonDel.isEnabled = enabled && selected
        buttonEditScore.isEnabled = enabled && selected
        buttonImport.isEnabled = enabled
        buttonExport.isEnabled = enabled
        buttonGenerateDocument.isEnabled = enabled
    }
}