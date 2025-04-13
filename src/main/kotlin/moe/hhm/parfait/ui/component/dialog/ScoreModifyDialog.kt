/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.component.dialog

import com.formdev.flatlaf.FlatClientProperties
import moe.hhm.parfait.dto.CourseType
import moe.hhm.parfait.dto.ScoreDTO
import moe.hhm.parfait.infra.i18n.I18nUtils
import net.miginfocom.swing.MigLayout
import java.awt.Window
import javax.swing.Box
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JTextField

class ScoreModifyDialog(owner: Window, existingScore: ScoreDTO? = null) : JDialog(owner) {
    var result: ScoreDTO? = null

    private val textCourseName = JTextField(20)
    private val comboType = JComboBox<String>(CourseType.entries.map { I18nUtils.getText(it.i18nKey) }.toTypedArray())
    private val textExam = JTextField(20)
    private val textCredit = JTextField(5)
    private val textScore = JTextField(5)
    private val checkGPA = JCheckBox()

    init {
        // 设置对话框属性
        isModal = true
        isResizable = false

        title = if (existingScore == null) {
            I18nUtils.getText("score.dialog.add.title")
        } else {
            I18nUtils.getText("score.dialog.edit.title")
        }
        defaultCloseOperation = DISPOSE_ON_CLOSE

        // 设置布局
        contentPane = JPanel(MigLayout("fillx, wrap 2", "[][grow,fill]"))

        // 添加组件
        add(JLabel(I18nUtils.getText("score.course")))
        add(textCourseName)

        add(JLabel(I18nUtils.getText("score.type")))
        add(comboType)

        add(JLabel(I18nUtils.getText("score.exam")))
        add(textExam)

        add(JLabel(I18nUtils.getText("score.credit")))
        add(textCredit)

        add(JLabel(I18nUtils.getText("score.score")))
        add(textScore)

        add(JLabel(I18nUtils.getText("score.gpa")))
        add(checkGPA)

        // 填充现有数据（如果有）
        existingScore?.let {
            textCourseName.text = it.name
            comboType.selectedItem = I18nUtils.getText(it.type.i18nKey)
            textExam.text = it.exam
            textCredit.text = it.credit.toString()
            textScore.text = it.score.toString()
            checkGPA.isSelected = it.gpa
        }

        // 设置提示文本
        textCourseName.putClientProperty(
            FlatClientProperties.PLACEHOLDER_TEXT,
            I18nUtils.getText("score.dialog.placeholder.course")
        )
        textExam.putClientProperty(
            FlatClientProperties.PLACEHOLDER_TEXT,
            I18nUtils.getText("score.dialog.placeholder.exam")
        )
        textCredit.putClientProperty(
            FlatClientProperties.PLACEHOLDER_TEXT,
            I18nUtils.getText("score.dialog.placeholder.credit")
        )
        textScore.putClientProperty(
            FlatClientProperties.PLACEHOLDER_TEXT,
            I18nUtils.getText("score.dialog.placeholder.score")
        )

        // 按钮面板
        val buttonPanel = JPanel(MigLayout("insets 10", "[grow][][]"))
        val okButton = object : JButton(I18nUtils.getText("button.ok")) {
            override fun isDefaultButton(): Boolean = true
        }
        val cancelButton = JButton(I18nUtils.getText("button.cancel"))

        okButton.addActionListener {
            if (validateAndSave()) {
                dispose()
            }
        }

        cancelButton.addActionListener {
            dispose()
        }

        buttonPanel.add(Box.createHorizontalGlue(), "grow")
        buttonPanel.add(okButton)
        buttonPanel.add(cancelButton)

        add(buttonPanel, "span, growx")

        pack()
        setLocationRelativeTo(owner)
    }

    private fun validateAndSave(): Boolean {
        val name = textCourseName.text
        val type = CourseType.entries[comboType.selectedIndex]
        val exam = textExam.text
        val creditText = textCredit.text
        val scoreText = textScore.text
        val gpaEnabled = checkGPA.isSelected

        // 检查必填字段
        if (name.isEmpty() || exam.isEmpty() || creditText.isEmpty() || scoreText.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                I18nUtils.getText("score.dialog.validation.required"),
                I18nUtils.getText("error.generic"),
                JOptionPane.ERROR_MESSAGE
            )
            return false
        }

        // 检查数字格式
        try {
            val credit = creditText.toInt()
            val score = scoreText.toDouble()

            // 创建成绩对象
            result = ScoreDTO(
                name = name,
                type = type,
                exam = exam,
                credit = credit,
                score = score,
                gpa = gpaEnabled
            )

            return true
        } catch (e: NumberFormatException) {
            JOptionPane.showMessageDialog(
                this,
                I18nUtils.getText("score.dialog.validation.numeric"),
                I18nUtils.getText("error.generic"),
                JOptionPane.ERROR_MESSAGE
            )
            return false
        }
    }
}