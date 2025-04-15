/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.component.dialog

import kotlinx.coroutines.launch
import moe.hhm.parfait.app.service.CertificateRecordService
import moe.hhm.parfait.infra.i18n.I18nUtils
import moe.hhm.parfait.infra.i18n.I18nUtils.createButton
import moe.hhm.parfait.ui.base.CoroutineComponent
import moe.hhm.parfait.ui.base.DefaultCoroutineComponent
import net.miginfocom.swing.MigLayout
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.awt.Dimension
import java.awt.Window
import java.time.format.DateTimeFormatter
import java.util.*
import javax.swing.*
import javax.swing.table.DefaultTableModel

/**
 * 证书记录查看对话框
 */
class CertificateRecordsDialog(
    private val templateId: UUID,
    private val templateName: String,
    owner: Window? = null,
    parent: CoroutineComponent? = null
) : JDialog(owner),
    KoinComponent, CoroutineComponent by DefaultCoroutineComponent(parent) {

    // 通过Koin获取服务
    private val recordService: CertificateRecordService by inject()

    // 表格相关组件
    private val tableModel = DefaultTableModel()
    private val table = JTable(tableModel)
    private val scrollPane = JScrollPane(table)

    // 关闭按钮
    private val buttonClose = createButton("button.close").apply {
        addActionListener { dispose() }
    }

    // 初始化
    init {
        initDialog()
        initComponents()
        loadData()
    }

    private fun initDialog() {
        title = I18nUtils.getFormattedText("certificate.record.dialog.title", templateName)
        // 设置对话框属性
        isModal = true
        defaultCloseOperation = DISPOSE_ON_CLOSE

        // 允许调整窗口大小
        isResizable = true

        // 设置默认大小
        preferredSize = Dimension(800, 400)
        minimumSize = Dimension(600, 300)

        // 使用布局管理器
        contentPane = JPanel(MigLayout("fill, insets 10", "[grow,fill]", "[grow,fill][]"))
    }

    private fun initComponents() {
        // 设置表格标题
        tableModel.setColumnIdentifiers(
            arrayOf(
                I18nUtils.getText("certificate.record.content"),
                I18nUtils.getText("certificate.record.issuedDate"),
                I18nUtils.getText("certificate.record.issuedBy"),
                I18nUtils.getText("certificate.record.purpose")
            )
        )

        // 表格属性设置
        table.apply {
            autoResizeMode = JTable.AUTO_RESIZE_ALL_COLUMNS
            fillsViewportHeight = true
            selectionModel.selectionMode = ListSelectionModel.SINGLE_SELECTION
        }

        // 添加组件到界面
        contentPane.add(scrollPane, "cell 0 0, grow")
        contentPane.add(buttonClose, "cell 0 1, tag right")
    }

    private fun loadData() {
        scope.launch {
            // 清空表格
            tableModel.rowCount = 0

            // 加载记录
            val records = recordService.getByTemplateId(templateId)

            // 日期格式化
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

            // 添加数据到表格
            for (record in records) {
                tableModel.addRow(
                    arrayOf(
                        record.content,
                        record.issuedDate.format(dateFormatter),
                        record.issuedBy,
                        record.purpose ?: ""
                    )
                )
            }
        }
    }

    // 显示对话框
    fun showDialog() {
        pack()
        setLocationRelativeTo(owner)
        isVisible = true
    }

    companion object {
        // 静态方法便于在其他地方调用
        fun show(templateId: UUID, templateName: String, owner: Window? = null) {
            val dialog = CertificateRecordsDialog(templateId, templateName, owner)
            dialog.showDialog()
        }
    }
}