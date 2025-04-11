/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.component.panel

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import moe.hhm.parfait.infra.i18n.I18nUtils.createButton
import moe.hhm.parfait.ui.base.CoroutineComponent
import moe.hhm.parfait.ui.base.DefaultCoroutineComponent
import moe.hhm.parfait.ui.component.dialog.StudentModifyDialog
import moe.hhm.parfait.ui.component.dialog.StudentGradesDialog
import moe.hhm.parfait.ui.state.StudentDataLoadState
import moe.hhm.parfait.ui.viewmodel.StudentDataViewModel
import net.miginfocom.swing.MigLayout
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.SwingUtilities

class StudentDataButtonPanel(parent: CoroutineComponent? = null) : JPanel(), KoinComponent,
    CoroutineComponent by DefaultCoroutineComponent(parent) {
    // 通过Koin获取ViewModel
    private val viewModel: StudentDataViewModel by inject()

    private val buttonAdd: JButton = createButton("student.action.add").apply {
        addActionListener {
            // 打开添加学生对话框
            val owner = SwingUtilities.getWindowAncestor(this@StudentDataButtonPanel)
            StudentModifyDialog.show(null, owner)
        }
    }

    private val buttonDel: JButton = createButton("student.action.delete").apply {
        addActionListener {
            val uuid = viewModel.selectedStudent.value?.uuid
            if (uuid != null) {
                viewModel.deleteStudent(uuid)
            }
        }
    }

    private val buttonEdit: JButton = createButton("student.action.edit").apply {
        addActionListener {
            val student = viewModel.selectedStudent.value
            if (student != null) {
                val owner = SwingUtilities.getWindowAncestor(this@StudentDataButtonPanel)
                StudentModifyDialog.show(student, owner)

            }
        }
    }

    private val buttonEditScore: JButton = createButton("grades.action.edit").apply {
        addActionListener {
            val student = viewModel.selectedStudent.value
            if (student != null) {
                // 打开编辑成绩对话框
                val owner = SwingUtilities.getWindowAncestor(this@StudentDataButtonPanel)
                StudentGradesDialog.show(student, owner)
            }
        }
    }

    private val buttonImport: JButton = createButton("student.action.import").apply {
        addActionListener {
            // 打开导入对话框
            // TODO: 实现导入功能
        }
    }

    private val buttonExport: JButton = createButton("student.action.export").apply {
        addActionListener {
            // 打开导出对话框
            // TODO: 实现导出功能
        }
    }

    private val buttonGenerateDocument: JButton = createButton("certificate.action.generate").apply {
        addActionListener {
            // 打开生成文档对话框
            // TODO: 实现生成文档功能
        }
    }

    init {
        this.layout = MigLayout("hidemode 3", "[fill]", "[][][][][][][][][][][][][]")
        this.add(buttonAdd, "cell 0 0")
        this.add(buttonDel, "cell 0 1")
        this.add(buttonEdit, "cell 0 2")
        this.add(buttonEditScore, "cell 0 3")
        this.add(buttonImport, "cell 0 4")
        this.add(buttonExport, "cell 0 5")
        this.add(buttonGenerateDocument, "cell 0 6")
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