/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.component.panel

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import moe.hhm.parfait.dto.StudentDTO
import moe.hhm.parfait.infra.i18n.I18nUtils
import moe.hhm.parfait.infra.i18n.I18nUtils.createButton
import moe.hhm.parfait.ui.action.StudentAction
import moe.hhm.parfait.ui.base.CoroutineComponent
import moe.hhm.parfait.ui.base.DefaultCoroutineComponent
import moe.hhm.parfait.ui.component.dialog.AdvancedFilterCriteria
import moe.hhm.parfait.ui.component.dialog.StudentModifyDialog
import moe.hhm.parfait.ui.component.dialog.StudentScoresDialog
import moe.hhm.parfait.ui.state.VMState
import moe.hhm.parfait.ui.viewmodel.StudentDataViewModel
import net.miginfocom.swing.MigLayout
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import javax.swing.JButton
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.SwingUtilities
import moe.hhm.parfait.app.service.StudentSearchService
import moe.hhm.parfait.ui.component.dialog.SearchFilterCriteria

class StudentDataButtonPanel(parent: CoroutineComponent? = null) : JPanel(), KoinComponent,
    CoroutineComponent by DefaultCoroutineComponent(parent) {
    // 通过Koin获取ViewModel
    private val viewModel: StudentDataViewModel by inject()
    private val studentSearchService: StudentSearchService by inject()

    private val buttonAdd: JButton = createButton("student.action.add").apply {
        addActionListener {
            // 打开添加学生对话框
            val owner = SwingUtilities.getWindowAncestor(this@StudentDataButtonPanel)
            StudentModifyDialog.show(null, owner)
        }
    }

    private val buttonDel: JButton = createButton("student.action.delete").apply {
        addActionListener {
            val selectedStudents = viewModel.selectedStudents.value
            if (selectedStudents.isNotEmpty()) {
                // 显示确认对话框
                val owner = SwingUtilities.getWindowAncestor(this@StudentDataButtonPanel)

                // 根据选择的学生数量构建不同的确认消息
                // 姓名取前五个人的
                val message = I18nUtils.getFormattedText(
                    "student.delete.confirm",
                    selectedStudents.take(5).joinToString(", ") { it.name },
                    selectedStudents.size
                )

                val result = JOptionPane.showConfirmDialog(
                    owner,
                    message,
                    I18nUtils.getText("student.delete.title"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                )

                // 如果用户确认删除
                if (result == JOptionPane.YES_OPTION) {
                    // 批量删除选中的学生
                    viewModel.deleteStudents(selectedStudents.mapNotNull { it.uuid })
                }
            }
        }
    }

    private val buttonEdit: JButton = createButton("student.action.edit").apply {
        addActionListener {
            val student = viewModel.selectedStudents.value.firstOrNull()
            if (student != null) {
                val owner = SwingUtilities.getWindowAncestor(this@StudentDataButtonPanel)
                StudentModifyDialog.show(student, owner)
            }
        }
    }

    private val buttonEditScore: JButton = createButton("score.action.edit").apply {
        addActionListener {
            val student = viewModel.selectedStudents.value.firstOrNull()
            if (student != null) {
                // 打开编辑成绩对话框
                val owner = SwingUtilities.getWindowAncestor(this@StudentDataButtonPanel)
                StudentScoresDialog.show(student, owner)
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
            val selectedStudents = viewModel.selectedStudents.value
            val owner = SwingUtilities.getWindowAncestor(this@StudentDataButtonPanel)
            moe.hhm.parfait.ui.component.dialog.CertificateGenerateDialog.show(selectedStudents, owner)
        }
    }

    private val buttonExportInformationStudent: JButton = createButton("student.action.export").apply {
        addActionListener {
            // 打开导出对话框
            val owner = SwingUtilities.getWindowAncestor(this@StudentDataButtonPanel)
            moe.hhm.parfait.ui.component.dialog.StudentExportDialog.show(owner)
        }
    }

    init {
        this.layout = MigLayout("hidemode 3", "[fill]", "[][][][][][][][][][][][][]")
        this.add(buttonAdd, "cell 0 0")
        this.add(buttonDel, "cell 0 1")
        this.add(buttonEdit, "cell 0 2")
        this.add(buttonEditScore, "cell 0 3")
        this.add(buttonImport, "cell 0 4")
        this.add(buttonExportInformationStudent, "cell 0 5")
        this.add(buttonGenerateDocument, "cell 0 6")
    }

    override fun observer() {
        // 订阅ViewModel的加载状态和选中学生
        scope.launch {
            combine(viewModel.vmState, viewModel.selectedStudents) { loadState, students ->
                loadState to students.isNotEmpty()
            }.collectLatest { (loadState, hasSelection) ->
                // 更新按钮状态
                updateState(loadState, hasSelection)
            }
        }
    }

    /**
     * 更新按钮状态
     */
    fun updateState(state: VMState, hasSelection: Boolean) {
        // 根据数据库连接状态和加载状态确定按钮启用状态
        val enabled = state == VMState.DONE

        // 设置按钮启用状态
        buttonAdd.isEnabled = enabled
        buttonDel.isEnabled = enabled && hasSelection
        buttonEdit.isEnabled = enabled && hasSelection && viewModel.selectedStudents.value.size == 1
        buttonEditScore.isEnabled = enabled && hasSelection && viewModel.selectedStudents.value.size == 1
        buttonImport.isEnabled = enabled
        buttonExport.isEnabled = enabled
        buttonExportInformationStudent.isEnabled = enabled
        buttonGenerateDocument.isEnabled = enabled && hasSelection
    }
}