/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.component.dialog

import com.alibaba.excel.EasyExcel
import com.alibaba.excel.context.AnalysisContext
import com.alibaba.excel.event.AnalysisEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moe.hhm.parfait.app.service.GpaStandardService
import moe.hhm.parfait.dto.*
import moe.hhm.parfait.exception.BusinessException
import moe.hhm.parfait.infra.i18n.I18nUtils
import moe.hhm.parfait.infra.i18n.I18nUtils.bindText
import moe.hhm.parfait.ui.action.ScoreAction
import moe.hhm.parfait.ui.base.CoroutineComponent
import moe.hhm.parfait.ui.base.DefaultCoroutineComponent
import moe.hhm.parfait.ui.component.table.model.ScoresTableModel
import moe.hhm.parfait.ui.viewmodel.StudentDataViewModel
import net.miginfocom.swing.MigLayout
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Window
import javax.swing.*
import javax.swing.filechooser.FileNameExtensionFilter

/**
 * 学生成绩管理对话框
 */
class StudentScoresDialog(
    private val studentDTO: StudentDTO,
    owner: Window? = null,
    parent: CoroutineComponent? = null
) : JDialog(owner), KoinComponent, CoroutineComponent by DefaultCoroutineComponent(parent) {

    // 通过Koin获取ViewModel和服务
    private val viewModel: StudentDataViewModel by inject()
    private val gpaService: GpaStandardService by inject()

    // 成绩表格相关组件
    private val tableModel = ScoresTableModel()
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

    private val importButton = JButton().apply {
        bindText(this, "score.dialog.action.import")
        addActionListener { importScoresFromExcel() }
    }

    private val exportButton = JButton().apply {
        bindText(this, "score.dialog.action.export")
        addActionListener { exportScoresToExcel() }
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
        title = I18nUtils.getFormattedText("score.dialog.title", "${studentDTO.studentId}-${studentDTO.name}")
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
        tablePanel.border = BorderFactory.createTitledBorder(I18nUtils.getText("score.dialog.scores"))

        // 表格设置
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
        table.rowHeight = 25
        table.autoResizeMode = JTable.AUTO_RESIZE_ALL_COLUMNS

        tablePanel.add(scrollPane, BorderLayout.CENTER)

        // 表格操作按钮面板
        val tableButtonPanel = JPanel(MigLayout("insets 5", "[][][][][grow]"))
        tableButtonPanel.add(addButton)
        tableButtonPanel.add(editButton)
        tableButtonPanel.add(deleteButton)
        tableButtonPanel.add(importButton)
        tableButtonPanel.add(exportButton)

        tablePanel.add(tableButtonPanel, BorderLayout.SOUTH)

        // 底部面板 - 统计信息和按钮
        val bottomPanel = JPanel(BorderLayout())

        // 统计信息面板
        val statsPanel = JPanel(MigLayout("fillx", "[][grow]"))
        statsPanel.border = BorderFactory.createTitledBorder(I18nUtils.getText("score.dialog.summary"))

        statsPanel.add(JLabel(I18nUtils.getText("score.dialog.stats.avg")))
        statsPanel.add(avgScoreLabel, "wrap")

        statsPanel.add(JLabel(I18nUtils.getText("score.dialog.stats.weighted")))
        statsPanel.add(weightedAvgLabel, "wrap")

        statsPanel.add(JLabel(I18nUtils.getText("score.dialog.stats.gpa")))
        statsPanel.add(gpaLabel, "wrap")

        statsPanel.add(JLabel(I18nUtils.getText("score.dialog.stats.total.credit")))
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
            avgScoreLabel.text = String.format("%.2f", scores.simpleMean())
            weightedAvgLabel.text = String.format("%.2f", scores.weightedMean())
            gpaLabel.text = String.format("%.2f", gpaService.getDefault().mapping.getGpa(scores))
            totalCreditLabel.text = scores.sumOf { it.credit }.toString()
        }
    }

    private fun addGrade() {
        val dialog = ScoreModifyDialog(this)
        dialog.setLocationRelativeTo(this)
        dialog.isVisible = true

        if (dialog.result != null) {
            tableModel.addScore(dialog.result!!)
            updateStatistics()
        }
    }

    private fun editGrade() {
        val selectedRow = table.selectedRow
        if (selectedRow < 0) throw BusinessException("score.dialog.validation.needSelect")

        val selectedScore = tableModel.getData()[selectedRow]
        val dialog = ScoreModifyDialog(this, selectedScore)
        dialog.setLocationRelativeTo(this)
        dialog.isVisible = true

        if (dialog.result != null) {
            tableModel.updateScore(selectedRow, dialog.result!!)
            updateStatistics()
        }
    }

    private fun deleteGrade() {
        val selectedRow = table.selectedRow
        if (selectedRow < 0) throw BusinessException("score.dialog.validation.needSelect")

        val confirm = JOptionPane.showConfirmDialog(
            this,
            I18nUtils.getText("score.dialog.confirm.delete"),
            I18nUtils.getText("score.action.delete"),
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
                    this@StudentScoresDialog,
                    I18nUtils.getText("score.dialog.save.success"),
                    I18nUtils.getText("button.save"),
                    JOptionPane.INFORMATION_MESSAGE
                )
            } finally {
                dispose()
            }
        }
    }

    private fun exportScoresToExcel() {
        scope.launch {
            ScoreAction.exportToExcel(tableModel.getData(), this@StudentScoresDialog)
        }
    }

    private fun importScoresFromExcel() {
        val fileChooser = JFileChooser()
        fileChooser.fileFilter = FileNameExtensionFilter("Excel Files", "xlsx")

        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return

        val filePath = fileChooser.selectedFile.absolutePath

        scope.launch {
            var successCount = 0
            var failCount = 0
            var msg = ""
            withContext(Dispatchers.IO) {
                val scoreList = mutableListOf<ScoreDTO>()

                val listener = object : AnalysisEventListener<Map<Int, String>>() {
                    // 处理每一行数据
                    override fun invoke(data: Map<Int, String>, context: AnalysisContext) {
                        // 修正：应该是 data 而不是 this.data
                        try {
                            // 跳过可能的标题行（第一行）
                            if (context.readRowHolder().rowIndex == 0) return

                            val score = ScoreDTO(
                                name = data[0] ?: "",
                                type = CourseType.entries.find { it.toString() == data[1] } ?: CourseType.DEFAULT,
                                exam = data[2] ?: "",
                                credit = data[3]?.toIntOrNull() ?: 0,
                                score = data[4]?.toDoubleOrNull() ?: 0.0,
                                gpa = when (data[5]) {
                                    I18nUtils.getText("score.gpa.no"), "N", "否", "Non", "F" -> false
                                    else -> true
                                }
                            )
                            scoreList.add(score)
                            successCount++
                        } catch (e: Exception) {
                            // 忽略格式错误的行
                            msg += e.localizedMessage
                            failCount++
                        }
                    }

                    override fun doAfterAllAnalysed(context: AnalysisContext) {
                        // 完成解析
                    }
                }

                EasyExcel.read(filePath)
                    .sheet()
                    .registerReadListener(listener)
                    .doRead()

                tableModel.setData(scoreList + tableModel.getData())
                updateStatistics()
            }
            JOptionPane.showMessageDialog(
                null,
                I18nUtils.getFormattedText("score.dialog.action.import.success", successCount, failCount, msg),
                I18nUtils.getFormattedText("score.dialog.title", studentDTO.name),
                JOptionPane.INFORMATION_MESSAGE
            )
        }
    }

    companion object {
        fun show(student: StudentDTO, owner: Window? = null) {
            val dialog = StudentScoresDialog(student, owner)
            dialog.pack()
            dialog.setLocationRelativeTo(owner)
            dialog.isVisible = true
        }
    }
} 