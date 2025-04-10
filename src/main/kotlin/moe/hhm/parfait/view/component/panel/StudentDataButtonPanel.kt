/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.view.component.panel

import moe.hhm.parfait.utils.i18n.I18nUtils.createButton
import moe.hhm.parfait.view.component.table.StudentDataTable
import moe.hhm.parfait.viewmodel.StudentDataUiState
import moe.hhm.parfait.viewmodel.StudentDataViewModel
import net.miginfocom.swing.MigLayout
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import javax.swing.JButton
import javax.swing.JPanel

class StudentDataButtonPanel : JPanel(), KoinComponent {
    // 通过Koin获取ViewModel
    private val viewModel: StudentDataViewModel by inject()

    private val buttonAdd: JButton = createButton("student.add")
    private val buttonDel: JButton = createButton("student.delete")
    private val buttonEditScore: JButton = createButton("grades.edit")
    private val buttonImport: JButton = createButton("button.import")
    private val buttonExport: JButton = createButton("button.export")
    private val buttonGenerateDocument: JButton = createButton("button.generate")

    init {
        this.layout = MigLayout("hidemode 3", "[fill]", "[][][][][][][][][][][][][]")
        this.add(buttonAdd, "cell 0 0")
        this.add(buttonDel, "cell 0 1")
        this.add(buttonEditScore, "cell 0 2")
        this.add(buttonImport, "cell 0 3")
        this.add(buttonExport, "cell 0 4")
        this.add(buttonGenerateDocument, "cell 0 5")
    }

    fun updateState(state: StudentDataUiState, table: StudentDataTable) {
        // 根据数据库连接状态和加载状态确定按钮启用状态
        val dbConnected = state.databaseConnected
        val enabled = dbConnected && !state.isLoading
        val hasSelection = table.getSelectedStudent() != null

        // 设置按钮启用状态
        buttonAdd.isEnabled = enabled
        buttonDel.isEnabled = enabled && hasSelection
        buttonEditScore.isEnabled = enabled && hasSelection
        buttonImport.isEnabled = enabled
        buttonExport.isEnabled = enabled
        buttonGenerateDocument.isEnabled = enabled
    }
}