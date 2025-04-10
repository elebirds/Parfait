/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.view.view

import moe.hhm.parfait.utils.i18n.I18nUtils.createButton
import moe.hhm.parfait.view.component.table.StudentDataTable
import net.miginfocom.swing.MigLayout
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane

class StudentDataView : JPanel() {
    private val buttonAdd: JButton = createButton("student.add")
    private val buttonDel: JButton = createButton("student.delete")
    private val buttonEditScore: JButton = createButton("grades.edit")
    private val buttonImport: JButton = createButton("button.import")
    private val buttonExport: JButton = createButton("button.export")
    private val buttonGenerateDocument: JButton = createButton("button.generate")
    private val buttonPanel: JPanel = JPanel().apply {
        this.layout = MigLayout("hidemode 3", "[fill]", "[][][][][][][][][][][][][]")
        this.add(buttonAdd, "cell 0 0")
        this.add(buttonDel, "cell 0 1")
        this.add(buttonEditScore, "cell 0 2")
        this.add(buttonImport, "cell 0 3")
        this.add(buttonExport, "cell 0 4")
        this.add(buttonGenerateDocument, "cell 0 5")
    }
    private val table: StudentDataTable = StudentDataTable()
    private val scrollPane = JScrollPane().apply {
        this.setViewportView(table)
    }

    init {
        this.layout = MigLayout(
            "hidemode 3",  // columns
            "[fill]" + "[fill]",  // rows
            "[]" + "[]" + "[]"
        )
        this.add(JLabel(), "cell 0 0") // 占位符？我也不知道有什么用
        this.add(scrollPane, "cell 0 0,dock center")
        this.add(buttonPanel, "cell 0 1")
    }
}