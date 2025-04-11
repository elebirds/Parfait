/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.component.dialog

import kotlinx.coroutines.launch
import moe.hhm.parfait.app.service.GradeCalculationService
import moe.hhm.parfait.dto.StudentDTO
import moe.hhm.parfait.infra.i18n.I18nUtils
import moe.hhm.parfait.infra.i18n.I18nUtils.bindText
import moe.hhm.parfait.ui.base.CoroutineComponent
import moe.hhm.parfait.ui.base.DefaultCoroutineComponent
import moe.hhm.parfait.ui.component.dialog.grade.GradeModifyDialog
import moe.hhm.parfait.ui.component.table.GradesTableModel
import moe.hhm.parfait.ui.viewmodel.StudentDataViewModel
import net.miginfocom.swing.MigLayout
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Window
import javax.swing.*

/**
 * 学生成绩管理对话框
 */
class StudentGradesDialog(
    private val studentDTO: StudentDTO,
    owner: Window? = null,
    parent: CoroutineComponent? = null
) : JDialog(owner), KoinComponent, CoroutineComponent by DefaultCoroutineComponent(parent) {

    // 通过Koin获取ViewModel和服务
    private val viewModel: StudentDataViewModel by inject()
    private val gradeCalculationService: GradeCalculationService by inject()

    // 成绩表格相关组件
    private val tableModel = GradesTableModel()
    private val table = JTable(tableModel)
    private val scrollPane = JScrollPane(table)

    // 统计信息标签
    private val avgScoreLabel = JLabel()
    private val weightedAvgLabel = JLabel()
    private val gpaLabel = JLabel()
    private val totalCreditLabel = JLabel()

    // 按钮
    private val addButton = JButton().apply {
        bindText(this, "button.add")
        addActionListener { addGrade() }
    }

    private val editButton = JButton().apply {
        bindText(this, "button.edit")
        addActionListener { editGrade() }
    }

    private val deleteButton = JButton().apply {
        bindText(this, "button.delete")
        addActionListener { deleteGrade() }
    }

    private val saveButton = JButton().apply {
        bindText(this, "button.save")
        addActionListener { saveChanges() }
    }

    private val closeButton = JButton().apply {
        bindText(this, "button.cancel")
        addActionListener { dispose() }
    }

    init {
        initDialog()
        initComponents()
        loadData()
        updateStatistics()
    }

    private fun initDialog() {
        title = I18nUtils.getFormattedText("grades.dialog.title", "${studentDTO.studentId}-${studentDTO.name}")
        isModal = true
        defaultCloseOperation = DISPOSE_ON_CLOSE
        isResizable = false
        preferredSize = Dimension(800, 500)
        minimumSize = Dimension(800, 500)
        maximumSize = Dimension(800, 500)

        // 设置内容面板
        contentPane.layout = BorderLayout()
    }

    private fun initComponents() {
        // 主面板 - 使用BorderLayout
        val mainPanel = JPanel(BorderLayout())

        // 中央面板 - 成绩表格
        val tablePanel = JPanel(BorderLayout())
        tablePanel.border = BorderFactory.createTitledBorder(I18nUtils.getText("grades.dialog.scores"))

        // 表格设置
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
        table.rowHeight = 25
        table.autoResizeMode = JTable.AUTO_RESIZE_ALL_COLUMNS

        tablePanel.add(scrollPane, BorderLayout.CENTER)

        // 表格操作按钮面板
        val tableButtonPanel = JPanel(MigLayout("insets 5", "[][][grow]"))
        tableButtonPanel.add(addButton)
        tableButtonPanel.add(editButton)
        tableButtonPanel.add(deleteButton)

        tablePanel.add(tableButtonPanel, BorderLayout.SOUTH)

        // 底部面板 - 统计信息和按钮
        val bottomPanel = JPanel(BorderLayout())

        // 统计信息面板
        val statsPanel = JPanel(MigLayout("fillx", "[][grow]"))
        statsPanel.border = BorderFactory.createTitledBorder(I18nUtils.getText("grades.dialog.summary"))

        statsPanel.add(JLabel(I18nUtils.getText("grades.dialog.stats.avg")))
        statsPanel.add(avgScoreLabel, "wrap")

        statsPanel.add(JLabel(I18nUtils.getText("grades.dialog.stats.weighted")))
        statsPanel.add(weightedAvgLabel, "wrap")

        statsPanel.add(JLabel(I18nUtils.getText("grades.dialog.stats.gpa")))
        statsPanel.add(gpaLabel, "wrap")

        statsPanel.add(JLabel(I18nUtils.getText("grades.dialog.stats.total.credit")))
        statsPanel.add(totalCreditLabel, "wrap")

        bottomPanel.add(statsPanel, BorderLayout.CENTER)

        // 底部按钮面板
        val buttonPanel = JPanel(MigLayout("insets 10", "[grow][][]"))
        buttonPanel.add(Box.createHorizontalGlue(), "grow")
        buttonPanel.add(saveButton)
        buttonPanel.add(closeButton)

        bottomPanel.add(buttonPanel, BorderLayout.SOUTH)

        // 将各面板添加到主面板
        mainPanel.add(tablePanel, BorderLayout.CENTER)
        mainPanel.add(bottomPanel, BorderLayout.SOUTH)

        // 将主面板添加到对话框
        contentPane.add(mainPanel, BorderLayout.CENTER)
    }

    private fun loadData() {
        // 加载学生成绩数据到表格
        tableModel.setData(studentDTO.scores)
    }

    private fun updateStatistics() {
        val scores = tableModel.getData()
        if (scores.isEmpty()) {
            avgScoreLabel.text = "0.0"
            weightedAvgLabel.text = "0.0"
            gpaLabel.text = "0.0"
            totalCreditLabel.text = "0"
        } else {
            avgScoreLabel.text = String.format("%.2f", gradeCalculationService.simpleMean(scores))
            weightedAvgLabel.text = String.format("%.2f", gradeCalculationService.weightedMean(scores))
            gpaLabel.text = String.format("%.2f", gradeCalculationService.gpa(scores))
            totalCreditLabel.text = scores.sumOf { it.credit }.toString()
        }
    }

    private fun addGrade() {
        val dialog = GradeModifyDialog(this)
        dialog.setLocationRelativeTo(this)
        dialog.isVisible = true

        if (dialog.result != null) {
            tableModel.addScore(dialog.result!!)
            updateStatistics()
        }
    }

    private fun editGrade() {
        val selectedRow = table.selectedRow
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(
                this,
                I18nUtils.getText("grades.dialog.validation.required"),
                I18nUtils.getText("error.generic"),
                JOptionPane.ERROR_MESSAGE
            )
            return
        }

        val selectedScore = tableModel.getData()[selectedRow]
        val dialog = GradeModifyDialog(this, selectedScore)
        dialog.setLocationRelativeTo(this)
        dialog.isVisible = true

        if (dialog.result != null) {
            tableModel.updateScore(selectedRow, dialog.result!!)
            updateStatistics()
        }
    }

    private fun deleteGrade() {
        val selectedRow = table.selectedRow
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(
                this,
                I18nUtils.getText("grades.dialog.validation.required"),
                I18nUtils.getText("error.generic"),
                JOptionPane.ERROR_MESSAGE
            )
            return
        }

        val confirm = JOptionPane.showConfirmDialog(
            this,
            I18nUtils.getText("grades.dialog.confirm.delete"),
            I18nUtils.getText("grades.action.delete"),
            JOptionPane.YES_NO_OPTION
        )

        if (confirm == JOptionPane.YES_OPTION) {
            tableModel.removeScore(selectedRow)
            updateStatistics()
        }
    }

    private fun saveChanges() {
        // 更新学生DTO中的成绩
        studentDTO.scores = tableModel.getData()

        // 提交到ViewModel
        scope.launch {
            try {
                viewModel.updateStudent(studentDTO, isScores = true)
                JOptionPane.showMessageDialog(
                    this@StudentGradesDialog,
                    I18nUtils.getText("grades.dialog.save.success"),
                    I18nUtils.getText("button.save"),
                    JOptionPane.INFORMATION_MESSAGE
                )
                dispose()
            } catch (e: Exception) {
                JOptionPane.showMessageDialog(
                    this@StudentGradesDialog,
                    e.message,
                    I18nUtils.getText("error.generic"),
                    JOptionPane.ERROR_MESSAGE
                )
            }
        }
    }

    companion object {
        fun show(student: StudentDTO, owner: Window? = null) {
            val dialog = StudentGradesDialog(student, owner)
            dialog.pack()
            dialog.setLocationRelativeTo(owner)
            dialog.isVisible = true
        }
    }
} 